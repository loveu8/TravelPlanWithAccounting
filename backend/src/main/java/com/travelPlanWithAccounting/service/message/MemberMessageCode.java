package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberMessageCode implements MessageCode {
  // 400 BAD_REQUEST
  EMAIL_REQUIRED("400101", HttpStatus.BAD_REQUEST),
  INVALID_OTP_TOKEN("400102", HttpStatus.BAD_REQUEST),

  // 409 CONFLICT
  EMAIL_ALREADY_EXISTS("409101", HttpStatus.CONFLICT),
  ;

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
