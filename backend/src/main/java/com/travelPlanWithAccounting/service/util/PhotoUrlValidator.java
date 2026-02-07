package com.travelPlanWithAccounting.service.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhotoUrlValidator {

  private static final int HEAD_FALLBACK_STATUS_METHOD_NOT_ALLOWED = 405;
  private static final int HEAD_FALLBACK_STATUS_NOT_IMPLEMENTED = 501;
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
  private static final Duration VALID_CACHE_TTL = Duration.ofMinutes(2);
  private static final Duration INVALID_CACHE_TTL = Duration.ofSeconds(30);
  private static final Set<String> ALLOWED_HOSTS =
      Set.of("places.googleapis.com", "lh3.googleusercontent.com", "maps.googleapis.com");

  private final HttpClient httpClient;
  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

  public PhotoUrlValidator(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public boolean isValid(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }
    URI uri = safeCreateUri(url);
    if (uri == null || !isAllowedUri(uri)) {
      return false;
    }
    CacheEntry cached = cache.get(url);
    if (cached != null && !cached.isExpired()) {
      return cached.valid();
    }

    boolean valid = validateByHttp(uri);
    cache.put(
        url, new CacheEntry(valid, Instant.now().plus(valid ? VALID_CACHE_TTL : INVALID_CACHE_TTL)));
    return valid;
  }

  private boolean validateByHttp(URI uri) {
    try {
      HttpRequest headRequest =
          HttpRequest.newBuilder()
              .uri(uri)
              .timeout(REQUEST_TIMEOUT)
              .method("HEAD", HttpRequest.BodyPublishers.noBody())
              .build();
      int headStatus =
          httpClient.send(headRequest, HttpResponse.BodyHandlers.discarding()).statusCode();
      if (isSuccessStatus(headStatus)) {
        return true;
      }
      if (headStatus == HEAD_FALLBACK_STATUS_METHOD_NOT_ALLOWED
          || headStatus == HEAD_FALLBACK_STATUS_NOT_IMPLEMENTED) {
        HttpRequest getRequest =
            HttpRequest.newBuilder()
                .uri(uri)
                .timeout(REQUEST_TIMEOUT)
                .header("Range", "bytes=0-0")
                .GET()
                .build();
        int getStatus =
            httpClient.send(getRequest, HttpResponse.BodyHandlers.discarding()).statusCode();
        return isSuccessStatus(getStatus);
      }
      return false;
    } catch (Exception ex) {
      log.warn("Failed to validate photo URL: {}", sanitizeUrlForLog(uri.toString()), ex);
      return false;
    }
  }

  private URI safeCreateUri(String url) {
    try {
      return URI.create(url);
    } catch (RuntimeException ex) {
      log.warn("Invalid photo URL format: {}", sanitizeUrlForLog(url));
      return null;
    }
  }

  private boolean isAllowedUri(URI uri) {
    String scheme = uri.getScheme();
    String host = uri.getHost();
    if (scheme == null || host == null) {
      return false;
    }
    if (!"https".equalsIgnoreCase(scheme)) {
      return false;
    }
    return ALLOWED_HOSTS.contains(host.toLowerCase(Locale.ROOT));
  }

  private boolean isSuccessStatus(int statusCode) {
    return statusCode >= 200 && statusCode < 300;
  }

  private String sanitizeUrlForLog(String url) {
    if (url == null || url.isBlank()) {
      return "<empty>";
    }
    int qIdx = url.indexOf('?');
    if (qIdx < 0) {
      return url;
    }
    String base = url.substring(0, qIdx);
    String query = url.substring(qIdx + 1);
    query = query.replaceAll("(?i)(key=)[^&]+", "$1***");
    return base + "?" + query;
  }

  private record CacheEntry(boolean valid, Instant expireAt) {
    boolean isExpired() {
      return Instant.now().isAfter(expireAt);
    }
  }
}
