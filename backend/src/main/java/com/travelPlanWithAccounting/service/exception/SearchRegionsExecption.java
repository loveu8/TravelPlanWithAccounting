package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.SearchRegionsMessageCode;

public class SearchRegionsExecption extends ApiException {

  public SearchRegionsExecption(SearchRegionsMessageCode code) {
    super(code);
  }

  public static class CountryError extends ApiException {
    public CountryError() {
      super(SearchRegionsMessageCode.COUNTRY_CODE_ERROR);
    }
  }

  public static class LangTypeError extends ApiException {
    public LangTypeError() {
      super(SearchRegionsMessageCode.LANG_TYPE_ERROR);
    }
  }

}
