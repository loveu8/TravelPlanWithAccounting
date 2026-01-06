package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.constant.CacheConstants;
import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OtpCacheService {

  /** 以 (token, purpose) 作為快取鍵，避免註冊與登入互相覆蓋 */
  @Cacheable(
      value = CacheConstants.OTP_CACHE,
      key = "T(String).valueOf(#token).concat(':').concat(#purpose.name())")
  public OtpData getOtpData(String token, OtpPurpose purpose) {
    return null; // 交由 Cache Manager 回填
  }

  @CachePut(
      value = CacheConstants.OTP_CACHE,
      key = "T(String).valueOf(#token).concat(':').concat(#purpose.name())")
  public OtpData updateOtpData(String token, OtpPurpose purpose, OtpData otpData) {
    return otpData;
  }

  @CacheEvict(
      value = CacheConstants.OTP_CACHE,
      key = "T(String).valueOf(#token).concat(':').concat(#purpose.name())")
  public void evictOtp(String token, OtpPurpose purpose) {
    // 僅負責快取移除
  }

  /** Verified Token 也加上 purpose 維度，避免拿登入 token 去註冊 */
  @CachePut(
      value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE,
      key = "T(String).valueOf(#token).concat(':').concat(#purpose.name())")
  public String putOtpVerifiedToken(String token, String email, OtpPurpose purpose) {
    return email;
  }

  @Cacheable(
      value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE,
      key = "T(String).valueOf(#token).concat(':').concat(#purpose.name())")
  public String getOtpVerifiedEmailByToken(String token, OtpPurpose purpose) {
    return null;
  }

  @CacheEvict(
      value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE,
      key = "T(String).valueOf(#token).concat(':').concat(#purpose.name())")
  public void evictOtpVerifiedToken(String token, OtpPurpose purpose) {
    // 僅負責快取移除
  }

  // ----------------- 舊版方法（保留過渡，預設 purpose=LOGIN） -----------------

  /**
   * @deprecated 請改用帶 purpose 的版本
   */
  @Deprecated
  @Cacheable(value = CacheConstants.OTP_CACHE, key = "#token + ':LOGIN'")
  public OtpData getOtpData(String token) {
    return null;
  }

  /**
   * @deprecated 請改用帶 purpose 的版本
   */
  @Deprecated
  @CachePut(value = CacheConstants.OTP_CACHE, key = "#token + ':LOGIN'")
  public OtpData updateOtpData(String token, OtpData otpData) {
    return otpData;
  }

  /**
   * @deprecated 請改用帶 purpose 的版本
   */
  @Deprecated
  @CacheEvict(value = CacheConstants.OTP_CACHE, key = "#token + ':LOGIN'")
  public void evictOtp(String token) {}

  /**
   * @deprecated 請改用帶 purpose 的版本
   */
  @Deprecated
  @CachePut(value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE, key = "#token + ':LOGIN'")
  public String putOtpVerifiedToken(String token, String email) {
    return email;
  }

  /**
   * @deprecated 請改用帶 purpose 的版本
   */
  @Deprecated
  @Cacheable(value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE, key = "#token + ':LOGIN'")
  public String getOtpVerifiedEmailByToken(String token) {
    return null;
  }

  /**
   * @deprecated 請改用帶 purpose 的版本
   */
  @Deprecated
  @CacheEvict(value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE, key = "#token + ':LOGIN'")
  public void evictOtpVerifiedToken(String token) {}
}
