package com.travelPlanWithAccounting.service.dto.travelPlan;

/**
 * 回傳人氣行程時的輔助資訊，用於呈現實際採用的策略與候選數量。
 */
public record PopularTravelMeta(
    String strategy,
    Integer minFavorites,
    int totalCandidates
) {

}
