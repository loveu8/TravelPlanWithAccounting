package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.List;

/**
 * 封裝人氣行程資料與對應的 meta 內容，方便 service 與 controller 之間傳遞。
 */
public record PopularTravelResult(List<PopularTravelResponse> travels, PopularTravelMeta meta) {

    public PopularTravelResult {
        travels = travels == null ? List.of() : List.copyOf(travels);
    }
}
