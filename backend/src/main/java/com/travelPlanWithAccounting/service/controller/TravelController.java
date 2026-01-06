package com.travelPlanWithAccounting.service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.UuidRequest;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.PopularTravelResult;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelCopyRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDateRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailPoiCreateRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelDetailSortRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelEditPermissionRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelEditPermissionResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainListRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainListResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelMainResponse;
import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelDetail;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.security.AccessTokenRequired;
import com.travelPlanWithAccounting.service.security.OptionalAccessToken;
import com.travelPlanWithAccounting.service.service.TravelPermissionService;
import com.travelPlanWithAccounting.service.service.TravelPopularityService;
import com.travelPlanWithAccounting.service.service.TravelService;
import com.travelPlanWithAccounting.service.util.RestResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Travel", description = "旅遊行程")
@RequestMapping("/api/travels")
public class TravelController {

    private final TravelService travelService;
    private final AuthContext authContext;
    private final TravelPermissionService travelPermissionService;
    private final TravelPopularityService travelPopularityService;

    public TravelController(TravelService travelService, AuthContext authContext, TravelPermissionService travelPermissionService,TravelPopularityService travelPopularityService) {
        this.travelService = travelService;
        this.authContext = authContext;
        this.travelPermissionService = travelPermissionService;
        this.travelPopularityService = travelPopularityService;
    }

    @PostMapping("/createTravelMain")
    @AccessTokenRequired
    public RestResponse<Object, Object> createTravelMain(@RequestBody TravelMainRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setMemberId(memberId);
        request.setCreatedBy(memberId);
        TravelMainResponse newTravelMainResponse = travelService.createTravelMain(request);
        return RestResponseUtils.success(newTravelMainResponse);
    }

    @PostMapping("/updateTravelMain")
    @AccessTokenRequired
    public RestResponse<Object, Object> updateTravelMain(@RequestBody TravelMainRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setMemberId(memberId);
        request.setCreatedBy(memberId);
        TravelMain updatedTravelMain = travelService.updateTravelMain(request);
        return RestResponseUtils.success(updatedTravelMain);
    }

    @PostMapping("/getTravelMain")
    public RestResponse<Object, Object> getTravelMain(@RequestBody UuidRequest request) {
        TravelMain travelMain = travelService.getTravelMainById(request.getId());
        return RestResponseUtils.success(travelMain);
    }

    @PostMapping("/checkEditPermission")
    @OptionalAccessToken
    @Operation(summary = "判斷行程是否可編輯")
    public RestResponse<Object, Object> checkEditPermission(
        @Valid @RequestBody TravelEditPermissionRequest request
    ) {
        UUID memberId = authContext.getCurrentMemberId();
        TravelEditPermissionResponse response = travelPermissionService.checkEditPermission(request, memberId);
        return RestResponseUtils.success(response);
    }

    @PostMapping("/getTravelMainsByMemberId")
    @AccessTokenRequired
    public RestResponse<Object, Object> getTravelMainsByMemberId() {
        UUID memberId = authContext.getCurrentMemberId();
        List<TravelMain> travelMains = travelService.getTravelMainsByMemberId(memberId);
        return RestResponseUtils.success(travelMains);
    }

    @PostMapping("/listTravelMains")
    @AccessTokenRequired
    @Operation(summary = "分頁取得會員主行程列表")
    public TravelMainListResponse listTravelMains(
        @RequestBody(required = false) TravelMainListRequest request
    ) {
        UUID memberId = authContext.getCurrentMemberId();
        return travelService.listTravelMains(memberId, request);
    }

    @PostMapping("/copyTravelPlan")
    @AccessTokenRequired
    public RestResponse<Object, Object> copyTravelPlan(@RequestBody TravelCopyRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setMemberId(memberId);
        request.setCreatedBy(memberId);
        return RestResponseUtils.success(travelService.copyTravelPlan(request));
    }

    @PostMapping("/addTravelDate")
    @AccessTokenRequired
    public RestResponse<Object, Object> addTravelDate(@RequestBody TravelDateRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setCreatedBy(memberId);
        TravelDate nextTravelDate =
            travelService.addTravelDate(request.getTravelMainId(), request.getCreatedBy());
        return RestResponseUtils.success(nextTravelDate);
    }

    @PostMapping("/deleteTravelDate")
    @AccessTokenRequired
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
    @AccessTokenRequired
    public RestResponse<Object, Object> createTravelDetail(@RequestBody TravelDetailRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setCreatedBy(memberId);
        TravelDetail newTravelDetail = travelService.createTravelDetail(request);
        return RestResponseUtils.success(newTravelDetail);
    }

    @PostMapping("/createTravelDetailByPoi")
    @AccessTokenRequired
    @Operation(summary = "建立景點行程明細")
    public RestResponse<Object, Object> createTravelDetailByPoi(
        @RequestBody TravelDetailPoiCreateRequest request
    ) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setCreatedBy(memberId);
        TravelDetail newTravelDetail = travelService.createTravelDetailByPoi(request);
        return RestResponseUtils.success(newTravelDetail);
    }

    @PostMapping("/updateTravelDetail")
    @AccessTokenRequired
    public RestResponse<Object, Object> updateTravelDetail(@RequestBody TravelDetailRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setCreatedBy(memberId);
        TravelDetail updatedTravelDetail = travelService.updateTravelDetail(request);
        return RestResponseUtils.success(updatedTravelDetail);
    }

    @PostMapping("/reorderTravelDetail")
    @AccessTokenRequired
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
    @AccessTokenRequired
    public RestResponse<Object, Object> checkTimeConflict(@RequestBody TravelDetailRequest request) {
        UUID memberId = authContext.getCurrentMemberId();
        request.setCreatedBy(memberId);
        return RestResponseUtils.success(travelService.checkTimeConflict(request));
    }

    @PostMapping("/deleteTravelDetail")
    @AccessTokenRequired
    public RestResponse<Object, Object> deleteTravelDetailById(@RequestBody UuidRequest request) {
        travelService.deleteTravelDetailById(request.getId());
        return RestResponseUtils.success(null);
    }

    @GetMapping("/popular")
    @Operation(summary = "取得人氣行程", description = "依據收藏數回傳最多四筆公開人氣行程")
    @OptionalAccessToken
    public RestResponse<Object, Object> getPopularTravels(
        @RequestParam(name = "strategy", defaultValue = "top") String strategy,
        @RequestParam(name = "minFavorites", required = false) Integer minFavorites
    ) {
        UUID memberId = authContext.getCurrentMemberId();
        PopularTravelResult result = travelPopularityService.getPopularTravels(strategy, minFavorites, memberId);
        return RestResponseUtils.successWithMeta(result.travels(), result.meta());
    }
}
