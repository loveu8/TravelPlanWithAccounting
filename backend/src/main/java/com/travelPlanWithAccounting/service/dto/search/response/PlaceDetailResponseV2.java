package com.travelPlanWithAccounting.service.dto.search.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceDetailResponseV2 {
  private String placeId;               // Google id
  private String name;                  // displayName.text
  private String address;               // formattedAddress
  private Double rating;                // rating
  private Integer reviewCount;          // userRatingCount
  private String description;           // editorialSummary.text
  private String phone;                 // internationalPhoneNumber
  private String website;               // websiteUri
  private List<PhotoMeta> photos;       // top N photo metas
  private JsonNode regularHoursRaw;     // full regularOpeningHours
  private Double lat;                   // location.latitude
  private Double lon;                   // location.longitude
  private String city;                  // derived from addressComponents
  private String country;               // derived from addressComponents
  private String primaryType;           // google primaryType
  private List<String> types;           // google types[]
  private JsonNode rawJson;             // full google JSON (lang-specific)
}

