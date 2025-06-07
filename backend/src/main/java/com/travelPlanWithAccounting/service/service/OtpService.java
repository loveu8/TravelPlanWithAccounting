package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.constant.CacheConstants;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.model.OtpInfo;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OtpService {

  private static final String CACHE_NAME = CacheConstants.OTP_CACHE;
  private final EmailService emailService;

  @CachePut(value = CACHE_NAME, key = "#email")
  public OtpInfo createOtp(String email, OtpPurpose purpose) {
    String code = generateRandomCode();
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10); // 10分鐘過期

    OtpInfo otpInfo = new OtpInfo(code, purpose, expiresAt, 0);

    // 發送郵件
    emailService.sendOtp(email, code, purpose);

    return otpInfo;
  }

  @Cacheable(value = CACHE_NAME, key = "#email")
  public OtpInfo getOtp(String email) {
    // 從快取中獲取，找不到返回 null
    return null;
  }

  @CacheEvict(value = CACHE_NAME, key = "#email")
  public void invalidateOtp(String email) {
    // 清除快取，方法內不需要任何代碼
  }

  @Transactional
  public boolean validateOtp(String email, String code) {
    OtpInfo otpInfo = getOtp(email);

    if (otpInfo == null) {
      throw new InvalidOtpException.NotFound();
    }

    if (otpInfo.getExpiresAt().isBefore(LocalDateTime.now())) {
      invalidateOtp(email);
      throw new InvalidOtpException.Expired();
    }

    if (otpInfo.getAttempts() >= 3) {
      invalidateOtp(email);
      throw new InvalidOtpException.MaxAttemptsExceeded();
    }

    // 增加嘗試次數
    otpInfo.setAttempts(otpInfo.getAttempts() + 1);

    // 更新快取
    createOtp(email, otpInfo.getPurpose());

    // 驗證 OTP
    boolean isValid = otpInfo.getCode().equals(code);

    if (isValid) {
      // 驗證成功後清除 OTP
      invalidateOtp(email);
    }

    return isValid;
  }

  private String generateRandomCode() {
    // 生成6位數的隨機驗證碼
    SecureRandom random = new SecureRandom();
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
  }
}
