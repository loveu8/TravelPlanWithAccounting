package com.travelPlanWithAccounting.service.dto.member;

import com.travelPlanWithAccounting.service.model.OtpPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreAuthFlowResponse {
  @Schema(description = "輸入的 email")
  private String email;

  @Schema(description = "帳號是否已存在")
  private boolean exists;

  @Schema(description = "系統判定下一步用途（REGISTRATION 或 LOGIN）")
  private OtpPurpose purpose;

  @Schema(description = "PRD action code（REGISTRATION=001, LOGIN=002）")
  private String actionCode;
}
