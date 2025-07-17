package com.travelPlanWithAccounting.service.util;

import java.util.Map;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonHelper {

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

}
