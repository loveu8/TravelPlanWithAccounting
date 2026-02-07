# JDK 25 + Spring Boot 4.0 升級計畫書

## 1. 目標與範圍
- **目標**：將後端服務升級至 JDK 25 與 Spring Boot 4.0，維持現有 API 行為與資料庫相容性。
- **範圍**：
  - Maven 建置與執行環境
  - Spring Boot 與相關相依套件
  - Docker/Docker Compose 映像與啟動流程
  - CI/CD（如有）之建置與測試流程

## 2. 現況盤點（基準資訊）
- **JDK**：`java.version=25`（`pom.xml`）
- **Spring Boot**：`4.0.0`（`spring-boot-starter-parent`）
- **Docker Base Image**：`eclipse-temurin:25-jre-alpine`（建置/運行）
- **關鍵相依**：
  - `springdoc-openapi`：`3.0.0`
  - `jjwt`：`0.13.0`
  - `Apache HttpClient`：`4.5.14`

> 上述資訊作為升級前基準點，升級後需重新驗證行為與相容性。

## 3. 風險與前置確認
1. **Spring Boot 4.0 相容性**
   - 確認 Spring Boot 4.0 的正式版與維護策略。
   - 檢查 Boot 4 對 Jakarta EE 版本（例如 Jakarta EE 11）要求與 API 變更。
2. **JDK 25 支援度**
   - 確認 JDK 25 是否為團隊可接受的版本（LTS 與否）。
   - 確認部署環境與第三方服務對 JDK 25 的支援狀況。
3. **依賴套件升級影響**
   - 核心依賴（springdoc、jjwt、Apache HttpClient 等）需確認支援 Boot 4。
   - 驗證序列化、Hibernate/JPA、Spring Security 版本差異。

## 4. 應用程式層面變更（預估）
### 4.1 Maven / Java 設定
- 更新 `pom.xml`：
  - `spring-boot-starter-parent` 升級至 4.0.x。
  - `maven-compiler-plugin` 與 `java.version` 設定為 25。
  - 若使用 Maven Toolchains，新增/更新 JDK 25 toolchain。

### 4.2 Spring Boot 與依賴套件
- 依賴版本需配合 Boot 4 BOM，若有手動鎖版請逐一檢查相容性。
- 重點檢查清單：
  - **springdoc-openapi**：確認支援 Boot 4 的版本與設定變更。
  - **jjwt**：驗證 API/配置變動、key format 相容性。
  - **Apache HttpClient**：確認 4.x/5.x 的支援策略，必要時升級。
  - **Hibernate/JPA**：確認 Jakarta EE 版本與 dialect 行為變化。
  - **Spring Security**：檢視 SecurityFilterChain、Csrf、Matchers 行為是否變更。

### 4.3 程式碼與設定檔調整
- 搜尋是否存在已棄用 API（例如 Spring Boot 4 移除的配置）。
- 確認 `application-*.yml` 設定是否需改名或語意改變。
- 若 Boot 4 需要 Jakarta EE 11：
  - 確認所有 `jakarta.*` 套件引用一致。
  - 進行必要的 import/annotation 更新。

## 5. Docker / 部署環境變更（預估）
### 5.1 Dockerfile
- 基礎映像更換為 JDK 25：
  - 例如 `eclipse-temurin:25-jre` 或 `eclipse-temurin:25-jdk`。
- 若有分離 build/runtime stage：
  - build stage 使用 JDK 25
  - runtime stage 使用 JRE 25

### 5.2 docker-compose.yml
- 更新 image tag 或 build context 對應 JDK 25。
- 驗證 JVM 參數、記憶體限制與健康檢查。

## 6. 計畫驗證與確認（Review Check）
### 6.1 版本與相依確認清單
- [ ] `pom.xml` 中 `spring-boot-starter-parent` 升至 4.0.x。
- [ ] `java.version` 由 21 改為 25。
- [ ] `springdoc-openapi` 版本確認支援 Boot 4。
- [ ] `jjwt` 版本與既有 token 兼容。
- [ ] `httpclient` 版本評估是否需升級到 5.x。
- [ ] `Dockerfile` base image 更新為 JDK 25。

### 6.2 設定檔與程式碼檢查
- [ ] `application-*.yml` 中 Boot 4 移除/改名的設定已調整。
- [ ] `jakarta.*` 套件引用與 Jakarta EE 版本一致。
- [ ] SecurityFilterChain / WebSecurityConfigurer 變更已對應更新。

### 6.3 驗證流程（建議檢查指令）
- [ ] `mvn clean install` 或 `./mvnw clean install` 通過。
- [ ] `docker build` 可建立映像且正常啟動。
- [ ] 基本 API（登入、行程、熱門景點）可通過冒煙測試。
- [ ] JWT 驗證流程未破壞（AccessTokenAspect / JwtUtil 正常）。

## 7. 測試與驗證計畫
1. **編譯與單元測試**
   - `mvn clean install` 或 `./mvnw clean install`
2. **啟動與冒煙測試**
   - 檢查健康檢查、主要 API 端點與 JWT 驗證流程。
3. **回歸測試**
   - 針對關鍵流程：會員登入、行程查詢、熱門景點等。

## 8. 交付與里程碑
1. **調查與版本評估**（1~2 天）
2. **升級與修正**（2~4 天）
3. **測試與回歸**（1~3 天）
4. **正式部署**（與維運窗口協調）

## 9. 交付物
- 變更後的 `pom.xml`、`Dockerfile`、`docker-compose.yml`
- 測試報告與驗證紀錄
- 升級說明文件（本文件更新為最終版）
