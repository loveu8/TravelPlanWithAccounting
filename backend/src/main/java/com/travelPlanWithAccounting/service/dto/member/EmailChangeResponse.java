package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeResponse {
  @Schema(description = "operation success")
  private boolean success;
}

