package com.travelPlanWithAccounting.service.dto.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * {@code RestResponse} 是統一的 API 回應格式，用於封裝成功或失敗的資料。<br>
 * {@code RestResponse} is a unified API response wrapper for success or error results.
 *
 * <p>包含主要資料（data）、輔助資訊（meta）、以及錯誤資訊（error）。<br>
 * Includes main data (data), metadata (meta), and error details (error).
 *
 * @param <D> 資料內容的型別 / Type of the main data payload
 * @param <M> 輔助資訊的型別 / Type of the metadata
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<D, M> {
  private D data;
  private M meta;
  private Error error;

  /**
   * 建立一個基於 {@link ApiException} 的錯誤回應建構子。<br>
   * Creates a {@code RestResponse} instance based on an {@link ApiException}.
   *
   * @param exception the ApiException instance
   */
  public RestResponse(ApiException exception) {
    if (exception == null || exception.getMessageCode() == null) {
      throw new IllegalArgumentException("ApiException or its MessageCode must not be null");
    }
    this.data = null;
    this.meta = null;
    this.error =
        Error.builder()
            .code(exception.getMessageCode().getCode())
            .message(exception.getMessageCode().getMessage(exception.getArgs()))
            .timestamp(Instant.now())
            .details(exception.getData())
            .originalException(exception)
            .build();
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Error {
    private String code;
    private String message;
    private Instant timestamp;
    private Object details;

    @JsonIgnore private Exception originalException;
  }
}
