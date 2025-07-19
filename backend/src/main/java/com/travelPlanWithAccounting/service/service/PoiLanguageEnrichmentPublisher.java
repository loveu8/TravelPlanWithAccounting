package com.travelPlanWithAccounting.service.service;

import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/** 發布語系補齊事件 */
@Component
public class PoiLanguageEnrichmentPublisher {
  private final ApplicationEventPublisher publisher;
  public PoiLanguageEnrichmentPublisher(ApplicationEventPublisher publisher){this.publisher=publisher;}
  public void publish(UUID poiId, String placeId, String savedLang){
    publisher.publishEvent(new PoiSavedEvent(poiId, placeId, savedLang));
  }
}
