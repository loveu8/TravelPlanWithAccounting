package com.travelPlanWithAccounting.service.util;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Google primaryType / types[] → internal P00x (score‑based). Scoring: • primaryType +100 • types[]
 * +10 / hit 平手依 priority；無則排序。 全無命中 → P018
 */
@Component
public class PoiTypeMapper {

  private static final Logger log = LoggerFactory.getLogger(PoiTypeMapper.class);

  private final Map<String, String> googleTypeToPcode; // normalized googleType -> P00x
  private final List<String> pcodePriority; // tie-break

  public PoiTypeMapper(
      @Qualifier("poiTypeLookup") Map<String, String> poiTypeLookup,
      @Qualifier("poiTypePriority") List<String> pcodePriority) {

    Map<String, String> norm = new HashMap<>();
    poiTypeLookup.forEach((k, v) -> norm.put(normKey(k), v));
    this.googleTypeToPcode = Collections.unmodifiableMap(norm);

    this.pcodePriority =
        (pcodePriority != null && !pcodePriority.isEmpty())
            ? List.copyOf(pcodePriority)
            : norm.values().stream().distinct().sorted().toList();

    if (log.isInfoEnabled()) {
      log.info(
          "PoiTypeMapper initialized: mappings={}, sample[japanese_restaurant]={}, priority={}",
          googleTypeToPcode.size(),
          googleTypeToPcode.get("japanese_restaurant"),
          this.pcodePriority);
    }
  }

  /**
   * @return P00x；若完全無命中，回傳 P018
   */
  public String map(String primaryType, List<String> types) {

    if (log.isDebugEnabled()) {
      log.debug("map primaryType={} types={}", primaryType, types);
    }

    Map<String, Integer> scores = new HashMap<>();

    /* ---------- primaryType ---------- */
    if (primaryType != null) {
      String p = googleTypeToPcode.get(normKey(primaryType));
      log.trace("primaryType [{}] => {}", primaryType, p);
      if (p != null) addScore(scores, p, 100);
    }

    /* ---------- types[] ---------- */
    if (types != null) {
      for (String t : types) {
        String p = googleTypeToPcode.get(normKey(t));
        log.trace("type [{}] => {}", t, p);
        if (p != null) addScore(scores, p, 10);
      }
    }

    if (scores.isEmpty()) {
      log.warn(
          "Unmapped google types: primaryType={}, types={} -> fallback P018", primaryType, types);
      return "P018";
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "scoreBoard={} (max={})",
          scores,
          scores.values().stream().mapToInt(v -> v).max().orElse(0));
    }

    /* ---------- 決定最高分 ---------- */
    int max = scores.values().stream().mapToInt(Integer::intValue).max().orElse(Integer.MIN_VALUE);

    List<String> candidates =
        scores.entrySet().stream()
            .filter(e -> e.getValue() == max)
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(ArrayList::new));

    String result;
    if (candidates.size() == 1) {
      result = candidates.get(0);
    } else {
      // tie‑break
      result =
          candidates.stream()
              .filter(pcodePriority::contains)
              .min(Comparator.comparingInt(pcodePriority::indexOf))
              .orElseGet(
                  () -> {
                    Collections.sort(candidates);
                    return candidates.get(0);
                  });
      log.debug("Tie {} -> pick {}", candidates, result);
    }

    log.debug("result={}", result);
    return result;
  }

  /* ---------- helper ---------- */
  private static void addScore(Map<String, Integer> scores, String pcode, int weightFromCaller) {
    // ⬇ 只有 P018(OTHER) 轉成 +1，其餘照舊
    int weight = "P018".equals(pcode) ? 1 : weightFromCaller;

    int sum = scores.merge(pcode, weight, Integer::sum);
    log.debug("addScore pcode={} +{} -> {}", pcode, weight, sum);
  }

  private static String normKey(String k) {
    return k == null ? null : k.trim().toLowerCase(Locale.ROOT);
  }
}
