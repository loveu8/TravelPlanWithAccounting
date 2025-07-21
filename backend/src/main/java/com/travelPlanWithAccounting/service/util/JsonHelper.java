package com.travelPlanWithAccounting.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.exception.JsonHelperException;
import com.travelPlanWithAccounting.service.message.JsonHelperMessageCode;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonHelper {

  private static final Logger log = LoggerFactory.getLogger(JsonHelper.class);

  private ObjectMapper objectMapper;

  public JsonHelper(Map<String, String> poiLangLookup) {
    this.objectMapper = new ObjectMapper();
  }

  public String serialize(Object any) {
    if (any == null) return null;
    try {
      return objectMapper.writeValueAsString(any);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  /** 把 JSON 字串 → 任意類別；null / 空字串回 Optional.empty() */
  public <T> T deserialize(String json, Class<T> clazz) {
    if (json == null || json.isBlank()) return null;
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      log.error("deserialize to {} error", clazz, e);
      throw new IllegalStateException("JSON deserialize error", e);
    }
  }

  /** 支援泛型：new TypeReference&lt;List&lt;String>>(){ } */
  public <T> T deserialize(String json, TypeReference<T> typeRef) {
    if (json == null || json.isBlank()) return null;
    try {
      return objectMapper.readValue(json, typeRef);
    } catch (JsonProcessingException e) {
      log.error("deserialize to {} error", typeRef.getType(), e);
      throw new IllegalStateException("JSON deserialize error", e);
    }
  }

  /* ---------- deserialize to JsonNode ---------- */

  /** 把 JSON 字串→Jackson JsonNode；null / 空字串 回 null。 */
  public JsonNode deserializeToNode(String json) {
    if (json == null || json.isBlank()) return null;
    try {
      return objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      log.error("deserializeToNode error", e);
      throw new JsonHelperException(JsonHelperMessageCode.JSON_DESERIALIZE_ERROR);
    }
  }
}
