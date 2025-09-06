package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TravelDateRequest {
    private UUID travelMainId;
    private LocalDate travelDate;
    @JsonIgnore private UUID createdBy;
}
