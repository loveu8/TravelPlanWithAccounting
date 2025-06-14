package com.travelPlanWithAccounting.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travelPlanWithAccounting.service.dto.google.AutocompleteRequest;
import com.travelPlanWithAccounting.service.dto.google.DirectionsRequest;
import com.travelPlanWithAccounting.service.dto.google.DistanceMatrixRequest;
import com.travelPlanWithAccounting.service.dto.google.GeocodingRequest;
import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.google.PlaceDetailRequestPost;
import com.travelPlanWithAccounting.service.dto.google.TextSearchRequest;
import com.travelPlanWithAccounting.service.service.MapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/api/maps")
@Tag(name = "Google Maps API 服務", description = "提供整合 Google Maps Platform 各項 API 的端點 (所有前端介面均為 POST 請求)。**注意：此服務內部因使用 block() 而變為阻塞式。**")
public class MapController {
    
    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    /**
     * 輔助方法：處理同步 API 呼叫的成功響應
     * @param response JsonNode 格式的 API 響應
     * @return 封裝後的 ResponseEntity
     */
    private ResponseEntity<JsonNode> handleBlockingApiResponse(JsonNode response) {
        if (response != null && !response.isMissingNode()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * 輔助方法：處理同步 API 呼叫中可能發生的錯誤
     * @param e 捕獲到的異常
     * @return 封裝錯誤訊息的 ResponseEntity
     */
    private ResponseEntity<JsonNode> handleBlockingApiError(Exception e) {
        // 使用 JsonNodeFactory 創建錯誤訊息的 JsonNode
        ObjectNode errorNode = JsonNodeFactory.instance.objectNode();

        if (e instanceof IllegalArgumentException) {
            errorNode = errorNode.put("error", "請求參數錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorNode);
        }
        // 雖然不再使用 WebClient，但如果底層還有其他可能的錯誤，可以考慮其類型
        // 這裡將 WebClientResponseException 替換為更通用的 RuntimeException 處理
        else if (e instanceof RuntimeException && e.getMessage().startsWith("Google API Error:")) {
            // 從 Service 層拋出的特定 Google API 錯誤
            errorNode = errorNode.put("error", e.getMessage());
            // 嘗試從訊息中提取狀態碼，如果可以
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorNode); // 預設 500
        }
        else {
            errorNode = errorNode.put("error", "內部服務錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorNode);
        }
    }


    @Operation(summary = "文字搜尋 (Places API V1)", description = "根據文字查詢獲取地點資訊，例如景點、餐廳等。請提供 FieldMask 以指定回應欄位。")
    // 移除 @ApiResponse
    @PostMapping("/places/v1/searchText")
    public ResponseEntity<JsonNode> searchText(
            // 使用 Spring 的 @RequestBody，只保留 required 屬性
            @RequestBody(required = true)
            TextSearchRequest request,
            // 移除 @io.swagger.v3.oas.annotations.Parameter
            @RequestParam List<String> fieldMask) {
        try {
            JsonNode response = mapService.searchText(request, fieldMask);
            return handleBlockingApiResponse(response);
        } catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

    @Operation(summary = "附近地點搜尋 (Places API V1)", description = "根據經緯度、半徑和其他條件搜尋附近的地點。請提供 FieldMask 以指定回應欄位。")
    // 移除 @ApiResponse
    @PostMapping("/places/v1/searchNearby")
    public ResponseEntity<JsonNode> searchNearby(
            @RequestBody(required = true)
            NearbySearchRequest request,
            @RequestParam List<String> fieldMask) {
        try {
            JsonNode response = mapService.searchNearby(request, fieldMask);
            return handleBlockingApiResponse(response);
        } catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

    @Operation(summary = "地點自動完成 (Places API V1)", description = "提供文字輸入時的自動完成建議。")
    // 移除 @ApiResponse
    @PostMapping("/places/v1/autocomplete")
    public ResponseEntity<JsonNode> autocomplete(
            @RequestBody(required = true)
            AutocompleteRequest request) {
        try {
            JsonNode response = mapService.autocomplete(request);
            return handleBlockingApiResponse(response);
        }
        catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

    @Operation(summary = "地點詳細資訊 (Places API V1)",
               description = "根據 Place ID 獲取地點的詳細資訊。前端透過 POST 傳遞 Place ID 和 FieldMask，但內部實際呼叫 Google API 仍為 GET 請求。")
    // 移除 @ApiResponse
    @PostMapping("/places/v1/details")
    public ResponseEntity<JsonNode> getPlaceDetails(
            @RequestBody(required = true)
            PlaceDetailRequestPost request) {
        try {
            JsonNode response = mapService.getPlaceDetails(request);
            return handleBlockingApiResponse(response);
        } catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

    @Operation(summary = "地理編碼 (Geocoding API)",
               description = "將地址轉換為經緯度，或將經緯度轉換為地址。前端透過 POST 傳遞請求體，但內部實際呼叫 Google API 仍為 GET 請求。")
    // 移除 @ApiResponse
    @PostMapping("/geocode")
    public ResponseEntity<JsonNode> geocode(
            @RequestBody(required = true)
            GeocodingRequest request) {
        try {
            JsonNode response = mapService.geocode(request);
            return handleBlockingApiResponse(response);
        } catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

    @Operation(summary = "路線規劃 (Directions API)",
               description = "計算兩點之間的路線，並提供詳細的步驟。前端透過 POST 傳遞請求體，但內部實際呼叫 Google API 仍為 GET 請求。")
    // 移除 @ApiResponse
    @PostMapping("/directions")
    public ResponseEntity<JsonNode> getDirections(
            @RequestBody(required = true)
            DirectionsRequest request) {
        try {
            JsonNode response = mapService.getDirections(request);
            return handleBlockingApiResponse(response);
        } catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

    @Operation(summary = "距離矩陣 (Distance Matrix API)",
               description = "計算多個起點與多個終點之間的距離與預計時間。前端透過 POST 傳遞請求體，但內部實際呼叫 Google API 仍為 GET 請求。")
    // 移除 @ApiResponse
    @PostMapping("/distanceMatrix")
    public ResponseEntity<JsonNode> getDistanceMatrix(
            @RequestBody(required = true)
            DistanceMatrixRequest request) {
        try {
            JsonNode response = mapService.getDistanceMatrix(request);
            return handleBlockingApiResponse(response);
        } catch (Exception e) {
            return handleBlockingApiError(e);
        }
    }

}