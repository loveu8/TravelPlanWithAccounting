package com.travelPlanWithAccounting.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code JacksonConfig} 用於自訂應用程式的 Jackson 物件映射設定。<br>
 * {@code JacksonConfig} is used to customize Jackson ObjectMapper configuration for the
 * application.
 *
 * <p>本設定主要註冊 {@link JavaTimeModule}，以支援 Java 8 時間型別（如 {@link java.time.Instant}, {@link
 * java.time.LocalDateTime}, {@link java.time.LocalDate} 等）在序列化與反序列化時的處理。<br>
 * This configuration mainly registers the {@link JavaTimeModule} to enable support for Java 8
 * Date/Time types such as {@link java.time.Instant}, {@link java.time.LocalDateTime}, {@link
 * java.time.LocalDate}, etc. during serialization and deserialization.
 *
 * <p>若需其他時間格式或自訂序列化行為，請在此類擴充額外設定。<br>
 * For further customization like formatting or custom serializers, extend this configuration.
 *
 * @author
 */
@Configuration
public class JacksonConfig {

  /**
   * 建立並設定 {@link ObjectMapper} Bean，並註冊 {@link JavaTimeModule}，以支援 Java 8 日期時間 API。<br>
   * Creates and configures an {@link ObjectMapper} Bean with {@link JavaTimeModule} registered to
   * support Java 8 Date and Time API.
   *
   * @return 自訂設定的 {@code ObjectMapper} / the customized {@code ObjectMapper}
   */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    JavaTimeModule module = new JavaTimeModule();
    objectMapper.registerModule(module);
    // 讓 Instant 輸出成 ISO 格式，不是數字
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
