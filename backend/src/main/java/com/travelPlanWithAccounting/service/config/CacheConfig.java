package com.travelPlanWithAccounting.service.config;

import com.travelPlanWithAccounting.service.constant.CacheConstants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    // 使用記憶體內快取，並指定快取名稱
    return new ConcurrentMapCacheManager(
        CacheConstants.OTP_CACHE,
        CacheConstants.MAGIC_LINK_CACHE,
        CacheConstants.ONE_TIME_TOKEN_CACHE,
        CacheConstants.REFRESH_TOKEN_CACHE,
        CacheConstants.USER_TOKENS_CACHE,
        CacheConstants.OTP_VERIFIED_TOKEN_CACHE,
        CacheConstants.SETTING_CACHE);
  }
}
