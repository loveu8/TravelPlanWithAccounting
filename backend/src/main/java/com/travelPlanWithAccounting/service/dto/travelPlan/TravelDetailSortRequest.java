package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import lombok.Data;

@Data
public class TravelDetailSortRequest {
    private UUID id;
    private Integer sort;
}
