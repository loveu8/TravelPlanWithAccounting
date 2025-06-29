package com.travelPlanWithAccounting.service.factory;

import static com.travelPlanWithAccounting.service.util.GooglePlaceConstants.*;

import com.travelPlanWithAccounting.service.dto.google.*;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class GoogleRequestFactory {

  /* NearbySearch builder */
  public NearbySearchRequest buildNearby(Location loc, SearchRequest ui) {
    NearbySearchRequest req = new NearbySearchRequest();
    req.setLanguageCode(ui.getLangType());
    req.setMaxResultCount(
        ui.getMaxResultCount() != null ? ui.getMaxResultCount() : MIN_RESULT_COUNT);
    req.setRankPreference(ui.getRankPreference() != null ? ui.getRankPreference() : RANK_DISTANCE);
    req.setIncludedTypes(ui.getIncludedTypes());

    // 地理限制
    LocationRestriction lr = new LocationRestriction();
    Circle circle = new Circle();
    LatLng center = new LatLng();
    center.setLatitude(loc.getLat().doubleValue());
    center.setLongitude(loc.getLon().doubleValue());
    circle.setCenter(center);
    circle.setRadius((double) DEFAULT_NEARBY_RADIUS_METERS);
    lr.setCircle(circle);
    req.setLocationRestriction(lr);

    return req;
  }

  /** TextSearch builder */
  public com.travelPlanWithAccounting.service.dto.google.TextSearchRequest buildText(
      Location loc, com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest ui) {

    // 1. 建立 Google 版 Request
    com.travelPlanWithAccounting.service.dto.google.TextSearchRequest req =
        new com.travelPlanWithAccounting.service.dto.google.TextSearchRequest();

    // 2. 基本參數
    req.setTextQuery(ui.getTextQuery());
    req.setLanguageCode(ui.getLangType());
    req.setMaxResultCount(
        ui.getMaxResultCount() != null ? ui.getMaxResultCount() : MIN_RESULT_COUNT);
    req.setRankPreference(ui.getRankPreference() != null ? ui.getRankPreference() : RANK_RELEVANCE);

    // 3. locationBias (circle)
    LocationBias bias = new LocationBias();
    Circle circle = new Circle();
    LatLng center = new LatLng();
    center.setLatitude(loc.getLat().doubleValue());
    center.setLongitude(loc.getLon().doubleValue());
    circle.setCenter(center);
    circle.setRadius((double) DEFAULT_TEXT_RADIUS_METERS);
    bias.setCircle(circle);
    req.setLocationBias(bias);

    // 4. includedType（TextSearch 只接受單一 type）
    if (ui.getIncludedTypes() != null && !ui.getIncludedTypes().isEmpty()) {
      req.setIncludedType(ui.getIncludedTypes().get(0));
    }

    return req;
  }
}
