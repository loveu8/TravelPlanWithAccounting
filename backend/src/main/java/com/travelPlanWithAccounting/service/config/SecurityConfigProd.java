package com.travelPlanWithAccounting.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("prod")
public class SecurityConfigProd {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    // 允許公開訪問 Swagger UI 和 API 文檔
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                    .permitAll()
                    // 其他所有請求需要身份驗證
                    .anyRequest()
                    .authenticated())
        // 禁用 CSRF（若 API 不需要，可選）
        .csrf(Customizer.withDefaults());

    return http.build();
  }
}
