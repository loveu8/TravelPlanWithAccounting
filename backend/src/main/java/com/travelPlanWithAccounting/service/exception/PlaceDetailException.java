package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.PlaceDetailMessageCode;

public class PlaceDetailException extends ApiException {
  public PlaceDetailException(PlaceDetailMessageCode c) {
    super(c);
  }

  public static class InvalidPlaceId extends ApiException {
    public InvalidPlaceId() {
      super(PlaceDetailMessageCode.INVALID_PLACE_ID);
    }
  }

  public static class UnsupportedLang extends ApiException {
    public UnsupportedLang() {
      super(PlaceDetailMessageCode.UNSUPPORTED_LANG);
    }
  }
}
