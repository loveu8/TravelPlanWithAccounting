package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.MemberResponse;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "會員認證流程", description = "preAuthFlow（判斷+發送OTP）與 authFlow（驗證OTP並完成登入/註冊）")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/pre-auth-flow")
  @Operation(
      summary = "preAuthFlow：判斷登入/註冊並寄送 OTP",
      description =
          "輸入 email，系統自動判斷 purpose（REGISTRATION 或 LOGIN），並立即寄送對應 OTP；回傳 purpose 與 actionCode。")
  public PreAuthFlowResponse preAuthFlow(@Valid @RequestBody PreAuthFlowRequest req) {
    return memberService.preAuthFlow(req.getEmail());
  }

  @PostMapping("/auth-flow")
  @Operation(
      summary = "authFlow：驗證 OTP 並完成登入或註冊",
      description = "輸入 email + otpCode（以及註冊所需欄位），系統自動依帳號是否存在決定登入或註冊，回覆登入內容。")
  public MemberResponse authFlow(@Valid @RequestBody AuthFlowRequest req) {
    return memberService.authFlow(req);
  }
}
