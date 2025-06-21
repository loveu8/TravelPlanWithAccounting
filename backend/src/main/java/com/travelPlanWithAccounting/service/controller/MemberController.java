package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.member.MemberRegisterRequest;
import com.travelPlanWithAccounting.service.dto.member.MemberRegisterResponse;
import com.travelPlanWithAccounting.service.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 會員註冊 API Controller。<br>
 * Member registration API controller.<br>
 *
 * <p>提供會員註冊功能，需經過 OTP 驗證並帶入驗證 token。<br>
 * Provides member registration, requires OTP verification and token.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "會員註冊", description = "會員註冊與查詢 API")
public class MemberController {

  private final MemberService memberService;

  /**
   * 會員註冊。<br>
   * Register a new member.<br>
   *
   * <p>必須先通過 OTP 驗證並取得驗證 token，註冊時 email 為必填，其餘欄位選填。<br>
   * Must pass OTP verification and provide the token. Email is required, other fields are optional.
   * <br>
   * 註冊成功後預留回傳 JWT 欄位，供未來登入使用。<br>
   * After registration, reserves a JWT field for future login use.
   *
   * @param req 會員註冊請求 (Member registration request)
   * @return 註冊成功的會員資料與預留 JWT 欄位 (Registered member and reserved JWT field)
   */
  @PostMapping("/register")
  @Operation(summary = "會員註冊 (Register member)")
  public MemberRegisterResponse register(@RequestBody MemberRegisterRequest req) {
    return memberService.register(req);
  }
}
