package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdentityOtpVerifyRequest {
  @Schema(description = "otp token from identity verification")
  @NotBlank
  private String otpToken;

  @Schema(description = "otp code")
  @NotBlank
  private String otpCode;
}

