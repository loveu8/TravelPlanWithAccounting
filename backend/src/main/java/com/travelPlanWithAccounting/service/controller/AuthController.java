package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.auth.AccessTokenResponse;
import com.travelPlanWithAccounting.service.dto.auth.AuthLogoutByRtRequest;
import com.travelPlanWithAccounting.service.dto.auth.AuthRefreshRequest;
import com.travelPlanWithAccounting.service.dto.auth.SimpleResult;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenRequest;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenResponse;
import com.travelPlanWithAccounting.service.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth")
@RequestMapping("/api/auth")
public class AuthController {

  private final RefreshTokenService refreshTokenService;

  @PostMapping("/refresh")
  @Operation(summary = "用 Refresh-Token 換新 Access-Token（MEM）")
  public AccessTokenResponse refresh(
      @Valid @RequestBody AuthRefreshRequest body,
      @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String lang) {
    return refreshTokenService.refreshAccessToken(
        body.getRefreshToken(), body.getIp(), body.getUa(), lang);
  }

  @PostMapping("/logout")
  @Operation(summary = "登出：由 refreshToken 判斷平台並撤銷（MEM）")
  public SimpleResult logout(
      @Valid @RequestBody AuthLogoutByRtRequest body,
      @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String lang) {
    boolean ok = refreshTokenService.logoutByRefreshToken(body.getRefreshToken(), lang);
    return new SimpleResult(ok);
  }

  @PostMapping("/verify-token")
  @Operation(summary = "驗證 Token（ACCESS/REFRESH）")
  public VerifyTokenResponse verifyToken(
      @Valid @RequestBody VerifyTokenRequest req,
      @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String lang) {
    return refreshTokenService.verifyToken(req, lang);
  }
}
