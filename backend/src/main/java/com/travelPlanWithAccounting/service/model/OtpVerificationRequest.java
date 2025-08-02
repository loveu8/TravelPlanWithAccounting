package com.travelPlanWithAccounting.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 驗證 OTP 的請求物件 */
public class OtpVerificationRequest {

  @NotBlank(message = "電子郵件不能為空")
  @Email(message = "請輸入有效的電子郵件格式")
  private String email;

  @NotBlank(message = "OTP 驗證碼不能為空")
  private String otpCode;

  /** 必須與發送時的 purpose 一致 */
  @NotNull(message = "purpose 不能為空")
  private OtpPurpose purpose;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
