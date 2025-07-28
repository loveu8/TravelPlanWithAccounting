package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.otp.OtpSendResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpStatusResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpVerifyResponse;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.model.OtpVerificationRequest;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@code OtpService} 提供 OTP 驗證碼產生、驗證、狀態查詢等業務邏輯，並委派快取操作給 {@link OtpCacheService}。<br>
 * {@code OtpService} provides business logic for OTP code generation, verification, and status
 * query, and delegates cache operations to {@link OtpCacheService}.
 *
 * <p>- 產生 OTP 驗證碼並快取於指定快取區。<br>
 * - 驗證 OTP 並處理過期、嘗試次數等安全邏輯。<br>
 * - 查詢 OTP 狀態，回傳驗證狀態、剩餘時間等資訊。<br>
 * - 所有快取相關操作皆由 {@link OtpCacheService} 處理，避免循環依賴。<br>
 *
 * <p>- Generates OTP codes and caches them in the specified cache region.<br>
 * - Verifies OTP codes, handling expiration and attempt limits for security.<br>
 * - Queries OTP status, returning verification state and remaining time.<br>
 * - All cache operations are handled by {@link OtpCacheService} to avoid circular dependencies.<br>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {
  private static final int OTP_LENGTH = 6;
  private static final int OTP_EXPIRY_MINUTES = 10; // OTP 有效時間 (10 分鐘)
  private static final int MAX_ATTEMPTS = 3;

  private final OtpCacheService otpCacheService;
  private final EmailService emailService;
  private final Random random = new Random();

  @Value("${otp.resend-interval-seconds:30}")
  private int otpResendIntervalSeconds;

  // -------------------- Public APIs --------------------

  /** 產生 OTP 並回傳發送結果 DTO。 */
  public OtpSendResponse sendOtp(OtpRequest request) {
    String email = request.getEmail();
    OtpPurpose purpose = request.getPurpose();
    if (purpose == null) {
      // 保底（不建議），避免 NPE；建議前端必帶 purpose
      purpose = OtpPurpose.LOGIN;
    }

    generateOtp(email, purpose);
    return new OtpSendResponse(email);
  }

  /** 驗證 OTP 並回傳驗證結果 DTO。 */
  public OtpVerifyResponse verifyOtpResponse(OtpVerificationRequest request) {
    String email = request.getEmail();
    String inputOtp = request.getOtpCode();
    OtpPurpose purpose = request.getPurpose();
    if (purpose == null) {
      purpose = OtpPurpose.LOGIN;
    }

    boolean isValid = verifyOtp(email, inputOtp, purpose);
    String token = null;
    if (isValid) {
      token = UUID.randomUUID().toString();
      // Verified token 維度也加 purpose
      otpCacheService.putOtpVerifiedToken(token, email, purpose);
    }
    return new OtpVerifyResponse(isValid, token);
  }

  /** 查詢 OTP 狀態並回傳狀態 DTO（建議加 purpose 版本）。 */
  public OtpStatusResponse getOtpStatusResponse(String email, OtpPurpose purpose) {
    if (purpose == null) {
      purpose = OtpPurpose.LOGIN;
    }
    OtpData otpData = getOtpData(email, purpose);
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

  // -------------------- Core Methods (purpose-aware) --------------------

  /** 產生新的 OTP 驗證碼並快取（以 (email, purpose) 為維度）。 */
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

    log.info("為用戶 {} 產生 OTP（purpose={}）: {}", email, purpose.name(), otpCode);

    // 依用途切換郵件/模板
    sendOtpNotification(email, otpCode, purpose);

    // 更新快取（以 (email, purpose) 維度）
    otpCacheService.updateOtpData(email, purpose, otpData);

    // TODO（之後實作）：寫入 auth_info 審計（code/email/memberId?/action=purpose.actionCode(),
    // validation=false, expire_at）
    return otpData;
  }

  /** 驗證 OTP（以 (email, purpose) 為維度），並處理過期、嘗試次數等安全邏輯。 */
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

    if (otpData.getOtpCode().equals(inputOtp)) {
      otpData.setVerified(true);
      log.info("用戶 {} OTP 驗證成功（purpose={}）", email, purpose.name());

      // 驗證成功即移除 OTP（避免重放）
      otpCacheService.evictOtp(email, purpose);

      // TODO（之後實作）：將對應 auth_info 記錄 validation 設為 true（以 email+action+code & 未過期）
      return true;
    }

    log.warn(
        "用戶 {} OTP 驗證失敗（purpose={}），嘗試次數: {}", email, purpose.name(), otpData.getAttemptCount());
    otpCacheService.updateOtpData(email, purpose, otpData);
    return false;
  }

  /** 以 (email, purpose) 取得 OTP 資料。 */
  public OtpData getOtpData(String email, OtpPurpose purpose) {
    return otpCacheService.getOtpData(email, purpose);
  }

  /** 測試用：產生 OTP 但不發送 email。 */
  public OtpData generateOtpWithoutMail(OtpRequest request) {
    String email = request.getEmail();
    OtpPurpose purpose = request.getPurpose();
    if (purpose == null) {
      purpose = OtpPurpose.LOGIN;
    }

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

    log.info("[TEST] 為用戶 {} 產生 OTP(不發送 mail)（purpose={}）: {}", email, purpose.name(), otpCode);
    otpCacheService.updateOtpData(email, purpose, otpData);
    return otpData;
  }

  // -------------------- Helpers --------------------

  private String generateRandomOtp() {
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < OTP_LENGTH; i++) {
      otp.append(random.nextInt(10));
    }
    return otp.toString();
  }

  /** 依不同 purpose 寄送對應模板（之後可接入 i18n） */
  private void sendOtpNotification(String email, String otpCode, OtpPurpose purpose) {
    emailService.sendOtp(email, otpCode, purpose);
  }
}
