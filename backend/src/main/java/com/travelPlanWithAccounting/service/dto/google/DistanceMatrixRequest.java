package com.travelPlanWithAccounting.service.dto.google;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Maps API 距離矩陣請求 DTO (POST 方法)")
public class DistanceMatrixRequest {
    @Schema(description = "起點列表，以 '|' 分隔，例如 '台北101|故宮博物院'", requiredMode = Schema.RequiredMode.REQUIRED)
    private String origins;

    @Schema(description = "終點列表，以 '|' 分隔，例如 '西門町|桃園機場'", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destinations;

    @Schema(description = "交通模式 (driving, walking, bicycling, transit)，預設為 'driving'", example = "driving")
    private String mode; // Optional

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String language;
}
