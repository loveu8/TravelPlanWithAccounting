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

  public String buildFromName(String photoName, int maxWidthPx) {
    if (photoName == null || photoName.isBlank()) {
      return null;
    }
    return "https://places.googleapis.com/v1/"
        + photoName
        + "/media?key="
        + googleApiConfig.getGoogleApiKey()
        + "&maxWidthPx="
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
    if (trimmed.startsWith(PHOTO_RESOURCE_PREFIX)) {
      return trimmed;
    }
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
    return trimmed.substring(start, end);
  }
}
