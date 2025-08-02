package com.travelPlanWithAccounting.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessTokenResponse {

  @Schema(description = "新的 Access-Token (JWT)")
  private String accessToken;

  @Schema(description = "Max-Age 秒數")
  private long expiresIn;     // ＝  jwtUtil.accessTtlSeconds()
}
