package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberMessageCode implements MessageCode {

  /* ---------- 400 BAD_REQUEST ---------- */
  EMAIL_REQUIRED("400101", HttpStatus.BAD_REQUEST),
  INVALID_OTP_TOKEN("400102", HttpStatus.BAD_REQUEST),
  EMAIL_FORMAT_INVALID("400103", HttpStatus.BAD_REQUEST),
  MEMBER_ID_INVALID("400104", HttpStatus.BAD_REQUEST),
  MEMBER_ID_MISMATCH("400105", HttpStatus.BAD_REQUEST),
  PROFILE_FIELDS_INVALID("400106", HttpStatus.BAD_REQUEST),

  /* ---------- 401 UNAUTHORIZED ---------- */
  ACCESS_TOKEN_EXPIRED("401101", HttpStatus.UNAUTHORIZED),
  ACCESS_TOKEN_INVALID("401102", HttpStatus.UNAUTHORIZED),

  /* ---------- 403 FORBIDDEN ------------ */
  MEMBER_NOT_ACTIVE("403101", HttpStatus.FORBIDDEN),

  /* ---------- 404 NOT_FOUND ------------ */
  EMAIL_NOT_FOUND("404101", HttpStatus.NOT_FOUND),
  MEMBER_NOT_FOUND("404102", HttpStatus.NOT_FOUND),

  /* ---------- 409 CONFLICT ------------- */
  EMAIL_ALREADY_EXISTS("409101", HttpStatus.CONFLICT);

  /* ------------ 共用方法 -------------- */
  private final String code;
  private final HttpStatus httpStatus;

  @Override
  public String getMessage(Object[] args) {
    return MessageSourceHolder.getMessage(code, args);
  }

  @Override
  public String getMessage() {
    return MessageSourceHolder.getMessage(code);
  }
}
