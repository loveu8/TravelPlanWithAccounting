package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.TravelMessageCode;

public class TravelException extends ApiException {
    public TravelException(TravelMessageCode code) {
        super(code);
    }

    public static class TravelMainIdRequired extends ApiException {
        public TravelMainIdRequired() {
            super(TravelMessageCode.TRAVEL_MAIN_ID_REQUIRED);
        }
    }

    public static class TravelMemberIdRequired extends ApiException {
        public TravelMemberIdRequired() {
            super(TravelMessageCode.TRAVEL_MEMBER_ID_REQUIRED);
        }
    }

    public static class TravelListPageInvalid extends ApiException {
        public TravelListPageInvalid(Integer value) {
            super(TravelMessageCode.TRAVEL_LIST_PAGE_INVALID, new Object[] {value});
        }
    }

    public static class TravelListSizeInvalid extends ApiException {
        public TravelListSizeInvalid(Integer value) {
            super(TravelMessageCode.TRAVEL_LIST_SIZE_INVALID, new Object[] {value});
        }
    }

    public static class TravelMainNotFound extends ApiException {
        public TravelMainNotFound() {
            super(TravelMessageCode.TRAVEL_MAIN_NOT_FOUND);
        }
    }

    public static class TravelMemberNotFound extends ApiException {
        public TravelMemberNotFound() {
            super(TravelMessageCode.TRAVEL_MEMBER_NOT_FOUND);
        }
    }

    public static class TravelDateIdRequired extends ApiException {
        public TravelDateIdRequired() {
            super(TravelMessageCode.TRAVEL_DATE_ID_REQUIRED);
        }
    }

    public static class TravelDateNotFound extends ApiException {
        public TravelDateNotFound() {
            super(TravelMessageCode.TRAVEL_DATE_NOT_FOUND);
        }
    }

    public static class TravelDateDeleteLastForbidden extends ApiException {
        public TravelDateDeleteLastForbidden() {
            super(TravelMessageCode.TRAVEL_DATE_DELETE_LAST_FORBIDDEN);
        }
    }

    public static class TravelDateUnexpectedState extends ApiException {
        public TravelDateUnexpectedState() {
            super(TravelMessageCode.TRAVEL_DATE_UNEXPECTED_STATE);
        }
    }

    public static class TravelDetailIdRequired extends ApiException {
        public TravelDetailIdRequired() {
            super(TravelMessageCode.TRAVEL_DETAIL_ID_REQUIRED);
        }
    }

    public static class TravelDetailNotFound extends ApiException {
        public TravelDetailNotFound() {
            super(TravelMessageCode.TRAVEL_DETAIL_NOT_FOUND);
        }
    }

    public static class TravelDetailSameDayRequired extends ApiException {
        public TravelDetailSameDayRequired() {
            super(TravelMessageCode.TRAVEL_DETAIL_SAME_DAY_REQUIRED);
        }
    }

    public static class TravelDetailInvalidTime extends ApiException {
        public TravelDetailInvalidTime() {
            super(TravelMessageCode.TRAVEL_DETAIL_INVALID_TIME);
        }
    }

    public static class TravelDetailTargetRequired extends ApiException {
        public TravelDetailTargetRequired() {
            super(TravelMessageCode.TRAVEL_DETAIL_TARGET_REQUIRED);
        }
    }

    public static class TravelDetailTimeConflict extends ApiException {
        public TravelDetailTimeConflict() {
            super(TravelMessageCode.TRAVEL_DETAIL_TIME_CONFLICT);
        }

        public TravelDetailTimeConflict(Object data) {
            super(TravelMessageCode.TRAVEL_DETAIL_TIME_CONFLICT, data);
        }
    }

    public static class DetailsNotSameTravel extends ApiException {
        public DetailsNotSameTravel() {
            super(TravelMessageCode.TRAVEL_DETAILS_NOT_SAME_TRAVEL);
        }
    }

    public static class PopularStrategyInvalid extends ApiException {
        public PopularStrategyInvalid(String strategy) {
            super(TravelMessageCode.POPULAR_STRATEGY_INVALID, new Object[] {strategy});
        }
    }

    public static class PopularMinFavoritesInvalid extends ApiException {
        public PopularMinFavoritesInvalid(Integer value) {
            super(TravelMessageCode.POPULAR_MIN_FAVORITES_INVALID, new Object[] {value});
        }
    }
}
