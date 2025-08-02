package com.travelPlanWithAccounting.service.security;

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
    String p = request.getServletPath();

    /* 唯一需要驗證的路徑 */
    if (MATCHER.match("/api/search/saveMemberPoi", p)) return false;

    /* 其餘 API 一律放行 ↓ */
    if (MATCHER.match("/api/members/**", p)) return true;
    if (MATCHER.match("/api/auth/**", p)) return true;
    if (MATCHER.match("/api/auth/otps/**", p)) return true;
    if (MATCHER.match("/api/auth/otps-test/**", p)) return true;
    if (MATCHER.match("/v3/api-docs/**", p)) return true;
    if (MATCHER.match("/swagger-ui/**", p)) return true;
    if (MATCHER.match("/actuator/health/**", p)) return true;

    return true; // 其它路徑一律不過濾
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
      jwtUtil.verify(auth.substring(7)); // 驗簽即可，通過就放行
      chain.doFilter(req, resp);

    } catch (Exception e) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }
}
