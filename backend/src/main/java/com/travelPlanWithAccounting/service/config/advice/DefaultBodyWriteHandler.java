package com.travelPlanWithAccounting.service.config.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;
import java.io.Serializable;

public class DefaultBodyWriteHandler implements BodyWriteHandler {

  @Override
  public boolean supports(Object body) {
    return body instanceof Serializable;
  }

  @Override
  public Object handle(Object body, ObjectMapper objectMapper) {
    return RestResponseUtils.success((Serializable) body);
  }
}
