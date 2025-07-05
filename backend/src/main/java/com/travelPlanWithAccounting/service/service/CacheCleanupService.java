package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.constant.CacheConstants;
import com.travelPlanWithAccounting.service.model.OtpInfo;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 快取清理服務，負責定期清理過期的快取項目。 Cache cleanup service responsible for periodically cleaning up expired
 * cache entries.
 *
 * <p>該服務主要用於清理： This service mainly cleans up:
 *
 * <ul>
 *   <li>OTP (一次性密碼) 快取 / OTP (One-Time Password) cache
 *   <li>Magic Link 快取 / Magic Link cache
 *   <li>一次性令牌快取 / One-time token cache
 *   <li>重新整理令牌快取 / Refresh token cache
 * </ul>
 *
 * <p>清理策略： Cleanup strategy:
 *
 * <ul>
 *   <li>每分鐘執行一次清理 / Runs cleanup every minute
 *   <li>只清理過期項目，保留有效項目 / Only removes expired entries, keeps valid ones
 *   <li>使用日誌記錄清理操作 / Logs cleanup operations
 * </ul>
 */
@Slf4j
@Component
@EnableScheduling
@Service
@RequiredArgsConstructor
public class CacheCleanupService {

  /** Spring 快取管理器實例。 Spring Cache Manager instance. */
  private final CacheManager cacheManager;

  /**
   * 定期執行的快取清理任務。 Periodically executed cache cleanup task.
   *
   * <p>該方法每52分鐘執行一次，清理所有類型的過期快取項目。 This method runs every 5 minute to clean up all types of expired
   * cache entries.
   */
  @Scheduled(fixedRate = 300000) // 每5分鐘執行一次
  public void cleanupExpiredEntries() {
    cleanupOtpCache();
    // TODO: 實現其他快取的清理邏輯
    // cleanupMagicLinkCache();
    // cleanupOneTimeTokenCache();
    // cleanupRefreshTokenCache();
  }

  /**
   * 清理 OTP 快取中的過期項目。 Cleans up expired entries in the OTP cache.
   *
   * <p>該方法會： This method will:
   *
   * <ul>
   *   <li>檢查每個 OTP 項目是否過期 / Check if each OTP entry has expired
   *   <li>移除過期的項目 / Remove expired entries
   *   <li>記錄清理操作 / Log cleanup operations
   * </ul>
   */
  private void cleanupOtpCache() {
    Cache cache = cacheManager.getCache(CacheConstants.OTP_CACHE);
    if (cache instanceof ConcurrentMapCache mapCache) {
      ConcurrentMap<Object, Object> store = mapCache.getNativeCache();
      LocalDateTime now = LocalDateTime.now();

      store
          .entrySet()
          .removeIf(
              entry -> {
                if (entry.getValue() instanceof Cache.ValueWrapper wrapper) {
                  Object value = wrapper.get();
                  if (value instanceof OtpInfo otpInfo) {
                    boolean isExpired = otpInfo.getExpiresAt().isBefore(now);
                    if (isExpired) {
                      log.debug("Removing expired OTP for key: {}", entry.getKey());
                    }
                    return isExpired;
                  }
                }
                return false;
              });
    }
  }
}
