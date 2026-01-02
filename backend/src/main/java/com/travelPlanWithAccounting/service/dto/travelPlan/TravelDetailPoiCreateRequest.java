package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TravelDetailPoiCreateRequest {
    private UUID travelMainId;
    private UUID travelDateId;
    private UUID poiId;
    @JsonIgnore private UUID createdBy;
}
