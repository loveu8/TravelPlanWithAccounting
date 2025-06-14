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
@Schema(description = "Places API V1 附近地點搜尋請求 DTO")
public class NearbySearchRequest {

    @Schema(description = "搜尋半徑 (公尺)，最大為 50000", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer radius; // Required

    @Schema(description = "可選。篩選結果，僅包含評分高於或等於此值的地點。值應在 0.0 到 5.0 之間。", example = "4.0")
    private Double minRating; // Optional

    @Schema(description = "可選。如果為 true，則僅返回目前營業中的地點。", example = "true")
    private Boolean openNow; // Optional

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String languageCode; // Optional

    @Schema(description = "可選。返回的最大結果數量，預設和最大值通常是 20。", example = "10")
    private Integer maxResultCount; // Optional

    @Schema(description = "可選。結果排序偏好設定。例如 'DISTANCE' (依距離排序)。", example = "DISTANCE")
    private String rankPreference; // 可選: 例如 "DISTANCE"

    @Schema(description = "可選。結果嚴格限制在指定地理區域內。", example = "{\"rectangle\": {\"low\": {\"latitude\": 24.9, \"longitude\": 121.4}, \"high\": {\"latitude\": 25.2, \"longitude\": 121.8}}}")
    private LocationRestriction locationRestriction;

    @Schema(description = "可選。限制搜尋結果的區域代碼，例如 'TW'。", example = "TW")
    private String regionCode;   // 可選

    @Schema(description = "可選。搜尋結果的最高價格級別。", example = "PRICE_LEVEL_HIGH")
    private PriceLevel maxPriceLevel; // 可選: 最高價格級別

    @Schema(description = "可選。搜尋結果的最低價格級別。", example = "PRICE_LEVEL_LOW")
    private PriceLevel minPriceLevel; // 可選: 最低價格級別

}