package com.travelPlanWithAccounting.service.dto.google;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LatLng {
    private Double latitude;  // 緯度
    private Double longitude; // 經度
}
