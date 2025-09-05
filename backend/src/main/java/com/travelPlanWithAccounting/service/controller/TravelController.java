package com.travelPlanWithAccounting.service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.UuidRequest;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelCopyRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDateRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailSortRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainResponse;
import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelDetail;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.service.TravelService;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Travel", description = "旅遊行程")
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @PostMapping("/createTravelMain")
    public RestResponse<Object, Object> createTravelMain(@RequestBody TravelMainRequest request) {
        TravelMainResponse newTravelMainResponse = travelService.createTravelMain(request);
        return RestResponseUtils.success(newTravelMainResponse);
    }

    @PostMapping("/updateTravelMain")
    public RestResponse<Object, Object> updateTravelMain(@RequestBody TravelMainRequest request) {
        TravelMain updatedTravelMain = travelService.updateTravelMain(request);
        return RestResponseUtils.success(updatedTravelMain);
    }

    @PostMapping("/getTravelMain")
    public RestResponse<Object, Object> getTravelMain(@RequestBody UuidRequest request) {
        TravelMain travelMain = travelService.getTravelMainById(request.getId());
        return RestResponseUtils.success(travelMain);
    }

    @PostMapping("/getTravelMainsByMemberId")
    public RestResponse<Object, Object> getTravelMainsByMemberId(@RequestBody UuidRequest request) {
        List<TravelMain> travelMains = travelService.getTravelMainsByMemberId(request.getId());
        return RestResponseUtils.success(travelMains);
    }

    @PostMapping("/copyTravelPlan")
    public RestResponse<Object, Object> copyTravelPlan(@RequestBody TravelCopyRequest request) {
        return RestResponseUtils.success(travelService.copyTravelPlan(request));
    }

    @PostMapping("/addTravelDate")
    public RestResponse<Object, Object> addTravelDate(@RequestBody TravelDateRequest request) {
        TravelDate nextTravelDate =
            travelService.addTravelDate(request.getTravelMainId(), request.getTravelDate(), request.getCreatedBy());
        return RestResponseUtils.success(nextTravelDate);
    }

    @PostMapping("/deleteTravelDate")
    public RestResponse<Object, Object> deleteTravelDate(@RequestBody UuidRequest request) {
        travelService.deleteTravelDate(request.getId());
        return RestResponseUtils.success(null);
    }

    @PostMapping("/getTravelDate")
    public RestResponse<Object, Object> getTravelDate(@RequestBody UuidRequest request) {
        return RestResponseUtils.success(travelService.getTravelDateById(request.getId()));
    }

    @PostMapping("/getTravelDatesByTravelMainId")
    public RestResponse<Object, Object> getTravelDatesByTravelMainId(@RequestBody UuidRequest request) {
        List<TravelDate> travelDates = travelService.getTravelDatesByTravelMainId(request.getId());
        return RestResponseUtils.success(travelDates);
    }

    @PostMapping("/createTravelDetail")
    public RestResponse<Object, Object> createTravelDetail(@RequestBody TravelDetailRequest request) {
        TravelDetail newTravelDetail = travelService.createTravelDetail(request);
        return RestResponseUtils.success(newTravelDetail);
    }

    @PostMapping("/updateTravelDetail")
    public RestResponse<Object, Object> updateTravelDetail(@RequestBody TravelDetailRequest request) {
        TravelDetail updatedTravelDetail = travelService.updateTravelDetail(request);
        return RestResponseUtils.success(updatedTravelDetail);
    }

    @PostMapping("/reorderTravelDetail")
    public RestResponse<Object, Object> reorderTravelDetail(
        @RequestBody List<TravelDetailSortRequest> requests) {
        travelService.reorderTravelDetails(requests);
        return RestResponseUtils.success(null);
    }

    @PostMapping("/getTravelDetail")
    public RestResponse<Object, Object> getTravelDetail(@RequestBody UuidRequest request) {
        return RestResponseUtils.success(travelService.getTravelDetailById(request.getId()));
    }

    @PostMapping("/getTravelDetailsByTravelDateId")
    public RestResponse<Object, Object> getTravelDetailsByTravelDateId(@RequestBody UuidRequest request) {
        List<TravelDetail> travelDetails = travelService.getTravelDetailsByTravelDateId(request.getId());
        return RestResponseUtils.success(travelDetails);
    }

    @PostMapping("/checkTimeConflict")
    public RestResponse<Object, Object> checkTimeConflict(@RequestBody TravelDetailRequest request) {
        return RestResponseUtils.success(travelService.checkTimeConflict(request));
    }

    @PostMapping("/deleteTravelDetail")
    public RestResponse<Object, Object> deleteTravelDetailById(@RequestBody UuidRequest request) {
        travelService.deleteTravelDetailById(request.getId());
        return RestResponseUtils.success(null);
    }
}
