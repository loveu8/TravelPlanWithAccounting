package com.travelPlanWithAccounting.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.i18n.LocaleContextHolder;

import com.travelPlanWithAccounting.service.repository.RefreshTokenRepository;
import com.travelPlanWithAccounting.service.security.JwtUtil;

class RefreshTokenServiceTest {

  RefreshTokenService service =
      new RefreshTokenService(
          Mockito.mock(RefreshTokenRepository.class), Mockito.mock(JwtUtil.class));

  @Test
  void setsLocaleFromHeader() {
    service.verifyToken(null, "en-US");
    assertEquals(Locale.US, LocaleContextHolder.getLocale());
  }

  @Test
  void fallsBackToDefaultWhenUnsupported() {
    service.verifyToken(null, "fr-FR");
    assertEquals(Locale.TAIWAN, LocaleContextHolder.getLocale());
  }

  @Test
  void fallsBackWhenHeaderMissing() {
    service.verifyToken(null, null);
    assertEquals(Locale.TAIWAN, LocaleContextHolder.getLocale());
  }
}

