package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.MemberMessageCode;

public class MemberException extends ApiException {
  public MemberException(MemberMessageCode code) {
    super(code);
  }

  public static class EmailRequired extends ApiException {
    public EmailRequired() {
      super(MemberMessageCode.EMAIL_REQUIRED);
    }
  }

  public static class OtpTokenInvalid extends ApiException {
    public OtpTokenInvalid() {
      super(MemberMessageCode.INVALID_OTP_TOKEN);
    }
  }

  public static class EmailAlreadyExists extends ApiException {
    public EmailAlreadyExists() {
      super(MemberMessageCode.EMAIL_ALREADY_EXISTS);
    }
  }

  public static class EmailNotFound extends ApiException {
    public EmailNotFound() {
      super(MemberMessageCode.EMAIL_NOT_FOUND);
    }
  }

  public static class EmailFormatInvalid extends ApiException {
    public EmailFormatInvalid() {
      super(MemberMessageCode.EMAIL_FORMAT_INVALID);
    }
  }
}
