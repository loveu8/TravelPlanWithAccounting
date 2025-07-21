package com.travelPlanWithAccounting.service.util;

import com.travelPlanWithAccounting.service.exception.MemberPoiException;
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
    String code = langMap.get(langType);
    if (code == null) {
      throw new MemberPoiException.UnsupportedLang(); // <‑‑ 直接拋自家錯誤
    }
    return code;
  }

  public String toLang(String code) {
    return langMap.entrySet().stream()
        .filter(e -> e.getValue().equals(code))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElseThrow(MemberPoiException.UnsupportedLang::new);
  }
}
