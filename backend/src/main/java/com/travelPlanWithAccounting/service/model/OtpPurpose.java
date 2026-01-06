package com.travelPlanWithAccounting.service.model;

import java.util.Arrays;

public enum OtpPurpose {
  REGISTRATION("001"), // 新註冊
  LOGIN("002"), // 會員登入
  GUEST_LOGIN("003"), // 訪客驗證登入（預留/可選）
  EMAIL_CHANGE("004"), // 信箱變更
  IDENTITY_VERIFICATION("005"); // 驗證資料

  private final String actionCode;

  OtpPurpose(String actionCode) {
    this.actionCode = actionCode;
  }

  /** 對應 PRD 的 action（"001"/"002"/"003"...） */
  public String actionCode() {
    return actionCode;
  }

  /** 允許從 DB 代碼反查 enum（找不到時丟 IllegalArgumentException） */
  public static OtpPurpose fromActionCode(String code) {
    return Arrays.stream(values())
        .filter(p -> p.actionCode.equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown OTP action code: " + code));
  }
}
