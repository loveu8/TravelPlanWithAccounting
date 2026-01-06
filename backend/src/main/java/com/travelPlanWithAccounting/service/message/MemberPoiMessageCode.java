package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import org.springframework.http.HttpStatus;

public enum MemberPoiMessageCode implements MessageCode {

  UNSUPPORTED_LANG      ("MP-001", HttpStatus.BAD_REQUEST),
  PLACE_NOT_FOUND       ("MP-002", HttpStatus.NOT_FOUND),
  PLACE_REQUIRED_MISSING("MP-003", HttpStatus.BAD_REQUEST),
  INVALID_POI_TYPE      ("MP-004", HttpStatus.BAD_REQUEST),
  INVALID_MAX_RESULT_COUNT("MP-005", HttpStatus.BAD_REQUEST),
  INVALID_PAGE          ("MP-006", HttpStatus.BAD_REQUEST),
  MEMBER_POI_NOT_FOUND  ("MP-007", HttpStatus.NOT_FOUND),
  FAVORITES_IDS_EMPTY   ("MP-008", HttpStatus.BAD_REQUEST),
  FAVORITES_IDS_TOO_MANY("MP-009", HttpStatus.BAD_REQUEST),
  FAVORITES_ID_EMPTY    ("MP-010", HttpStatus.BAD_REQUEST);

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
