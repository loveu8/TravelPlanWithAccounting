package com.travelPlanWithAccounting.service.util;

import java.util.Map;
import org.springframework.stereotype.Component;

/** zh-TW <-> 001 / en-US <-> 002 */
@Component
public class LangTypeMapper {

  private final Map<String, String> langMap; // e.g., {zh-TW=001,en-US=002}

  public LangTypeMapper(Map<String, String> poiLangLookup) {
    this.langMap = poiLangLookup;
  }

  public String toCode(String langType) {
    String c = langMap.get(langType);
    if (c == null) throw new IllegalArgumentException("unsupported langType: " + langType);
    return c;
  }

  public String toLang(String code) {
    return langMap.entrySet().stream()
        .filter(e -> e.getValue().equals(code))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("unsupported lang code:" + code));
  }
}
