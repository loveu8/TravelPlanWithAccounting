package com.travelPlanWithAccounting.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Slf4j
public class ServiceApplication {

  public static void main(String[] args) {
    loadEnv();
    SpringApplication.run(ServiceApplication.class, args);
  }

  /** 負責從環境檔案中載入變數，並優先於系統環境變數 */
  private static void loadEnv() {
    Path[] possiblePaths = {
      Paths.get(".backendEnv"), Paths.get("../.backendEnv"), Paths.get("/application/.backendEnv")
    };

    log.info("當前工作目錄: {}", System.getProperty("user.dir"));

    for (Path envPath : possiblePaths) {
      if (tryLoadEnvFile(envPath)) {
        return; // 成功載入一個環境檔案後即返回
      }
    }

    log.info("未找到環境變數檔案，將使用系統環境變數。");
  }

  /**
   * 嘗試從指定路徑載入環境變數檔案
   *
   * @param envPath 環境變數檔案路徑
   * @return 是否成功載入
   */
  private static boolean tryLoadEnvFile(Path envPath) {
    log.info("檢查路徑: {}", envPath.toAbsolutePath());

    if (!Files.exists(envPath)) {
      return false;
    }

    log.info("開始載入環境變數檔案：{}", envPath.toAbsolutePath());

    try (BufferedReader reader = Files.newBufferedReader(envPath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        processEnvLine(line.trim());
      }
      log.info("環境變數檔案載入完成。");
      return true;
    } catch (IOException e) {
      log.error("讀取環境變數檔案錯誤：{}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * 處理環境變數檔案中的單行
   *
   * @param line 一行環境變數設定
   */
  private static void processEnvLine(String line) {
    // 如果是空行或註解，略過
    if (line.isEmpty() || line.startsWith("#")) {
      return;
    }

    // 以第一個 "=" 為分界點，分割 key 與 value
    int separatorIndex = line.indexOf("=");
    if (separatorIndex == -1) {
      return;
    }

    String key = line.substring(0, separatorIndex).trim();
    String value = extractValue(line.substring(separatorIndex + 1).trim());

    // 記錄是否覆蓋環境變數
    logPropertySetting(key, value);

    // 設定至系統屬性
    System.setProperty(key, value);
  }

  /**
   * 從字串中提取實際的值，移除前後引號
   *
   * @param rawValue 原始值字串
   * @return 處理後的值
   */
  private static String extractValue(String rawValue) {
    if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
      return rawValue.substring(1, rawValue.length() - 1);
    }
    return rawValue;
  }

  /**
   * 記錄屬性設定的日誌，包含是否覆蓋原有環境變數
   *
   * @param key 屬性鍵
   * @param value 屬性值
   */
  private static void logPropertySetting(String key, String value) {
    String envValue = System.getenv(key);
    boolean isPasswordKey =
        key.contains("PASSWORD") || key.contains("SECRET") || key.contains("KEY");
    String maskedValue = isPasswordKey ? "********" : value;

    if (envValue != null) {
      String maskedEnvValue = isPasswordKey ? "********" : envValue;
      log.info("覆蓋環境變數 - Key: {}, 原始值: {}, 新值: {}", key, maskedEnvValue, maskedValue);
    } else {
      log.info("設定新屬性 - Key: {}, Value: {}", key, maskedValue);
    }
  }
}
