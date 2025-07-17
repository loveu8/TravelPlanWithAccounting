package com.travelPlanWithAccounting.service.config;

import java.util.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 對應 poi-type-map.yaml -> Spring Properties.
 *
 * YAML 結構：
 * lang: { zh-TW: "001", en-US: "002" }
 * P001: { token: FOOD_DRINK, google_types: [restaurant,...] }
 */
@Configuration
@ConfigurationProperties(prefix = "poi-type-map")
public class PoiTypeMappingProperties {
   
  private Map<String,String> lang = new HashMap<>();
  private Map<String,PoiTypeEntry> entries = new LinkedHashMap<>();

  public Map<String, String> getLang() {return lang;}
  public void setLang(Map<String, String> lang) {this.lang = lang;}

  public Map<String, PoiTypeEntry> getEntries() {return entries;}
  public void setEntries(Map<String, PoiTypeEntry> entries) {this.entries = entries;}

  public record PoiTypeEntry(String token, List<String> google_types) {} 
}
