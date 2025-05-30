package com.travelPlanWithAccounting.service.dto.google;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Maps API 路線規劃請求 DTO (POST 方法)")
public class DirectionsRequest {
    @Schema(description = "起點地址或經緯度，例如 '台北101' 或 '25.0330,121.5647'", requiredMode = Schema.RequiredMode.REQUIRED)
    private String origin;

    @Schema(description = "終點地址或經緯度，例如 '桃園國際機場' 或 '25.0777,121.2327'", requiredMode = Schema.RequiredMode.REQUIRED)
    private String destination;

    @Schema(description = "交通模式 (driving, walking, bicycling, transit)，預設為 'driving'", example = "driving")
    private String mode; // Optional

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String language;
}
