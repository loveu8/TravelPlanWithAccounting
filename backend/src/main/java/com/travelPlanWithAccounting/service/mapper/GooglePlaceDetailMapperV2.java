package com.travelPlanWithAccounting.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.search.response.PhotoMeta;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponseV2;
import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Google Places API v1 Place Details (New) -> PlaceDetailResponseV2.
 */
@Component
public class GooglePlaceDetailMapperV2 {
  private final GoogleApiConfig googleApiConfig;
  public GooglePlaceDetailMapperV2(GoogleApiConfig cfg){this.googleApiConfig = cfg;}

  public PlaceDetailResponseV2 toDto(JsonNode p) {
    String id   = p.path("id").asText();
    String name = p.path("displayName").path("text").asText();
    String addr = p.path("formattedAddress").asText();
    Double rate = p.has("rating") ? p.path("rating").asDouble() : null;
    Integer cnt = p.has("userRatingCount") ? p.path("userRatingCount").asInt() : null;
    String desc = p.path("editorialSummary").path("text").asText(null);
    String tel  = p.path("internationalPhoneNumber").asText(null);
    String site = p.path("websiteUri").asText(null);

    // location
    Double lat = null, lon = null;
    if (p.has("location")) {
      JsonNode loc = p.path("location");
      lat = loc.path("latitude").isMissingNode()?null:loc.path("latitude").asDouble();
      lon = loc.path("longitude").isMissingNode()?null:loc.path("longitude").asDouble();
    }

    // city/country from addressComponents
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

    // primaryType / types
    String primaryType = p.path("primaryType").asText(null);
    List<String> types = new ArrayList<>();
    JsonNode tarr = p.path("types");
    if (tarr.isArray()) {
      for (JsonNode tn : tarr) types.add(tn.asText());
    }

    // photos (top 5)
    List<PhotoMeta> photoMetas = new ArrayList<>();
    JsonNode parr = p.path("photos");
    int limit = Math.min(5, parr.size());
    for (int i=0;i<limit;i++) {
      JsonNode ph = parr.get(i);
      String photoName = ph.path("name").asText();
      Integer w = ph.has("widthPx")?ph.path("widthPx").asInt():null;
      Integer h = ph.has("heightPx")?ph.path("heightPx").asInt():null;
      String urlSmall = buildPhotoUrl(photoName, 400);
      String attribution = null;
      if (ph.has("authorAttributions") && ph.path("authorAttributions").size()>0) {
        attribution = ph.path("authorAttributions").get(0).asText();
      }
      photoMetas.add(new PhotoMeta(photoName,w,h,attribution,urlSmall));
    }

    JsonNode rawHours = p.path("regularOpeningHours");

    return PlaceDetailResponseV2.builder()
        .placeId(id)
        .name(name)
        .address(addr)
        .rating(rate)
        .reviewCount(cnt)
        .description(desc)
        .phone(tel)
        .website(site)
        .photos(photoMetas)
        .regularHoursRaw(rawHours)
        .lat(lat)
        .lon(lon)
        .city(city)
        .country(country)
        .primaryType(primaryType)
        .types(types)
        .rawJson(p)
        .build();
  }

  private String buildPhotoUrl(String photoName, int maxWidthPx){
    return "https://places.googleapis.com/v1/" + photoName + "/media?key=" +
        googleApiConfig.getGoogleApiKey() + "&maxWidthPx=" + maxWidthPx;
  }
}