package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.RefreshTokenMessageCode;

/** Refresh token related exceptions. */
public class RefreshTokenException extends ApiException {

  public RefreshTokenException(RefreshTokenMessageCode code) {
    super(code);
  }

  /** Thrown when refresh token is not found. */
  public static class NotFound extends ApiException {
    public NotFound() {
      super(RefreshTokenMessageCode.NOT_FOUND);
    }
  }

  /** Thrown when refresh token state is invalid (revoked, used or expired). */
  public static class Invalid extends ApiException {
    public Invalid() {
      super(RefreshTokenMessageCode.INVALID);
    }
  }
}
