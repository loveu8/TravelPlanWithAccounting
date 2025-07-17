package com.travelPlanWithAccounting.service.dto.memberpoi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SaveMemberPoiRequest {
  private String memberId;      // optional, server will validate vs auth
  @NotBlank private String placeId;
  @NotBlank
  @Pattern(regexp="zh-TW|en-US")
  private String langType;
}
