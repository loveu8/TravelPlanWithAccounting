package com.travelPlanWithAccounting.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

/**
 * 載入 resources/poi-type-map.yaml -> Map；同時建立 googleType -> Pcode 快速查表。
 */
@Configuration
public class PoiTypeMappingLoader {
  private static final Logger log = LoggerFactory.getLogger(PoiTypeMappingLoader.class);

  @Value("${poi-type-map.location:classpath:poi-type-map.yaml}")
  private org.springframework.core.io.Resource mappingResource;

  @Bean(name="poiTypeLookup")
  public Map<String,String> poiTypeLookup() throws IOException {
    try (InputStream in = mappingResource.getInputStream()) {
      Yaml y = new Yaml();
      Map<String,Object> raw = y.load(in);
      Map<String,String> lookup = new HashMap<>();
      for (Map.Entry<String,Object> e : raw.entrySet()) {
        String key = e.getKey();
        if ("lang".equals(key)) continue;
        @SuppressWarnings("unchecked") Map<String,Object> node = (Map<String,Object>) e.getValue();
        @SuppressWarnings("unchecked") List<Object> g = (List<Object>) node.get("google_types");
        if (g == null) continue;
        for (Object o : g) {
          String googleType = String.valueOf(o).trim();
          lookup.put(googleType, key); // googleType -> P00x
        }
      }
      log.info("Loaded {} google type mappings", lookup.size());
      return lookup;
    }
  }

  @Bean(name="poiLangLookup")
  public Map<String,String> poiLangLookup() throws IOException {
    try (InputStream in = mappingResource.getInputStream()) {
      Yaml y = new Yaml();
      Map<String,Object> raw = y.load(in);
      @SuppressWarnings("unchecked") Map<String,String> lang = (Map<String,String>) raw.get("lang");
      return lang != null ? lang : Map.of();
    }
  }
}
