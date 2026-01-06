package com.travelPlanWithAccounting.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 發送 OTP 的請求物件 */
public class OtpRequest {

  @NotBlank(message = "電子郵件不能為空")
  @Email(message = "請輸入有效的電子郵件格式")
  private String email;

  /** 用途：REGISTRATION/LOGIN/GUEST_LOGIN... */
  @NotNull(message = "purpose 不能為空")
  private OtpPurpose purpose;

  /** 預留：訪客登入可帶 deviceId（目前不強制） */
  private String deviceId;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public OtpPurpose getPurpose() {
    return purpose;
  }

  public void setPurpose(OtpPurpose purpose) {
    this.purpose = purpose;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
}
