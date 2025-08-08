package com.travelPlanWithAccounting.service.dto.search.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 定義請求物件
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 序列化時忽略 null 值的字段
@Schema(description = "搜尋引擎附近地點搜尋請求 DTO")
public class SearchRequest {

  @Schema(description = "國家/城市/區域 代碼", example = "TW", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Schema(
      description = "可選。搜尋附近特定地點。",
      example =
          "restaurant(餐廳),hotel(飯店/住宿),tourist_attraction(景點),其他範例可參閱"
              + " https://developers.google.com/maps/documentation/places/web-service/place-types?hl=zh-tw#facilities",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private List<String> includedTypes; // 可選: 搜尋附近特定地點

  @Schema(description = "可選。返回的最大結果數量，限制在 5-20 之間。", example = "10")
  private Integer maxResultCount; // 可選: 限制 5-20 之間

  @Schema(description = "可選。結果排序偏好設定。", example = "DISTANCE")
  private String rankPreference; // 可選: RELEVANCE 或 DISTANCE
}
