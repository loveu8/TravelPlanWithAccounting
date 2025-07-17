package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.memberpoi.SaveMemberPoiRequest;
import com.travelPlanWithAccounting.service.dto.memberpoi.SaveMemberPoiResponse;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.dto.setting.SettingResponse;
import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.exception.ApiException;
import com.travelPlanWithAccounting.service.service.SearchService;
import com.travelPlanWithAccounting.service.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Search", description = "搜尋引擎")
@RequestMapping("/api/search")
public class SearchController {

  @Autowired private SearchService searchService;
  @Autowired private SettingService settingService;

  // ==================== 設定相關 API ====================

  @GetMapping("/settings/{category}")
  @Operation(summary = "根據類別查詢設定")
  public List<SettingResponse> getSettingsByCategory(@PathVariable String category) {
    return settingService.getSettingsByCategory(category);
  }

  @GetMapping("/settings/language-types")
  @Operation(summary = "查詢所有語言類型設定")
  public List<SettingResponse> getAllLanguageTypes() {
    return settingService.getAllLanguageTypes();
  }

  // ==================== 原有的搜尋 API ====================

  @GetMapping("/countries/{langType}")
  @Operation(summary = "取得國家列表 (DTO 格式)")
  public List<Country> searchCountries(@PathVariable String langType) {
    return searchService.searchCountries(langType);
  }

  @PostMapping("/regions")
  @Operation(summary = "取得地區和城市 (DTO 格式)")
  public List<Region> searchRegions(@RequestBody SearchRequest request) {
    return searchService.searchRegions(request.getCode(), request.getLangType());
  }

  @GetMapping("/allLocations")
  @Operation(summary = "取得所有地點 (DTO 格式)")
  public List<LocationName> allLocations() {
    return searchService.searchLocations();
  }

  @PostMapping("/searchNearby")
  @Operation(summary = "搜尋附近景點")
  public List<LocationSearch> searchNearby(@RequestBody NearbySearchRequest request) {
    return searchService.searchNearby(request);
  }

  @GetMapping("/locations/countries")
  @Operation(summary = "取得所有國家 (Location 物件)")
  public List<Location> getAllCountries() {
    return searchService.getAllCountries();
  }

  @GetMapping("/locations/provinces/{countryCode}")
  @Operation(summary = "根據國家代碼取得州省列表")
  public List<Location> getProvincesByCountryCode(@PathVariable String countryCode) {
    return searchService.getProvincesByCountryCode(countryCode);
  }

  @GetMapping("/locations/cities/{provinceCode}")
  @Operation(summary = "根據州省代碼取得城市列表")
  public List<Location> getCitiesByProvinceCode(@PathVariable String provinceCode) {
    return searchService.getCitiesByProvinceCode(provinceCode);
  }

  @GetMapping("/locations/{code}")
  @Operation(summary = "根據代碼查詢單一 Location")
  public Optional<Location> getLocationByCode(@PathVariable String code) {
    return searchService.getLocationByCode(code);
  }

  @GetMapping("/locations/{code}/level/{level}")
  @Operation(summary = "根據代碼和層級查詢 Location")
  public Optional<Location> getLocationByCodeAndLevel(
      @PathVariable String code, @PathVariable Short level) {
    return searchService.getLocationByCodeAndLevel(code, level);
  }

  @GetMapping("/locations/level/{level}")
  @Operation(summary = "根據層級查詢所有 Location")
  public List<Location> getLocationsByLevel(@PathVariable Short level) {
    return searchService.getLocationsByLevel(level);
  }

  @GetMapping("/locations/coordinates")
  @Operation(summary = "根據經緯度範圍查詢附近的 Location")
  public List<Location> getLocationsByCoordinates(
      @RequestParam Double minLat,
      @RequestParam Double maxLat,
      @RequestParam Double minLon,
      @RequestParam Double maxLon) {
    return searchService.getLocationsByCoordinates(minLat, maxLat, minLon, maxLon);
  }

  @GetMapping("/locations/iso-type/{isoType}")
  @Operation(summary = "根據 ISO 類型查詢 Location")
  public List<Location> getLocationsByIsoType(@PathVariable String isoType) {
    return searchService.getLocationsByIsoType(isoType);
  }

  @PostMapping("/searchNearbyByLocationCode")
  @Operation(summary = "根據 Location 代碼搜尋附近景點")
  public List<LocationSearch> searchNearbyByLocationCode(@RequestBody SearchRequest request) {
    return searchService.searchNearbyByLocationCode(request);
  }

  @PostMapping("/searchTextByLocationCode")
  @Operation(summary = "根據 Location 代碼和文字查詢搜尋景點")
  public List<LocationSearch> searchTextByLocationCode(@RequestBody TextSearchRequest request) {
    return searchService.searchTextByLocationCode(request);
  }

  @GetMapping("/placeDetails")
  @Operation(summary = "取得地點詳細資訊 (含照片)")
  public PlaceDetailResponse getPlaceDetails(
      @RequestParam String placeId, @RequestParam(defaultValue = "zh-TW") String langType) {
    return searchService.getPlaceDetailById(placeId, langType);
  }

  @PostMapping("/saveMemberPoi")
  @Operation(summary = "儲存會員景點")
  public ResponseEntity<SaveMemberPoiResponse> save(
      @RequestHeader("X-member-id") UUID memberId, @Valid @RequestBody SaveMemberPoiRequest req) {
    try {
      SaveMemberPoiResponse resp = searchService.saveMemberPoi(memberId, req);
      return ResponseEntity.ok(resp);
    } catch (ApiException ex) {
      return ResponseEntity.ok(
          SaveMemberPoiResponse.builder()
              .code(0)
              .desc(ex.getDesc())
              .poiCreated(false)
              .langInserted(false)
              .alreadySaved(false)
              .build());
    }
  }
}
