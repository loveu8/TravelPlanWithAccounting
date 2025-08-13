package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TransI18nRequest {
    private UUID id;
    private UUID startDetailId;
    private UUID endDetailId;
    private String infosRaw;
    private String transType;
    private LocalTime transTime;
    private String summary;
    private String notes;
    private UUID createdBy;
}
