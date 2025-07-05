package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TravelDetailRequest {
    private UUID id;
    private UUID travelMainId;
    private UUID travelDateId;
    private String type;
    private LocalTime startTime;
    private LocalTime endTime;
    private String googleMapInfo;
    private String notes;
    private UUID createdBy;
}
