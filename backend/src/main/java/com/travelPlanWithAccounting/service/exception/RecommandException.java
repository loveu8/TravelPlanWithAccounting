package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.RecommandMessageCode;

public class RecommandException {

  private RecommandException() {}

  public static class InvalidCountry extends ApiException {
    public InvalidCountry(String country) {
      super(RecommandMessageCode.INVALID_COUNTRY, new Object[] {country});
    }
  }

  public static class UnsupportedLang extends ApiException {
    public UnsupportedLang(String language) {
      super(RecommandMessageCode.UNSUPPORTED_LANG, new Object[] {language});
    }
  }

  public static class ConfigError extends ApiException {
    public ConfigError(String country) {
      super(RecommandMessageCode.CONFIG_ERROR, new Object[] {country});
    }
  }
}
