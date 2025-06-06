package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.SystemMessageCode;

public class InvalidTokenException extends ApiException {
  public InvalidTokenException() {
    super(SystemMessageCode.INVALID_TOKEN);
  }
}
