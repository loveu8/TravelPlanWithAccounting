package com.travelPlanWithAccounting.service.config.advice;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.message.SystemMessageCode;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code GlobalExceptionHandler} 全域攔截應用程式中的例外錯誤，統一包裝成標準 {@link RestResponse} 格式回應。<br>
 * 此類別專注於處理不同型別的例外，並將錯誤資訊結構化回傳給前端。<br>
 * <br>
 * {@code GlobalExceptionHandler} globally intercepts exceptions throughout the application and
 * wraps them into a standardized {@link RestResponse} format.<br>
 * This class focuses on handling various types of exceptions and returning structured error
 * responses to clients.<br>
 * <br>
 * <b>注意事項 / Notes：</b><br>
 * 本類別僅負責例外處理，不負責正常資料包裝。<br>
 * 正常回應資料包裝請參考 {@link ResponseBodyWrapperAdvice} 類別。
 *
 * @author
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.travelPlanWithAccounting"})
public class GlobalExceptionHandler {
  private static final String TRAVEL_MAIN_UPSERT_PATH = "/api/travels/upsertTravelMain";
  private static final List<String> TRAVEL_MAIN_ERROR_FIELDS =
      List.of("title", "start_date", "end_date", "visit_place", "notes");

  /**
   * 處理應用程式自訂的 {@link ApiException}，回傳標準化錯誤格式。<br>
   * Handles {@link ApiException} thrown within the application and returns a standardized error
   * response.
   *
   * @param ex 捕獲到的 {@link ApiException} / the captured {@link ApiException}
   * @return 標準化錯誤回應 / the standardized error response wrapped in {@link RestResponse}
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<RestResponse<Object, Object>> handleApiException(ApiException ex, HttpServletRequest request) {
    log.error(
        "[ApiException] code={}, message={}",
        ex.getMessageCode().getCode(),
        ex.getMessage(),
        ex.getOriginalException());
    RestResponse<Object, Object> response =
        isTravelMainUpsertRequest(request)
            ? RestResponseUtils.error(ex, buildTravelMainFieldErrors(ex.getData()))
            : RestResponseUtils.error(ex);
    return ResponseEntity.status(ex.getHttpStatus()).body(response);
  }

  /**
   * 處理所有未預期的例外狀況，並回傳標準化錯誤回應。<br>
   * Handles any unexpected {@link Exception} and returns a standardized error response.
   *
   * @param ex 捕獲到的未預期例外 / the captured unexpected {@link Exception}
   * @return 標準化錯誤回應 / the standardized error response wrapped in {@link RestResponse}
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse<Object, Object>> handleUnexpectedException(Exception ex, HttpServletRequest request) {
    log.error("[UnexpectedException] {}", ex.getMessage(), ex);
    RestResponse<Object, Object> response =
        isTravelMainUpsertRequest(request)
            ? RestResponseUtils.error(
                SystemMessageCode.UNEXPECTED_ERROR,
                null,
                ex,
                buildTravelMainFieldErrors(null))
            : RestResponseUtils.error(SystemMessageCode.UNEXPECTED_ERROR, null, ex);
    return ResponseEntity.status(SystemMessageCode.UNEXPECTED_ERROR.getHttpStatus()).body(response);
  }

  /**
   * 處理 HTTP 請求體無法讀取或解析的例外狀況，例如 JSON 格式錯誤或不合法的資料。<br>
   * Handles {@link HttpMessageNotReadableException} when the HTTP request body cannot be read or
   * parsed (e.g., invalid JSON format).
   *
   * @param ex 捕捉到的 {@link HttpMessageNotReadableException} / the captured {@link
   *     HttpMessageNotReadableException}
   * @return 標準化錯誤回應 / the standardized error response wrapped in {@link RestResponse}
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<RestResponse<Object, Object>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.error("[HttpMessageNotReadableException] {}", ex.getMessage(), ex);
    RestResponse<Object, Object> response =
        isTravelMainUpsertRequest(request)
            ? RestResponseUtils.error(
                SystemMessageCode.REQUEST_NOT_READABLE,
                null,
                ex,
                buildTravelMainFieldErrors(null))
            : RestResponseUtils.error(SystemMessageCode.REQUEST_NOT_READABLE, null, ex);
    return ResponseEntity.status(SystemMessageCode.REQUEST_NOT_READABLE.getHttpStatus())
        .body(response);
  }

  private boolean isTravelMainUpsertRequest(HttpServletRequest request) {
    if (request == null) {
      return false;
    }
    String requestUri = request.getRequestURI();
    return requestUri != null && requestUri.endsWith(TRAVEL_MAIN_UPSERT_PATH);
  }

  private List<RestResponse.FieldError> buildTravelMainFieldErrors(Object data) {
    Map<String, String> messageMap = extractFieldErrorMessages(data);
    return TRAVEL_MAIN_ERROR_FIELDS.stream()
        .map(
            field ->
                RestResponse.FieldError.builder()
                    .field(field)
                    .message(messageMap.getOrDefault(field, ""))
                    .build())
        .toList();
  }

  private Map<String, String> extractFieldErrorMessages(Object data) {
    if (!(data instanceof Map<?, ?> rawMap)) {
      return Collections.emptyMap();
    }
    return rawMap.entrySet().stream()
        .filter(entry -> entry.getKey() instanceof String && entry.getValue() instanceof String)
        .collect(
            java.util.stream.Collectors.toMap(
                entry -> (String) entry.getKey(), entry -> (String) entry.getValue()));
  }
}
