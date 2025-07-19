package com.travelPlanWithAccounting.service.dto.search.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetailResponse {
  private String placeId;
  private String name;
  private String address;
  private Double rating;
  private Integer reviewCount; // userRatingCount
  private String description; // editorialSummary.text
  private String phone;
  private String website;
  private List<String> photoUrls; // 最多 5 張
  private JsonNode regularHoursRaw; // 原始 regularOpeningHours
  private Double lat;
  private Double lon;
  private String city;
  private String country;
  private List<String> types; // google types[]
  private String primaryType; // google primaryType (新增)
  private JsonNode rawJson; // 整包 Google JSON (新增)
}
