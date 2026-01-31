package com.travelPlanWithAccounting.service.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhotoUrlValidator {

  private static final int VALID_STATUS_MAX = 400;
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

  private final HttpClient httpClient;

  public PhotoUrlValidator(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public boolean isValid(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .timeout(REQUEST_TIMEOUT)
              .method("HEAD", HttpRequest.BodyPublishers.noBody())
              .build();
      HttpResponse<Void> response =
          httpClient.send(request, HttpResponse.BodyHandlers.discarding());
      int statusCode = response.statusCode();
      return statusCode >= 200 && statusCode < VALID_STATUS_MAX;
    } catch (Exception ex) {
      log.warn("Failed to validate photo URL: {}", url, ex);
      return false;
    }
  }
}
