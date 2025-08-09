package com.travelPlanWithAccounting.service.aspect;

import java.util.List;
import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.context.i18n.LocaleContextHolder;

@Aspect
@Component
public class LocaleAspect {
  private static final List<Locale> SUPPORTED = List.of(Locale.TAIWAN, Locale.US);

  @Before("within(@org.springframework.web.bind.annotation.RestController *)")
  public void setLocaleFromHeader() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) return;

    HttpServletRequest req = attrs.getRequest();
    String header = req.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

    Locale locale =
        StringUtils.hasText(header) ? Locale.forLanguageTag(header) : Locale.TAIWAN;

    if (!SUPPORTED.contains(locale)) locale = Locale.TAIWAN;
    LocaleContextHolder.setLocale(locale);
  }
}
