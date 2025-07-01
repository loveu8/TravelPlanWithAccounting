package com.travelPlanWithAccounting.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GooglePlaceDetailMapper {

  private final GoogleApiConfig googleApiConfig;

  public GooglePlaceDetailMapper(GoogleApiConfig cfg) {
    this.googleApiConfig = cfg;
  }

  public PlaceDetailResponse toDto(JsonNode p) {

    String id = p.path("id").asText();
    String name = p.path("displayName").path("text").asText();
    String addr = p.path("formattedAddress").asText();
    Double rate = p.has("rating") ? p.path("rating").asDouble() : null;
    Integer cnt = p.has("userRatingCount") ? p.path("userRatingCount").asInt() : null;
    String desc = p.path("editorialSummary").path("text").asText(null);
    String tel = p.path("internationalPhoneNumber").asText(null);
    String site = p.path("websiteUri").asText(null);

    /* 相片 (最多 5 張) */
    List<String> photos = new ArrayList<>();
    JsonNode photoArr = p.path("photos");
    for (int i = 0; i < photoArr.size() && i < 5; i++) {
      String photoName = photoArr.get(i).path("name").asText();
      photos.add(
          "https://places.googleapis.com/v1/"
              + photoName
              + "/media?key="
              + googleApiConfig.getGoogleApiKey()
              + "&maxWidthPx=400");
    }

    /* regularOpeningHours 原始物件 */
    JsonNode rawHours = p.path("regularOpeningHours");

    return new PlaceDetailResponse(
        id,
        name,
        addr,
        rate,
        cnt,
        desc,
        tel,
        site,
        photos, // photoUrls
        rawHours // regularHoursRaw
        );
  }
}
