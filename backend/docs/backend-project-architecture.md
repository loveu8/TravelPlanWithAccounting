# Backend Project Architecture Snapshot

- Scan date: 2026-04-07
- Scope: `backend/`
- Scanner: Codex (`scan-project-backend`)
- Execution mode: `repo-root`
- Evidence path base: `backend/...`
- Source commit at scan start: `8294539`

## 1. Summary
- Backend style classification: `Layered monolith`
- Confidence: `High`
- Notes:
  - 目前為單一 Spring Boot 服務（單一 `backend/pom.xml`、單一 `ServiceApplication` 入口）。
  - 主要以技術層分包（`controller/service/repository/dto/mapper/config/aspect`）並搭配 `security/message/exception/util` 等跨領域支援。
  - 另有 `factory/model/handler/constant` 套件支援外部 API、資料模型與例外流程。

## 2. Confirmed facts
- Build/runtime baseline:
  - Spring Boot parent `4.0.4`、Java `25`、artifact `backend-service`。
  - 依賴包含 Web、Data JPA、Validation、Security、Mail、PostgreSQL、springdoc、jjwt。
  - Evidence files:
    - `backend/pom.xml`
    - `backend/src/main/resources/application.properties`
  - Evidence commands:
    - `sed -n '1,260p' backend/pom.xml`
    - `sed -n '1,240p' backend/src/main/resources/application.properties`
- Package/layer map:
  - 主 package root 為 `com.travelPlanWithAccounting.service`，主程式 `ServiceApplication` 位於 root package。
  - 技術分層目錄存在：`controller/`, `service/`, `repository/`, `dto/`, `mapper/`, `entity/`。
  - 橫切與支援目錄存在：`config/`, `aspect/`, `security/`, `exception/`, `message/`, `util/`, `validator/`, `constant/`, `factory/`, `handler/`, `model/`。
  - Evidence files:
    - `backend/src/main/java/com/travelPlanWithAccounting/service/ServiceApplication.java`
  - Evidence commands:
    - `find backend/src/main/java -maxdepth 5 -type d`
    - `rg --files backend/src/main/java | head -n 80`
- API/security/i18n/response conventions:
  - 支援 `Accept-Language` 的 i18n 行為與 zh-TW/en-US 支援由 backend README 明示。
  - `application.properties` 設定 `spring.messages.basename=classpath:i18n/messages`，且 DB 設定採 `ddl-auto=validate`。
  - Evidence files:
    - `backend/README.md`
    - `backend/src/main/resources/application.properties`
  - Evidence commands:
    - `sed -n '1,260p' backend/README.md`
    - `sed -n '1,240p' backend/src/main/resources/application.properties`

## 3. Inferred facts
- Inference:
  - 專案目前以技術層分層為主要架構，業務流程預期集中在 `service/` 並由 `controller` 對外提供 API。
- Why inferred:
  - 目錄命名與單一 deployable 形態符合 layered monolith，且 `src/test` 目前可見測試入口仍以整體應用啟動測試為主。
- Evidence files:
  - `backend/src/test/java/com/travelPlanWithAccounting/service/ServiceApplicationTests.java`
- Evidence commands:
  - `find backend/src/test/java -maxdepth 5 -type d`
  - `rg --files backend/src/test/java | head -n 80`

## 4. Unknowns (`REQUIRES CONFIRMATION`)
- Item:
  - 生產環境部署拓樸（單機、compose、K8s、或其他）與正式 profile 切換策略。
- Why unknown:
  - 本次僅掃描到本地/容器啟動腳本與一般設定，未看到 CI/CD 與 infra 部署來源。
- How to verify:
  - 補查 CI workflow、infra repository 或部署文件。

- Item:
  - 資料庫 migration 流程由何者主責（repo 內 migration tool 或外部流程）。
- Why unknown:
  - `pom.xml` 未見 Flyway/Liquibase 依賴，且 `application.properties` 僅顯示 `ddl-auto=validate`。
- How to verify:
  - 與 DevOps/DBA 確認 migration 來源與發版流程。

## 5. Command map (confirmed only)
- Build:
  - `cd backend && ./mvnw clean install`
- Test:
  - `cd backend && ./mvnw test`
- Run:
  - `cd backend && sh up.sh`
  - `cd backend && sh down.sh`
- Tooling:
  - `cd backend && sh build.sh`
  - `cd backend && ./mvnw`
- Evidence files:
  - `backend/AGENTS.md`
  - `backend/README.md`
  - `backend/mvnw`

## 5.1 Command execution notes
- Successful commands:
  - `find backend -name AGENTS.md -print`
  - `find backend -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|application.properties|README|docs'`
  - `find backend/src/main/java -maxdepth 5 -type d | head -n 200`
  - `find backend/src/test/java -maxdepth 5 -type d | head -n 200`
  - `rg --files backend/src/main/java | head -n 80`
  - `rg --files backend/src/test/java | head -n 80`
  - `sed -n '1,260p' backend/pom.xml`
  - `sed -n '1,260p' backend/README.md`
  - `sed -n '1,220p' backend/src/main/java/com/travelPlanWithAccounting/service/ServiceApplication.java`
  - `sed -n '1,240p' backend/src/main/resources/application.properties`
- Failed commands and reasons:
  - 無。
- Unverified areas caused by command limitations:
  - 本次屬架構掃描，未執行 `./mvnw test` / `./mvnw clean install`（無程式行為修改）。

## 6. Risks and constraints
- Contract/DB/security/dependency risks:
  - 目前掃描未觸及 API contract 測試與 DB migration 流程，若後續更動 repository/entity 需先確認 migration owner。
- Operational risks:
  - 主程式包含 `.backendEnv` 路徑嘗試邏輯；不同工作目錄可能造成設定來源差異。

## 7. Backend pattern comparison (GitHub baselines)
- Closest baseline:
  - `Layered monolith`（單一 Spring Boot deployable + 技術分層套件）。
- Similarities:
  - 標準 Maven 目錄 (`src/main`, `src/test`)。
  - API/Service/Repository 分層存在。
- Differences:
  - 具備較多客製 cross-cutting 與外部 API/郵件整合 supporting package。
- Potential implications:
  - 維持單體分層有利於快速開發，但需持續維持 service 邊界，避免跨 domain 耦合成長。

## 8. Convention compliance check
- Maven standard layout: `Pass`
  - Evidence:
    - `backend/src/main/java`
    - `backend/src/main/resources`
    - `backend/src/test/java`
- Spring package/root scanning convention: `Pass`
  - Evidence:
    - `ServiceApplication` 位於 root package `com.travelPlanWithAccounting.service`，其他組件位於其子套件。
- GitHub discoverability (README/docs/commands): `Pass`
  - Evidence:
    - `backend/README.md` 提供環境與啟動方式。
    - `backend/docs/` 具 API 與流程文件。
    - `backend/mvnw` 作為建置入口存在。

## 9. How to update this snapshot
1. Re-run backend scan steps from skill.
2. Refresh changed sections only.
3. Keep Confirmed/Inferred/Unknown separation strict.
4. Update scan date and source commit.

## 10. Citation format requirement
- Use repository-relative file path citations when reporting scan results.
- Recommended format: `【F:<path>†Lx-Ly】`.

## 11. AI usability checklist
- Keep headings unchanged for downstream parsing.
- Keep status values deterministic: `Pass | Partial | Fail`.
- Keep unknowns explicit with `REQUIRES CONFIRMATION`.
