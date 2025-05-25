package com.travelPlanWithAccounting.service.dto.google;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Maps API 地理編碼請求 DTO (POST 方法)")
public class GeocodingRequest {
    @Schema(description = "要進行地理編碼的地址字串 (與經緯度擇一提供)", example = "台北車站")
    private String address;

    @Schema(description = "要進行反向地理編碼的經緯度字串 (與地址擇一提供)，例如 '25.0330,121.5647'", example = "25.0478,121.5172")
    private String latLng;

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String language;
}
