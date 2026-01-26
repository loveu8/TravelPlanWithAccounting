package com.travelPlanWithAccounting.service.message;

import org.springframework.http.HttpStatus;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;

public enum ValidationMessageCode implements MessageCode {
    VALIDATION_REQUIRED("VAL-001", HttpStatus.BAD_REQUEST),
    VALIDATION_TYPE_MISMATCH("VAL-002", HttpStatus.BAD_REQUEST),
    VALIDATION_LENGTH_EXCEEDED("VAL-003", HttpStatus.BAD_REQUEST),
    VALIDATION_LENGTH_TOO_SHORT("VAL-004", HttpStatus.BAD_REQUEST),
    VALIDATION_UNSAFE_INPUT("VAL-005", HttpStatus.BAD_REQUEST),
    VALIDATION_DATE_RANGE_INVALID("VAL-006", HttpStatus.BAD_REQUEST),
    VALIDATION_DATE_RANGE_TOO_SHORT("VAL-007", HttpStatus.BAD_REQUEST),
    VALIDATION_DATE_RANGE_TOO_LONG("VAL-008", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatus status;

    ValidationMessageCode(String code, HttpStatus status) {
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
  public String getMessage(Object[] a) {
    return MessageSourceHolder.getMessage(code, a);
  }

  @Override
  public String getMessage() {
    return MessageSourceHolder.getMessage(code);
  }
}
