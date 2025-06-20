package com.travelPlanWithAccounting.service.dto.otp;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtpVerifyResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private boolean verified;
  private String token;

  public OtpVerifyResponse(boolean verified, String token) {
    this.verified = verified;
    this.token = token;
  }
}
