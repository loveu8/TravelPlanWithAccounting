package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentityOtpVerifyResponse {
  @Schema(description = "verified identity token")
  private String identityOtpToken;

  @Schema(description = "token expiration time")
  private OffsetDateTime expireAt;
}

