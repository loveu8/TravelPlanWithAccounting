package com.travelPlanWithAccounting.service.dto.setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "設定回應 DTO")
public class SettingResponse {

  @Schema(description = "設定類別", example = "POI_TYPE")
  private String category;

  @Schema(description = "設定代碼", example = "P001")
  private String codeName;

  @Schema(description = "固定描述代碼", example = "FOOD_DRINK")
  private String codeDesc;

  @Schema(description = "顯示名稱", example = "Restaurants")
  private String name;

  @Schema(description = "設定描述", example = "Food & Drink")
  private String description;
}
