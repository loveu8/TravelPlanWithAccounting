package com.travelPlanWithAccounting.service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvTestController {

    @Value("${DB_HOST:not-found}")
    private String dbHost;

    @Value("${DB_PASSWORD:not-found}")
    private String dbPassword;

    @GetMapping("/api/env-test")
    public Map<String, String> testEnvVariables() {
        Map<String, String> envInfo = new HashMap<>();
        envInfo.put("DB_HOST", dbHost);
        envInfo.put("DB_PASSWORD", dbPassword != null ? "******" : "not-found");
        envInfo.put("ENV_FILE_LOCATION", System.getProperty("user.dir") + "/.backendEnv");
        return envInfo;
    }
}