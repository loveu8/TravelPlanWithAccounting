package com.travelPlanWithAccounting.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelPlanWithAccounting.service.dto.recommand.LocationRecommand;
import com.travelPlanWithAccounting.service.exception.RecommandException;
import com.travelPlanWithAccounting.service.repository.PoiRepository;
import com.travelPlanWithAccounting.service.repository.PoiRepository.LocationSummary;
import com.travelPlanWithAccounting.service.util.LangTypeMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

@ExtendWith(MockitoExtension.class)
class RecommandServiceTest {

  private static final UUID TAIPEI_101 =
      UUID.fromString("c7e64b2e-b63f-4fa6-b476-2962324fc04b");
  private static final UUID NATIONAL_PALACE =
      UUID.fromString("b81e7d63-a1bc-478a-aa6f-964812611500");

  @Mock private PoiRepository poiRepository;

  private RecommandService recommandService;

  @BeforeEach
  void setUp() {
    LangTypeMapper langTypeMapper = new LangTypeMapper(Map.of("zh-TW", "001", "en-US", "002"));
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    recommandService =
        new RecommandService(poiRepository, langTypeMapper, resourceLoader, new ObjectMapper());
  }

  @Test
  void shouldReturnRecommendationsInConfiguredOrder() {
    List<LocationSummary> rows =
        List.of(
            new TestLocationSummary(
                NATIONAL_PALACE,
                "place-2",
                BigDecimal.valueOf(4.7),
                "[\"https://img.example.com/palace.jpg\"]",
                BigDecimal.valueOf(25.102),
                BigDecimal.valueOf(121.548),
                "National Palace Museum",
                "台北市"),
            new TestLocationSummary(
                TAIPEI_101,
                "place-1",
                BigDecimal.valueOf(4.6),
                "[\"https://img.example.com/101.jpg\"]",
                BigDecimal.valueOf(25.0339649),
                BigDecimal.valueOf(121.5623213),
                "Taipei 101",
                "台北市"));

    when(poiRepository.findAllWithI18nByIdInAndLangType(anyCollection(), eq("001")))
        .thenReturn(rows);

    List<LocationRecommand> result = recommandService.getRecommendations("TW", "zh-TW");

    assertEquals(2, result.size());
    assertEquals(TAIPEI_101, result.get(0).getPoiId());
    assertEquals("https://img.example.com/101.jpg", result.get(0).getPhotoUrl());
    assertEquals(NATIONAL_PALACE, result.get(1).getPoiId());
    assertEquals(4.7d, result.get(1).getRating());
  }

  @Test
  void shouldThrowWhenCountryUnsupported() {
    assertThrows(
        RecommandException.InvalidCountry.class,
        () -> recommandService.getRecommendations("US", "zh-TW"));
  }

  @Test
  void shouldThrowWhenLanguageUnsupported() {
    assertThrows(
        RecommandException.UnsupportedLang.class,
        () -> recommandService.getRecommendations("TW", "ja-JP"));
  }

  private static class TestLocationSummary implements LocationSummary {
    private final UUID id;
    private final String externalId;
    private final BigDecimal rating;
    private final String photoUrls;
    private final BigDecimal lat;
    private final BigDecimal lon;
    private final String name;
    private final String cityName;

    private TestLocationSummary(
        UUID id,
        String externalId,
        BigDecimal rating,
        String photoUrls,
        BigDecimal lat,
        BigDecimal lon,
        String name,
        String cityName) {
      this.id = id;
      this.externalId = externalId;
      this.rating = rating;
      this.photoUrls = photoUrls;
      this.lat = lat;
      this.lon = lon;
      this.name = name;
      this.cityName = cityName;
    }

    @Override
    public UUID getId() {
      return id;
    }

    @Override
    public String getExternalId() {
      return externalId;
    }

    @Override
    public BigDecimal getRating() {
      return rating;
    }

    @Override
    public String getPhotoUrls() {
      return photoUrls;
    }

    @Override
    public BigDecimal getLat() {
      return lat;
    }

    @Override
    public BigDecimal getLon() {
      return lon;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getCityName() {
      return cityName;
    }
  }
}
