package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelDetailCreateDTO {
    private String type; // 例如：'TRS', 'ATT', 'ACM', 'EAT'
    private LocalTime startTime;
    private LocalTime endTime;
    private String googleMapInfo; // JSON 字符串，用於詳細地圖信息（Places API 等）
    private String notes;
}
