package com.travelPlanWithAccounting.service.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleApiConfig {

    @Value("${spring.google.apiKey}")
    private String googleApiKey;

    @Value("${google.places-api-v1-base-url}")
    private String placesApiV1BaseUrl;

    @Value("${google.maps-api-base-url}")
    private String mapsApiBaseUrl;

    @Bean
    public HttpClient httpClient() {
        // 配置 HttpClient，例如設置超時時間
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2) // 建議使用 HTTP/2
                .connectTimeout(Duration.ofSeconds(10)) // 連線超時時間
                .build();
    }

    public String getGoogleApiKey() {
        return googleApiKey;
    }

    public String getPlacesApiV1BaseUrl() {
        return placesApiV1BaseUrl;
    }

    public String getMapsApiBaseUrl() {
        return mapsApiBaseUrl;
    }
}
