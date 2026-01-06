package com.travelPlanWithAccounting.service.dto.auth;

import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyTokenResponse {
  private boolean valid; // true=有效
  private String tokenType; // "ACCESS"/"REFRESH"
  private String reason; // 無效原因（可為 null）
  private UUID sub; // token的 subject（ACCESS 有；REFRESH 成功也可回 ownerId）
  private String role; // ACCESS 可回 role（例如 "001"）
  private Long exp; // 逾時(秒, epoch second)，ACCESS 有
}
