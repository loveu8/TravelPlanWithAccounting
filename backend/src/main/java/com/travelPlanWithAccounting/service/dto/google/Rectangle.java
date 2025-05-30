package com.travelPlanWithAccounting.service.dto.google;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rectangle {

    @Schema(description = "矩形左下角經緯度")
    private LatLng low;
    @Schema(description = "矩形右上角經緯度")
    private LatLng high;
}
