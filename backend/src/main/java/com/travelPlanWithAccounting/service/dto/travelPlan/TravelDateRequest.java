package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TravelDateRequest {
    private UUID travelMainId;
    @JsonIgnore private UUID createdBy;
}
