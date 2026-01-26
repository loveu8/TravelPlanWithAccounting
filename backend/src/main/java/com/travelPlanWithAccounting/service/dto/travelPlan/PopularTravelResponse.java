package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 簡要描述人氣行程內容，供前端列表顯示使用。
 */
public record PopularTravelResponse(
    UUID travelMainId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    List<String> visitPlace,
    long favoritesCount,
    boolean isPrivate,
    boolean isFavorited,
    String creator,
    String locationName,
    String imgUrl
) {

}
