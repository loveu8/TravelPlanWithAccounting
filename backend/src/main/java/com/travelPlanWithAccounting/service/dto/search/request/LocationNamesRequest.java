package com.travelPlanWithAccounting.service.dto.search.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "批次查詢 Location 代碼對應名稱請求")
public class LocationNamesRequest {

  @NotEmpty
  @Schema(
      description = "Location 代碼清單",
      example = "[\"TPE\", \"KHH\"]",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> code;
}
