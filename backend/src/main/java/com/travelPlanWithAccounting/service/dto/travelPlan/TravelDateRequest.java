package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class TravelDateRequest {
    private UUID travelMainId;
    private LocalDate travelDate;
    private UUID createdBy;
}
