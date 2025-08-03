package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.otp.OtpSendResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpStatusResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpVerifyResponse;
import com.travelPlanWithAccounting.service.entity.AuthInfo;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.model.OtpVerificationRequest;
import com.travelPlanWithAccounting.service.repository.AuthInfoRepository;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OTP 相關核心服務。
 *
 * <p>提供 OTP 產生、驗證及查詢狀態等功能，並以 {@link InvalidOtpException} 進行錯誤處理， 與其他服務保持一致的例外風格。
 */
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

  /**
   * 發送 OTP：更新 cache、寫入 auth_info 並寄出 Email。
   *
   * @param email 目標 Email
   * @param purpose 目的
   * @return 產生的 {@link OtpData}
   */
  @Transactional
  public OtpData generateOtp(String email, OtpPurpose purpose) {
    validateEmail(email);
    if (purpose == null) throw new InvalidOtpException();
    OtpData otpData = createOtp(email, purpose);
    emailService.sendOtp(email, otpData.getOtpCode(), purpose);
    log.info(
        "為用戶 {} 產生 OTP（purpose={}，expire={}）: {}",
        email,
        purpose.name(),
        otpData.getExpiryTime().atOffset(ZoneOffset.UTC),
        otpData.getOtpCode());
    return otpData;
  }

  /** 驗證 OTP：檢查 cache、處理嘗試次數與過期，成功時核銷 cache 與 auth_info。 */
  @Transactional
  public AuthInfo verifyOtp(String token, String inputOtp, OtpPurpose purpose) {
    if (token == null || token.isBlank() || inputOtp == null || inputOtp.isBlank() || purpose == null) {
      throw new InvalidOtpException();
    }
    UUID id;
    try {
      id = UUID.fromString(token);
    } catch (IllegalArgumentException e) {
      throw new InvalidOtpException();
    }

    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    AuthInfo authInfo =
        authInfoRepository
            .findByIdAndCodeAndActionAndExpireAtAfter(id, inputOtp, purpose.actionCode(), nowUtc)
            .orElseThrow(InvalidOtpException.NotFound::new);

    String email = authInfo.getEmail();
    OtpData otpData = otpCacheService.getOtpData(email, purpose);
    if (otpData == null) {
      log.warn("找不到用戶 {} 的 OTP 資料（purpose={}）", email, purpose.name());
      throw new InvalidOtpException.NotFound();
    }
    if (otpData.isExpired()) {
      log.warn("用戶 {} 的 OTP 已過期（purpose={}）", email, purpose.name());
      otpCacheService.evictOtp(email, purpose);
      throw new InvalidOtpException.Expired();
    }
    if (otpData.getAttemptCount() >= MAX_ATTEMPTS) {
      log.warn("用戶 {} 的 OTP 嘗試次數已超過限制（purpose={}）", email, purpose.name());
      otpCacheService.evictOtp(email, purpose);
      throw new InvalidOtpException.MaxAttemptsExceeded();
    }

    otpData.incrementAttemptCount();

    if (!otpData.getOtpCode().equals(inputOtp)) {
      log.warn(
          "用戶 {} OTP 驗證失敗（purpose={}），嘗試次數: {}", email, purpose.name(), otpData.getAttemptCount());
      otpCacheService.updateOtpData(email, purpose, otpData);
      throw new InvalidOtpException();
    }

    // 成功：核銷 cache
    otpData.setVerified(true);
    otpCacheService.evictOtp(email, purpose);

    authInfoRepository.markValidated(authInfo.getId());

    log.info("用戶 {} OTP 驗證成功（purpose={}），auth_info 已核銷", email, purpose.name());
    return authInfo;
  }

  // ------- 相容用 DTO APIs（若仍有地方直接呼叫 OtpController） -------

  public OtpSendResponse sendOtp(OtpRequest request) {
    validateOtpRequest(request);
    String email = request.getEmail();
    OtpPurpose purpose = request.getPurpose();
    OtpData otpData = generateOtp(email, purpose);
    return new OtpSendResponse(email, otpData.getToken().toString());
  }

  public OtpVerifyResponse verifyOtpResponse(OtpVerificationRequest request) {
    validateOtpVerificationRequest(request);
    String token = request.getToken();
    String inputOtp = request.getOtpCode();
    OtpPurpose purpose = request.getPurpose();
    try {
      verifyOtp(token, inputOtp, purpose);
      return new OtpVerifyResponse(true, token);
    } catch (InvalidOtpException ex) {
      return new OtpVerifyResponse(false, null);
    }
  }

  public OtpStatusResponse getOtpStatusResponse(String email, OtpPurpose purpose) {
    validateEmail(email);
    if (purpose == null) purpose = OtpPurpose.LOGIN;
    OtpData otpData = otpCacheService.getOtpData(email, purpose);
    if (otpData == null) {
      return new OtpStatusResponse(false, null, null, null, null);
    }
    return new OtpStatusResponse(
        true,
        otpData.isExpired(),
        otpData.getAttemptCount(),
        otpData.isVerified(),
        otpData.getExpiryTime());
  }

  // ------- Helpers -------

  private OtpData createOtp(String email, OtpPurpose purpose) {
    OtpData existingOtp = otpCacheService.getOtpData(email, purpose);
    LocalDateTime now = LocalDateTime.now();
    if (existingOtp != null
        && existingOtp.getLastSentTime() != null
        && existingOtp.getLastSentTime().plusSeconds(otpResendIntervalSeconds).isAfter(now)) {
      throw new InvalidOtpException.ResendTooFrequently(otpResendIntervalSeconds);
    }

    // 產生 OTP 並先寫入 auth_info 取得 token
    String otpCode = generateRandomOtp();
    LocalDateTime expiryTime = now.plusMinutes(OTP_EXPIRY_MINUTES);
    OffsetDateTime expiryAtUtc = expiryTime.atOffset(ZoneOffset.UTC);
    Member member = memberRepository.findByEmail(email).orElse(null);
    AuthInfo row =
        AuthInfo.builder()
            .code(otpCode)
            .email(email)
            .member(member)
            .action(purpose.actionCode())
            .validation(Boolean.FALSE)
            .expireAt(expiryAtUtc)
            .build();
    authInfoRepository.save(row);

    // 更新快取
    OtpData otpData = new OtpData(otpCode, email, expiryTime, row.getId());
    otpData.setLastSentTime(now);
    otpCacheService.updateOtpData(email, purpose, otpData);

    return otpData;
  }

  private String generateRandomOtp() {
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < OTP_LENGTH; i++) {
      otp.append(random.nextInt(10));
    }
    return otp.toString();
  }

  /** dev/test 專用：產生 OTP 但不發送 Email。 仍會更新 cache 與寫入 auth_info(validation=false)，便於稽核與後續驗證。 */
  @Transactional
  public OtpData generateOtpWithoutMail(OtpRequest request) {
    validateOtpRequest(request);
    String email = request.getEmail();
    OtpPurpose purpose = request.getPurpose();
    OtpData otpData = createOtp(email, purpose);
    log.info(
        "[DEV] 為用戶 {} 產生 OTP(不寄信)（purpose={}）: {}", email, purpose.name(), otpData.getOtpCode());
    return otpData;
  }

  // ------- Validation helpers -------

  private void validateOtpRequest(OtpRequest request) {
    if (request == null) throw new MemberException.EmailRequired();
    validateEmail(request.getEmail());
    if (request.getPurpose() == null) {
      throw new InvalidOtpException();
    }
  }

  private void validateOtpVerificationRequest(OtpVerificationRequest request) {
    if (request == null) throw new MemberException.EmailRequired();
    if (request.getToken() == null || request.getToken().isBlank()) {
      throw new InvalidOtpException();
    }
    if (request.getOtpCode() == null || request.getOtpCode().isBlank()) {
      throw new InvalidOtpException();
    }
    if (request.getPurpose() == null) {
      throw new InvalidOtpException();
    }
  }

  private void validateEmail(String email) {
    if (email == null || email.isEmpty()) throw new MemberException.EmailRequired();
    if (!EmailValidatorUtil.isValid(email)) throw new MemberException.EmailFormatInvalid();
  }
}
