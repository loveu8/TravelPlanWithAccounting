package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.auth.AccessTokenResponse;
import com.travelPlanWithAccounting.service.dto.auth.AuthLogoutByRtRequest;
import com.travelPlanWithAccounting.service.dto.auth.AuthRefreshRequest;
import com.travelPlanWithAccounting.service.dto.auth.SimpleResult;
import com.travelPlanWithAccounting.service.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
      @Valid @RequestBody AuthRefreshRequest body, HttpServletRequest req) {

    String ip = firstNonBlank(body.getIp(), req.getHeader("X-Forwarded-For"));
    if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
    String ua = firstNonBlank(body.getUa(), req.getHeader("User-Agent"));

    return refreshTokenService.refreshAccessToken(body.getRefreshToken(), ip, ua);
  }

  @PostMapping("/logout")
  @Operation(summary = "登出：由 refreshToken 判斷平台並撤銷（MEM）")
  public SimpleResult logout(@Valid @RequestBody AuthLogoutByRtRequest body) {
    boolean ok = refreshTokenService.logoutByRefreshToken(body.getRefreshToken());
    return new SimpleResult(ok);
  }

  private static String firstNonBlank(String a, String b) {
    if (a != null && !a.isBlank()) return a;
    return b;
  }
}
