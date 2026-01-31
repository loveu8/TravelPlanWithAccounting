package com.travelPlanWithAccounting.service.util;

import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import org.springframework.stereotype.Component;

@Component
public class GooglePhotoUrlBuilder {

  private static final String PHOTO_RESOURCE_PREFIX = "places/";
  private static final String PHOTO_RESOURCE_MARKER = "/photos/";
  private static final String MEDIA_SEGMENT = "/media";

  private final GoogleApiConfig googleApiConfig;

  public GooglePhotoUrlBuilder(GoogleApiConfig googleApiConfig) {
    this.googleApiConfig = googleApiConfig;
  }

  /**
   * 建構 Place Photos (New) 的 media URL。
   * 官方格式：https://places.googleapis.com/v1/NAME/media?key=API_KEY&maxWidthPx=...&maxHeightPx=...
   * NAME 須為 photos[].name（格式 places/PLACE_ID/photos/PHOTO_REFERENCE），且會過期，勿快取。
   */
  public String buildFromName(String photoName, int maxWidthPx) {
    if (photoName == null || photoName.isBlank()) {
      return null;
    }
    String name = normalizePhotoResourceName(photoName);
    if (name == null) {
      return null;
    }
    return "https://places.googleapis.com/v1/"
        + name
        + "/media?key="
        + googleApiConfig.getGoogleApiKey()
        + "&maxWidthPx="
        + maxWidthPx
        + "&maxHeightPx="
        + maxWidthPx;
  }

  public String resolvePhotoUrl(String value, int maxWidthPx) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String photoName = extractPhotoName(value);
    if (photoName != null) {
      return buildFromName(photoName, maxWidthPx);
    }
    return value;
  }

  private String extractPhotoName(String value) {
    String trimmed = value.trim();
    int start = trimmed.indexOf(PHOTO_RESOURCE_PREFIX);
    if (start < 0 || !trimmed.contains(PHOTO_RESOURCE_MARKER)) {
      return null;
    }
    int end = trimmed.indexOf(MEDIA_SEGMENT, start);
    if (end < 0) {
      end = trimmed.indexOf('?', start);
    }
    if (end < 0) {
      end = trimmed.length();
    }
    if (end <= start) {
      return null;
    }
    return normalizePhotoResourceName(trimmed.substring(start, end));
  }

  /**
   * 正規化為 Place Photos (New) 的 resource name：places/PLACE_ID/photos/PHOTO_REFERENCE，
   * 不含 /media 與 query string，避免組出無效 URL。
   */
  private String normalizePhotoResourceName(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String s = value.trim();
    if (!s.contains(PHOTO_RESOURCE_PREFIX) || !s.contains(PHOTO_RESOURCE_MARKER)) {
      return null;
    }
    int start = s.indexOf(PHOTO_RESOURCE_PREFIX);
    int mediaIdx = s.indexOf(MEDIA_SEGMENT, start);
    int qIdx = s.indexOf('?', start);
    int end = s.length();
    if (mediaIdx > start) {
      end = mediaIdx;
    }
    if (qIdx > start && qIdx < end) {
      end = qIdx;
    }
    if (end <= start) {
      return null;
    }
    return s.substring(start, end);
  }
}
