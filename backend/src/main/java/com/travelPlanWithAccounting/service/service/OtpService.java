package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.otp.OtpSendResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpStatusResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpVerifyResponse;
import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private static final int OTP_EXPIRY_MINUTES = 5;
  private static final int MAX_ATTEMPTS = 3;

  private final OtpCacheService otpCacheService;
  private final EmailService emailService;
  private final Random random = new Random();

  /**
   * 產生新的 OTP 驗證碼並快取。<br>
   * Generates a new OTP code and caches it.
   *
   * @param email 用戶電子郵件 (User email)
   * @return 產生的 OTP 資料物件 (Generated OtpData)
   */
  public OtpData generateOtp(String email) {
    String otpCode = generateRandomOtp();
    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
    OtpData otpData = new OtpData(otpCode, email, expiryTime);
    log.info("為用戶 {} 產生OTP: {}", email, otpCode);
    sendOtpNotification(email, otpCode);
    otpCacheService.updateOtpData(email, otpData);
    return otpData;
  }

  /**
   * 驗證 OTP 驗證碼，並處理過期、嘗試次數等安全邏輯。<br>
   * Verifies the OTP code, handling expiration and attempt limits.
   *
   * @param email 用戶電子郵件 (User email)
   * @param inputOtp 使用者輸入的 OTP 驗證碼 (User input OTP code)
   * @return 驗證是否通過 (true: 驗證成功, false: 驗證失敗)
   */
  public boolean verifyOtp(String email, String inputOtp) {
    OtpData otpData = otpCacheService.getOtpData(email);
    if (otpData == null) {
      log.warn("找不到用戶 {} 的OTP資料", email);
      return false;
    }
    if (otpData.isExpired()) {
      log.warn("用戶 {} 的OTP已過期", email);
      otpCacheService.evictOtp(email);
      return false;
    }
    if (otpData.getAttemptCount() >= MAX_ATTEMPTS) {
      log.warn("用戶 {} 的OTP嘗試次數已超過限制", email);
      otpCacheService.evictOtp(email);
      return false;
    }
    otpData.incrementAttemptCount();
    if (otpData.getOtpCode().equals(inputOtp)) {
      otpData.setVerified(true);
      log.info("用戶 {} OTP驗證成功", email);
      otpCacheService.evictOtp(email);
      return true;
    }
    log.warn("用戶 {} OTP驗證失敗，嘗試次數: {}", email, otpData.getAttemptCount());
    otpCacheService.updateOtpData(email, otpData);
    return false;
  }

  /**
   * 查詢指定 email 的 OTP 資料。<br>
   * Gets the OTP data for the given email.
   *
   * @param email 用戶電子郵件 (User email)
   * @return OTP 資料物件，若無則為 null (OtpData or null if not found)
   */
  public OtpData getOtpData(String email) {
    return otpCacheService.getOtpData(email);
  }

  /**
   * 產生 OTP 並回傳發送結果 DTO。<br>
   * Generates OTP and returns send result DTO.
   *
   * @param email 用戶電子郵件 (User email)
   * @return 發送結果 DTO (OtpSendResponse)
   */
  public OtpSendResponse sendOtp(String email) {
    generateOtp(email);
    return new OtpSendResponse(email);
  }

  /**
   * 驗證 OTP 並回傳驗證結果 DTO。<br>
   * Verifies OTP and returns verify result DTO.
   *
   * @param email 用戶電子郵件 (User email)
   * @param inputOtp 使用者輸入的 OTP 驗證碼 (User input OTP code)
   * @return 驗證結果 DTO (OtpVerifyResponse)
   */
  public OtpVerifyResponse verifyOtpResponse(String email, String inputOtp) {
    boolean isValid = verifyOtp(email, inputOtp);
    return new OtpVerifyResponse(isValid);
  }

  /**
   * 查詢 OTP 狀態並回傳狀態 DTO。<br>
   * Gets OTP status and returns status DTO.
   *
   * @param email 用戶電子郵件 (User email)
   * @return 狀態 DTO (OtpStatusResponse)
   */
  public OtpStatusResponse getOtpStatusResponse(String email) {
    OtpData otpData = getOtpData(email);
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

  private String generateRandomOtp() {
    StringBuilder otp = new StringBuilder();
    for (int i = 0; i < OTP_LENGTH; i++) {
      otp.append(random.nextInt(10));
    }
    return otp.toString();
  }

  private void sendOtpNotification(String email, String otpCode) {
    // 實際應用中整合郵件服務
    emailService.sendOtp(email, otpCode, OtpPurpose.LOGIN);
  }
}
