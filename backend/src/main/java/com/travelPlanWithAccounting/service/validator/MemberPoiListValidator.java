package com.travelPlanWithAccounting.service.validator;

import com.travelPlanWithAccounting.service.exception.MemberPoiException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class MemberPoiListValidator {
  private static final Pattern POI_TYPE_PATTERN = Pattern.compile("P0(0[1-9]|1[0-8])");

  public void validate(String poiType, Integer page, Integer maxResultCount) {
    if (poiType == null || !POI_TYPE_PATTERN.matcher(poiType).matches()) {
      throw new MemberPoiException.InvalidPoiType();
    }
    if (page != null && page < 1) {
      throw new MemberPoiException.InvalidPage();
    }
    if (maxResultCount != null && (maxResultCount < 5 || maxResultCount > 20)) {
      throw new MemberPoiException.InvalidMaxResultCount();
    }
  }
}
