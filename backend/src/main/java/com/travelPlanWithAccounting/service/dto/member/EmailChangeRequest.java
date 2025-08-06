package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailChangeRequest {
  @Schema(description = "identity verification token")
  @NotBlank
  private String identityOtpToken;

  @Schema(description = "new email otp token")
  @NotBlank
  private String otpToken;

  @Schema(description = "new email otp code")
  @NotBlank
  private String otpCode;
}

