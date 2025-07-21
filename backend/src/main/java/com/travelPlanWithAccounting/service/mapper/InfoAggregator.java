package com.travelPlanWithAccounting.service.mapper;

import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.City;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.entity.LocationGroup;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InfoAggregator {

  public List<Region> searchRegions(List<Object[]> rows) {
    Map<UUID, Region> map = new HashMap<>();

    for (Object[] r : rows) {
      LocationGroup group = (LocationGroup) r[0];
      String regionName   = (String)       r[1];
      Location location   = (Location)     r[3];
      String cityName     = (String)       r[4];

      Region region = map.computeIfAbsent(group.getId(), id -> {
        Region dto = new Region();
        dto.setRegionCode(group.getCode());
        dto.setRegionName(regionName != null ? regionName : group.getCode());
        dto.setOrderIndex(group.getOrderIndex());
        dto.setCities(new ArrayList<>());
        return dto;
      });

      City city = new City();
      city.setCode(location.getCode());
      city.setName(cityName != null ? cityName : location.getCode());
      region.getCities().add(city);
    }

    List<Region> list = new ArrayList<>(map.values());
    list.sort(Comparator.comparing(Region::getOrderIndex));
    return list;
  }

  public List<Country> searchCountries(List<Object[]> results) {
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
}