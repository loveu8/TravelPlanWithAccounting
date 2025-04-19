package com.travelPlanWithAccounting.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 提供環境變數測試的控制器 */
@RestController
@Slf4j
@Tag(name = "Test", description = "測試用")
public class EnvTestController {

  @Value("${DB_HOST:not-found}")
  private String dbHost;

  @Value("${DB_PASSWORD:not-found}")
  private String dbPassword;

  /**
   * 測試環境變數是否正確載入
   *
   * @return 環境變數信息
   */
  @GetMapping("/api/env-test")
  @Operation(summary = "測試環境變數是否正確載入")
  public Map<String, String> testEnvVariables() {
    log.info("接收到環境變數測試請求");

    Map<String, String> envInfo = new HashMap<>();
    envInfo.put("DB_HOST", dbHost);

    // 對密碼進行安全處理
    String maskedPassword = maskPasswordIfPresent(dbPassword);
    envInfo.put("DB_PASSWORD", maskedPassword);

    // 增加環境檔案位置信息
    String envFileLocation = System.getProperty("user.dir") + "/.backendEnv";
    envInfo.put("ENV_FILE_LOCATION", envFileLocation);

    log.info("返回環境變數信息 (不含敏感信息)");
    return envInfo;
  }

  /**
   * 對密碼進行遮罩處理
   *
   * @param password 原始密碼
   * @return 遮罩後的密碼
   */
  private String maskPasswordIfPresent(String password) {
    if (password == null) {
      return "not-found";
    }

    if ("not-found".equals(password)) {
      return password;
    }

    return "******";
  }
}
