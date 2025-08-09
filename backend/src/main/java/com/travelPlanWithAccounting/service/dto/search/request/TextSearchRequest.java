package com.travelPlanWithAccounting.service.dto.search.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "搜尋引擎文字搜尋請求 DTO")
public class TextSearchRequest {

  @Schema(description = "要搜尋的文字查詢", example = "台北101", requiredMode = Schema.RequiredMode.REQUIRED)
  private String textQuery;

  @Schema(description = "國家/城市/區域 代碼", example = "TW", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Schema(
      description = "可選。搜尋附近特定地點。",
      example =
          "restaurant(餐廳),hotel(飯店/住宿),tourist_attraction(景點),其他範例可參閱"
              + " https://developers.google.com/maps/documentation/places/web-service/place-types?hl=zh-tw#facilities",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> includedTypes; // 可選: 搜尋附近特定地點

  @Schema(description = "可選。返回的最大結果數量，預設和最大值通常是 20。", example = "10")
  private Integer maxResultCount; // 可選

  @Schema(
      description = "可選。結果排序偏好設定。例如 'RELEVANCE' (相關性，預設) 或 'DISTANCE' (距離)。",
      example = "RELEVANCE")
  private String rankPreference; // 可選: 例如 "RELEVANCE" (預設) 或 "DISTANCE"
}
