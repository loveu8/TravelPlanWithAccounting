package com.travelPlanWithAccounting.service.dto.google;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PriceLevel {

    PRICE_LEVEL_UNSPECIFIED("PRICE_LEVEL_UNSPECIFIED"),
    FREE("FREE"),
    INEXPENSIVE("INEXPENSIVE"),
    MODERATE("MODERATE"),
    EXPENSIVE("EXPENSIVE"),
    VERY_EXPENSIVE("VERY_EXPENSIVE");

    private final String value;

    PriceLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
