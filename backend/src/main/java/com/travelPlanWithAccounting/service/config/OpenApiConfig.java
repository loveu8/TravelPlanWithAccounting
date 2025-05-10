package com.travelPlanWithAccounting.service.config;

import com.travelPlanWithAccounting.service.constant.OpenApiHeader;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** OpenAPI 設定類別，提供全域 OpenAPI 文件資訊設定。 */
@Configuration
public class OpenApiConfig {

  private static final String API_TITLE = "OPEN API Document";
  private static final String API_DESCRIPTION = "Beta version for development";

  /**
   * 建立並初始化 OpenAPI 實例，用於 Swagger 文件顯示基本資訊。<br>
   * Initializes the OpenAPI object with metadata for Swagger UI.
   *
   * @param appVersion 應用程式版本（由 springdoc.version 提供）
   * @return OpenAPI 實例
   */
  @Bean
  public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
    return new OpenAPI().info(apiInfo(appVersion)).components(createDefaultComponents());
  }

  /**
   * 建立 API 文件資訊。<br>
   * Builds the API documentation metadata.
   *
   * @param version 應用版本號
   * @return Info 實例
   */
  private Info apiInfo(String version) {
    return new Info().title(API_TITLE).description(API_DESCRIPTION).version(version);
  }

  @Bean
  public GroupedOpenApi allApi() {
    return GroupedOpenApi.builder()
        .group("01.ALL")
        .pathsToMatch("/**")
        .addOperationCustomizer(
            (operation, handlerMethod) -> {
              return createDefaultOperation(operation);
            })
        .build();
  }

  /**
   * 建立預設的操作安全性元件。<br>
   * Creates default security components for the OpenAPI documentation.
   *
   * @param operation 要設定的操作
   * @return Operation 實例
   */
  public Operation createDefaultOperation(Operation operation) {

    for (OpenApiHeader header : OpenApiHeader.values()) {
      operation.addSecurityItem(new SecurityRequirement().addList(header.getHeaderName()));
    }

    return operation;
  }

  /**
   * 建立預設的安全性元件。<br>
   * Creates default security components for the OpenAPI documentation.
   *
   * @return Components 實例
   */
  private Components createDefaultComponents() {
    Components components = new Components();

    for (OpenApiHeader header : OpenApiHeader.values()) {
      components.addSecuritySchemes(
          header.getHeaderName(),
          new SecurityScheme()
              .name(header.getHeaderName())
              .type(SecurityScheme.Type.APIKEY)
              .description(header.getDescription())
              .in(SecurityScheme.In.HEADER));
    }

    return components;
  }
}
