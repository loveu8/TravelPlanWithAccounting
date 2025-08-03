package com.travelPlanWithAccounting.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 驗證 OTP 的請求物件 */
public class OtpVerificationRequest {

  @NotBlank(message = "token 不能為空")
  private String token;

  @NotBlank(message = "OTP 驗證碼不能為空")
  private String otpCode;

  /** 必須與發送時的 purpose 一致 */
  @NotNull(message = "purpose 不能為空")
  private OtpPurpose purpose;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getOtpCode() {
    return otpCode;
  }

  public void setOtpCode(String otpCode) {
    this.otpCode = otpCode;
  }

  public OtpPurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(OtpPurpose purpose) {
    this.purpose = purpose;
  }
}
