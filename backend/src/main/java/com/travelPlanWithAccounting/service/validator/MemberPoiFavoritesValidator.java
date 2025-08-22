package com.travelPlanWithAccounting.service.validator;

import com.travelPlanWithAccounting.service.exception.MemberPoiException;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MemberPoiFavoritesValidator {
  public void validate(List<String> placeIds) {
    if (placeIds == null || placeIds.isEmpty()) {
      throw new MemberPoiException.FavoritesIdsEmpty();
    }
    if (placeIds.size() > 30) {
      throw new MemberPoiException.FavoritesIdsTooMany();
    }
    if (placeIds.stream().anyMatch(id -> !StringUtils.hasText(id))) {
      throw new MemberPoiException.FavoriteIdEmpty();
    }
  }
}
