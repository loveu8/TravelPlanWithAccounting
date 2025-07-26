package com.travelPlanWithAccounting.service.mapper;

import static com.travelPlanWithAccounting.service.util.GooglePlaceConstants.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class GooglePlaceMapper {

  private final GoogleApiConfig googleApiConfig;

  public GooglePlaceMapper(GoogleApiConfig googleApiConfig) {
    this.googleApiConfig = googleApiConfig;
  }

  public List<LocationSearch> toLocationSearchList(JsonNode root) {
    List<LocationSearch> list = new ArrayList<>();
    for (JsonNode place : root.path("places")) {
      list.add(toLocationSearch(place));
    }
    return list;
  }

  /* 單筆轉換 */
  public LocationSearch toLocationSearch(JsonNode place) {

    String placeId = place.path("id").asText();
    String name = place.path("displayName").path("text").asText();
    Double rating = place.has("rating") ? place.path("rating").asDouble() : -1.0;

    // Photo URL
    String photoUrl = null;
    JsonNode photosNode = place.path("photos");
    if (photosNode.isArray() && photosNode.size() > 0) {
      String photoName = photosNode.get(0).path("name").asText();
      photoUrl =
          "https://places.googleapis.com/v1/"
              + photoName
              + "/media?key="
              + googleApiConfig.getGoogleApiKey()
              + "&maxWidthPx=400";
    }

    // City 決策
    String city = null;
    Map<String, String> candidate = new HashMap<>();
    for (JsonNode comp : place.path("addressComponents")) {
      String longText = comp.path("longText").asText();
      for (JsonNode t : comp.path("types")) {
        String type = t.asText();
        if (CITY_TYPES.contains(type)) {
          candidate.put(type, longText);
        }
      }
    }
    for (String key : CITY_TYPES) {
      if (candidate.containsKey(key)) {
        city = candidate.get(key);
        break;
      }
    }
    if (city == null && place.has("formattedAddress")) {
      city = place.path("formattedAddress").asText();
    }

    // DTO 組裝
    LocationSearch loc = new LocationSearch();
    loc.setPlaceId(placeId);
    loc.setName(name);
    loc.setCity(city);
    loc.setRating(rating);
    loc.setPhotoUrl(photoUrl);
    return loc;
  }
}
