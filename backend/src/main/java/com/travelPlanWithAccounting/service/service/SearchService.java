package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import com.travelPlanWithAccounting.service.dto.search.response.City;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.entity.LocationGroup;
import com.travelPlanWithAccounting.service.entity.LocationGroupMapping;
import com.travelPlanWithAccounting.service.repository.SearchAllCountryRepository;
import com.travelPlanWithAccounting.service.repository.SearchAllLocationRepository;
import com.travelPlanWithAccounting.service.repository.SearchCountryRepository;
import com.travelPlanWithAccounting.service.repository.SearchLocationByCodeRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  @Autowired private SearchCountryRepository searchCountryRepository;

  @Autowired private MapService mapService;

  @Autowired private GoogleApiConfig googleApiConfig;

  @Autowired private SearchLocationByCodeRepository searchLocationByCodeRepository;

  public List<Region> searchRegions(String countryCode, String langType) {
    List<Object[]> results = searchCountryRepository.findRegionsAndCities(countryCode, langType);

    // 使用 Map 按地區聚合城市資料
    Map<UUID, Region> regionMap = new HashMap<>();

    for (Object[] row : results) {
      LocationGroup group = (LocationGroup) row[0];
      String regionName = (String) row[1];
      LocationGroupMapping mapping = (LocationGroupMapping) row[2];
      Location location = (Location) row[3];
      String cityName = (String) row[4];

      // 若該地區尚未存在於 Map 中，則初始化
      Region region =
          regionMap.computeIfAbsent(
              group.getId(),
              id -> {
                Region dto = new Region();
                dto.setRegionCode(group.getCode());
                dto.setRegionName(regionName != null ? regionName : group.getCode());
                dto.setOrderIndex(group.getOrderIndex());
                dto.setCities(new ArrayList<>());
                return dto;
              });

      // 添加城市資料
      City city = new City();
      city.setCode(location.getCode());
      city.setName(cityName != null ? cityName : location.getCode());
      region.getCities().add(city);
    }

    // 轉換為 List 並按 orderIndex 排序
    List<Region> regions = new ArrayList<>(regionMap.values());
    regions.sort(Comparator.comparing(Region::getOrderIndex));
    return regions;
  }

  @Autowired private SearchAllCountryRepository searchAllCountryRepository;

  public List<Country> searchCountries(String langType) {
    List<Object[]> results = searchAllCountryRepository.findCountriesByLangType(langType);
    List<Country> countries = new ArrayList<>();

    for (Object[] row : results) {
      Location location = (Location) row[0];
      String countryName = (String) row[1];

      Country country = new Country();
      country.setCode(location.getCode());
      country.setName(countryName != null ? countryName : location.getCode());
      countries.add(country);
    }

    return countries;
  }

  @Autowired private SearchAllLocationRepository searchAllLocationRepository;

  public List<LocationName> searchLocations() {
    List<Object[]> results = searchAllLocationRepository.findAllLocation();
    List<LocationName> locationNames = new ArrayList<>();

    for (Object[] row : results) {
      Location location = (Location) row[0];
      String name = (String) row[1];
      String langType = (String) row[2];

      LocationName locationName = new LocationName();
      locationName.setCode(location.getCode());
      locationName.setName(name != null ? name : location.getCode());
      locationName.setLangType(langType);
      locationNames.add(locationName);
    }

    return locationNames;
  }

  // ==================== 新增：直接回傳 Location 物件的方法 ====================

  /**
   * 取得所有國家 (直接回傳 Location 物件)
   *
   * @return List<Location>
   */
  public List<Location> getAllCountries() {
    return searchLocationByCodeRepository.findAllCountries();
  }

  /**
   * 根據國家代碼取得該國家的所有州省
   *
   * @param countryCode 國家代碼
   * @return List<Location>
   */
  public List<Location> getProvincesByCountryCode(String countryCode) {
    return searchLocationByCodeRepository.findProvincesByCountryCode(countryCode);
  }

  /**
   * 根據州省代碼取得該州省的所有城市
   *
   * @param provinceCode 州省代碼
   * @return List<Location>
   */
  public List<Location> getCitiesByProvinceCode(String provinceCode) {
    return searchLocationByCodeRepository.findCitiesByProvinceCode(provinceCode);
  }

  /**
   * 根據代碼查詢單一 Location
   *
   * @param code Location 代碼
   * @return Optional<Location>
   */
  public Optional<Location> getLocationByCode(String code) {
    return searchLocationByCodeRepository.findByCode(code);
  }

  /**
   * 根據代碼和層級查詢 Location
   *
   * @param code Location 代碼
   * @param level 層級 (1=國家 2=州省 3=城市)
   * @return Optional<Location>
   */
  public Optional<Location> getLocationByCodeAndLevel(String code, Short level) {
    return searchLocationByCodeRepository.findByCodeAndLevel(code, level);
  }

  /**
   * 根據層級查詢所有該層級的 Location
   *
   * @param level 層級 (1=國家 2=州省 3=城市)
   * @return List<Location>
   */
  public List<Location> getLocationsByLevel(Short level) {
    return searchLocationByCodeRepository.findByLevelOrderByOrderIndex(level);
  }

  /**
   * 根據經緯度範圍查詢附近的 Location
   *
   * @param minLat 最小緯度
   * @param maxLat 最大緯度
   * @param minLon 最小經度
   * @param maxLon 最大經度
   * @return List<Location>
   */
  public List<Location> getLocationsByCoordinates(
      Double minLat, Double maxLat, Double minLon, Double maxLon) {
    return searchLocationByCodeRepository.findLocationsByCoordinates(
        minLat, maxLat, minLon, maxLon);
  }

  /**
   * 根據 ISO 類型查詢 Location
   *
   * @param isoType ISO 類型 (001: ISO-3166-1, 002: ISO-3166-2)
   * @return List<Location>
   */
  public List<Location> getLocationsByIsoType(String isoType) {
    return searchLocationByCodeRepository.findByIsoTypeOrderByOrderIndex(isoType);
  }

  /**
   * 根據 Location 代碼搜尋附近景點
   *
   * @param request 包含 Location 代碼的搜尋請求
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchNearbyByLocationCode(SearchRequest request) {
    // 1. 先拿到 Location 的對應經緯度
    String code = request.getCode();
    Optional<Location> locationOpt = searchLocationByCodeRepository.findByCode(code);

    if (locationOpt.isEmpty()) {
      throw new RuntimeException("找不到代碼為 " + code + " 的地點");
    }

    Location location = locationOpt.get();

    // 檢查是否有經緯度資料
    if (location.getLat() == null || location.getLon() == null) {
      throw new RuntimeException("地點 " + code + " 沒有經緯度資料");
    }

    // 2. 驗證和設定 maxResultCount (限制 5-20 之間)
    Integer maxResultCount = request.getMaxResultCount();
    if (maxResultCount != null) {
      if (maxResultCount < 5 || maxResultCount > 20) {
        throw new RuntimeException("maxResultCount 必須在 5-20 之間，當前值: " + maxResultCount);
      }
    } else {
      maxResultCount = 5; // 預設值
    }

    // 3. 驗證和設定 rankPreference (只允許 RELEVANCE 或 DISTANCE)
    String rankPreference = request.getRankPreference();
    if (rankPreference != null) {
      if (!"RELEVANCE".equals(rankPreference) && !"DISTANCE".equals(rankPreference)) {
        throw new RuntimeException(
            "rankPreference 只能是 RELEVANCE 或 DISTANCE，當前值: " + rankPreference);
      }
    } else {
      rankPreference = "DISTANCE"; // 預設值
    }

    // 4. 建立 NearbySearchRequest
    NearbySearchRequest nearbyRequest = new NearbySearchRequest();
    nearbyRequest.setMaxResultCount(maxResultCount);
    nearbyRequest.setRankPreference(rankPreference);
    nearbyRequest.setLanguageCode(request.getLangType());

    // 設定位置限制
    com.travelPlanWithAccounting.service.dto.google.LocationRestriction locationRestriction =
        new com.travelPlanWithAccounting.service.dto.google.LocationRestriction();
    com.travelPlanWithAccounting.service.dto.google.Circle circle =
        new com.travelPlanWithAccounting.service.dto.google.Circle();
    com.travelPlanWithAccounting.service.dto.google.LatLng center =
        new com.travelPlanWithAccounting.service.dto.google.LatLng();

    center.setLatitude(location.getLat().doubleValue());
    center.setLongitude(location.getLon().doubleValue());
    circle.setCenter(center);
    circle.setRadius(5000.0); // radius 在這裡設定
    locationRestriction.setCircle(circle);
    nearbyRequest.setLocationRestriction(locationRestriction);
    nearbyRequest.setIncludedTypes(request.getIncludedTypes());

    // 5. 呼叫 Google Places API
    return searchNearby(nearbyRequest);
  }

  public List<LocationSearch> searchNearby(NearbySearchRequest request) {

    // 1. 欄位遮罩 ─ 新增 addressComponents，並保留 photos.name
    List<String> fieldMask =
        List.of(
            "places.id",
            "places.displayName",
            "places.rating",
            "places.photos", // ★ photos 陣列
            "places.addressComponents" // ★ 取城市用
            );
    // 2. 先把想抓城市的 type 全列進 Set，方便 contains()
    LinkedHashSet<String> cityTypes = new LinkedHashSet<String>();
    cityTypes.add("administrative_area_level_1");
    cityTypes.add("locality");
    cityTypes.add("postal_town");
    cityTypes.add("administrative_area_level_3");
    cityTypes.add("administrative_area_level_2");

    JsonNode result = mapService.searchNearby(request, fieldMask);
    List<LocationSearch> locations = new ArrayList<>();

    for (JsonNode place : result.path("places")) {

      String placeId = place.path("id").asText();
      String name = place.path("displayName").path("text").asText();
      Double rating = place.has("rating") ? place.path("rating").asDouble() : -1;

      /* ---------- 取相片 ---------- */
      String photoUrl = null;
      JsonNode photosNode = place.path("photos");
      if (photosNode.isArray() && photosNode.size() > 0) {
        // ★ 直接拿到 photo name（包含 placeId 與 photo resource）
        String photoName = photosNode.get(0).path("name").asText();

        // ★ 新版 Photo 端點：…/{PHOTO_NAME}/media
        photoUrl =
            "https://places.googleapis.com/v1/"
                + photoName
                + "/media?key="
                + googleApiConfig.getGoogleApiKey()
                + "&maxWidthPx=400"; // 至少要給 width 或 height
      }

      String city = null;
      Map<String, String> candidate = new HashMap<>();

      for (JsonNode comp : place.path("addressComponents")) {
        String longText = comp.path("longText").asText();
        for (JsonNode t : comp.path("types")) {
          String type = t.asText();
          // 只收我們關心的層級
          if (cityTypes.contains(type)) {
            candidate.put(type, longText);
          }
        }
      }

      /* 依優先順序決定最終城市 */
      for (String key : cityTypes) {
        if (candidate.containsKey(key)) {
          city = candidate.get(key);
          break;
        }
      }

      /* 後備：formattedAddress 整條地址 */
      if (city == null && place.has("formattedAddress")) {
        city = place.path("formattedAddress").asText();
      }

      /* ---------- 組回結果 ---------- */
      LocationSearch loc = new LocationSearch();
      loc.setPlaceId(placeId);
      loc.setName(name);
      loc.setCity(city);
      loc.setRating(rating);
      loc.setPhotoUrl(photoUrl);

      locations.add(loc);
    }
    return locations;
  }

  /**
   * 根據 Location 代碼和文字查詢搜尋景點
   *
   * @param request 包含 Location 代碼和搜尋文字的請求
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchTextByLocationCode(TextSearchRequest request) {
    // 1. 先拿到 Location 的對應經緯度
    String code = request.getCode();
    Optional<Location> locationOpt = searchLocationByCodeRepository.findByCode(code);

    if (locationOpt.isEmpty()) {
      throw new RuntimeException("找不到代碼為 " + code + " 的地點");
    }

    Location location = locationOpt.get();

    // 檢查是否有經緯度資料
    if (location.getLat() == null || location.getLon() == null) {
      throw new RuntimeException("地點 " + code + " 沒有經緯度資料");
    }

    // 2. 建立 TextSearchRequest
    com.travelPlanWithAccounting.service.dto.google.TextSearchRequest textRequest =
        new com.travelPlanWithAccounting.service.dto.google.TextSearchRequest();
    textRequest.setTextQuery(request.getTextQuery());
    textRequest.setLanguageCode(request.getLangType());
    textRequest.setMaxResultCount(
        request.getMaxResultCount() != null ? request.getMaxResultCount() : 5);
    textRequest.setRankPreference(
        request.getRankPreference() != null ? request.getRankPreference() : "RELEVANCE");

    // 設定位置偏向 (locationBias) - 讓結果偏向指定區域
    com.travelPlanWithAccounting.service.dto.google.LocationBias locationBias =
        new com.travelPlanWithAccounting.service.dto.google.LocationBias();
    com.travelPlanWithAccounting.service.dto.google.Circle circle =
        new com.travelPlanWithAccounting.service.dto.google.Circle();
    com.travelPlanWithAccounting.service.dto.google.LatLng center =
        new com.travelPlanWithAccounting.service.dto.google.LatLng();

    center.setLatitude(location.getLat().doubleValue());
    center.setLongitude(location.getLon().doubleValue());
    circle.setCenter(center);
    circle.setRadius(50000.0); // 文字搜尋使用較大的半徑
    locationBias.setCircle(circle);
    textRequest.setLocationBias(locationBias);

    // 設定地點類型篩選
    if (request.getIncludedTypes() != null && !request.getIncludedTypes().isEmpty()) {
      textRequest.setIncludedType(request.getIncludedTypes().get(0)); // TextSearch 只支援單一類型
    }

    // 3. 呼叫 Google Places API
    return searchText(textRequest);
  }

  /**
   * 使用 Google Places API 進行文字搜尋
   *
   * @param request TextSearchRequest 物件
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchText(
      com.travelPlanWithAccounting.service.dto.google.TextSearchRequest request) {
    // 1. 欄位遮罩 ─ 與 searchNearby 保持一致
    List<String> fieldMask =
        List.of(
            "places.id",
            "places.displayName",
            "places.rating",
            "places.photos", // ★ photos 陣列
            "places.addressComponents" // ★ 取城市用
            );

    // 2. 先把想抓城市的 type 全列進 Set，方便 contains()
    LinkedHashSet<String> cityTypes = new LinkedHashSet<String>();
    cityTypes.add("administrative_area_level_1");
    cityTypes.add("locality");
    cityTypes.add("postal_town");
    cityTypes.add("administrative_area_level_3");
    cityTypes.add("administrative_area_level_2");

    JsonNode result = mapService.searchText(request, fieldMask);
    List<LocationSearch> locations = new ArrayList<>();

    for (JsonNode place : result.path("places")) {

      String placeId = place.path("id").asText();
      String name = place.path("displayName").path("text").asText();
      Double rating = place.has("rating") ? place.path("rating").asDouble() : -1.0;

      /* ---------- 取相片 ---------- */
      String photoUrl = null;
      JsonNode photosNode = place.path("photos");
      if (photosNode.isArray() && photosNode.size() > 0) {
        // ★ 直接拿到 photo name（包含 placeId 與 photo resource）
        String photoName = photosNode.get(0).path("name").asText();

        // ★ 新版 Photo 端點：…/{PHOTO_NAME}/media
        photoUrl =
            "https://places.googleapis.com/v1/"
                + photoName
                + "/media?key="
                + googleApiConfig.getGoogleApiKey()
                + "&maxWidthPx=400"; // 至少要給 width 或 height
      }

      String city = null;
      Map<String, String> candidate = new HashMap<>();

      for (JsonNode comp : place.path("addressComponents")) {
        String longText = comp.path("longText").asText();
        for (JsonNode t : comp.path("types")) {
          String type = t.asText();
          // 只收我們關心的層級
          if (cityTypes.contains(type)) {
            candidate.put(type, longText);
          }
        }
      }

      /* 依優先順序決定最終城市 */
      for (String key : cityTypes) {
        if (candidate.containsKey(key)) {
          city = candidate.get(key);
          break;
        }
      }

      /* 後備：formattedAddress 整條地址 */
      if (city == null && place.has("formattedAddress")) {
        city = place.path("formattedAddress").asText();
      }

      /* ---------- 組回結果 ---------- */
      LocationSearch loc = new LocationSearch();
      loc.setPlaceId(placeId);
      loc.setName(name);
      loc.setCity(city);
      loc.setRating(rating);
      loc.setPhotoUrl(photoUrl);

      locations.add(loc);
    }
    return locations;
  }
}
