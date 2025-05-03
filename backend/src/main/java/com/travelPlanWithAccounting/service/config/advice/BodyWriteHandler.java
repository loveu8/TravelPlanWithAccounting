package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@code BodyWriteHandler} 定義回應處理器介面，用於根據回應物件類型決定是否處理及如何包裝回應。<br>
 * {@code BodyWriteHandler} defines an interface for response handlers that decide whether to
 * process a response body based on its type and how to handle the response wrapping.
 *
 * <p>- 每種型別的回應（例如 {@link String}、{@link org.springframework.data.domain.Page}）都有對應的處理邏輯。<br>
 * - 透過責任鏈模式 (Chain of Responsibility) 建立靈活、可擴展的處理流程。<br>
 */
public interface BodyWriteHandler {

  /**
   * 判斷此處理器是否支援該回應體類型。<br>
   * Determines whether this handler supports processing the given response body.
   *
   * @param body 回應物件 (Response body object)
   * @return {@code true} 若支援處理；否則回傳 {@code false} <br>
   *     {@code true} if this handler can process the given body, {@code false} otherwise.
   */
  boolean supports(Object body);

  /**
   * 處理回應物件並回傳處理後的結果。<br>
   * Handles the response body and returns the processed object.
   *
   * @param body 原始回應物件 (Original response body object)
   * @param objectMapper 用於序列化的 {@link ObjectMapper} 實例 (ObjectMapper for serialization if needed)
   * @return 處理後的回應物件 (Processed response body)
   * @throws RuntimeException 若處理過程中發生錯誤時拋出 (Throws RuntimeException if processing fails)
   */
  Object handle(Object body, ObjectMapper objectMapper);
}
