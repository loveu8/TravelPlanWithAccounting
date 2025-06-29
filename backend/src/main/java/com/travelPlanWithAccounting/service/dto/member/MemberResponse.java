package com.travelPlanWithAccounting.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 會員回應 DTO。<br>
 * Member response DTO.<br>
 *
 * <p>回傳會員資料與預留 JWT 欄位。<br>
 * Returns member data and reserved JWT field.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  @Schema(description = "電子郵件 (Email)")
  private String email;

  @Schema(description = "JWT 權杖 (JWT token, reserved for future use)")
  private String jwt;
}
