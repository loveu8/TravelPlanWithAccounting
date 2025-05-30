package com.travelPlanWithAccounting.service.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.config.GoogleApiConfig;
import com.travelPlanWithAccounting.service.dto.google.AutocompleteRequest;
import com.travelPlanWithAccounting.service.dto.google.DirectionsRequest;
import com.travelPlanWithAccounting.service.dto.google.DistanceMatrixRequest;
import com.travelPlanWithAccounting.service.dto.google.GeocodingRequest;
import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.google.PlaceDetailRequestPost;
import com.travelPlanWithAccounting.service.dto.google.TextSearchRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MapService {

    private final HttpClient httpClient; // 注入 HttpClient
    private final GoogleApiConfig googleApiConfig;
    private final ObjectMapper objectMapper;


    public MapService(HttpClient httpClient, GoogleApiConfig googleApiConfig,
                            ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.googleApiConfig = googleApiConfig;
        this.objectMapper = objectMapper;
    }


    /**
     * 輔助方法：執行 HTTP 請求並處理響應
     */
    private JsonNode executeHttpRequest(String apiType, String query, HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                //logApiCall(apiType, query, jsonResponse);
                return jsonResponse;
            } else {
                log.error("Error calling {} API: Status: {}, Response: {}", apiType, response.statusCode(), response.body());
                /* 
                JsonNode errorNode = objectMapper.createObjectNode()
                        .put("status", response.statusCode())
                        .put("error_message", response.body());
                logApiCall(apiType, query, errorNode);
                */
                throw new RuntimeException("Google API Error: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Network or parsing error calling {} API: {}", apiType, e.getMessage());
            /* 
            JsonNode errorNode = objectMapper.createObjectNode()
                    .put("error_message", "Network or processing error: " + e.getMessage());
            logApiCall(apiType, query, errorNode);
            */
            throw new RuntimeException("Network or processing error during Google API call: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling {} API: {}", apiType, e.getMessage());
            /*
            JsonNode errorNode = objectMapper.createObjectNode()
                    .put("error_message", "Unexpected error: " + e.getMessage());
            logApiCall(apiType, query, errorNode);
            */
            throw new RuntimeException("Unexpected error during Google API call: " + e.getMessage(), e);
        }
    }


    /**
     * 1. Text Search (Places API v1)
     * @param request TextSearchRequest 物件
     * @param fieldMask 響應字段掩碼
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode searchText(TextSearchRequest request, List<String> fieldMask) {
        String url = googleApiConfig.getPlacesApiV1BaseUrl() + "places:searchText";
        log.info("Calling Text Search API (POST): {}", url);
        try {
            String requestBody = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Goog-Api-Key", googleApiConfig.getGoogleApiKey())
                    .header("X-Goog-FieldMask", String.join(",", fieldMask))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            return executeHttpRequest("TextSearch", request.getTextQuery(), httpRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error preparing Text Search request: " + e.getMessage(), e);
        }
    }

    /**
     * 2. Nearby Search (Places API v1)
     * @param request NearbySearchRequest 物件
     * @param fieldMask 響應字段掩碼
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode searchNearby(NearbySearchRequest request, List<String> fieldMask) {
        String url = googleApiConfig.getPlacesApiV1BaseUrl() + "places:searchNearby";
        log.info("Calling Nearby Search API (POST): {}", url);
        try {
            String requestBody = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Goog-Api-Key", googleApiConfig.getGoogleApiKey())
                    .header("X-Goog-FieldMask", String.join(",", fieldMask))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            return executeHttpRequest("NearbySearch", request.getLocationRestriction().getCircle().getCenter().getLatitude() + "," + request.getLocationRestriction().getCircle().getCenter().getLongitude(), httpRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error preparing Nearby Search request: " + e.getMessage(), e);
        }
    }

    /**
     * 3. Autocomplete (Places API v1)
     * @param request AutocompleteRequest 物件
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode autocomplete(AutocompleteRequest request) {
        String url = googleApiConfig.getPlacesApiV1BaseUrl() + "places:autocomplete";
        log.info("Calling Autocomplete API (POST): {}", url);
        try {
            String requestBody = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Goog-Api-Key", googleApiConfig.getGoogleApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            return executeHttpRequest("Autocomplete", request.getInput(), httpRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error preparing Autocomplete request: " + e.getMessage(), e);
        }
    }

    /**
     * 4. Place Details (Places API v1)
     * 本地端為 POST 請求，但 Google API 實際仍是 GET 請求，參數透過 URL Query 和 Header 傳遞。
     * @param request PlaceDetailRequestPost 物件
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode getPlaceDetails(PlaceDetailRequestPost request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(googleApiConfig.getPlacesApiV1BaseUrl() + "places/" + request.getPlaceId());
        if (request.getLanguageCode() != null) uriBuilder.queryParam("languageCode", request.getLanguageCode());
        if (request.getRegionCode() != null) uriBuilder.queryParam("regionCode", request.getRegionCode());

        String url = uriBuilder.build().toUriString();
        log.info("Calling Place Details API (internally GET): {}", url);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Goog-Api-Key", googleApiConfig.getGoogleApiKey())
                .header("X-Goog-FieldMask", String.join(",", request.getFieldMask()))
                .GET() // 內部實際呼叫 Google API 仍然使用 GET
                .build();
        return executeHttpRequest("PlaceDetails", request.getPlaceId(), httpRequest);
    }

    /**
     * 5. Geocoding (Maps Geocoding API)
     * 本地端為 POST 請求，但 Google API 實際仍是 GET 請求，參數透過 URL Query 傳遞。
     * @param request GeocodingRequest 物件
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode geocode(GeocodingRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(googleApiConfig.getMapsApiBaseUrl() + "geocode/json");
        uriBuilder.queryParam("key", googleApiConfig.getGoogleApiKey());

        String queryParamValue = null;
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            uriBuilder.queryParam("address", URLEncoder.encode(request.getAddress(), StandardCharsets.UTF_8));
            queryParamValue = request.getAddress();
        } else if (request.getLatLng() != null && !request.getLatLng().isEmpty()) {
            uriBuilder.queryParam("latlng", request.getLatLng());
            queryParamValue = request.getLatLng();
        } else {
            throw new IllegalArgumentException("Either address or latLng must be provided for Geocoding API.");
        }
        if (request.getLanguage() != null) uriBuilder.queryParam("language", request.getLanguage());

        String url = uriBuilder.build().toUriString();
        log.info("Calling Geocoding API (internally GET): {}", url);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET() // 內部實際呼叫 Google API 仍然使用 GET
                .build();
        return executeHttpRequest("Geocoding", queryParamValue, httpRequest);
    }

    /**
     * 6. Directions (Maps Directions API)
     * 本地端為 POST 請求，但 Google API 實際仍是 GET 請求，參數透過 URL Query 傳遞。
     * @param request DirectionsRequest 物件
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode getDirections(DirectionsRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(googleApiConfig.getMapsApiBaseUrl() + "directions/json");
        uriBuilder.queryParam("origin", URLEncoder.encode(request.getOrigin(), StandardCharsets.UTF_8));
        uriBuilder.queryParam("destination", URLEncoder.encode(request.getDestination(), StandardCharsets.UTF_8));
        uriBuilder.queryParam("key", googleApiConfig.getGoogleApiKey());
        if (request.getMode() != null && !request.getMode().isEmpty()) uriBuilder.queryParam("mode", request.getMode());
        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) uriBuilder.queryParam("language", request.getLanguage());

        String url = uriBuilder.build().toUriString();
        log.info("Calling Directions API (internally GET): {}", url);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET() // 內部實際呼叫 Google API 仍然使用 GET
                .build();
        return executeHttpRequest("Directions", request.getOrigin() + "->" + request.getDestination(), httpRequest);
    }

    /**
     * 7. Distance Matrix (Maps Distance Matrix API)
     * 本地端為 POST 請求，但 Google API 實際仍是 GET 請求，參數透過 URL Query 傳遞。
     * @param request DistanceMatrixRequest 物件
     * @return JsonNode 格式的 API 響應
     */
    public JsonNode getDistanceMatrix(DistanceMatrixRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(googleApiConfig.getMapsApiBaseUrl() + "distancematrix/json");
        uriBuilder.queryParam("origins", URLEncoder.encode(request.getOrigins(), StandardCharsets.UTF_8));
        uriBuilder.queryParam("destinations", URLEncoder.encode(request.getDestinations(), StandardCharsets.UTF_8));
        uriBuilder.queryParam("key", googleApiConfig.getGoogleApiKey());
        if (request.getMode() != null && !request.getMode().isEmpty()) uriBuilder.queryParam("mode", request.getMode());
        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) uriBuilder.queryParam("language", request.getLanguage());

        String url = uriBuilder.build().toUriString();
        log.info("Calling Distance Matrix API (internally GET): {}", url);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET() // 內部實際呼叫 Google API 仍然使用 GET
                .build();
        return executeHttpRequest("DistanceMatrix", request.getOrigins() + " to " + request.getDestinations(), httpRequest);
    }


    
    
}