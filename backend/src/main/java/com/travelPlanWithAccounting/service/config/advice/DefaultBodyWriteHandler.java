package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

public class DefaultBodyWriteHandler implements BodyWriteHandler {

  /**
   * 支援所有非 null 的物件，交由 Jackson 處理序列化，不再強制要求 Serializable。
   *
   * @param body 回傳物件
   * @return true 表示可處理
   */
  @Override
  public boolean supports(Object body) {
    // 支援所有情境（包含 null ），讓 null 也能被包裝成標準回應
    return true;
  }

  @Override
  public Object handle(Object body, ObjectMapper objectMapper) {
    // 直接包裝，不再轉型 Serializable，交由 Jackson 處理
    return RestResponseUtils.success(body);
  }
}
