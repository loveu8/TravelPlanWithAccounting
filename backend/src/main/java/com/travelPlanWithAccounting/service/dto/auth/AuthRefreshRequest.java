package com.travelPlanWithAccounting.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRefreshRequest {

  @Schema(description = "Refresh Token 明文", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank
  private String refreshToken;

  @Schema(description = "客戶端標識（web / ios / android…）未帶預設 web")
  private String clientId;

  // 以下兩個欄位可選：若前端不帶，後端會用 request fallback
  @Schema(description = "來源 IP（可選）")
  private String ip;

  @Schema(description = "User-Agent（可選）")
  private String ua;
}
