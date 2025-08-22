package com.travelPlanWithAccounting.service.aspect;

import com.travelPlanWithAccounting.service.controller.AuthContext;
import com.travelPlanWithAccounting.service.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessTokenAspect {
  private final JwtUtil jwtUtil;
  private final AuthContext authContext;

  @Around("@annotation(com.travelPlanWithAccounting.service.security.AccessTokenRequired)")
  public Object verify(ProceedingJoinPoint pjp) throws Throwable {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) return pjp.proceed();

    HttpServletRequest req = attrs.getRequest();
    HttpServletResponse resp = attrs.getResponse();
    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
    try {
      if (auth == null || !auth.startsWith("Bearer ")) {
        if (resp != null) resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return null;
      }
      var jws = jwtUtil.verify(auth.substring(7));
      UUID id = UUID.fromString(jws.getPayload().getSubject());
      authContext.setCurrentMemberId(id);
    } catch (JwtException | IllegalArgumentException e) {
      if (resp != null) resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }
    // 業務例外不應被吞掉，直接向外拋出
    try {
      return pjp.proceed();
    } finally {
      authContext.clear();
    }
  }
}
