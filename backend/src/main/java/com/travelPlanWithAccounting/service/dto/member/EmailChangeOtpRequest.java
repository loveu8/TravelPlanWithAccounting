package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailChangeOtpRequest {
  @Schema(description = "identity verification token")
  @NotBlank
  private String identityOtpToken;

  @Schema(description = "new email")
  @NotBlank
  @Email
  private String email;
}

