package com.travelPlanWithAccounting.service.dto.google;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略 null 字段，避免不必要的傳輸
public class ComputeRoutesRequest {
    private Place origin;
    private Place destination;
    private List<Waypoint> intermediates; // 途經點
    private String travelMode = "DRIVE"; // DRIVE, WALKING, BICYCLE, TRANSIT
    private String routingPreference = "TRAFFIC_AWARE_OPTIMAL"; // TRAFFIC_AWARE_OPTIMAL, TRAFFIC_UNAWARE, AVOID_FERRIES, AVOID_TOLLS
    private String units = "METRIC"; // METRIC, IMPERIAL

    public static class Place {
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
