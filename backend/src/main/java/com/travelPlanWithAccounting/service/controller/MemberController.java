package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.EmailChangeOtpRequest;
import com.travelPlanWithAccounting.service.dto.member.EmailChangeRequest;
import com.travelPlanWithAccounting.service.dto.member.EmailChangeResponse;
import com.travelPlanWithAccounting.service.dto.member.IdentityOtpVerifyRequest;
import com.travelPlanWithAccounting.service.dto.member.IdentityOtpVerifyResponse;
import com.travelPlanWithAccounting.service.dto.member.MemberProfileResponse;
import com.travelPlanWithAccounting.service.dto.member.MemberProfileUpdateRequest;
import com.travelPlanWithAccounting.service.dto.member.OtpTokenResponse;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member/Otp", description = "登入註冊流程 + Token 驗證")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/pre-auth-flow")
  @Operation(summary = "判斷登入/註冊並發送 OTP")
  public PreAuthFlowResponse preAuthFlow(@RequestBody PreAuthFlowRequest req) {
    return memberService.preAuthFlow(req);
  }

  @PostMapping("/auth-flow")
  @Operation(summary = "驗證 OTP -> 登入/註冊 -> 回 AT/RT（只回一層 data）")
  public AuthResponse authFlow(@RequestBody AuthFlowRequest req) {
    return memberService.authFlow(req);
  }

  @GetMapping("/profile")
  @Operation(summary = "查詢會員資料")
  public MemberProfileResponse getProfile(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
    return memberService.getProfile(auth);
  }

  @PostMapping("/profile")
  @Operation(summary = "修改會員資料")
  public MemberProfileResponse updateProfile(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
      @RequestBody MemberProfileUpdateRequest req) {
    return memberService.updateProfile(auth, req);
  }

  @PostMapping("/email/identity-otp")
  @Operation(summary = "發送信箱 OTP")
  public OtpTokenResponse sendIdentityOtp(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
    return memberService.sendIdentityOtp(auth);
  }

  @PostMapping("/email/identity-otp/verify")
  @Operation(summary = "驗證信箱 OTP")
  public IdentityOtpVerifyResponse verifyIdentityOtp(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
      @RequestBody IdentityOtpVerifyRequest req) {
    return memberService.verifyIdentityOtp(auth, req);
  }

  @PostMapping("/email/change-otp")
  @Operation(summary = "發送新信箱 OTP")
  public OtpTokenResponse sendEmailChangeOtp(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
      @RequestBody EmailChangeOtpRequest req) {
    return memberService.sendEmailChangeOtp(auth, req);
  }

  @PostMapping("/email")
  @Operation(summary = "更新信箱")
  public EmailChangeResponse changeEmail(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
      @RequestBody EmailChangeRequest req) {
    return memberService.changeEmail(auth, req);
  }

}
