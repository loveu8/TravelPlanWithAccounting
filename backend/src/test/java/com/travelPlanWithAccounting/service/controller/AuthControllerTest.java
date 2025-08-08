package com.travelPlanWithAccounting.service.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travelPlanWithAccounting.service.dto.auth.AccessTokenResponse;
import com.travelPlanWithAccounting.service.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RefreshTokenService refreshTokenService;

  @Test
  void refreshPassesAcceptLanguageHeader() throws Exception {
    when(refreshTokenService.refreshAccessToken("rt", "ip", "ua", "en-US"))
        .thenReturn(new AccessTokenResponse("at", 100L));

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-US")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"rt\",\"ip\":\"ip\",\"ua\":\"ua\"}"))
        .andExpect(status().isOk());

    verify(refreshTokenService).refreshAccessToken("rt", "ip", "ua", "en-US");
  }
}

