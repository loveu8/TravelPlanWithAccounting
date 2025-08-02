package com.travelPlanWithAccounting.service.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PreAuthFlowRequest {
  @NotBlank @Email
  private String email;

  // 可選：保留 clientId 供未來使用（此步不使用）
  private String clientId;
}
