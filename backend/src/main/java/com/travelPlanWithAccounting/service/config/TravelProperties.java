package com.travelPlanWithAccounting.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "travel")
public class TravelProperties {
    
    /** 行程最大天數（含起訖）。 */
    private int maxDays = 31;

    public int getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }
}
