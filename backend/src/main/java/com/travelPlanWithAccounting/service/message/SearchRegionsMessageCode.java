package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

public enum SearchRegionsMessageCode implements MessageCode {
  COUNTRY_CODE_ERROR("SR-001", HttpStatus.BAD_REQUEST),
  LANG_TYPE_ERROR("SR-002", HttpStatus.BAD_REQUEST);

  private final String code;
  private final HttpStatus httpStatus;

  SearchRegionsMessageCode(String code, HttpStatus httpStatus) {
    this.code = code;
    this.httpStatus = httpStatus;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
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
