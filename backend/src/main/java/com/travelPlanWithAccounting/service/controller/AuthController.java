package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.auth.AuthLogoutByRtRequest;
import com.travelPlanWithAccounting.service.dto.auth.AuthRefreshRequest;
import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
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
  @Operation(summary = "Refresh Token Rotation（MEM）- body 版")
  public AuthResponse refresh(@Valid @RequestBody AuthRefreshRequest body, HttpServletRequest req) {

    String clientId =
        (body.getClientId() == null || body.getClientId().isBlank()) ? "web" : body.getClientId();

    // IP / UA：若 body 沒帶，就從 request 取得（支援經 Proxy）
    String ip =
        (body.getIp() != null && !body.getIp().isBlank())
            ? body.getIp()
            : firstNonBlank(req.getHeader("X-Forwarded-For"), req.getRemoteAddr());

    String ua =
        (body.getUa() != null && !body.getUa().isBlank())
            ? body.getUa()
            : req.getHeader("User-Agent");

    return refreshTokenService.rotateForMember(body.getRefreshToken(), clientId, ip, ua);
  }

  @PostMapping("/logout")
  @Operation(summary = "登出：由 refreshToken 判斷目前平台並撤銷（MEM）")
  public void logout(@Valid @RequestBody AuthLogoutByRtRequest body) {
    // 以這顆 RT 找出 ownerId + clientId，撤銷該平台全部 RT
    refreshTokenService.logoutByRefreshToken(body.getRefreshToken());
  }

  private static String firstNonBlank(String a, String b) {
    if (a != null && !a.isBlank()) return a;
    return b;
  }
}
