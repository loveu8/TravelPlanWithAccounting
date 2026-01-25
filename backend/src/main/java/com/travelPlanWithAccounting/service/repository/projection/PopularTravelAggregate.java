package com.travelPlanWithAccounting.service.repository.projection;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 將旅遊主行程與收藏數聚合後的結果投影，供人氣行程邏輯使用。
 */
public record PopularTravelAggregate(
    UUID travelMainId,
    UUID memberId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    List<String> visitPlace,
    Boolean isPrivate,
    Instant createdAt,
    long favoritesCount
) {

}
