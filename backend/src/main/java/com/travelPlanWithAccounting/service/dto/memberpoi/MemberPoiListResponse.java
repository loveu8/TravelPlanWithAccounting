package com.travelPlanWithAccounting.service.dto.memberpoi;

import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.system.PageMeta;
import java.util.List;
import lombok.Data;

@Data
public class MemberPoiListResponse {
  private List<LocationSearch> list;
  private PageMeta meta;
}
