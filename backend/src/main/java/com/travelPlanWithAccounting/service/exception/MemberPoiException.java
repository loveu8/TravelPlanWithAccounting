package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.MemberPoiMessageCode;

public class MemberPoiException extends ApiException {
  public MemberPoiException(MemberPoiMessageCode c) {
    super(c);
  }

  public static class UnsupportedLang extends ApiException {
    public UnsupportedLang() {
      super(MemberPoiMessageCode.UNSUPPORTED_LANG);
    }
  }

  public static class PlaceNotFound extends ApiException {
    public PlaceNotFound() {
      super(MemberPoiMessageCode.PLACE_NOT_FOUND);
    }
  }

  public static class PlaceRequiredMissing extends ApiException {
    public PlaceRequiredMissing() {
      super(MemberPoiMessageCode.PLACE_REQUIRED_MISSING);
    }
  }

  public static class InvalidPoiType extends ApiException {
    public InvalidPoiType() {
      super(MemberPoiMessageCode.INVALID_POI_TYPE);
    }
  }

  public static class InvalidMaxResultCount extends ApiException {
    public InvalidMaxResultCount() {
      super(MemberPoiMessageCode.INVALID_MAX_RESULT_COUNT);
    }
  }

  public static class InvalidPage extends ApiException {
    public InvalidPage() {
      super(MemberPoiMessageCode.INVALID_PAGE);
    }
  }

  public static class MemberPoiNotFound extends ApiException {
    public MemberPoiNotFound() {
      super(MemberPoiMessageCode.MEMBER_POI_NOT_FOUND);
    }
  }
}
