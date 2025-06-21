package com.travelPlanWithAccounting.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupLogger implements CommandLineRunner {

  private final Environment env;

  public StartupLogger(Environment env) {
    this.env = env;
  }

  @Override
  public void run(String... args) {
    String[] profiles = env.getActiveProfiles();
    if (profiles.length == 0) {
      log.info("==== 啟動執行環境 (Spring Profile): default ====");
    } else {
      log.info("==== 啟動執行環境 (Spring Profile): {} ====", String.join(",", profiles));
    }
  }
}
