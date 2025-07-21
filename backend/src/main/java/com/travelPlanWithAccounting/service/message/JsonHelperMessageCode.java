package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

public enum JsonHelperMessageCode implements MessageCode {
  JSON_DESERIALIZE_ERROR("JH-001", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final HttpStatus status;

  JsonHelperMessageCode(String c, HttpStatus s) {
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
