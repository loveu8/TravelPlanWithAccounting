package com.travelPlanWithAccounting.service.dto.search.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
  private JsonNode regularHoursRaw; // ★ 保留原始 regularOpeningHours 物件
}
