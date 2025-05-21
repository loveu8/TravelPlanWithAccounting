package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.search.response.City;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
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
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  @Autowired private SearchCountryRepository searchCountryRepository;

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
}
