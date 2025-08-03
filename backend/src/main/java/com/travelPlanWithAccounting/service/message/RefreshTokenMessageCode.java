package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

/** Message codes for refresh token operations. */
public enum RefreshTokenMessageCode implements MessageCode {

  NOT_FOUND("RT-001", HttpStatus.NOT_FOUND),
  INVALID("RT-002", HttpStatus.BAD_REQUEST);

  private final String code;
  private final HttpStatus status;

  RefreshTokenMessageCode(String code, HttpStatus status) {
    this.code = code;
    this.status = status;
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
