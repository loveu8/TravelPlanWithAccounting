package com.travelPlanWithAccounting.service.config;

import java.util.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 若你選擇在 application.yml 中以 prefix=poi-type-map 配置，就可以用這個類自動綁定。 （直接讀 resources/poi-type-map.yaml
 * 時，可不使用本類。）
 *
 * <p>YAML 例： poi-type-map: lang: zh-TW: "001" en-US: "002" priority: [P001, P002, ...] entries:
 * P001: token: FOOD_DRINK google_types: [restaurant,...]
 */
@Configuration
@ConfigurationProperties(prefix = "poi-type-map")
public class PoiTypeMappingProperties {

  private Map<String, String> lang = new LinkedHashMap<>();
  private List<String> priority = new ArrayList<>();
  private Map<String, PoiTypeEntry> entries = new LinkedHashMap<>();

  public Map<String, String> getLang() {
    return lang;
  }

  public void setLang(Map<String, String> lang) {
    this.lang = lang;
  }

  public List<String> getPriority() {
    return priority;
  }

  public void setPriority(List<String> priority) {
    this.priority = priority;
  }

  public Map<String, PoiTypeEntry> getEntries() {
    return entries;
  }

  public void setEntries(Map<String, PoiTypeEntry> entries) {
    this.entries = entries;
  }

  public static class PoiTypeEntry {
    private String token;
    private List<String> google_types = new ArrayList<>();

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public List<String> getGoogle_types() {
      return google_types;
    }

    public void setGoogle_types(List<String> google_types) {
      this.google_types = google_types;
    }
  }
}
