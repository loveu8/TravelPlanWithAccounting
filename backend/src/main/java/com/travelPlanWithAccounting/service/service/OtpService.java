package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.otp.OtpSendResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpStatusResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpVerifyResponse;
import com.travelPlanWithAccounting.service.entity.AuthInfo;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.model.OtpVerificationRequest;
import com.travelPlanWithAccounting.service.repository.AuthInfoRepository;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {
  private static final int OTP_LENGTH = 6;
  private static final int OTP_EXPIRY_MINUTES = 10;
  private static final int MAX_ATTEMPTS = 3;

  private final OtpCacheService otpCacheService;
  private final EmailService emailService;
  private final AuthInfoRepository authInfoRepository;
  private final MemberRepository memberRepository;

  private final Random random = new Random();

  @Value("${otp.resend-interval-seconds:30}")
  private int otpResendIntervalSeconds;

  // ------- 提供給 MemberService 的核心方法 -------

  /** 發送 OTP：更新 cache + 寫入 auth_info(validation=false) + 發送 Email */
  @Transactional
  public OtpData generateOtp(String email, OtpPurpose purpose) {
    OtpData existingOtp = otpCacheService.getOtpData(email, purpose);
    LocalDateTime now = LocalDateTime.now();

    if (existingOtp != null
        && existingOtp.getLastSentTime() != null
        && existingOtp.getLastSentTime().plusSeconds(otpResendIntervalSeconds).isAfter(now)) {
      throw new InvalidOtpException.ResendTooFrequently(otpResendIntervalSeconds);
    }

    String otpCode = generateRandomOtp();
    LocalDateTime expiryTime = now.plusMinutes(OTP_EXPIRY_MINUTES);
    OtpData otpData = new OtpData(otpCode, email, expiryTime);
    otpData.setLastSentTime(now);

    // cache：以 (email, purpose) 維度
    otpCacheService.updateOtpData(email, purpose, otpData);

    // auth_info（UTC）
    OffsetDateTime expiryAtUtc = expiryTime.atOffset(ZoneOffset.UTC);
    // 取出 Optional<Member>
    var optMember = memberRepository.findByEmail(email);
    AuthInfo row =
        AuthInfo.builder()
            .code(otpCode) // 建議未來改為 hash
            .email(email)
            .member(optMember.orElse(null))       // << 這行
            .action(purpose.actionCode())
            .validation(Boolean.FALSE)
            .expireAt(expiryAtUtc)
            .build();
    authInfoRepository.save(row);

    // 寄信（依 purpose 切模板）
    emailService.sendOtp(email, otpCode, purpose);

    log.info(
        "為用戶 {} 產生 OTP（purpose={}，expire={}）: {}", email, purpose.name(), expiryAtUtc, otpCode);
    return otpData;
  }

  /** 驗證 OTP：檢查 cache + 失敗增計數 + 成功核銷 cache 與 auth_info.validation=true */
  @Transactional
  public boolean verifyOtp(String email, String inputOtp, OtpPurpose purpose) {
    OtpData otpData = otpCacheService.getOtpData(email, purpose);
    if (otpData == null) {
      log.warn("找不到用戶 {} 的 OTP 資料（purpose={}）", email, purpose.name());
      return false;
    }
    if (otpData.isExpired()) {
      log.warn("用戶 {} 的 OTP 已過期（purpose={}）", email, purpose.name());
      otpCacheService.evictOtp(email, purpose);
      return false;
    }
    if (otpData.getAttemptCount() >= MAX_ATTEMPTS) {
      log.warn("用戶 {} 的 OTP 嘗試次數已超過限制（purpose={}）", email, purpose.name());
      otpCacheService.evictOtp(email, purpose);
      return false;
    }

    otpData.incrementAttemptCount();

    if (!otpData.getOtpCode().equals(inputOtp)) {
      log.warn(
          "用戶 {} OTP 驗證失敗（purpose={}），嘗試次數: {}", email, purpose.name(), otpData.getAttemptCount());
      otpCacheService.updateOtpData(email, purpose, otpData);
      return false;
    }

    // 成功：核銷 cache
    otpData.setVerified(true);
    otpCacheService.evictOtp(email, purpose);

    // 標記 auth_info.validation = true
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    Optional<AuthInfo> last =
        authInfoRepository.findFirstByEmailAndCodeAndActionAndExpireAtAfterOrderByCreatedAtDesc(
            email, inputOtp, purpose.actionCode(), nowUtc);
    last.ifPresent(ai -> authInfoRepository.markValidated(ai.getId()));

    log.info("用戶 {} OTP 驗證成功（purpose={}），auth_info 已核銷", email, purpose.name());
    return true;
  }

  // ------- 相容用 DTO APIs（若仍有地方直接呼叫 OtpController） -------

  public OtpSendResponse sendOtp(OtpRequest request) {
    String email = request.getEmail();
    OtpPurpose purpose = request.getPurpose() != null ? request.getPurpose() : OtpPurpose.LOGIN;
    generateOtp(email, purpose);
    return new OtpSendResponse(email);
  }

  public OtpVerifyResponse verifyOtpResponse(OtpVerificationRequest request) {
    String email = request.getEmail();
    String inputOtp = request.getOtpCode();
    OtpPurpose purpose = request.getPurpose() != null ? request.getPurpose() : OtpPurpose.LOGIN;
    boolean isValid = verifyOtp(email, inputOtp, purpose);
    String token = null;
    if (isValid) {
      token = UUID.randomUUID().toString();
      // 若舊流程還需要 token，沿用 cache；新流程（authFlow）不再用 token
      otpCacheService.putOtpVerifiedToken(token, email, purpose);
    }
    return new OtpVerifyResponse(isValid, token);
  }

  public OtpStatusResponse getOtpStatusResponse(String email, OtpPurpose purpose) {
    if (purpose == null) purpose = OtpPurpose.LOGIN;
    OtpData otpData = otpCacheService.getOtpData(email, purpose);
    if (otpData == null) {
      return new OtpStatusResponse(false, null, null, null, null);
    } else {
      return new OtpStatusResponse(
          true,
          otpData.isExpired(),
          otpData.getAttemptCount(),
          otpData.isVerified(),
          otpData.getExpiryTime());
    }
  }

  // ------- Helpers -------

  private String generateRandomOtp() {
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < OTP_LENGTH; i++) {
      otp.append(random.nextInt(10));
    }
    return otp.toString();
  }

  /**
 * dev/test 專用：產生 OTP 但不發送 Email。
 * 仍會更新 cache 與寫入 auth_info(validation=false)，便於稽核與後續驗證。
 */
@org.springframework.transaction.annotation.Transactional
public OtpData generateOtpWithoutMail(OtpRequest request) {
  String email = request.getEmail();
  OtpPurpose purpose = request.getPurpose() != null ? request.getPurpose() : OtpPurpose.LOGIN;

  OtpData existingOtp = otpCacheService.getOtpData(email, purpose);
  LocalDateTime now = LocalDateTime.now();
  if (existingOtp != null
      && existingOtp.getLastSentTime() != null
      && existingOtp.getLastSentTime().plusSeconds(otpResendIntervalSeconds).isAfter(now)) {
    throw new InvalidOtpException.ResendTooFrequently(otpResendIntervalSeconds);
  }

  // 產生並寫入快取
  String otpCode = generateRandomOtp();
  LocalDateTime expiryTime = now.plusMinutes(OTP_EXPIRY_MINUTES);
  OtpData otpData = new OtpData(otpCode, email, expiryTime);
  otpData.setLastSentTime(now);
  otpCacheService.updateOtpData(email, purpose, otpData);

  // 寫入 auth_info（UTC）
  OffsetDateTime expiryAtUtc = expiryTime.atOffset(ZoneOffset.UTC);
  UUID memberId = memberRepository.findByEmail(email).map(Member::getId).orElse(null);
  AuthInfo row = AuthInfo.builder()
      .code(otpCode) // 之後可改成 hash 存放
      .email(email)
      .memberId(memberId)
      .action(purpose.actionCode())   // "001"/"002"/"003"...
      .validation(Boolean.FALSE)
      .expireAt(expiryAtUtc)
      .build();
  authInfoRepository.save(row);

  log.info("[DEV] 為用戶 {} 產生 OTP(不寄信)（purpose={}）: {}", email, purpose.name(), otpCode);
  return otpData;
}
}
