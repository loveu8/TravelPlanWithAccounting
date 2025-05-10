package com.travelPlanWithAccounting.service.dto.search.response;

import java.util.List;
import lombok.Data;

@Data
public class Region {
  private String regionCode;
  private String regionName;
  private Short orderIndex;
  private List<City> cities;
}
