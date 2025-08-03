package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.MemberMessageCode;
import java.util.Map;

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

  public static class MemberIdInvalid extends ApiException {
    public MemberIdInvalid() {
      super(MemberMessageCode.MEMBER_ID_INVALID);
    }
  }

  public static class MemberIdMismatch extends ApiException {
    public MemberIdMismatch() {
      super(MemberMessageCode.MEMBER_ID_MISMATCH);
    }
  }

  public static class MemberNotFound extends ApiException {
    public MemberNotFound() {
      super(MemberMessageCode.MEMBER_NOT_FOUND);
    }
  }

  public static class AccessTokenExpired extends ApiException {
    public AccessTokenExpired() {
      super(MemberMessageCode.ACCESS_TOKEN_EXPIRED);
    }
  }

  public static class AccessTokenInvalid extends ApiException {
    public AccessTokenInvalid() {
      super(MemberMessageCode.ACCESS_TOKEN_INVALID);
    }
  }

  public static class MemberNotActive extends ApiException {
    public MemberNotActive() {
      super(MemberMessageCode.MEMBER_NOT_ACTIVE);
    }
  }

  public static class ProfileFieldsInvalid extends ApiException {
    public ProfileFieldsInvalid(Map<String, String> fieldErrors) {
      super(MemberMessageCode.PROFILE_FIELDS_INVALID, fieldErrors);
    }
  }
}
