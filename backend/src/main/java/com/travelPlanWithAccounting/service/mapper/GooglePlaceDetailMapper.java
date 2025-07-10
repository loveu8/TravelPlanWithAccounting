package com.travelPlanWithAccounting.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 解析 Google Places API v1「Place Details (New)」回傳 JSON → PlaceDetailResponse<br>
 * 參考： 1) 資料欄位總表：location / addressComponents / photos… :contentReference[oaicite:0]{index=0} 2)
 * addressComponents 層級說明（country‧locality 等） :contentReference[oaicite:1]{index=1}
 */
@Component
public class GooglePlaceDetailMapper {

  private final GoogleApiConfig googleApiConfig;

  public GooglePlaceDetailMapper(GoogleApiConfig cfg) {
    this.googleApiConfig = cfg;
  }

  public PlaceDetailResponse toDto(JsonNode p) {

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
    if (p.has("location")) { // location.latLng :contentReference[oaicite:2]{index=2}
      JsonNode locNode = p.path("location");
      lat = locNode.path("latitude").asDouble();
      lon = locNode.path("longitude").asDouble();
    }

    /* ======== 解析城市 / 國家 ======== */
    String city = null, country = null;
    for (JsonNode comp :
        p.path("addressComponents")) { // addressComponents :contentReference[oaicite:3]{index=3}
      String longTxt = comp.path("longText").asText();
      for (JsonNode t : comp.path("types")) {
        switch (t.asText()) {
          case "country" -> country = longTxt;
          case "locality", "postal_town", "administrative_area_level_2" -> {
            if (city == null) city = longTxt; // 優先填第一次出現的城市名稱
          }
          default -> {}
        }
      }
    }

    /* ======== 相片 (最多 5 張) ======== */
    List<String> photos = new ArrayList<>();
    JsonNode photoArr = p.path("photos"); // photos[].name :contentReference[oaicite:4]{index=4}
    for (int i = 0; i < photoArr.size() && i < 5; i++) {
      String photoName = photoArr.get(i).path("name").asText();
      photos.add(
          "https://places.googleapis.com/v1/" // Place Photos (New) Endpoint
              // :contentReference[oaicite:5]{index=5}
              + photoName
              + "/media?key="
              + googleApiConfig.getGoogleApiKey()
              + "&maxWidthPx=400");
    }

    /* ======== regularOpeningHours 原始 JSON (含 periods/weekdayDescriptions) ======== */
    JsonNode rawHours =
        p.path("regularOpeningHours"); // regularOpeningHours :contentReference[oaicite:6]{index=6}

    /* ======== 解析 types ======== */
    List<String> types = new ArrayList<>();
    for (JsonNode t : p.path("types")) { // types[] :contentReference[oaicite:2]{index=2}
      types.add(t.asText());
    }

    /* ======== 組 DTO ======== */
    return new PlaceDetailResponse(
        id, // 1 placeId
        name, // 2 name
        addr, // 3 address
        rate, // 4 rating
        cnt, // 5 reviewCount
        desc, // 6 description
        tel, // 7 phone
        site, // 8 website
        photos, // 9  photoUrls
        rawHours, // 10 regularHoursRaw
        lat, // 11
        lon, // 12
        city, // 13
        country, // 14
        types);
  }
}
