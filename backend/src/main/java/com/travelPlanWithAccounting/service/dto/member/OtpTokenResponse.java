package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpTokenResponse {
  @Schema(description = "otp token")
  private String otpToken;

  @Schema(description = "token expiration time")
  private OffsetDateTime expireAt;
}

