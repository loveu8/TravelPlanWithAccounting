package com.travelPlanWithAccounting.service.repository.projection;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 將旅遊主行程與收藏數聚合後的結果投影，供人氣行程邏輯使用。
 */
public record PopularTravelAggregate(
    UUID travelMainId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String visitPlace,
    Boolean isPrivate,
    Instant createdAt,
    long favoritesCount
) {

}
