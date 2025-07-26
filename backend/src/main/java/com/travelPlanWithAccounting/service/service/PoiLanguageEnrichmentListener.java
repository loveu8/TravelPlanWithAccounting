package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import com.travelPlanWithAccounting.service.entity.Poi;
import com.travelPlanWithAccounting.service.entity.PoiI18n;
import com.travelPlanWithAccounting.service.repository.PoiI18nRepository;
import com.travelPlanWithAccounting.service.repository.PoiRepository;
import com.travelPlanWithAccounting.service.util.JsonHelper;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 背景補齊另一語系; 失敗記 log 不拋 */
@Component
@RequiredArgsConstructor
public class PoiLanguageEnrichmentListener {

  private final PlaceDetailFacade placeDetailFacade;
  private final PoiRepository poiRepository;
  private final PoiI18nRepository poiI18nRepository;
  private final LangTypeMapper langTypeMapper;
  private final JsonHelper jsonHelper;

  @EventListener
  @Transactional
  public void onPoiSaved(PoiSavedEvent evt) {
    String targetLang = resolveTargetLang(evt.savedLang());
    enrich(evt.poiId(), evt.placeId(), targetLang);
  }

  private String resolveTargetLang(String savedLang) {
    return "zh-TW".equalsIgnoreCase(savedLang) ? "en-US" : "zh-TW";
  }

  private void enrich(UUID poiId, String placeId, String targetLang) {
    PlaceDetailResponse dto = placeDetailFacade.fetch(placeId, targetLang);
    if (dto == null || dto.getPlaceId() == null) return;

    String langCode = langTypeMapper.toCode(targetLang);

    Poi poi = poiRepository.getReferenceById(poiId);

    PoiI18n i18n =
        poiI18nRepository.findByPoi_IdAndLangType(poiId, langCode).orElseGet(PoiI18n::new);

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

    i18n.setInfosRaw(jsonHelper.serialize(dto.getRawJson()));

    poiI18nRepository.save(i18n);
  }
}

