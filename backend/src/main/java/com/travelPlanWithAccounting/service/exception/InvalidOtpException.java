package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.SystemMessageCode;
import java.util.Arrays;

public class InvalidOtpException extends ApiException {
  public InvalidOtpException() {
    super(SystemMessageCode.INVALID_OTP);
  }

  public static class Expired extends ApiException {
    public Expired() {
      super(SystemMessageCode.OTP_EXPIRED);
    }
  }

  public static class MaxAttemptsExceeded extends ApiException {
    public MaxAttemptsExceeded() {
      super(SystemMessageCode.OTP_MAX_ATTEMPTS_EXCEEDED);
    }
  }

  public static class NotFound extends ApiException {
    public NotFound() {
      super(SystemMessageCode.OTP_NOT_FOUND);
    }
  }

  public static class ResendTooFrequently extends ApiException {
    public ResendTooFrequently(int seconds) {
      super(
          SystemMessageCode.OTP_RESEND_TOO_FREQUENTLY,
          Arrays.asList(seconds).toArray(new Integer[0]));
    }
  }
}
