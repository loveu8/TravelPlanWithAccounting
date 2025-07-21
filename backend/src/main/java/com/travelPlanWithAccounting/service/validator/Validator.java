package com.travelPlanWithAccounting.service.validator;

import com.travelPlanWithAccounting.service.exception.CountriesExecption;
import com.travelPlanWithAccounting.service.exception.SearchRegionsExecption;
import com.travelPlanWithAccounting.service.exception.SettingExecption;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Validator {

  private static final List<String> ALLOW_COUNTRY = List.of("JP", "TW");
  private static final List<String> ALLOW_LANG = List.of("zh-TW", "en-US");
  private static final List<String> ALLOW_CATEGORY = List.of("LANG_TYPE");

  public void validate(String countryCode, String langType) {
    if (!ALLOW_COUNTRY.contains(countryCode)) {
      throw new SearchRegionsExecption.CountryError();
    }
    if (!ALLOW_LANG.contains(langType)) {
      throw new SearchRegionsExecption.LangTypeError();
    }
  }

  public void validate(String langType) {
    if (!ALLOW_LANG.contains(langType)) {
      throw new CountriesExecption.LangTypeError();
    }
  }

  public void validateCategory(String category) {
    if (!ALLOW_CATEGORY.contains(category)) {
      throw new SettingExecption.SettingTypeError();
    }
  }
}
