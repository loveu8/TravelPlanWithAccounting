package com.travelPlanWithAccounting.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

  @Schema(description = "主體 ID（會員）")
  private UUID id;

  @Schema(description = "身分代碼（例如 001=MEMBER，002=GUEST）")
  private String role;

  @Schema(description = "要由 BFF 寫入 Cookie 的 token 資訊")
  private Map<String, TokenNode> cookies;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TokenNode {
    /** JWT 或 Refresh Token 明文 */
    private String code;

    /** Max-Age（秒） */
    private long time;
  }
}
