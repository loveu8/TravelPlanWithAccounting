package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@code ResponseBodyWrapperAdvice} 統一攔截控制器回應內容，將回應資料包裝成標準格式 {@link RestResponse}。<br>
 * 此類別專注於處理正常回傳的資料格式，不負責例外處理。<br>
 * <br>
 * {@code ResponseBodyWrapperAdvice} intercepts controller responses and wraps the response data
 * into a standardized {@link RestResponse} format.<br>
 * This class focuses solely on handling normal responses and does not manage exception handling.
 * <br>
 * <br>
 * <b>主要功能 / Responsibilities：</b><br>
 * - 自動將非標準格式的回應包裝成 {@link RestResponse} 標準格式<br>
 * - 支援特定型別（例如 {@link String}、分頁結果等）以正確方式進行處理<br>
 * - 對於已經是 {@link RestResponse} 或 {@link ResponseEntity} 類型的回應則不做任何改動<br>
 *
 * @author
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.travelPlanWithAccounting"})
@RequiredArgsConstructor
public class ResponseBodyWrapperAdvice implements ResponseBodyAdvice<Object> {

  private final ObjectMapper objectMapper;

  private final List<BodyWriteHandler> handlers =
      List.of(
          new StringBodyWriteHandler(), new PageBodyWriteHandler(), new DefaultBodyWriteHandler());

  /**
   * 判斷此 Advice 是否應該應用於當前控制器方法的回傳型別與訊息轉換器。<br>
   * Determines whether this advice supports the given controller method return type and converter
   * type.
   *
   * @param returnType 控制器方法的回傳型別 / the return type of the controller method
   * @param converterType HTTP 訊息轉換器型別 / the selected converter type
   * @return 是否應用此 Advice / {@code true} to apply this advice, {@code false} otherwise
   */
  @Override
  public boolean supports(
      @NonNull MethodParameter returnType,
      @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  /**
   * 在控制器方法回傳結果後、寫入回應內容前，進行回應資料的攔截與包裝。<br>
   * Intercepts the response body before it is written to the output, and wraps it into a
   * standardized {@link RestResponse} format.
   *
   * @param body 控制器方法回傳的原始資料 / the body to be written
   * @param returnType 控制器方法的回傳型別 / the return type of the controller method
   * @param selectedContentType 選定的回應內容類型 / the content type selected through content negotiation
   * @param selectedConverterType 選定的訊息轉換器型別 / the converter type selected to write the response
   * @param request 當前的 HTTP 請求物件 / the current HTTP request
   * @param response 當前的 HTTP 回應物件 / the current HTTP response
   * @return 包裝後的回應資料 / the wrapped response body
   */
  @Override
  public Object beforeBodyWrite(
      @Nullable Object body,
      @NonNull MethodParameter returnType,
      @NonNull MediaType selectedContentType,
      @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response) {

    // 已經是標準格式或自定義回應類型，直接返回
    if (body instanceof RestResponse || body instanceof ResponseEntity) {
      return body;
    }

    // 使用符合的處理器進行包裝處理
    for (BodyWriteHandler handler : handlers) {
      if (handler.supports(body)) {
        return handler.handle(body, objectMapper);
      }
    }

    // 理論上不應該到此，如果到此表示沒有合適的處理器
    throw new IllegalStateException(
        "No BodyWriteHandler could handle the body: "
            + (body != null ? body.getClass() : "null")
            + ". 請確認回傳物件可被 Jackson 正確序列化 (Please ensure the response object can be serialized by"
            + " Jackson, e.g., is a standard POJO or record). 若遇到序列化錯誤，請檢查欄位型別與 getter/setter。");
  }
}
