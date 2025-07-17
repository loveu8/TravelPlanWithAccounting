package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.dto.google.PlaceDetailRequestPost;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import com.travelPlanWithAccounting.service.factory.GoogleRequestFactory;
import com.travelPlanWithAccounting.service.mapper.GooglePlaceDetailMapper;
import com.travelPlanWithAccounting.service.validator.PlaceDetailValidator;
import org.springframework.stereotype.Service;

/**
 * 專責呼叫 Google Place Details 並映射為 PlaceDetailResponseV2。 你可以： (A) 直接注入此 Facade 至 MemberPoiService；或
 * (B) 把這個類內容搬進既有 SearchService 並刪除此檔。
 */
@Service
public class PlaceDetailFacade {
  private final PlaceDetailValidator validator;
  private final GoogleRequestFactory requestFactory;
  private final MapService mapService;
  private final GooglePlaceDetailMapper mapper;

  public PlaceDetailFacade(
      PlaceDetailValidator validator,
      GoogleRequestFactory requestFactory,
      MapService mapService,
      GooglePlaceDetailMapper mapper) {
    this.validator = validator;
    this.requestFactory = requestFactory;
    this.mapService = mapService;
    this.mapper = mapper;
  }

  public PlaceDetailResponse fetch(String placeId, String langType) {
    validator.validate(placeId, langType);
    PlaceDetailRequestPost req = requestFactory.buildPlaceDetails(placeId, langType);
    JsonNode json = mapService.getPlaceDetails(req);
    return mapper.toDto(json, true);
  }
}
