package com.travelPlanWithAccounting.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

/**
 * 載入 resources/poi-type-map.yaml。
 *
 * <p>提供三個 Bean： • poiTypeLookup : Map<googleType,Pcode> • poiLangLookup : Map<lang,內碼> •
 * poiTypePriority : List<Pcode> (UI排序 / tie-break)
 */
@Configuration
public class PoiTypeMappingLoader {

  private static final Logger log = LoggerFactory.getLogger(PoiTypeMappingLoader.class);

  @Value("${poi-type-map.location:classpath:poi-type-map.yaml}")
  private org.springframework.core.io.Resource mappingResource;

  /* --------------- Beans --------------- */

  @Bean(name = "poiTypeLookup")
  public Map<String, String> poiTypeLookup() throws IOException {
    Map<String, Object> raw = loadRawYaml();
    Map<String, String> lookup = new HashMap<>();

    raw.forEach(
        (key, val) -> {
          if ("lang".equals(key) || "priority".equals(key)) return;
          if (!(val instanceof Map<?, ?> node)) return;
          Object g = node.get("google_types");
          if (!(g instanceof Collection<?> coll)) return;
          for (Object o : coll) {
            if (o == null) continue;
            String googleType = String.valueOf(o).trim();
            if (googleType.isEmpty()) continue;
            if (lookup.put(googleType, key) != null) {
              log.warn(
                  "Duplicate googleType {} appears in multiple P codes; last wins ({})",
                  googleType,
                  key);
            }
          }
        });

    log.info("Loaded {} google type mappings from {}.", lookup.size(), mappingResource);
    return Collections.unmodifiableMap(lookup);
  }

  @Bean(name = "poiLangLookup")
  public Map<String, String> poiLangLookup() throws IOException {
    Map<String, Object> raw = loadRawYaml();
    @SuppressWarnings("unchecked")
    Map<String, String> lang = (Map<String, String>) raw.get("lang");
    if (lang == null) {
      log.warn("poi-type-map.yaml missing 'lang' section; returning empty map.");
      return Map.of();
    }
    return Collections.unmodifiableMap(new LinkedHashMap<>(lang));
  }

  @Bean(name = "poiTypePriority")
  public List<String> poiTypePriority() throws IOException {
    Map<String, Object> raw = loadRawYaml();
    Object arr = raw.get("priority");
    if (!(arr instanceof Collection<?> col)) {
      log.warn("poi-type-map.yaml missing 'priority' list; fallback to sorted P keys.");
      // fallback: 收集所有 Pxxx key，排序
      List<String> fallback =
          raw.keySet().stream().filter(k -> k.startsWith("P")).sorted().toList();
      return Collections.unmodifiableList(fallback);
    }
    List<String> out = new ArrayList<>(col.size());
    for (Object o : col) {
      if (o == null) continue;
      String p = String.valueOf(o).trim();
      if (!p.isEmpty()) out.add(p);
    }
    return Collections.unmodifiableList(out);
  }

  /* --------------- internal --------------- */

  @SuppressWarnings("unchecked")
  private Map<String, Object> loadRawYaml() throws IOException {
    try (InputStream in = mappingResource.getInputStream()) {
      Yaml y = new Yaml();
      Object doc = y.load(in);
      if (!(doc instanceof Map<?, ?> m)) {
        throw new IllegalStateException("poi-type-map.yaml root is not a map");
      }
      // 轉成可操作 Map<String,Object>
      Map<String, Object> raw = new LinkedHashMap<>();
      m.forEach((k, v) -> raw.put(String.valueOf(k), v));
      return raw;
    }
  }
}
