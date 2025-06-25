package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  @Autowired private SearchCountryRepository searchCountryRepository;

  @Autowired private MapService mapService;

  @Autowired private GoogleApiConfig googleApiConfig;

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
    Set<String> cityTypes =
        Set.of(
            "locality", // 一般城市
            "postal_town", // 英式地區常用
            "administrative_area_level_3", // 台灣、日韓部分縣市
            "administrative_area_level_2" // 縣、市
            );
    JsonNode result = mapService.searchNearby(request, fieldMask);
    List<LocationSearch> locations = new ArrayList<>();

    for (JsonNode place : result.path("places")) {

      String placeId = place.path("id").asText();
      String name = place.path("displayName").path("text").asText();
      double rating = place.has("rating") ? place.path("rating").asDouble() : -1;

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
        // 若要 JSON 而不是 302 轉址可再加 &skipHttpRedirect=true
      }

      String city = null;
      Map<String, String> candidate = new HashMap<>();

      for (JsonNode comp : place.path("addressComponents")) {
        String longText = comp.path("longText").asText();
        for (JsonNode t : comp.path("types")) {
          String type = t.asText();
          // 只收我們關心的層級
          if (List.of(
                  "locality",
                  "postal_town",
                  "administrative_area_level_2",
                  "administrative_area_level_3")
              .contains(type)) {
            candidate.put(type, longText);
          }
        }
      }

      /* 依優先順序決定最終城市 */
      for (String key :
          List.of(
              "administrative_area_level_2",
              "administrative_area_level_3",
              "locality",
              "postal_town")) {
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
      loc.setRating(String.valueOf(rating));
      loc.setPhotoUrl(photoUrl);

      locations.add(loc);
    }
    return locations;
  }
}
