package com.travelPlanWithAccounting.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 解析 Google Places API v1 「Place Details (New)」回傳 JSON → PlaceDetailResponse. */
@Component
@RequiredArgsConstructor
public class GooglePlaceDetailMapper {

  private final GoogleApiConfig googleApiConfig;

  public PlaceDetailResponse toDto(JsonNode p, boolean withDetails) {

    /* ======== 基本欄位 ======== */
    String id = p.path("id").asText();
    String name = p.path("displayName").path("text").asText();
    String addr = p.path("formattedAddress").asText();
    Double rate = p.has("rating") ? p.path("rating").asDouble() : null;
    Integer cnt = p.has("userRatingCount") ? p.path("userRatingCount").asInt() : null;
    String desc = p.path("editorialSummary").path("text").asText(null);
    String tel = p.path("internationalPhoneNumber").asText(null);
    String site = p.path("websiteUri").asText(null);

    /* ======== 經緯度 ======== */
    Double lat = null, lon = null;
    if (p.has("location")) {
      JsonNode locNode = p.path("location");
      if (!locNode.path("latitude").isMissingNode()) lat = locNode.path("latitude").asDouble();
      if (!locNode.path("longitude").isMissingNode()) lon = locNode.path("longitude").asDouble();
    }

    /* ======== 城市 / 國家 ======== */
    String city = null, country = null;
    for (JsonNode comp : p.path("addressComponents")) {
      String longTxt = comp.path("longText").asText();
      for (JsonNode t : comp.path("types")) {
        switch (t.asText()) {
          case "country" -> country = longTxt;
          case "locality", "postal_town", "administrative_area_level_2" -> {
            if (city == null) city = longTxt;
          }
          default -> {}
        }
      }
    }

    /* ======== primaryType ======== */
    String primaryType = p.path("primaryType").asText(null);

    /* ======== 相片 (最多 5 張) ======== */
    List<String> photos = new ArrayList<>();
    JsonNode photoArr = p.path("photos");
    for (int i = 0; i < photoArr.size() && i < 5; i++) {
      String photoName = photoArr.get(i).path("name").asText();
      photos.add(buildPhotoUrl(photoName, 400)); // 400px 預設，可依需求 param
    }

    /* ======== regularOpeningHours 原始 JSON ======== */
    JsonNode rawHours = p.path("regularOpeningHours");

    /* ======== types ======== */
    List<String> types = new ArrayList<>();
    for (JsonNode t : p.path("types")) {
      types.add(t.asText());
    }

    /* ======== DTO ======== */
    return PlaceDetailResponse.builder()
        .placeId(id)
        .name(name)
        .address(addr)
        .rating(rate)
        .reviewCount(cnt)
        .description(desc)
        .phone(tel)
        .website(site)
        .photoUrls(photos)
        .regularHoursRaw(rawHours)
        .lat(lat)
        .lon(lon)
        .city(city)
        .country(country)
        .types(types)
        .primaryType(primaryType)
        .rawJson(withDetails ? p : null) // 整包資料
        .build();
  }

  private String buildPhotoUrl(String photoName, int maxWidthPx) {
    return "https://places.googleapis.com/v1/"
        + photoName
        + "/media?key="
        + googleApiConfig.getGoogleApiKey()
        + "&maxWidthPx="
        + maxWidthPx;
  }
}
