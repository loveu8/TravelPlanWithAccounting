package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import lombok.Data;

@Data
public class TravelDateSortRequest {
    private UUID travelDateId;
    private Integer sort;
}
