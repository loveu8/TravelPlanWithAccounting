package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.CountriesMessageCode;

public class CountriesExecption extends ApiException {

  public CountriesExecption(CountriesMessageCode code) {
    super(code);
  }

  public static class LangTypeError extends ApiException {
    public LangTypeError() {
      super(CountriesMessageCode.LANG_TYPE_ERROR);
    }
  }
}
