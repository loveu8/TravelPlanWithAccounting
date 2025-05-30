package com.travelPlanWithAccounting.service.dto.google;

import com.fasterxml.jackson.annotation.JsonInclude; // 引入這個註解

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 序列化時忽略 null 值的字段
@Schema(description = "Places API V1 地點自動完成請求 DTO")
public class AutocompleteRequest {
    @Schema(description = "使用者輸入的文字，用於生成建議。", requiredMode = Schema.RequiredMode.REQUIRED)
    private String input; // Required

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String languageCode; // Optional

    @Schema(description = "可選。用於對結果進行排名或偏好顯示的區域代碼，例如 'TW', 'JP'。", example = "TW")
    private String regionCode; // Optional

    @Schema(description = "可選。結果偏向的地理區域。", example = "{\"circle\": {\"center\": {\"latitude\": 25.0330, \"longitude\": 121.5647}, \"radius\": 10000}}")
    private LocationBias locationBias; // Optional

    @Schema(description = "可選。結果嚴格限制在指定地理區域內。", example = "{\"rectangle\": {\"low\": {\"latitude\": 24.9, \"longitude\": 121.4}, \"high\": {\"latitude\": 25.2, \"longitude\": 121.8}}}")
    private LocationRestriction locationRestriction; // Optional

}
