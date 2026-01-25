package com.travelPlanWithAccounting.service.entity.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VisitPlaceConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        List<String> cleaned = attribute.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .toList();
        if (cleaned.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(cleaned);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("visitPlace serialize error", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(dbData);
            return normalizeVisitPlace(node);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("visitPlace deserialize error", e);
        }
    }

    private List<String> normalizeVisitPlace(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            for (JsonNode item : node) {
                values.addAll(normalizeVisitPlace(item));
            }
            return values.isEmpty() ? Collections.emptyList() : values;
        }
        if (node.isTextual()) {
            String value = node.asText().trim();
            return value.isBlank() ? Collections.emptyList() : List.of(value);
        }
        if (node.isObject()) {
            String value = extractCode(node);
            return value == null || value.isBlank() ? Collections.emptyList() : List.of(value);
        }
        return Collections.emptyList();
    }

    private String extractCode(JsonNode node) {
        if (node.hasNonNull("code")) {
            return node.get("code").asText();
        }
        if (node.hasNonNull("country")) {
            return node.get("country").asText();
        }
        return null;
    }
}
