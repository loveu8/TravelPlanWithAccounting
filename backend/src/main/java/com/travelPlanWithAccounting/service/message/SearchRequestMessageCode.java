package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

public enum SearchRequestMessageCode implements MessageCode {

  // ------- 共用 ----------
  TEXT_QUERY_REQUIRED("SNTL-001", HttpStatus.BAD_REQUEST),
  MAX_RESULT_RANGE("SNTL-002", HttpStatus.BAD_REQUEST),
  RANK_PREFERENCE_ERR("SNTL-003", HttpStatus.BAD_REQUEST),
  INCLUDED_TYPES_LIMIT("SNTL-004", HttpStatus.BAD_REQUEST),
  LOCATION_NOT_FOUND     ("SNTL-005", HttpStatus.NOT_FOUND),   // 找不到代碼
  LOCATION_LATLON_MISSING("SNTL-006", HttpStatus.BAD_REQUEST); // 無經緯度;

  private final String code;
  private final HttpStatus status;

  SearchRequestMessageCode(String code, HttpStatus status) {
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
