package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

public enum MemberPoiMessageCode implements MessageCode {

  UNSUPPORTED_LANG      ("MP-001", HttpStatus.BAD_REQUEST),
  PLACE_NOT_FOUND       ("MP-002", HttpStatus.NOT_FOUND),
  PLACE_REQUIRED_MISSING("MP-003", HttpStatus.BAD_REQUEST);

  private final String code;
  private final HttpStatus status;

  MemberPoiMessageCode(String c, HttpStatus s) {
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
