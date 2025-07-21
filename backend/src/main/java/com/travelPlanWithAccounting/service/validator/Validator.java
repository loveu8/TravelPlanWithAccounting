package com.travelPlanWithAccounting.service.validator;

import com.travelPlanWithAccounting.service.exception.CountriesExecption;
import com.travelPlanWithAccounting.service.exception.SearchRegionsExecption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Validator {

  private static final List<String> ALLOW_COUNTRY = List.of("JP", "TW");
  private static final List<String> ALLOW_LANG    = List.of("zh-TW", "en-US");

  public void validate(String countryCode, String langType) {
    if (!ALLOW_COUNTRY.contains(countryCode)) {
      throw new SearchRegionsExecption.CountryError();
    }
    if (!ALLOW_LANG.contains(langType)) {
      throw new SearchRegionsExecption.LangTypeError();
    }
  }

  public void validate(String langType){
    if (!ALLOW_LANG.contains(langType)) {
      throw new CountriesExecption.LangTypeError();
    }
  }
}
