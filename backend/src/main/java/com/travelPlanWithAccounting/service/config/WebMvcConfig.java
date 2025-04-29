package com.travelPlanWithAccounting.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@code WebMvcConfig} 設定全局的 Web MVC 行為，如攔截器、CORS、Header處理、內容協商等。<br>
 * {@code WebMvcConfig} configures global Web MVC behaviors such as interceptors, CORS policies,
 * forwarded headers, and content negotiation settings.
 *
 * <p>- 可以在此添加登入攔截器、權限檢查攔截器等功能。<br>
 * - 統一處理跨來源資源分享 (CORS) 設定。<br>
 * - 支援反向代理轉發 Header。<br>
 * - 預設回應內容型態為 JSON。<br>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  /**
   * 註冊自訂攔截器（目前尚未啟用，可依需求新增）。<br>
   * Registers custom interceptors (currently not active; can be customized as needed).
   *
   * @param registry 攔截器註冊器 (Interceptor registry)
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 在這邊可以加登入攔截器、權限檢查等等
    // registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/api/**");
  }

  /**
   * 註冊 {@link ForwardedHeaderFilter}，用來支援處理反向代理伺服器傳遞的 Forwarded Header。<br>
   * Registers a {@link ForwardedHeaderFilter} to handle forwarded headers from reverse proxies.
   *
   * @return ForwardedHeaderFilter 實例 (ForwardedHeaderFilter instance)
   */
  @Bean
  ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
  }

  /**
   * 設定跨來源資源分享 (CORS) 規則，允許所有來源、所有方法，並支援帶憑證的請求。<br>
   * Configures Cross-Origin Resource Sharing (CORS) rules to allow all origins, all methods, and
   * support credentials.
   *
   * <p>- 本設定主要針對開發階段（如前後端分離，本機不同 Port / Domain 測試）提供跨域呼叫支援。<br>
   * - 若正式環境由反向代理（如 Nginx、Gateway）負責處理 CORS，則可選擇移除或限縮此設定。<br>
   * - 注意：開放 {@code allowedOriginPatterns("*")} 與 {@code allowCredentials(true)} 同時存在
   * 於正式環境時，需謹慎控管以避免安全性風險。<br>
   *
   * @param registry CORS 註冊器 (CORS registry)
   */
  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("*")
        .allowCredentials(true)
        .maxAge(3600);
  }

  /**
   * 設定內容協商，將預設回應型別設為 {@link MediaType#APPLICATION_JSON}。<br>
   * Configures content negotiation to default the response content type to {@link
   * MediaType#APPLICATION_JSON}.
   *
   * @param configurer 內容協商設定器 (Content Negotiation Configurer)
   */
  @Override
  public void configureContentNegotiation(@NonNull ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }
}
