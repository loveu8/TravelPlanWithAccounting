package com.travelPlanWithAccounting.service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.dto.recommand.LocationRecommand;
import com.travelPlanWithAccounting.service.dto.search.response.PlaceDetailResponse;
import com.travelPlanWithAccounting.service.exception.MemberPoiException;
import com.travelPlanWithAccounting.service.exception.RecommandException;
import com.travelPlanWithAccounting.service.repository.PoiRepository;
import com.travelPlanWithAccounting.service.repository.PoiRepository.LocationSummary;
import com.travelPlanWithAccounting.service.repository.PoiRepository.PoiExternalId;
import com.travelPlanWithAccounting.service.service.SearchService;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommandService {

  private static final String RESOURCE_PATTERN = "classpath:recommand/%s.json";
  private static final int EXPECTED_RECOMMANDATIONS = 10;
  private static final Set<String> SUPPORTED_COUNTRY_CODES = Set.of("TW", "JP", "KR", "HK");

  private final PoiRepository poiRepository;
  private final LangTypeMapper langTypeMapper;
  private final SearchService searchService;
  private final ResourceLoader resourceLoader;
  private final ObjectMapper objectMapper;

  private final ConcurrentMap<String, List<RecommandDefinition>> cache = new ConcurrentHashMap<>();

  public List<LocationRecommand> getRecommendations(String countryCode) {
    String country = normalizeCountry(countryCode);
    String languageTag = LocaleContextHolder.getLocale().toLanguageTag();
    String langType = toLangType(languageTag);

    List<RecommandDefinition> definitions = loadDefinitions(country);
    if (definitions.isEmpty()) {
      return List.of();
    }

    List<UUID> poiIds = definitions.stream().map(RecommandDefinition::poiId).toList();
    List<LocationSummary> rows = poiRepository.findAllWithI18nByIdInAndLangType(poiIds, langType);
    Map<UUID, LocationSummary> dataMap =
        rows.stream()
            .collect(Collectors.toMap(LocationSummary::getId, r -> r, (left, right) -> left));

    List<LocationRecommand> result = new ArrayList<>(definitions.size());
    Map<UUID, String> externalIdMap = null;
    for (RecommandDefinition definition : definitions) {
      LocationSummary summary = dataMap.get(definition.poiId());
      if (summary == null) {
        if (externalIdMap == null) {
          externalIdMap = loadExternalIds(poiIds);
        }
        LocationRecommand refreshed = refreshFromPlaceDetails(definition, externalIdMap);
        if (refreshed == null) {
          log.warn(
              "POI {} defined in {} recommendations missing from database.",
              definition.poiId(),
              country);
          continue;
        }
        result.add(refreshed);
        continue;
      }
      result.add(toDto(summary));
    }
    return result;
  }

  private LocationRecommand toDto(LocationSummary summary) {
    return LocationRecommand.builder()
        .poiId(summary.getId())
        .placeId(summary.getExternalId())
        .name(summary.getName())
        .city(summary.getCityName())
        .photoUrl(extractFirstPhoto(summary.getPhotoUrls()))
        .rating(toDouble(summary.getRating()))
        .lat(toDouble(summary.getLat()))
        .lon(toDouble(summary.getLon()))
        .build();
  }

  private LocationRecommand toDto(PlaceDetailResponse detail) {
    return LocationRecommand.builder()
        .poiId(detail.getPoiId())
        .placeId(detail.getPlaceId())
        .name(detail.getName())
        .city(detail.getCity())
        .photoUrl(firstPhoto(detail.getPhotoUrls()))
        .rating(detail.getRating())
        .lat(detail.getLat())
        .lon(detail.getLon())
        .build();
  }

  private Double toDouble(BigDecimal value) {
    return value == null ? null : value.doubleValue();
  }

  private String extractFirstPhoto(String photoJson) {
    if (photoJson == null || photoJson.isBlank()) {
      return null;
    }
    try {
      JsonNode node = objectMapper.readTree(photoJson);
      if (!node.isArray() || node.isEmpty()) {
        return null;
      }
      JsonNode first = node.get(0);
      if (first == null) {
        return null;
      }
      if (first.isTextual()) {
        return first.asText();
      }
      if (first.hasNonNull("url")) {
        return first.get("url").asText();
      }
      if (first.hasNonNull("photoUrl")) {
        return first.get("photoUrl").asText();
      }
    } catch (IOException e) {
      log.warn("Failed to parse photoUrls JSON for poi {}", photoJson, e);
    }
    return null;
  }

  private String firstPhoto(List<String> photos) {
    if (photos == null || photos.isEmpty()) {
      return null;
    }
    return photos.get(0);
  }

  private List<RecommandDefinition> loadDefinitions(String country) {
    return cache.computeIfAbsent(country, this::readDefinitions);
  }

  private List<RecommandDefinition> readDefinitions(String country) {
    Resource resource = resourceLoader.getResource(RESOURCE_PATTERN.formatted(country));
    if (!resource.exists()) {
      throw new RecommandException.ConfigError(country);
    }
    try (InputStream inputStream = resource.getInputStream()) {
      List<RecommandDefinition> definitions =
          objectMapper.readValue(inputStream, new TypeReference<List<RecommandDefinition>>() {});
      if (definitions.size() != EXPECTED_RECOMMANDATIONS
          || definitions.stream().map(RecommandDefinition::poiId).anyMatch(Objects::isNull)) {
        throw new RecommandException.ConfigError(country);
      }
      return List.copyOf(definitions);
    } catch (IOException e) {
      log.error("Failed to load recommendations for {}", country, e);
      throw new RecommandException.ConfigError(country);
    }
  }

  private String normalizeCountry(String countryCode) {
    if (countryCode == null) {
      throw new RecommandException.InvalidCountry(null);
    }
    String normalized = countryCode.trim().toUpperCase(Locale.ROOT);
    if (!SUPPORTED_COUNTRY_CODES.contains(normalized)) {
      throw new RecommandException.InvalidCountry(countryCode);
    }
    return normalized;
  }

  private String toLangType(String languageTag) {
    try {
      return langTypeMapper.toCode(languageTag);
    } catch (MemberPoiException.UnsupportedLang ex) {
      throw new RecommandException.UnsupportedLang(languageTag);
    }
  }

  private Map<UUID, String> loadExternalIds(List<UUID> poiIds) {
    if (poiIds.isEmpty()) {
      return Map.of();
    }
    return poiRepository.findAllExternalIdsByIdIn(poiIds).stream()
        .collect(Collectors.toMap(PoiExternalId::getId, PoiExternalId::getExternalId));
  }

  private LocationRecommand refreshFromPlaceDetails(
      RecommandDefinition definition, Map<UUID, String> externalIdMap) {
    String externalId = externalIdMap.get(definition.poiId());
    if (externalId == null || externalId.isBlank()) {
      log.warn("POI {} missing externalId while refreshing recommendations.", definition.poiId());
      return null;
    }
    try {
      PlaceDetailResponse detail = searchService.getPlaceDetailById(externalId);
      if (detail == null) {
        log.warn("Place detail API returned null for poiId {}.", definition.poiId());
        return null;
      }
      return toDto(detail);
    } catch (RuntimeException ex) {
      log.error(
          "Failed to refresh recommendation for poi {} via placeDetails API.",
          definition.poiId(),
          ex);
      return null;
    }
  }

  private record RecommandDefinition(UUID poiId, String nameZh) {}
}
