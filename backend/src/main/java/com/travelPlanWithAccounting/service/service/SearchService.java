package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.dto.google.NearbySearchRequest;
import com.travelPlanWithAccounting.service.dto.google.PlaceDetailRequestPost;
import com.travelPlanWithAccounting.service.dto.memberpoi.SaveMemberPoiRequest;
import com.travelPlanWithAccounting.service.dto.memberpoi.SaveMemberPoiResponse;
import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import com.travelPlanWithAccounting.service.dto.search.response.City;
import com.travelPlanWithAccounting.service.dto.search.response.Country;
import com.travelPlanWithAccounting.service.dto.search.response.LocationName;
import com.travelPlanWithAccounting.service.dto.search.response.LocationSearch;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import com.travelPlanWithAccounting.service.dto.search.response.Region;
import com.travelPlanWithAccounting.service.entity.Location;
import com.travelPlanWithAccounting.service.entity.LocationGroup;
import com.travelPlanWithAccounting.service.entity.Poi;
import com.travelPlanWithAccounting.service.entity.PoiI18n;
import com.travelPlanWithAccounting.service.entity.TxResult;
import com.travelPlanWithAccounting.service.exception.ApiException;
import com.travelPlanWithAccounting.service.factory.GoogleRequestFactory;
import com.travelPlanWithAccounting.service.mapper.GooglePlaceDetailMapper;
import com.travelPlanWithAccounting.service.mapper.GooglePlaceMapper;
import com.travelPlanWithAccounting.service.repository.MemberPoiRepository;
import com.travelPlanWithAccounting.service.repository.PoiI18nRepository;
import com.travelPlanWithAccounting.service.repository.PoiRepository;
import com.travelPlanWithAccounting.service.repository.SearchAllCountryRepository;
import com.travelPlanWithAccounting.service.repository.SearchAllLocationRepository;
import com.travelPlanWithAccounting.service.repository.SearchCountryRepository;
import com.travelPlanWithAccounting.service.repository.SearchLocationByCodeRepository;
import com.travelPlanWithAccounting.service.util.GooglePlaceConstants;
import com.travelPlanWithAccounting.service.util.JsonHelper;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import com.travelPlanWithAccounting.service.util.LocationHelper;
import com.travelPlanWithAccounting.service.util.PoiTypeMapper;
import com.travelPlanWithAccounting.service.validator.PlaceDetailValidator;
import com.travelPlanWithAccounting.service.validator.SearchRequestValidator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  @Autowired private SearchCountryRepository searchCountryRepository;

  @Autowired private SearchLocationByCodeRepository searchLocationByCodeRepository;

  @Autowired private MapService mapService;

  // Google 相關服務
  @Autowired private LocationHelper locationHelper;
  @Autowired private SearchRequestValidator validator;
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
    List<Object[]> results = searchCountryRepository.findRegionsAndCities(countryCode, langType);

    // 使用 Map 按地區聚合城市資料
    Map<UUID, Region> regionMap = new HashMap<>();

    for (Object[] row : results) {
      LocationGroup group = (LocationGroup) row[0];
      String regionName = (String) row[1];
      Location location = (Location) row[3];
      String cityName = (String) row[4];

      // 若該地區尚未存在於 Map 中，則初始化
      Region region =
          regionMap.computeIfAbsent(
              group.getId(),
              id -> {
                Region dto = new Region();
                dto.setRegionCode(group.getCode());
                dto.setRegionName(regionName != null ? regionName : group.getCode());
                dto.setOrderIndex(group.getOrderIndex());
                dto.setCities(new ArrayList<>());
                return dto;
              });

      // 添加城市資料
      City city = new City();
      city.setCode(location.getCode());
      city.setName(cityName != null ? cityName : location.getCode());
      region.getCities().add(city);
    }

    // 轉換為 List 並按 orderIndex 排序
    List<Region> regions = new ArrayList<>(regionMap.values());
    regions.sort(Comparator.comparing(Region::getOrderIndex));
    return regions;
  }

  @Autowired private SearchAllCountryRepository searchAllCountryRepository;

  public List<Country> searchCountries(String langType) {
    List<Object[]> results = searchAllCountryRepository.findCountriesByLangType(langType);
    List<Country> countries = new ArrayList<>();

    for (Object[] row : results) {
      Location location = (Location) row[0];
      String countryName = (String) row[1];

      Country country = new Country();
      country.setCode(location.getCode());
      country.setName(countryName != null ? countryName : location.getCode());
      countries.add(country);
    }

    return countries;
  }

  @Autowired private SearchAllLocationRepository searchAllLocationRepository;

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

  // ==================== 新增：直接回傳 Location 物件的方法 ====================

  /**
   * 取得所有國家 (直接回傳 Location 物件)
   *
   * @return List<Location>
   */
  public List<Location> getAllCountries() {
    return searchLocationByCodeRepository.findAllCountries();
  }

  /**
   * 根據國家代碼取得該國家的所有州省
   *
   * @param countryCode 國家代碼
   * @return List<Location>
   */
  public List<Location> getProvincesByCountryCode(String countryCode) {
    return searchLocationByCodeRepository.findProvincesByCountryCode(countryCode);
  }

  /**
   * 根據州省代碼取得該州省的所有城市
   *
   * @param provinceCode 州省代碼
   * @return List<Location>
   */
  public List<Location> getCitiesByProvinceCode(String provinceCode) {
    return searchLocationByCodeRepository.findCitiesByProvinceCode(provinceCode);
  }

  /**
   * 根據代碼查詢單一 Location
   *
   * @param code Location 代碼
   * @return Optional<Location>
   */
  public Optional<Location> getLocationByCode(String code) {
    return searchLocationByCodeRepository.findByCode(code);
  }

  /**
   * 根據代碼和層級查詢 Location
   *
   * @param code Location 代碼
   * @param level 層級 (1=國家 2=州省 3=城市)
   * @return Optional<Location>
   */
  public Optional<Location> getLocationByCodeAndLevel(String code, Short level) {
    return searchLocationByCodeRepository.findByCodeAndLevel(code, level);
  }

  /**
   * 根據層級查詢所有該層級的 Location
   *
   * @param level 層級 (1=國家 2=州省 3=城市)
   * @return List<Location>
   */
  public List<Location> getLocationsByLevel(Short level) {
    return searchLocationByCodeRepository.findByLevelOrderByOrderIndex(level);
  }

  /**
   * 根據經緯度範圍查詢附近的 Location
   *
   * @param minLat 最小緯度
   * @param maxLat 最大緯度
   * @param minLon 最小經度
   * @param maxLon 最大經度
   * @return List<Location>
   */
  public List<Location> getLocationsByCoordinates(
      Double minLat, Double maxLat, Double minLon, Double maxLon) {
    return searchLocationByCodeRepository.findLocationsByCoordinates(
        minLat, maxLat, minLon, maxLon);
  }

  /**
   * 根據 ISO 類型查詢 Location
   *
   * @param isoType ISO 類型 (001: ISO-3166-1, 002: ISO-3166-2)
   * @return List<Location>
   */
  public List<Location> getLocationsByIsoType(String isoType) {
    return searchLocationByCodeRepository.findByIsoTypeOrderByOrderIndex(isoType);
  }

  /**
   * 根據 Location 代碼搜尋附近景點
   *
   * @param request 包含 Location 代碼的搜尋請求
   * @return List<LocationSearch>
   */
  public List<LocationSearch> searchNearbyByLocationCode(SearchRequest request) {
    // 驗證
    validator.validateNearby(request);

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
    validator.validateText(uiReq);

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
  public PlaceDetailResponse getPlaceDetailById(String placeId, String langType) {

    // 1) 驗證輸入
    placeDetailValidator.validate(placeId, langType);

    // 2) 交給 Factory 組請求
    PlaceDetailRequestPost req = requestFactory.buildPlaceDetails(placeId, langType);

    // 3) MapService 呼叫
    JsonNode json = mapService.getPlaceDetails(req);

    // 4) Mapper 轉 DTO
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

    Optional<Poi> opt = poiRepository.lockByExternalId(dto.getPlaceId());
    boolean poiCreated = opt.isEmpty();
    Poi poi = opt.orElseGet(Poi::new);
    if (poiCreated) poi.setExternalId(dto.getPlaceId());

    String poiType = poiTypeMapper.map(dto.getPrimaryType(), dto.getTypes());
    poi.setPoiType(poiType);

    // scalar fields (null-safe)
    poi.setRating(dto.getRating() == null ? null : BigDecimal.valueOf(dto.getRating()));
    poi.setReviewCount(dto.getReviewCount());
    poi.setPhone(dto.getPhone());
    poi.setWebsite(dto.getWebsite());

    // JSON cols
    // opening_periods / types 還是字串欄位 → serialize
    poi.setOpeningPeriods(jsonHelper.serialize(dto.getRegularHoursRaw()));
    poi.setTypes(jsonHelper.serialize(dto.getTypes()));

    // photo_urls 是 List<String> → 直接塞
    poi.setPhotoUrls(jsonHelper.serialize(dto.getPhotoUrls()));

    poi.setLat(dto.getLat() == null ? null : BigDecimal.valueOf(dto.getLat()));
    poi.setLon(dto.getLon() == null ? null : BigDecimal.valueOf(dto.getLon()));

    poi = poiRepository.save(poi);

    // i18n upsert
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

    JsonNode hours = dto.getRegularHoursRaw();
    JsonNode weekday =
        (hours != null && hours.has("weekdayDescriptions"))
            ? hours.get("weekdayDescriptions")
            : null;
    i18n.setWeekdayDescriptions(jsonHelper.serialize(weekday));

    // 保留完整 raw JSON
    i18n.setInfosRaw(jsonHelper.serialize(dto.getRawJson()));

    poiI18nRepository.save(i18n);

    // member_poi link
    boolean alreadySaved;
    if (memberPoiRepository.existsByMemberIdAndPoi_Id(memberId, poi.getId())) {
      alreadySaved = true;
    } else {
      int rows = memberPoiRepository.insertIgnore(memberId, poi.getId());
      alreadySaved = rows == 0;
    }

    return new TxResult(poi.getId(), poiCreated, langInserted, alreadySaved);
  }
}
