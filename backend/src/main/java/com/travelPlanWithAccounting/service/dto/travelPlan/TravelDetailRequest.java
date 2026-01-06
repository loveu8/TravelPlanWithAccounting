package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private boolean timeConflict;
    private String notes;
    @JsonIgnore private UUID createdBy;
}
