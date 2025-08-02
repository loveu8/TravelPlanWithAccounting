package com.travelPlanWithAccounting.service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleResult {
  @Schema(description = "true=成功, false=失敗")
  private boolean success;
}
