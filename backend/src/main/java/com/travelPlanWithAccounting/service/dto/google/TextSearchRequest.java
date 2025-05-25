package com.travelPlanWithAccounting.service.dto.google;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 序列化時忽略 null 值的字段
@Schema(description = "Places API V1 文字搜尋請求 DTO")
public class TextSearchRequest {
    @Schema(description = "要搜尋的文字查詢，例如 '台北101'、'高雄美食'")
    private String textQuery;

    @Schema(description = "可選。結果偏向的地理區域。如果結果在區域內，則排名更高，但結果不會嚴格限制在此區域內。", example = "{\"circle\": {\"center\": {\"latitude\": 25.0330, \"longitude\": 121.5647}, \"radius\": 10000}}")
    private LocationBias locationBias; // Optional

    @Schema(description = "可選。結果嚴格限制在指定地理區域內。", example = "{\"rectangle\": {\"low\": {\"latitude\": 24.9, \"longitude\": 121.4}, \"high\": {\"latitude\": 25.2, \"longitude\": 121.8}}}")
    private LocationRestriction locationRestriction; // Optional

    @Schema(description = "可選。篩選結果，僅包含評分高於或等於此值的地點。值應在 0.0 到 5.0 之間。", example = "4.0")
    private Double minRating; // Optional

    @Schema(description = "可選。如果為 true，則僅返回目前營業中的地點。", example = "true")
    private Boolean openNow; // Optional

    @Schema(description = "可選。回應結果的語言代碼，例如 'zh-TW', 'en'。", example = "zh-TW")
    private String languageCode; // Optional

    @Schema(description = "可選。用於對結果進行排名或偏好顯示的區域代碼，例如 'TW', 'JP'。", example = "TW")
    private String regionCode; // Optional

    @Schema(description = "可選。返回的最大結果數量，預設和最大值通常是 20。", example = "10")
    private Integer maxResultCount; // Optional

    @Schema(description = "可選。結果排序偏好設定。例如 'RELEVANCE' (相關性，預設) 或 'DISTANCE' (距離)。", example = "RELEVANCE")
    private String rankPreference; // 可選: 例如 "RELEVANCE" (預設) 或 "DISTANCE"

    @Schema(description = "可選。搜尋結果的最高價格級別。", example = "PRICE_LEVEL_HIGH")
    private PriceLevel maxPriceLevel; // 可選: 最高價格級別

    @Schema(description = "可選。搜尋結果的最低價格級別。", example = "PRICE_LEVEL_LOW")
    private PriceLevel minPriceLevel; // 可選: 最低價格級別

    @Schema(description = "可選。包含的地點類型，例如 'restaurant'。", example = "restaurant")
    private String includedType;      // 可選: 包含的地點類型，例如 "restaurant"

    @Schema(description = "可選。包含的主要地點類型，例如 'restaurant'。此為更精確的類型篩選。", example = "restaurant")
    private String includedPrimaryType; // 可選: 包含的主要地點類型

}
