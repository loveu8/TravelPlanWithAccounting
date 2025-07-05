package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

public class StringBodyWriteHandler implements BodyWriteHandler {

  @Override
  public boolean supports(Object body) {
    return body instanceof String;
  }

  @Override
  public Object handle(Object body, ObjectMapper objectMapper) {
    try {
      RestResponse<Object, Object> responseBody = RestResponseUtils.success((String) body);
      return objectMapper.writeValueAsString(responseBody);
    } catch (Exception e) {
      throw new RuntimeException("String return type mapping error", e);
    }
  }
}
