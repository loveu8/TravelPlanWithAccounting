package com.travelPlanWithAccounting.service.util;

import java.util.*;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Google primaryType / types[] -> internal P00x。
 * 未命中 -> P018 OTHER。
 */
@Component
public class PoiTypeMapper {
  private static final Logger log = LoggerFactory.getLogger(PoiTypeMapper.class);
  private final Map<String,String> googleTypeToPcode; // googleType -> P00x

  public PoiTypeMapper(Map<String,String> poiTypeLookup) {
    this.googleTypeToPcode = poiTypeLookup;
  }

  public String map(String primaryType, List<String> types) {
    if (primaryType != null) {
      String p = googleTypeToPcode.get(primaryType);
      if (p != null) return p;
    }
    if (types != null) {
      for (String t : types) {
        String p = googleTypeToPcode.get(t);
        if (p != null) return p;
      }
    }
    log.warn("Unmapped google types: primaryType={}, types={} -> fallback P018", primaryType, types);
    return "P018"; // OTHER fallback
  }
}
