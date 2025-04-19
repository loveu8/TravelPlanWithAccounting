package com.travelPlanWithAccounting.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI initOpenAPI(@Value("${springdoc.version}") String appVersion) {
    OpenAPI defaultOpenAPI = new OpenAPI();
    defaultOpenAPI.info(
        new Info()
            .version(appVersion)
            .title("OPEN API Document")
            .description("Beta version for develop"));
    return defaultOpenAPI;
  }
}
