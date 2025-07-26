package com.travelPlanWithAccounting.service.util;

import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.exception.SearchRequestException;
import com.travelPlanWithAccounting.service.repository.SearchLocationByCodeRepository;
import org.springframework.stereotype.Component;

@Component
public class LocationHelper {

  private final SearchLocationByCodeRepository repository;

  public LocationHelper(SearchLocationByCodeRepository repository) {
    this.repository = repository;
  }

  /** 依代碼取得並驗證存在與經緯度，否則拋例外 */
  public Location getLocationOrThrow(String code) {
    // 1) 取資料
    Location loc = repository.findByCode(code).orElseThrow(SearchRequestException.NotFound::new);

    // 2) 驗證經緯度
    if (loc.getLat() == null || loc.getLon() == null) {
      throw new SearchRequestException.LatLonMissing();
    }
    return loc;
  }
}
