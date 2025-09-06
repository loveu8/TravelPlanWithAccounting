package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TravelCopyRequest {
    private UUID id; // 原始行程主表 ID
    @JsonIgnore private UUID memberId; // 新擁有者 ID
    @JsonIgnore private UUID createdBy; // 複製操作者 ID
}
