package com.travelPlanWithAccounting.service.dto.google;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Circle {

    private LatLng center; // 圓心經緯度
    private Double radius; // 半徑，單位為公尺 (meters)
}
