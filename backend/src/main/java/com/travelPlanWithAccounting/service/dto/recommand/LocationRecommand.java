package com.travelPlanWithAccounting.service.dto.recommand;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationRecommand {
  private UUID poiId;
  private String placeId;
  private String name;
  private String city;
  private String photoUrl;
  private Double rating;
  private Double lat;
  private Double lon;
}
