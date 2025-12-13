package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 簡要描述人氣行程內容，供前端列表顯示使用。
 */
public record PopularTravelResponse(
    UUID travelMainId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String visitPlace,
    long favoritesCount,
    boolean isPrivate,
    boolean isFavorited
) {

}
