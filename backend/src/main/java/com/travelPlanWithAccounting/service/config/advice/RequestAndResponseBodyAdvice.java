package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.message.SystemMessageCode;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@code RequestAndResponseBodyAdvice} 統一攔截請求與回應，用於包裝 API 回傳格式與處理例外。<br>
 * {@code RequestAndResponseBodyAdvice} intercepts requests and responses to wrap API responses and
 * handle exceptions uniformly.
 *
 * <p>- 將所有非標準格式的回應包裝成 {@link RestResponse} 標準回應格式。<br>
 * - 特別處理 {@link String}、{@link org.springframework.data.domain.Page} 等常見型別。<br>
 * - 捕捉並統一處理常見例外，如 {@link ApiException}、{@link HttpMessageNotReadableException}。<br>
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.travelPlanWithAccounting"})
@RequiredArgsConstructor
public class RequestAndResponseBodyAdvice extends RequestBodyAdviceAdapter
    implements ResponseBodyAdvice<Object> {

  private final ObjectMapper objectMapper;

  private final List<BodyWriteHandler> handlers =
      List.of(
          new StringBodyWriteHandler(), new PageBodyWriteHandler(), new DefaultBodyWriteHandler());

  /**
   * 判斷是否要攔截回應。 Determines if this advice supports the given controller method return type and
   * converter type.
   *
   * @param returnType 控制器方法的返回型別
   * @param converterType 轉換器型別
   * @return 是否攔截
   */
  @Override
  public boolean supports(
      @NonNull MethodParameter returnType,
      @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    return true; // 攔全部
  }

  /**
   * 判斷是否要攔截請求體。 Determines if this advice supports the given method parameter and target type.
   *
   * @param methodParameter 方法參數
   * @param targetType 目標型別
   * @param converterType 轉換器型別
   * @return 是否攔截
   */
  public boolean supports(
      @NonNull MethodParameter methodParameter,
      @NonNull Type targetType,
      @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    return true; // 攔全部
  }

  /**
   * 處理回傳結果，將回應包裝成標準 {@link RestResponse} 格式。 Wraps the controller response into a standardized
   * {@link RestResponse}.
   *
   * @param body 回應體
   * @param returnType 返回型別
   * @param selectedContentType 選定的內容類型
   * @param selectedConverterType 選定的轉換器型別
   * @param request 請求物件
   * @param response 回應物件
   * @return 包裝後的回應體
   */
  @Override
  public Object beforeBodyWrite(
      @Nullable Object body,
      @NonNull MethodParameter returnType,
      @NonNull MediaType selectedContentType,
      @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response) {

    if (body instanceof RestResponse || body instanceof ResponseEntity) {
      return body;
    }

    for (BodyWriteHandler handler : handlers) {
      if (handler.supports(body)) {
        return handler.handle(body, objectMapper);
      }
    }

    // fallback，應該不會到這邊
    throw new IllegalStateException(
        "No BodyWriteHandler could handle the body: " + (body != null ? body.getClass() : "null"));
  }

  /**
   * 處理 {@link ApiException}，回傳標準化錯誤回應。 Handles {@link ApiException} and returns a standardized
   * error response.
   *
   * @param ex 捕獲到的 {@link ApiException}
   * @return 標準化錯誤回應
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<RestResponse<Serializable, Serializable>> handleApiException(
      ApiException ex) {
    log.error(
        "[ApiException] code={}, message={}", ex.getMessageCode().getCode(), ex.getMessage(), ex);
    RestResponse<Serializable, Serializable> response = new RestResponse<>(ex);
    return ResponseEntity.status(ex.getHttpStatus()).body(response);
  }

  /**
   * 處理未預期的例外，回傳標準化錯誤回應。 Handles unexpected exceptions and returns a standardized error response.
   *
   * @param ex 捕獲到的 Exception
   * @return 標準化錯誤回應
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse<Serializable, Serializable>> handleUnexpectedException(
      Exception ex) {
    log.error("[UnexpectedException] {}", ex.getMessage(), ex);
    ApiException apiException = new ApiException(SystemMessageCode.UNEXPECTED_ERROR, ex);
    RestResponse<Serializable, Serializable> response = new RestResponse<>(apiException);
    return ResponseEntity.status(apiException.getHttpStatus()).body(response);
  }

  /**
   * 處理請求體無法解析的錯誤 (例如格式錯誤、不正確的JSON等)。<br>
   * Handle errors when request body cannot be read (e.g., invalid JSON format).
   *
   * @param ex 捕捉到的 HttpMessageNotReadableException
   * @return 標準化的錯誤回應
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<RestResponse<Serializable, Serializable>>
      handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    log.error("[HttpMessageNotReadableException] {}", ex.getMessage(), ex);
    ApiException apiException = new ApiException(SystemMessageCode.REQUEST_NOT_READABLE, ex);
    RestResponse<Serializable, Serializable> response = new RestResponse<>(apiException);
    return ResponseEntity.status(apiException.getHttpStatus()).body(response);
  }
}
