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

  @Schema(description = "設定類別", example = "LANG_TYPE")
  private String category;

  @Schema(description = "設定名稱", example = "繁體中文")
  private String name;

  @Schema(description = "設定代碼", example = "zh-TW")
  private String codeName;

  @Schema(description = "設定描述", example = "繁體中文 (台灣)")
  private String codeDesc;
}
