package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.CountriesMessageCode;
import com.travelPlanWithAccounting.service.message.SettingTypeMessageCode;

public class SettingExecption extends ApiException {

  public SettingExecption(CountriesMessageCode code) {
    super(code);
  }

  public static class SettingTypeError extends ApiException {
    public SettingTypeError() {
      super(SettingTypeMessageCode.SETTING_TYPE_ERROR);
    }
  }
}
