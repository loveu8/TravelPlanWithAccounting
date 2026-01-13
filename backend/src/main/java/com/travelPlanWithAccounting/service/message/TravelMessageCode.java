package com.travelPlanWithAccounting.service.message;

import org.springframework.http.HttpStatus;

import com.travelPlanWithAccounting.service.config.MessageSourceHolder;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TravelMessageCode implements MessageCode {
    TRAVEL_MAIN_ID_REQUIRED(HttpStatus.BAD_REQUEST),
    TRAVEL_MEMBER_ID_REQUIRED(HttpStatus.BAD_REQUEST),
    TRAVEL_LIST_PAGE_INVALID(HttpStatus.BAD_REQUEST),
    TRAVEL_LIST_SIZE_INVALID(HttpStatus.BAD_REQUEST),
    TRAVEL_MAIN_NOT_FOUND(HttpStatus.NOT_FOUND),
    TRAVEL_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND),
    TRAVEL_DATE_ID_REQUIRED(HttpStatus.BAD_REQUEST),
    TRAVEL_DATE_NOT_FOUND(HttpStatus.NOT_FOUND),
    TRAVEL_DATE_DELETE_LAST_FORBIDDEN(HttpStatus.CONFLICT),
    TRAVEL_DATE_UNEXPECTED_STATE(HttpStatus.INTERNAL_SERVER_ERROR),
    TRAVEL_DATE_EXCEEDS_MAX_DAYS(HttpStatus.BAD_REQUEST),
    TRAVEL_DATE_RANGE_INVALID(HttpStatus.BAD_REQUEST),
    TRAVEL_DATE_SORT_INVALID(HttpStatus.BAD_REQUEST),
    TRAVEL_DATE_SORT_MISMATCH(HttpStatus.BAD_REQUEST),
    TRAVEL_DETAIL_ID_REQUIRED(HttpStatus.BAD_REQUEST),
    TRAVEL_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND),
    TRAVEL_DETAIL_SAME_DAY_REQUIRED(HttpStatus.BAD_REQUEST),
    TRAVEL_DETAIL_INVALID_TIME(HttpStatus.BAD_REQUEST),
    TRAVEL_DETAIL_TARGET_REQUIRED(HttpStatus.BAD_REQUEST),
    TRAVEL_DETAIL_TIME_CONFLICT(HttpStatus.CONFLICT),
    TRAVEL_DETAILS_NOT_SAME_TRAVEL(HttpStatus.BAD_REQUEST),
    TRAVEL_POI_NOT_FOUND(HttpStatus.NOT_FOUND),
    POPULAR_STRATEGY_INVALID(HttpStatus.BAD_REQUEST),
    POPULAR_MIN_FAVORITES_INVALID(HttpStatus.BAD_REQUEST);


    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage(Object[] args) {
        return MessageSourceHolder.getMessage(name(), args);
    }

    @Override
    public String getMessage() {
        return MessageSourceHolder.getMessage(name());
    }
}
