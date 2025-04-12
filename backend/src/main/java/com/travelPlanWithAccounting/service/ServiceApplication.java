package com.travelPlanWithAccounting.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(ServiceApplication.class, args);
	}

	private static void loadEnv() {
		// 取得 .env 檔案路徑（預設在專案根目錄）
		Path envPath = Paths.get(".backendEnv");
		System.out.println("當前工作目錄: " + System.getProperty("user.dir"));
		System.out.println("完整路徑: " + envPath.toAbsolutePath());

		if (Files.exists(envPath)) {
			try (BufferedReader reader = Files.newBufferedReader(envPath)) {
				String line;
				System.out.println("開始載入 .backendEnv 檔案內容：");
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					// 印出每一行的內容
					// System.out.println("原始內容: " + line);
					// 如果是空行或註解，略過
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					// 以第一個 "=" 為分界點，分割 key 與 value
					int idx = line.indexOf("=");
					if (idx != -1) {
						String key = line.substring(0, idx).trim();
						String value = line.substring(idx + 1).trim();
						// 如果 value 包含前後雙引號，移除它們
						if (value.startsWith("\"") && value.endsWith("\"")) {
							value = value.substring(1, value.length() - 1);
						}
						// 設定至系統屬性，使 Spring 可以取得
						System.setProperty(key, value);
						// 印出解析出的 key 與 value
						System.out.println("讀取到的設定 - Key: " + key + ", Value: " + value);
					}
				}
				System.out.println(".env 檔案載入完成。");
			} catch (IOException e) {
				System.err.println("讀取 .env 檔案錯誤：" + e.getMessage());
			}
		} else {
			System.out.println("找不到 .env 檔案。");
		}
	}

}