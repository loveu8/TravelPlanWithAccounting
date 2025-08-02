package com.travelPlanWithAccounting.service.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyTokenRequest {
  @NotNull
  private VerifyTokenType tokenType;   // ACCESS or REFRESH
  @NotBlank
  private String token;                // JWT 或 RT 明文
  private String clientId;             // REFRESH 可帶（預設 "web"）
}
