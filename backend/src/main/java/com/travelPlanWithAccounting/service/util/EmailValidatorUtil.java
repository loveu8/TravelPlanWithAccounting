package com.travelPlanWithAccounting.service.util;

import java.util.regex.Pattern;

public class EmailValidatorUtil {

  private EmailValidatorUtil() {
    // 私有構造函數，防止實例化
  }

  // RFC 5322 簡化版正則
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  /**
   * 驗證電子郵件格式是否有效。<br>
   * Validates if the email format is correct.
   *
   * @param email 要驗證的電子郵件 (Email to validate)
   * @return 如果格式正確則返回 true，否則返回 false (Returns true if valid, false otherwise)
   */
  public static boolean isValid(String email) {
    if (email == null || email.isEmpty()) return false;
    return EMAIL_PATTERN.matcher(email).matches();
  }
}
