package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import lombok.Data;

@Data
public class TravelCopyRequest {
    private UUID id; // 原始行程主表 ID
    private UUID memberId; // 新擁有者 ID
    private UUID createdBy; // 複製操作者 ID
}
