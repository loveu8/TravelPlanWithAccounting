package com.travelPlanWithAccounting.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** OtpVerificationRequest 用於驗證用戶輸入的 OTP 碼。 包含電子郵件和 OTP 碼兩個字段，並進行相應的驗證。 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationRequest {
  @NotBlank(message = "電子郵件不能為空")
  @Email(message = "請輸入有效的電子郵件格式")
  private String email;

  @NotBlank(message = "OTP驗證碼不能為空")
  @Pattern(regexp = "\\d{6}", message = "OTP驗證碼必須是6位數字")
  private String otpCode;
}
