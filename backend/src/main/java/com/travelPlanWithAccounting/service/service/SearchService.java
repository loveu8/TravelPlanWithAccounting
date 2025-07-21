package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.google.PlaceDetailRequestPost;
import com.travelPlanWithAccounting.service.dto.memberpoi.SaveMemberPoiRequest;
import com.travelPlanWithAccounting.service.dto.memberpoi.SaveMemberPoiResponse;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.entity.Poi;
import com.travelPlanWithAccounting.service.entity.PoiI18n;
import com.travelPlanWithAccounting.service.entity.TxPoiResult;
import com.travelPlanWithAccounting.service.entity.TxResult;
import com.travelPlanWithAccounting.service.exception.ApiException;
import com.travelPlanWithAccounting.service.factory.GoogleRequestFactory;
import com.travelPlanWithAccounting.service.mapper.GooglePlaceDetailMapper;
import com.travelPlanWithAccounting.service.mapper.GooglePlaceMapper;
import com.travelPlanWithAccounting.service.mapper.InfoAggregator;
import com.travelPlanWithAccounting.service.repository.MemberPoiRepository;
import com.travelPlanWithAccounting.service.repository.PoiI18nRepository;
import com.travelPlanWithAccounting.service.repository.PoiRepository;
import com.travelPlanWithAccounting.service.repository.SearchAllCountryRepository;
import com.travelPlanWithAccounting.service.repository.SearchAllLocationRepository;
import com.travelPlanWithAccounting.service.repository.SearchCountryRepository;
import com.travelPlanWithAccounting.service.util.GooglePlaceConstants;
import com.travelPlanWithAccounting.service.util.JsonHelper;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import com.travelPlanWithAccounting.service.util.LocationHelper;
import com.travelPlanWithAccounting.service.util.PoiTypeMapper;
import com.travelPlanWithAccounting.service.validator.PlaceDetailValidator;
import com.travelPlanWithAccounting.service.validator.SearchRequestValidator;
import com.travelPlanWithAccounting.service.validator.Validator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  private static final Logger log = LoggerFactory.getLogger(SearchService.class);

  @Autowired private SearchCountryRepository searchCountryRepository;

  @Autowired private MapService mapService;

  // searchRegions
  @Autowired private Validator coommonValidator;
  @Autowired private InfoAggregator infoAggregator;

  @Autowired private SearchAllLocationRepository searchAllLocationRepository;
  @Autowired private SearchAllCountryRepository searchAllCountryRepository;

  // Google 相關服務
  @Autowired private LocationHelper locationHelper;
  @Autowired private SearchRequestValidator searchRequestValidator;
  @Autowired private GoogleRequestFactory requestFactory;
  @Autowired private GooglePlaceMapper placeMapper;
  @Autowired private PlaceDetailValidator placeDetailValidator;
  @Autowired private GooglePlaceDetailMapper placeDetailMapper;

  @Autowired private MemberService memberService;
  @Autowired private PoiRepository poiRepository;
  @Autowired private PoiI18nRepository poiI18nRepository;
  @Autowired private MemberPoiRepository memberPoiRepository;
  @Autowired private LangTypeMapper langTypeMapper;
  @Autowired private PoiTypeMapper poiTypeMapper;
  @Autowired private PoiLanguageEnrichmentPublisher enrichmentPublisher;
  @Autowired private PlaceDetailFacade placeDetailFacade;
  @Autowired private JsonHelper jsonHelper;

  public List<Region> searchRegions(String countryCode, String langType) {

    // 1) 驗證資料
    coommonValidator.validate(countryCode, langType);

    // 2) 查詢DB
    List<Object[]> results = searchCountryRepository.findRegionsAndCities(countryCode, langType);

    // 3) 聚合結果
    return infoAggregator.searchRegions(results);
  }

  public List<Country> searchCountries(String langType) {
    // 1) 驗證資料
    coommonValidator.validate(langType);

    // 2) 查詢資料
    List<Object[]> results = searchAllCountryRepository.findCountriesByLangType(langType);

    // 3) 聚合結果
    return infoAggregator.searchCountries(results);
  }

  public List<LocationName> searchLocations() {
    List<Object[]> results = searchAllLocationRepository.findAllLocation();
    List<LocationName> locationNames = new ArrayList<>();

    for (Object[] row : results) {
      Location location = (Location) row[0];
      String name = (String) row[1];
      String langType = (String) row[2];

      LocationName locationName = new LocationName();
      locationName.setCode(location.getCode());
      locationName.setName(name != null ? name : location.getCode());
      locationName.setLangType(langType);
      locationNames.add(locationName);
    }
    return locationNames;
  }

  /**
   * 根據 Location 代碼搜尋附近景點
   *
   * @param request 包含 Location 代碼的搜尋請求
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchNearbyByLocationCode(SearchRequest request) {
    // 驗證
    searchRequestValidator.validateNearby(request);

    // 取 Location（內含存在＋經緯度檢查）
    Location loc = locationHelper.getLocationOrThrow(request.getCode());

    // 組 Google Request
    com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest googleReq =
        requestFactory.buildNearby(loc, request);

    // 呼叫 API
    JsonNode json = mapService.searchNearby(googleReq, GooglePlaceConstants.FIELD_MASK);

    // 映射 & 回傳
    return placeMapper.toLocationSearchList(json);
  }

  public List<LocationSearch> searchNearby(NearbySearchRequest request) {
    // 1. 呼叫 Google Places API（共用欄位遮罩）
    JsonNode json = mapService.searchNearby(request, GooglePlaceConstants.FIELD_MASK);

    // 2. 交由 Mapper 處理所有 JSON → DTO 解析
    return placeMapper.toLocationSearchList(json);
  }

  /**
   * 根據 Location 代碼和文字查詢搜尋景點
   *
   * @param request 包含 Location 代碼和搜尋文字的請求
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchTextByLocationCode(TextSearchRequest uiReq) {

    // 1) 驗證參數
    searchRequestValidator.validateText(uiReq);

    // 2) 取 Location（含代碼存在 & 經緯度檢查）
    Location loc = locationHelper.getLocationOrThrow(uiReq.getCode());

    // 3) 組 Google TextSearchRequest（UI → Google DTO）
    com.travelPlanWithAccounting.service.dto.google.TextSearchRequest googleReq =
        requestFactory.buildText(loc, uiReq);

    // 4) 呼叫 API
    JsonNode json = mapService.searchText(googleReq, GooglePlaceConstants.FIELD_MASK);

    // 5) Mapper 轉 DTO
    return placeMapper.toLocationSearchList(json);
  }

  /**
   * 使用 Google Places API 進行文字搜尋
   *
   * @param request TextSearchRequest 物件
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchText(
      com.travelPlanWithAccounting.service.dto.google.TextSearchRequest request) {
    // 1) 呼叫 Google API，欄位遮罩沿用共用常數
    JsonNode json = mapService.searchText(request, GooglePlaceConstants.FIELD_MASK);

    // 2) 交由 Mapper 解析 JSON → DTO
    return placeMapper.toLocationSearchList(json);
  }

  /**
   * 根據 placeId 查詢完整景點
   *
   * @param placeId Google Map placeId
   * @param langType 語系
   * @return PlaceDetailResponse
   */
  @Transactional
  public PlaceDetailResponse getPlaceDetailById(String placeId, String langType) {

    // 1) 驗證
    placeDetailValidator.validate(placeId, langType);
    String langCode = langTypeMapper.toCode(langType);

    // 2) 先找 DB（替換原本的 infos_raw 快取）
    Optional<String> cachedJson = poiRepository.findCachedRawJson(placeId, langCode);
    JsonNode json;
    boolean hit = cachedJson.isPresent();

    if (hit) {
      log.debug("Cache‑hit placeId={} langType={}", placeId, langType);
      json = jsonHelper.deserializeToNode(cachedJson.get());
    } else {
      log.debug("Call api placeId={} langType={}", placeId, langType);
      PlaceDetailRequestPost req = requestFactory.buildPlaceDetails(placeId, langType);
      json = mapService.getPlaceDetails(req);

      // 2.3) API 成功 → 立即 upsert 進 DB
      PlaceDetailResponse dto = placeDetailMapper.toDto(json, false);
      upsertPoiAndI18n(dto, langCode); // 共用方法，避免重複
    }

    return placeDetailMapper.toDto(json, false);
  }

  @Transactional
  public SaveMemberPoiResponse saveMemberPoi(UUID authMemberId, SaveMemberPoiRequest req) {
    // 1) 確認會員存在
    memberService.assertActiveMember(authMemberId, req.getMemberId());

    // 2) 確認 語言傳遞正確
    String langCode;
    try {
      langCode = langTypeMapper.toCode(req.getLangType());
    } catch (Exception e) {
      throw new ApiException("unsupported langType");
    }

    // 3) 查詢API確認存在此地點
    PlaceDetailResponse dto;
    try {
      dto = placeDetailFacade.fetch(req.getPlaceId(), req.getLangType());
    } catch (Exception ex) {
      throw new ApiException("place not found");
    }
    if (dto.getPlaceId() == null || dto.getName() == null) {
      throw new ApiException("place missing required fields");
    }

    // 4) 確認儲存
    TxResult tx = doSaveTx(authMemberId, dto, langCode);

    // 5) 背景處理其他語系
    enrichmentPublisher.publish(tx.poiId(), dto.getPlaceId(), req.getLangType());

    return SaveMemberPoiResponse.builder()
        .code(1)
        .desc(tx.alreadySaved() ? "already saved" : "OK")
        .poiId(tx.poiId())
        .poiCreated(tx.poiCreated())
        .langInserted(tx.langInserted())
        .alreadySaved(tx.alreadySaved())
        .build();
  }

  @Transactional
  protected TxResult doSaveTx(UUID memberId, PlaceDetailResponse dto, String langCode) {

    // 1) 先 upsert Poi + i18n
    TxPoiResult poiTx = upsertPoiAndI18n(dto, langCode);

    // 2) member_poi link（原本程式碼保留）
    boolean alreadySaved =
        memberPoiRepository.existsByMemberIdAndPoi_Id(memberId, poiTx.getPoiId());
    if (!alreadySaved) {
      int rows = memberPoiRepository.insertIgnore(memberId, poiTx.getPoiId());
      alreadySaved = rows == 0;
    }

    return new TxResult(
        poiTx.getPoiId(), poiTx.isPoiCreated(), poiTx.isLangInserted(), alreadySaved);
  }

  /** 專責「Poi + PoiI18n」upsert，不處理 member_poi。 傳回 poiId 與旗標，方便統計或後續處理。 */
  @Transactional
  protected TxPoiResult upsertPoiAndI18n(PlaceDetailResponse dto, String langCode) {

    Optional<Poi> opt = poiRepository.lockByExternalId(dto.getPlaceId());
    boolean poiCreated = opt.isEmpty();
    Poi poi = opt.orElseGet(Poi::new);
    if (poiCreated) poi.setExternalId(dto.getPlaceId());

    // ===== scalar fields =====
    poi.setPoiType(poiTypeMapper.map(dto.getPrimaryType(), dto.getTypes()));
    poi.setRating(dto.getRating() == null ? null : BigDecimal.valueOf(dto.getRating()));
    poi.setReviewCount(dto.getReviewCount());
    poi.setPhone(dto.getPhone());
    poi.setWebsite(dto.getWebsite());

    // ===== JSON fields =====
    poi.setOpeningPeriods(jsonHelper.serialize(dto.getRegularHoursRaw()));
    poi.setTypes(jsonHelper.serialize(dto.getTypes()));
    poi.setPhotoUrls(jsonHelper.serialize(dto.getPhotoUrls()));
    poi.setLat(dto.getLat() == null ? null : BigDecimal.valueOf(dto.getLat()));
    poi.setLon(dto.getLon() == null ? null : BigDecimal.valueOf(dto.getLon()));

    poiRepository.save(poi);

    // ===== i18n upsert =====
    PoiI18n i18n =
        poiI18nRepository.findByPoi_IdAndLangType(poi.getId(), langCode).orElseGet(PoiI18n::new);

    boolean langInserted = i18n.getId() == null;

    i18n.setPoi(poi);
    i18n.setLangType(langCode);
    i18n.setName(dto.getName());
    i18n.setDescription(dto.getDescription());
    i18n.setAddress(dto.getAddress());
    i18n.setCityName(dto.getCity());
    i18n.setCountryName(dto.getCountry());

    JsonNode weekday =
        Optional.ofNullable(dto.getRegularHoursRaw())
            .filter(h -> h.has("weekdayDescriptions"))
            .map(h -> h.get("weekdayDescriptions"))
            .orElse(null);
    i18n.setWeekdayDescriptions(jsonHelper.serialize(weekday));
    i18n.setInfosRaw(jsonHelper.serialize(dto.getRawJson()));

    poiI18nRepository.save(i18n);

    return new TxPoiResult(poi.getId(), poiCreated, langInserted);
  }
}
