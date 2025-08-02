package com.travelPlanWithAccounting.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLogoutByRtRequest {

  @Schema(description = "Refresh Token 明文", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank
  private String refreshToken;
}
