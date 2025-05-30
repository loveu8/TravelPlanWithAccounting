package com.travelPlanWithAccounting.service.dto.google;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Places API V1 地點詳細資訊請求 DTO (POST 方法)")
public class PlaceDetailRequestPost {
    @Schema(description = "要查詢地點的唯一識別符 Place ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String placeId; // Required

    @Schema(description = "回應所需的欄位列表，以逗號分隔。例如: 'id', 'displayName', 'formattedAddress'", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> fieldMask; // Required

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String languageCode; // Optional

    @Schema(description = "可選。回應的區域代碼，例如 'TW', 'JP'。", example = "TW")
    private String regionCode; // Optional
}
