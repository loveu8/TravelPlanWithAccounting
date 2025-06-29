package com.travelPlanWithAccounting.service.dto.search.response;

import lombok.Data;

@Data
public class LocationSearch {
  private String placeId;
  private String name;
  private String city;
  private String photoUrl;
  private Double rating;
}
