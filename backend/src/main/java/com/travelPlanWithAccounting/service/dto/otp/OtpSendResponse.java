package com.travelPlanWithAccounting.service.dto.otp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpSendResponse {
  private String email;
  private String token;
}
