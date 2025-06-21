package com.travelPlanWithAccounting.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OtpRequest {
  @NotBlank(message = "電子郵件不能為空")
  @Email(message = "請輸入有效的電子郵件格式")
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
