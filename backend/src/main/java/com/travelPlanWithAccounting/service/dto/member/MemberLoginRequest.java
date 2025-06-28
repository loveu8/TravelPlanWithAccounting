package com.travelPlanWithAccounting.service.dto.member;

import lombok.Data;

/** 會員登入請求 DTO */
@Data
public class MemberLoginRequest {
  private String email;
  private String otpToken;
}
