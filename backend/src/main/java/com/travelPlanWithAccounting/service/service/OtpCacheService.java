package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.constant.CacheConstants;
import com.travelPlanWithAccounting.service.model.OtpData;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OtpCacheService {
  @Cacheable(value = CacheConstants.OTP_CACHE, key = "#email")
  public OtpData getOtpData(String email) {
    return null;
  }

  @CachePut(value = CacheConstants.OTP_CACHE, key = "#email")
  public OtpData updateOtpData(String email, OtpData otpData) {
    return otpData;
  }

  @CacheEvict(value = CacheConstants.OTP_CACHE, key = "#email")
  public void evictOtp(String email) {
    // 僅負責快取移除
  }

  @CachePut(value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE, key = "#token")
  public String putOtpVerifiedToken(String token, String email) {
    return email;
  }

  @Cacheable(value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE, key = "#token")
  public String getOtpVerifiedEmailByToken(String token) {
    return null;
  }

  @CacheEvict(value = CacheConstants.OTP_VERIFIED_TOKEN_CACHE, key = "#token")
  public void evictOtpVerifiedToken(String token) {
    // 僅負責快取移除
  }
}
