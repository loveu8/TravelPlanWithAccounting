package com.travelPlanWithAccounting.service.dto.search.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class LocationSearch {
  private UUID poiId;
  private String placeId;
  private String name;
  private String city;
  private String photoUrl;
  private Double rating;
}
