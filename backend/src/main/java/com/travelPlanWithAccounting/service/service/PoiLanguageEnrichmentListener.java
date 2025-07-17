package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponseV2;
import com.travelPlanWithAccounting.service.entity.Poi;
import com.travelPlanWithAccounting.service.entity.PoiI18n;
import com.travelPlanWithAccounting.service.repository.PoiI18nRepository;
import com.travelPlanWithAccounting.service.repository.PoiRepository;
import com.travelPlanWithAccounting.service.util.JsonHelper;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 背景補齊另一語系; 失敗記 log 不拋 */
@Component
public class PoiLanguageEnrichmentListener {
  private static final Logger log = LoggerFactory.getLogger(PoiLanguageEnrichmentListener.class);

  @Autowired private PoiRepository poiRepository;
  @Autowired private PoiI18nRepository poiI18nRepository;
  @Autowired private LangTypeMapper langTypeMapper;
  @Autowired private PlaceDetailFacade placeDetailFacade;
  @Autowired private JsonHelper jsonHelper;

  @Async
  @EventListener
  public void onPoiSaved(PoiSavedEvent ev) {
    String otherLang = ev.savedLang().equals("zh-TW") ? "en-US" : "zh-TW";
    String otherCode;
    try {
      otherCode = langTypeMapper.toCode(otherLang);
    } catch (Exception e) {
      return;
    }

    Optional<PoiI18n> exists = poiI18nRepository.findByPoi_IdAndLangType(ev.poiId(), otherCode);
    if (exists.isPresent()) return; // already have

    log.info("Enriching poi {} with lang {}", ev.poiId(), otherLang);
    try {
      PlaceDetailResponseV2 dto = placeDetailFacade.fetch(ev.placeId(), otherLang);
      enrich(ev.poiId(), otherCode, dto);
    } catch (Exception ex) {
      log.warn("Failed enrich poi {} lang {}: {}", ev.poiId(), otherLang, ex.toString());
    }
  }

  @Transactional
  protected void enrich(UUID poiId, String langCode, PlaceDetailResponseV2 dto) {
    Poi poi = poiRepository.findById(poiId).orElseThrow();
    PoiI18n i18n = new PoiI18n();
    i18n.setPoi(poi);
    i18n.setLangType(langCode);
    i18n.setName(dto.getName());
    i18n.setDescription(dto.getDescription());
    i18n.setAddress(dto.getAddress());
    i18n.setCityName(dto.getCity());
    i18n.setCountryName(dto.getCountry());
    Object weekday = null;
    if (dto.getRegularHoursRaw() != null && dto.getRegularHoursRaw().has("weekdayDescriptions")) {
      weekday = dto.getRegularHoursRaw().get("weekdayDescriptions");
    }
    i18n.setWeekdayDescriptions(jsonHelper.serialize(weekday));
    i18n.setInfosRaw(jsonHelper.serialize(dto.getRawJson()));
    poiI18nRepository.save(i18n);
  }
}

