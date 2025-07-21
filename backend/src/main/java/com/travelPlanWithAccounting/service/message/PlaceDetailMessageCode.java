package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

public enum PlaceDetailMessageCode implements MessageCode {
    
  INVALID_PLACE_ID("PD-001", HttpStatus.BAD_REQUEST),
  UNSUPPORTED_LANG("PD-002", HttpStatus.BAD_REQUEST);

  private final String code;
  private final HttpStatus status;

  PlaceDetailMessageCode(String c, HttpStatus s) {
    this.code = c;
    this.status = s;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return status;
  }

  @Override
  public String getMessage(Object[] args) {
    return MessageSourceHolder.getMessage(code, args);
  }

  @Override
  public String getMessage() {
    return MessageSourceHolder.getMessage(code);
  }
}
