package com.travelPlanWithAccounting.service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** 產生 64 bytes RT 明文 + SHA-256(Base64URL) 雜湊 */
public final class TokenUtil {
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String randomRefreshToken() {
    byte[] buf = new byte[64];
    RANDOM.nextBytes(buf);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
  }

  public static String sha256Base64Url(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] dg = md.digest(input.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(dg);
    } catch (Exception e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}
