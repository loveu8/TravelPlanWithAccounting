package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Search", description = "搜尋引擎")
@RequestMapping("/api/search")
public class SearchController {

  @Autowired private SearchService searchService;

  @GetMapping("/countries/{langType}")
  public List<Country> searchCountries(@PathVariable String langType) {
    return searchService.searchCountries(langType);
  }

  @PostMapping("/regions")
  public List<Region> searchRegions(@RequestBody SearchRequest request) {
    return searchService.searchRegions(request.getCountryCode(), request.getLangType());
  }

  @GetMapping("/allLocations")
  public List<LocationName> allLocations() {
    return searchService.searchLocations();
  }

  @PostMapping("/searchNearby")
  public List<LocationSearch> searchNearby(@RequestBody NearbySearchRequest request) {
    return searchService.searchNearby(request);
  }
}
