package com.travelPlanWithAccounting.service.security;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travelPlanWithAccounting.service.aspect.AccessTokenAspect;
import com.travelPlanWithAccounting.service.controller.AuthContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(AccessTokenAspectTests.TestController.class)
@Import({AccessTokenAspect.class, AuthContextImpl.class})
class AccessTokenAspectTests {

  @Autowired private MockMvc mockMvc;
  @MockBean private JwtUtil jwtUtil;

  @Test
  void missingToken() throws Exception {
    mockMvc.perform(get("/test")).andExpect(status().isUnauthorized());
  }

  @Test
  void invalidToken() throws Exception {
    when(jwtUtil.verify("bad")).thenThrow(new RuntimeException());
    mockMvc
        .perform(get("/test").header(HttpHeaders.AUTHORIZATION, "Bearer bad"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void validToken() throws Exception {
    UUID id = UUID.randomUUID();
    Claims claims = Jwts.claims().subject(id.toString()).build();
    Jws<Claims> jws = mock(Jws.class);
    when(jws.getPayload()).thenReturn(claims);
    when(jwtUtil.verify("good")).thenReturn(jws);
    mockMvc
        .perform(get("/test").header(HttpHeaders.AUTHORIZATION, "Bearer good"))
        .andExpect(status().isOk());
  }

  @RestController
  static class TestController {
    private final AuthContext authContext;

    TestController(AuthContext authContext) {
      this.authContext = authContext;
    }

    @GetMapping("/test")
    @AccessTokenRequired
    String test() {
      return authContext.getCurrentMemberId().toString();
    }
  }
}
