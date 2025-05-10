package com.travelPlanWithAccounting.service.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * {@code MessageSourceHolder} 提供靜態方法以存取 Spring 的 {@link MessageSource}， 支援在非 Spring 管理的類別中（如 {@code
 * enum}、{@code static} 工具類）取得國際化訊息。<br>
 * {@code MessageSourceHolder} provides static access to Spring's {@link MessageSource}, enabling
 * internationalized message retrieval in non-Spring-managed classes such as {@code enum} or utility
 * classes.
 *
 * <p>- 透過 {@link ApplicationContextAware} 介面，在 Spring 啟動階段注入 {@link MessageSource} 實例。<br>
 * - 支援使用 {@link LocaleContextHolder} 自動解析當前使用者語系。<br>
 * - 適用於例外處理、列舉常數、非 Spring bean 中的訊息格式化需求。<br>
 * <br>
 * - Implements {@link ApplicationContextAware} to inject the {@link MessageSource} at startup.<br>
 * - Uses {@link LocaleContextHolder} to resolve the current user locale dynamically.<br>
 * - Suitable for formatting messages in enums, exceptions, or static contexts outside Spring beans.
 *
 * <p><b>注意 / Note:</b><br>
 * 雖然此設計便於使用，但應避免濫用靜態存取，建議仍以依賴注入為優先。<br>
 * While convenient, static access should be used with caution. Prefer dependency injection when
 * possible.
 *
 * <p>範例 / Example:<br>
 *
 * <pre>{@code
 * String message = MessageSourceHolder.getMessage("400001", "email");
 * }</pre>
 */
@Component
public class MessageSourceHolder implements ApplicationContextAware {

  /**
   * 靜態儲存 {@link MessageSource} 實例，供全域靜態方法使用。<br>
   * Holds the injected {@link MessageSource} for global static access.
   */
  private static MessageSource messageSource;

  /**
   * 實作 {@link ApplicationContextAware} 接口，於 Spring 初始化時取得 {@link MessageSource} 實例。<br>
   * Implements {@link ApplicationContextAware} to retrieve and store the {@link MessageSource} at
   * application startup.
   *
   * @param applicationContext Spring 的應用程式上下文容器<br>
   *     the application context provided by Spring
   * @throws BeansException 若無法取得 {@link MessageSource} bean<br>
   *     if the {@link MessageSource} cannot be retrieved from context
   */
  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    messageSource = context.getBean(MessageSource.class);
  }

  /**
   * 依據訊息代碼與參數，取得當前語系的多語系訊息。<br>
   * Retrieves an internationalized message using the provided code and arguments for the current
   * locale.
   *
   * @param code 訊息代碼（對應 i18n properties 中的 key）<br>
   *     message code (key defined in i18n resource files)
   * @param args 參數陣列，將套用至訊息模板中的 {0}, {1}...<br>
   *     arguments to be substituted into message format
   * @return 當前語系對應的訊息文字<br>
   *     the localized message text
   */
  public static String getMessage(String code, Object... args) {
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }

  /**
   * 依據訊息代碼取得訊息（無參數格式化）。<br>
   * Retrieves a localized message by its code without applying any arguments.
   *
   * @param code 訊息代碼（對應 i18n properties 中的 key）<br>
   *     message code (key defined in i18n resource files)
   * @return 當前語系對應的訊息文字<br>
   *     the localized message text
   */
  public static String getMessage(String code) {
    return getMessage(code, new Object());
  }
}
