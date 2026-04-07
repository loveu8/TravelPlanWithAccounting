# Backend Project Architecture Snapshot

- Scan date: 2026-04-07
- Scope: `backend/`
- Scanner: Codex (`scan-project-backend`)
- Execution mode: `repo-root`
- Evidence path base: `backend/...`

## 1. Summary
- Backend style classification: `Layered monolith`
- Confidence: `High`
- Notes:
  - 目前為單一 Spring Boot 服務（單一 `pom.xml` + 單一主程式類別），且以技術層分包（controller/service/repository/dto/mapper/config/aspect）。
  - 專案同時包含 auth、i18n、統一 response wrapper、exception advice 與外部 API 整合（Google/Mail）能力。

## 2. Confirmed facts
- Build/runtime baseline:
  - Spring Boot parent `4.0.4`、Java `25`，artifact 為 `backend-service`。
  - 依賴包含 JPA、Validation、Security、Mail、PostgreSQL、springdoc、jjwt。
  - Evidence files:
    - `backend/pom.xml`
    - `backend/src/main/resources/application.properties`
  - Evidence commands:
    - `find backend -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|README|docs'`
    - `sed -n '1,220p' backend/pom.xml`
    - `sed -n '1,220p' backend/src/main/resources/application.properties`
- Package/layer map:
  - 主 package root 為 `com.travelPlanWithAccounting.service`，主程式 `ServiceApplication` 位於 root package。
  - 技術分層目錄存在：`controller/`, `service/`, `repository/`, `dto/`, `mapper/`, `config/`, `aspect/`, `exception/`, `message/`, `entity/`, `security/`, `util/`。
  - Evidence files:
    - `backend/src/main/java/com/travelPlanWithAccounting/service/ServiceApplication.java`
  - Evidence commands:
    - `find backend/src/main/java -maxdepth 4 -type d`
    - `rg --files backend/src/main/java | head -n 40`
- API/security/i18n/response conventions:
  - 全域回應包裝由 `ResponseBodyWrapperAdvice` 統一包成 `RestResponse`。
  - 例外由 `GlobalExceptionHandler` 統一轉為 `RestResponse`。
  - 驗證 access token 使用 `@AccessTokenRequired` + `AccessTokenAspect` + `JwtUtil`。
  - i18n locale 由 `LocaleAspect` 讀取 `Accept-Language`，僅支援 `Locale.TAIWAN` / `Locale.US`。
  - Evidence files:
    - `backend/src/main/java/com/travelPlanWithAccounting/service/config/advice/ResponseBodyWrapperAdvice.java`
    - `backend/src/main/java/com/travelPlanWithAccounting/service/config/advice/GlobalExceptionHandler.java`
    - `backend/src/main/java/com/travelPlanWithAccounting/service/security/AccessTokenRequired.java`
    - `backend/src/main/java/com/travelPlanWithAccounting/service/aspect/AccessTokenAspect.java`
    - `backend/src/main/java/com/travelPlanWithAccounting/service/aspect/LocaleAspect.java`
    - `backend/src/main/java/com/travelPlanWithAccounting/service/util/RestResponseUtils.java`
  - Evidence commands:
    - `sed -n '1,260p' backend/src/main/java/com/travelPlanWithAccounting/service/config/advice/ResponseBodyWrapperAdvice.java`
    - `sed -n '1,260p' backend/src/main/java/com/travelPlanWithAccounting/service/config/advice/GlobalExceptionHandler.java`
    - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/aspect/AccessTokenAspect.java`
    - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/aspect/LocaleAspect.java`

## 3. Inferred facts
- Inference:
  - 目前專案以「技術層分層」為核心，尚未切成 domain-module 或多服務；大多數業務邏輯應集中在 `service/`，controller 偏向 API entry。
- Why inferred:
  - 目錄命名與主要類別分布呈現典型 layered package；單一測試入口 `ServiceApplicationTests` 也符合單服務專案。
- Evidence files:
  - `backend/src/test/java/com/travelPlanWithAccounting/service/ServiceApplicationTests.java`
- Evidence commands:
  - `find backend/src/test/java -maxdepth 4 -type d`
  - `rg --files backend/src/test/java | head -n 40`

## 4. Unknowns (`REQUIRES CONFIRMATION`)
- Item:
  - 生產環境部署拓樸（單機、容器編排、或其他）與 profile 策略細節。
- Why unknown:
  - 掃描到 `application.properties` 與 README/docker scripts，但未看到完整部署說明或環境分層文件。
- How to verify:
  - 檢查 CI/CD pipeline 設定、infra repo、或補充 `backend/docs/` 部署文件。

- Item:
  - 資料庫 migration 管理工具（Flyway/Liquibase）是否在其他 repo 或 pipeline 管理。
- Why unknown:
  - `pom.xml` 未見 migration 依賴，且目前僅看到 `spring.jpa.hibernate.ddl-auto=validate`。
- How to verify:
  - 搜尋組織層級 migration 倉庫或確認 DBA/DevOps 流程。

## 5. Command map (confirmed only)
- Build:
  - `./mvnw clean install`（由 backend AGENTS 規範）
- Test:
  - `./mvnw test`（由 backend AGENTS 規範）
- Run:
  - `sh up.sh`（Linux）
  - `./up.bat`（Windows）
- Tooling:
  - `./mvnw` / `mvnw.cmd`
  - `sh build.sh` / `./build.bat`
- Evidence files:
  - `backend/AGENTS.md`
  - `backend/README.md`
  - `backend/mvnw`
  - `backend/mvnw.cmd`

## 5.1 Command execution notes
- Successful commands:
  - `find backend -name AGENTS.md -print`
  - `find backend -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|README|docs'`
  - `find backend/src/main/java -maxdepth 4 -type d`
  - `find backend/src/test/java -maxdepth 4 -type d`
  - `rg --files backend/src/main/java | head -n 40`
  - `rg --files backend/src/test/java | head -n 40`
  - `sed -n '1,220p' backend/pom.xml`
  - `sed -n '1,220p' backend/README.md`
  - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/ServiceApplication.java`
  - `sed -n '1,260p' backend/src/main/java/com/travelPlanWithAccounting/service/config/advice/ResponseBodyWrapperAdvice.java`
  - `sed -n '1,260p' backend/src/main/java/com/travelPlanWithAccounting/service/config/advice/GlobalExceptionHandler.java`
  - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/aspect/AccessTokenAspect.java`
  - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/aspect/LocaleAspect.java`
  - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/security/AccessTokenRequired.java`
  - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/util/RestResponseUtils.java`
  - `sed -n '1,220p' backend/src/main/resources/application.properties`
- Failed commands and reasons:
  - 無。
- Unverified areas caused by command limitations:
  - 未執行 `./mvnw test` / `./mvnw clean install`（本次為架構掃描任務，無程式碼行為變更）。

## 6. Risks and constraints
- Contract/DB/security/dependency risks:
  - `AccessTokenAspect` 在 token 不合法時直接回 `null` 並設 `401`，若 controller 或 advice 對此分支的預期不一致，可能出現可觀測行為差異（建議後續回歸測試確認）。
  - `spring.jpa.hibernate.ddl-auto=validate` 代表 schema 管理由外部流程決定；若 migration 管線不透明，容易在跨環境部署時出現 schema mismatch。
- Operational risks:
  - 讀取 `.backendEnv` 的啟動前載入邏輯位於主程式，若不同啟動工作目錄不一致，可能造成環境變數來源差異。

## 7. Backend pattern comparison (GitHub baselines)
- Closest baseline:
  - `Layered monolith`（接近 `spring-petclinic-rest` 類型）。
- Similarities:
  - 單一 Maven module、標準 `src/main/java` + `src/test/java`。
  - REST controller + service + repository 技術分層清楚。
- Differences:
  - 專案包含較多自訂 cross-cutting 機制（response wrapper advice、locale aspect、token aspect）與 travel domain 特化資源檔。
- Potential implications:
  - 維持分層單體可降低協作複雜度，但隨 domain 擴大後，可能需要更強的模組邊界紀律以避免 service 層耦合上升。

## 8. Convention compliance check
- Maven standard layout: `Pass`
  - Evidence:
    - `backend/src/main/java`
    - `backend/src/main/resources`
    - `backend/src/test/java`
- Spring package/root scanning convention: `Pass`
  - Evidence:
    - `ServiceApplication` 位於 root package `com.travelPlanWithAccounting.service`。
    - 主要元件位於其子套件（`controller`, `service`, `repository`, `config`, `aspect`）。
- GitHub discoverability (README/docs/commands): `Pass`
  - Evidence:
    - `backend/README.md` 含環境需求與啟動指令。
    - `backend/docs/` 有 API/流程文件。
    - `backend/mvnw` 與 `backend/mvnw.cmd` 可作為一致建置入口。

## 9. How to update this snapshot
1. Re-run backend scan steps from skill.
2. Refresh changed sections only.
3. Keep Confirmed/Inferred/Unknown separation strict.
4. Update scan date.

## 10. Citation format requirement
- Use repository-relative file path citations when reporting scan results.
- Recommended format: `【F:<path>†Lx-Ly】`.

## 11. AI usability checklist
- Keep headings unchanged for downstream parsing.
- Keep status values deterministic: `Pass | Partial | Fail`.
- Keep unknowns explicit with `REQUIRES CONFIRMATION`.
