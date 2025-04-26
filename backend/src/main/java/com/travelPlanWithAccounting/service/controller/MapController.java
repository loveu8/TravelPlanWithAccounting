package com.travelPlanWithAccounting.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.travelPlanWithAccounting.service.entity.MapRequest;
import com.travelPlanWithAccounting.service.service.MapService;
import java.lang.reflect.Type;
import java.util.Map;



@RestController
@Tag(name = "Test", description = "測試用")
@RequestMapping("/api/map")
public class MapController {
    
    @Autowired
    private MapService mapService;

    @PostMapping("/place")
    @Operation(summary = "搜尋地點")
    public Map<String, Object> searchPlace(@RequestBody MapRequest request) throws Exception {
        JsonObject json = mapService.searchPlace(request.getQuery());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    @PostMapping("/geocode")
    @Operation(summary = "地址轉經緯度")
    public Map<String, Object> geocode(@RequestBody MapRequest request) throws Exception {
        JsonObject json = mapService.geocode(request.getAddress());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    @PostMapping("/reverse-geocode")
    @Operation(summary = "經緯度轉地址")
    public Map<String, Object> reverseGeocode(@RequestBody MapRequest request) throws Exception {
        JsonObject json = mapService.reverseGeocode(request.getLat(), request.getLng());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    @PostMapping("/directions")
    @Operation(summary = "路線規劃")
    public Map<String, Object> getDirections(@RequestBody MapRequest request) throws Exception {
        JsonObject json = mapService.getDirections(request.getOrigin(), request.getDestination());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, type); 
    }

    @PostMapping("/distance")
    @Operation(summary = "距離與時間")
    public Map<String, Object> getDistanceMatrix(@RequestBody MapRequest request) throws Exception {
        JsonObject json = mapService.getDistanceMatrix(request.getOrigin(), request.getDestination());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(json, type); 
    }

}