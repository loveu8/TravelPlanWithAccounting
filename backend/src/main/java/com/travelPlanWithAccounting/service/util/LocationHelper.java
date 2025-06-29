package com.travelPlanWithAccounting.service.util;

import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.repository.SearchLocationByCodeRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class LocationHelper {

  private final SearchLocationByCodeRepository repository;

  public LocationHelper(SearchLocationByCodeRepository repository) {
    this.repository = repository;
  }

  /** 依代碼取得並驗證存在與經緯度，否則拋例外 */
  public Location getLocationOrThrow(String code) {
    Optional<Location> locOpt = repository.findByCode(code);
    if (locOpt.isEmpty()) {
      throw new IllegalArgumentException("找不到代碼為 " + code + " 的地點");
    }
    Location loc = locOpt.get();
    if (loc.getLat() == null || loc.getLon() == null) {
      throw new IllegalStateException("地點 " + code + " 沒有經緯度資料");
    }
    return loc;
  }
}
