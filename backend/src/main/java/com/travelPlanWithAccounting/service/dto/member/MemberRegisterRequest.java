package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

/**
 * 會員註冊請求 DTO (精簡版)。<br>
 * Member registration request DTO (simplified).<br>
 *
 * <p>僅包含註冊所需欄位。<br>
 * Contains only required fields for registration.
 */
@Data
public class MemberRegisterRequest {
  @Schema(description = "名 (Given name)")
  private String givenName;

  @Schema(description = "姓 (Family name)")
  private String familyName;

  @Schema(description = "暱稱 (Nick name)")
  private String nickName;

  @Schema(description = "生日 (Birthday)")
  private LocalDate birthday;

  @Schema(description = "電子郵件 (Email)")
  private String email;

  @Schema(description = "OTP 驗證 token (OTP verification token)")
  private String otpToken;
}
