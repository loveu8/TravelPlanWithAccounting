package com.travelPlanWithAccounting.service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelPlanWithAccounting.service.dto.travelPlan.TravelFavoriteListRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelFavoriteListResponse;
import com.travelPlanWithAccounting.service.security.AccessTokenRequired;
import com.travelPlanWithAccounting.service.service.TravelFavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Travel Favorite", description = "旅遊行程收藏")
@RequestMapping("/api/travelFav")
public class TravelFavoriteController {

    private final TravelFavoriteService travelFavoriteService;
    private final AuthContext authContext;

    public TravelFavoriteController(
      TravelFavoriteService travelFavoriteService, AuthContext authContext) {
    this.travelFavoriteService = travelFavoriteService;
    this.authContext = authContext;
  }

  @PostMapping("/list")
  @AccessTokenRequired
  @Operation(summary = "分頁取得收藏行程列表")
  public TravelFavoriteListResponse listTravelFavorites(
      @RequestBody(required = false) TravelFavoriteListRequest request) {
    UUID memberId = authContext.getCurrentMemberId();
    return travelFavoriteService.listTravelFavorites(memberId, request);
  }
}
