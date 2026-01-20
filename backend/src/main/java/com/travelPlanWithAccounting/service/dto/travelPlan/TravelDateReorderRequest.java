package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TravelDateReorderRequest {
    private UUID travelMainId;
    private List<TravelDateSortRequest> orders;
    @JsonIgnore private UUID updatedBy;
}
