package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Search", description = "搜尋引擎")
@RequestMapping("/api/search")
public class SearchController {

  @Autowired private SearchService searchService;

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
}
