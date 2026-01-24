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
  private String country;
  private String city;
  private String photoUrl;
  private Double rating;
  private Integer reviewCount;
  private Double lat;
  private Double lon;
}
