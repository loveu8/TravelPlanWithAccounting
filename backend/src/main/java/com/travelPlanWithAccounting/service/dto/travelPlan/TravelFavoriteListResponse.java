package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.List;

import com.travelPlanWithAccounting.service.dto.system.PageMeta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelFavoriteListResponse {
    private List<TravelFavoriteSummary> list;
    private PageMeta meta;
}
