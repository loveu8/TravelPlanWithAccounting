package com.travelPlanWithAccounting.service.validator;

public record DateRangeRule(
    String startDateField,
    String endDateField,
    boolean allowNull,
    Integer minDays,
    Integer maxDays) {}
