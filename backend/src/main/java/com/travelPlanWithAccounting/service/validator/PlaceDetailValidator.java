package com.travelPlanWithAccounting.service.validator;

import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PlaceDetailValidator {

  private static final Pattern PLACE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{25,}$");
  private static final Set<String> SUPPORTED_LANG = Set.of("zh-TW", "en-US");

  public void validate(String placeId, String lang) {
    if (!StringUtils.hasText(placeId) || !PLACE_ID_PATTERN.matcher(placeId).matches()) {
      throw new IllegalArgumentException("非法的 placeId");
    }
    if (!SUPPORTED_LANG.contains(lang)) {
      throw new IllegalArgumentException("不支援的語系: " + lang);
    }
  }
}
