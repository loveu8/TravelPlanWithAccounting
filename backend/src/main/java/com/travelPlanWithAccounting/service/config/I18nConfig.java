package com.travelPlanWithAccounting.service.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class I18nConfig {

  @Value("${spring.messages.basename:classpath:i18n/messages}")
  private String messageSourceBasename;

  @Value("${spring.messages.encoding:UTF-8}")
  private String messageSourceEncoding;

  @Value("${spring.messages.cache-duration:3600}")
  private int messageSourceCacheDuration;

  @Value("${spring.messages.fallback-to-system-locale:false}")
  private boolean messageSourceFallbackToSystemLocale;

  @Bean
  public ReloadableResourceBundleMessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setBasename(messageSourceBasename);
    messageSource.setDefaultEncoding(messageSourceEncoding);
    messageSource.setCacheSeconds(messageSourceCacheDuration);
    messageSource.setFallbackToSystemLocale(messageSourceFallbackToSystemLocale);
    return messageSource;
  }

  /** Spring Boot 預設採用 AcceptHeader Resolver */
  @Bean
  public LocaleResolver localeResolver() {
    // 設定支援的 Locales，若 Accept-Language 沒設定或不在這清單內就會使用預設的 Locale
    List<Locale> supportedLocales = new ArrayList<>();
    supportedLocales.add(Locale.TAIWAN);
    supportedLocales.add(Locale.ENGLISH);

    AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
    acceptHeaderLocaleResolver.setDefaultLocale(Locale.ENGLISH); // 預設 Locale
    acceptHeaderLocaleResolver.setSupportedLocales(supportedLocales);
    return acceptHeaderLocaleResolver;
  }
}
