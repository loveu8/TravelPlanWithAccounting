package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthFlowRequest {

  @NotBlank @Email
  @Schema(description = "會員 Email")
  private String email;

  @NotBlank
  @Schema(description = "使用者輸入的 OTP 驗證碼（由 preAuthFlow 寄出）")
  private String otpCode;

  @NotBlank
  @Schema(description = "預先發送 OTP 時取得的 token")
  private String token;

  @Schema(description = "BFF 產生的 clientId（預留給 RT 管理）")
  private String clientId;

  private String ip; // optional（若由後端取，可不帶）
  private String ua; // optional

  // ---- 註冊（purpose=REGISTRATION）時可用欄位（登入可不填）----
  private String givenName;
  private String familyName;
  private String nickName;
  private java.time.LocalDate birthday;
}
