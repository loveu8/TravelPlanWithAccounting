package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.SearchRequestMessageCode;

public class SearchRequestException extends ApiException {
  public SearchRequestException(SearchRequestMessageCode c) {
    super(c);
  }

  public static class TextQueryRequired extends ApiException {
    public TextQueryRequired() {
      super(SearchRequestMessageCode.TEXT_QUERY_REQUIRED);
    }
  }

  public static class MaxResultRange extends ApiException {
    public MaxResultRange() {
      super(SearchRequestMessageCode.MAX_RESULT_RANGE);
    }
  }

  public static class RankPreferenceError extends ApiException {
    public RankPreferenceError() {
      super(SearchRequestMessageCode.RANK_PREFERENCE_ERR);
    }
  }

  public static class IncludedTypeLimit extends ApiException {
    public IncludedTypeLimit() {
      super(SearchRequestMessageCode.INCLUDED_TYPES_LIMIT);
    }
  }

  /** 找不到地點代碼 */
  public static class NotFound extends ApiException {
    public NotFound() {
      super(SearchRequestMessageCode.LOCATION_NOT_FOUND);
    }
  }

  /** 地點缺少經緯度 */
  public static class LatLonMissing extends ApiException {
    public LatLonMissing() {
      super(SearchRequestMessageCode.LOCATION_LATLON_MISSING);
    }
  }
}
