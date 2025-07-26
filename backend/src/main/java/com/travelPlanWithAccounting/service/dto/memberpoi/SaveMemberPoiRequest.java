package com.travelPlanWithAccounting.service.dto.memberpoi;

import lombok.Data;

@Data
public class SaveMemberPoiRequest {
  private String memberId;      // optional, server will validate vs auth
  private String placeId;
  private String langType;
}
