package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class TravelMainRequest {
    private UUID id;
    private UUID memberId;
    private Boolean isPrivate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String notes;
    private String visitPlace; // JSONB content as String
    private UUID createdBy; // 誰創建的，通常從認證資訊中獲取
}
