package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TravelDetailRequest {
    private UUID id;
    private UUID travelMainId;
    private UUID travelDateId;
    private UUID poiId;
    private Integer sort;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private UUID createdBy;
}
