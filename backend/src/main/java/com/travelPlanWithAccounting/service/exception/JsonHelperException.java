package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.JsonHelperMessageCode;

public class JsonHelperException extends ApiException {
  public JsonHelperException(JsonHelperMessageCode c) {
    super(c);
  }

  public static class JsonDeserializeError extends ApiException {
    public JsonDeserializeError() {
      super(JsonHelperMessageCode.JSON_DESERIALIZE_ERROR);
    }
  }

}
