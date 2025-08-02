package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.dto.member.VerifyTokenRequest;
import com.travelPlanWithAccounting.service.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

  @PostMapping(
      value = "/verify-token",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "驗證 Token（ACCESS/REFRESH）")
  public VerifyTokenResponse verifyToken(@Valid @RequestBody VerifyTokenRequest req) {
    return memberService.verifyToken(req);
  }
}
