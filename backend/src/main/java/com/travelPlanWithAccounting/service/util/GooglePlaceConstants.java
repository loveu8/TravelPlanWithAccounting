package com.travelPlanWithAccounting.service.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class GooglePlaceConstants {

  private GooglePlaceConstants() {}

  /** Google Places API 欄位遮罩 (Nearby / Text 共用) */
  public static final List<String> FIELD_MASK =
      List.of(
          "places.id",
          "places.displayName",
          "places.rating",
          "places.photos",
          "places.addressComponents");

  /** 依優先序排列的城市層級型別 */
  public static final Set<String> CITY_TYPES =
      new LinkedHashSet<>(
          List.of(
              "administrative_area_level_1",
              "locality",
              "postal_town",
              "administrative_area_level_3",
              "administrative_area_level_2"));

  /** 查詢地圖詳情遮罩 (Place Details) */
  public static final List<String> PLACE_DETAILS_FIELD_MASK =
      List.of(
          "id",
          "displayName",
          "formattedAddress",
          "rating",
          "userRatingCount", // 評論數
          "editorialSummary", // 簡介
          "internationalPhoneNumber",
          "websiteUri",
          "regularOpeningHours", // 完整營業時段
          "photos" // 最多 5 張
          );

  public static final int DEFAULT_NEARBY_RADIUS_METERS = 5_000;
  public static final int DEFAULT_TEXT_RADIUS_METERS = 50_000;
  public static final int MIN_RESULT_COUNT = 5;
  public static final int MAX_RESULT_COUNT = 20;

  public static final String RANK_RELEVANCE = "RELEVANCE";
  public static final String RANK_DISTANCE = "DISTANCE";
}
