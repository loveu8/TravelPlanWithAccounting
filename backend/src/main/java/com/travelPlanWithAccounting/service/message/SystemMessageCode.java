package com.travelPlanWithAccounting.service.message;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SystemMessageCode implements MessageCode {

  // 400 BAD_REQUEST
  INVALID_PARAMETER("400001", HttpStatus.BAD_REQUEST),
  MISSING_REQUIRED_PARAMETER("400002", HttpStatus.BAD_REQUEST),
  REQUEST_NOT_READABLE("400003", HttpStatus.BAD_REQUEST),
  INVALID_OTP("400004", HttpStatus.BAD_REQUEST),
  OTP_EXPIRED("400005", HttpStatus.BAD_REQUEST),
  OTP_MAX_ATTEMPTS_EXCEEDED("400006", HttpStatus.BAD_REQUEST),
  INVALID_TOKEN("400007", HttpStatus.BAD_REQUEST),

  // 401 UNAUTHORIZED
  UNAUTHORIZED_ACCESS("401001", HttpStatus.UNAUTHORIZED),

  // 403 FORBIDDEN
  FORBIDDEN_OPERATION("403001", HttpStatus.FORBIDDEN),

  // 404 NOT_FOUND
  RESOURCE_NOT_FOUND("404001", HttpStatus.NOT_FOUND),
  OTP_NOT_FOUND("404002", HttpStatus.NOT_FOUND),

  // 409 CONFLICT
  DUPLICATE_RESOURCE("409001", HttpStatus.CONFLICT),
  RESOURCE_CONFLICT("409002", HttpStatus.CONFLICT),

  // 500 INTERNAL_SERVER_ERROR
  INTERNAL_SERVER_ERROR("500001", HttpStatus.INTERNAL_SERVER_ERROR),
  SERVICE_UNAVAILABLE("500002", HttpStatus.SERVICE_UNAVAILABLE),
  UNEXPECTED_ERROR("500003", HttpStatus.INTERNAL_SERVER_ERROR),
  EMAIL_SEND_ERROR("500004", HttpStatus.INTERNAL_SERVER_ERROR),
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
