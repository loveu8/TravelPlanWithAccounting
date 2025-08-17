package com.travelPlanWithAccounting.service.dto.memberpoi;

import lombok.Data;

@Data
public class MemberPoiListRequest {
  private String poiType;
  private Integer page;
  private Integer maxResultCount;
}
