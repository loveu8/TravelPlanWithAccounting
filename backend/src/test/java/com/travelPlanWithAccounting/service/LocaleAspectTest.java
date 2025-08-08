package com.travelPlanWithAccounting.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
@AutoConfigureMockMvc
class LocaleAspectTest {

  @Autowired private MockMvc mockMvc;

  @RestController
  static class LocaleTestController {
    @GetMapping("/locale")
    public String locale(Locale locale) {
      return locale.toLanguageTag();
    }
  }

  @Test
  void supportedLanguage() throws Exception {
    mockMvc
        .perform(get("/locale").header(HttpHeaders.ACCEPT_LANGUAGE, "en-US"))
        .andExpect(status().isOk())
        .andExpect(content().string("en-US"));
  }

  @Test
  void defaultLanguageWhenHeaderMissingOrUnsupported() throws Exception {
    mockMvc
        .perform(get("/locale"))
        .andExpect(status().isOk())
        .andExpect(content().string("zh-TW"));

    mockMvc
        .perform(get("/locale").header(HttpHeaders.ACCEPT_LANGUAGE, "fr-FR"))
        .andExpect(status().isOk())
        .andExpect(content().string("zh-TW"));
  }
}
