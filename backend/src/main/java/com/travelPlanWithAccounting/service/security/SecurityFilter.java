package com.travelPlanWithAccounting.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private static final AntPathMatcher MATCHER = new AntPathMatcher();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // 用 getServletPath 比較直觀，不含 domain/query
    String p = request.getServletPath();

    // --- 身分前置 / OTP / 刷新 / 登出（不需要攜帶 AT） ---
    if (MATCHER.match("/api/members/pre-auth-flow", p)) return true;
    if (MATCHER.match("/api/members/auth-flow", p)) return true;
    if (MATCHER.match("/api/members/verify-token", p)) return true;

    // 這兩個一定要是新路徑 (/api/auth/...) 才會放行
    if (MATCHER.match("/api/auth/refresh", p)) return true;
    if (MATCHER.match("/api/auth/logout", p)) return true;

    if (MATCHER.match("/api/auth/otps/**", p)) return true;
    if (MATCHER.match("/api/auth/otps-test/**", p)) return true; // dev only

    // 可選：Swagger / 健康檢查
    if (MATCHER.match("/v3/api-docs/**", p)) return true;
    if (MATCHER.match("/swagger-ui/**", p)) return true;
    if (MATCHER.match("/actuator/health/**", p)) return true;

    return false;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
      throws ServletException, IOException {
    try {
      String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
      if (auth == null || !auth.startsWith("Bearer ")) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
      String jwt = auth.substring("Bearer ".length());
      Jws<Claims> jws = jwtUtil.verify(jwt); // 0.12.x：verify().getPayload()
      // TODO: 將 sub/role/jti 放入 SecurityContext
      chain.doFilter(req, resp);
    } catch (Exception e) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }
}
