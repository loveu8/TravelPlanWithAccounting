package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TransI18nRequest {
    private UUID id;
    private String langType;
    private UUID startDetailId;
    private UUID endDetailId;
    private String infosRaw;
    private String transType;
    private LocalTime transTime;
    private String summary;
    private String notes;
    @JsonIgnore private UUID createdBy;
}
