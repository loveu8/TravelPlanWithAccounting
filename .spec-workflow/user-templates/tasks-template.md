# Tasks Document

> **工作流程**：SDD → BDD → TDD → 實作 → Hardening → Release。所有任務描述一律使用繁體中文，但必須保留 File/_Prompt/_Leverage/_Requirements 等欄位，確保 Dashboard 可以正確解析。

---

## 任務狀態標記

- `[ ]` - 待處理
- `[-]` - 進行中
- `[x]` - 已完成

啟動任務立刻改成 `[-]`，僅在程式碼、測試與文件都完成後才標記 `[x]`，並同步 `tasks.md` 與 `spec-status`。

---

## 任務欄位說明

- `File:` 實際修改的檔案或目錄。
- `_Prompt:` 以 `Role | Task | Restrictions | Success` 格式提供 AI / 協作者上下文，可混合中英文。
- `_Leverage:` 必須重用的規格、設計或工具。
- `_Requirements:` 相對應的需求或 NFR 代碼。

缺少任何欄位都會讓 Spec Workflow 失效。

---

## Phase 1: SDD - 規格模型（已完成）

**狀態**：Formulation 已完成  
**產物**：
- Data Model：`spec/erm.dbml`
- Functional Model：`spec/features/{{featureSlug}}.feature`

下一步：依據 Gherkin 規格在 Phase 2 建立可執行步驟。

---

## Phase 2: BDD - Step Definitions（Red）

- [ ] 1. 建立 Step Definition 模組骨架
  - File: `tests/steps/{{featureSlug}}Steps.[ts|py|java]`
  - 目的：載入 Feature、建立共享 world state、註冊 hooks
  - _Leverage: spec/features/{{featureSlug}}.feature, tech.md, structure.md_
  - _Requirements: BDD-2.1_
  - _Prompt: Role: BDD 工程師 | Task: 建立步驟模組與情境容器 | Restrictions: 必須使用專案指定的 Cucumber 執行器，保持步驟獨立 | Success: Feature glue 能載入並提供可重用的情境物件_

- [ ] 2. 綁定全部 `@Given` 步驟
  - File: `tests/steps/{{featureSlug}}Steps.[ts|py|java]`
  - 目的：初始化測試資料與環境，準確對應每個 `Given`
  - _Leverage: spec/features/{{featureSlug}}.feature (Given 區段), tests/fixtures/*_
  - _Requirements: BDD-2.2_
  - _Prompt: Role: 測試工程師 | Task: 將每個 Given regex 映射到決定性資料建構器 | Restrictions: 禁止隱藏副作用，優先使用共用 fixture | Success: 所有 Given 步驟皆有具體實作且可重複使用_

- [ ] 3. 綁定全部 `@When` 步驟
  - File: `tests/steps/{{featureSlug}}Steps.[ts|py|java]`
  - 目的：呼叫公開服務/轉接器並維護 transient state
  - _Leverage: tests/helpers/*, src/application/services/*_
  - _Requirements: BDD-2.3_
  - _Prompt: Role: 領域熟悉的 QA | Task: 將 When 敘述轉成服務或 adapter 呼叫並捕捉結果 | Restrictions: 不可直接操作 DB，必須遵守交易邊界 | Success: When 只改變情境狀態且僅透過公開 API_

- [ ] 4. 綁定全部 `@Then` 步驟與 Hooks
  - File: `tests/steps/{{featureSlug}}Steps.[ts|py|java]`
  - 目的：實作斷言、Before/After hooks、重置情境
  - _Leverage: tests/assertions/*, tests/helpers/*_
  - _Requirements: BDD-2.4_
  - _Prompt: Role: QA 工程師 | Task: 實作 Then 綁定與清理 hooks，涵蓋正反案例 | Restrictions: 斷言必須依公開契約，hook 要把狀態還原 | Success: 情境結果可重現且涵蓋所有期望_

- [ ] 5. 將情境接上 DI 與 CI
  - File: `tests/steps/{{featureSlug}}Steps.[ts|py|java]`, `src/config/container.ts`
  - 目的：確保 BDD 與正式環境共用相同 wiring
  - _Leverage: structure.md, src/config/*_
  - _Requirements: BDD-2.5_
  - _Prompt: Role: 整合工程師 | Task: 讓 Step Context 掛到 DI Graph 並在 CI 跑通 | Restrictions: 禁止自訂 wiring，必須沿用正式設定 | Success: CI 能以真實 wiring 執行完整 BDD 套件_

---

## Phase 3: TDD - Unit Tests（Red）

- [ ] 6. Domain 單元測試
  - File: `tests/domain/{{entity}}Model.test.[ts|py|cs]`
  - 目的：以測試鎖定 Entity/Value Object 的不變條件與邊界
  - _Leverage: spec/features 規則, design.md:[Domain Model]_
  - _Requirements: TDD-3.1_
  - _Prompt: Role: Domain TDD 實踐者 | Task: 為每個不變條件寫出失敗測試 | Restrictions: 需為純函式，禁止基礎建設依賴 | Success: 紅測完整描述所有規則與邊界_

- [ ] 7. Application / Service 單元測試
  - File: `tests/application/{{serviceName}}Service.test.[ts|py|java]`
  - 目的：利用 mock/stub 鎖定服務合約、錯誤路徑
  - _Leverage: design.md:[Service Implementation], tests/mocks/*_
  - _Requirements: TDD-3.2_
  - _Prompt: Role: 服務層工程師 | Task: 為成功與失敗路徑建立紅測並 mock 依賴 | Restrictions: 隔離 repository/adapter，驗證錯誤映射 | Success: 每個服務方法都有清楚紅測定義其行為_

- [ ] 8. Presentation / Adapter 單元測試
  - File: `tests/presentation/{{adapter}}.test.[ts|py|java]`
  - 目的：驗證 Controller/Resolver/CLI 的輸入輸出、驗證與錯誤處理
  - _Leverage: structure.md, tests/httpFixtures/*_
  - _Requirements: TDD-3.3_
  - _Prompt: Role: 介面工程師 | Task: 測試請求/回應映射、驗證訊息與狀態碼 | Restrictions: 以 stub 取代服務，避免實際網路 | Success: 介面契約以測試鎖定_

---

## Phase 4: Implementation（Green）

- [ ] 9. 實作 Domain Entities / Value Objects
  - File: `src/domain/{{entity}}.[ts|kt|cs]`
  - 目的：依測試實作不可變模型、工廠、mapper
  - _Leverage: tests/domain/{{entity}}Model.test.*, spec/erm.dbml_
  - _Requirements: Impl-4.1_
  - _Prompt: Role: Domain 模型作者 | Task: 實作 entity/value object 並維持不變條件 | Restrictions: 儘量保持純函式、避免副作用 | Success: 第 6 項測試全部轉綠且無額外特例_

- [ ] 10. 實作 Domain Services / Policies / Exceptions
  - File: `src/domain/services/{{policy}}Service.[ts|kt|cs]`, `src/domain/errors/*`
  - 目的：把規則、聚合行為、專屬錯誤完整落地
  - _Leverage: design.md:[Policies], spec/features 規則_
  - _Requirements: Impl-4.2_
  - _Prompt: Role: Domain 專家 | Task: 以資料結構消除特例並實作自訂例外 | Restrictions: 禁止深層巢狀條件，維持資料驅動 | Success: 所有規則可由服務層直接重用_

- [ ] 11. 實作 Repository 與基礎建設 Adapter
  - File: `src/domain/repositories/{{entity}}Repository.ts`, `src/infrastructure/repositories/*`
  - _Leverage: design.md:[Repository], tech.md:[Storage Strategy]_
  - _Requirements: Impl-4.3_
  - _Prompt: Role: Repository 實作者 | Task: 定義介面並提供具交易性的 adapter | Restrictions: Domain 模組保持純淨，重用既有資料來源工具 | Success: Repository 易於 mock，實作通過整合 smoke test_

- [ ] 12. 實作 Application Service / Use Case
  - File: `src/application/services/{{useCase}}Service.[ts|kt|cs]`
  - 目的：透過 DI 串聯 domain 與 repository 並輸出 DTO
  - _Leverage: tests/application/{{serviceName}}Service.test.*, design.md_
  - _Requirements: Impl-4.4_
  - _Prompt: Role: 應用層工程師 | Task: 組合相依並套用 domain 規則 | Restrictions: 禁止重複 domain 邏輯，必須使用 DI | Success: 第 7 項測試全部轉綠_

- [ ] 13. 實作 Presentation Adapters
  - File: `src/presentation/{{adapter}}.[ts|kt|py]`
  - 目的：公開 REST/GraphQL/CLI 介面、驗證與錯誤映射
  - _Leverage: structure.md:[Interface], tests/presentation/{{adapter}}.test.*_
  - _Requirements: Impl-4.5_
  - _Prompt: Role: 介面工程師 | Task: 將外部契約映射到應用層服務 | Restrictions: 必須遵守 API 版本與文件的回應格式 | Success: 合約測試（第 8 項）全數通過且無手動調整_

- [ ] 14. 串接跨切 Concern（DI / Config / Runtime）
  - File: `src/config/container.ts`, `src/config/runtime/*`
  - _Leverage: tech.md:[Runtime], docs/architecture.md_
  - _Requirements: Impl-4.6_
  - _Prompt: Role: 系統整合者 | Task: 設定 DI Graph、組態載入與排程 | Restrictions: 禁止循環依賴，需文件化環境覆寫 | Success: 應用在本機與 CI 皆能透過單一入口啟動_

- [ ] 15. Migration / 背景作業 / 自動化
  - File: `scripts/migrations/*`, `infra/pipelines/*`
  - _Leverage: tech.md:[Deployment], spec/features Examples_
  - _Requirements: Impl-4.7_
  - _Prompt: Role: 平台工程師 | Task: 建立資料遷移、批次作業與 CI/CD 流程 | Restrictions: 遷移需前後相容、Job 需具備冪等性 | Success: 遷移可安全在 staging/prod 執行並有 roll-forward 計畫_

---

## Phase 5: Refactor & Hardening

- [ ] 16. 程式碼清理、效能與靜態分析
  - Scope: 消除重複、套用 Lint/Format、優化熱點
  - _Leverage: scripts/lint.sh, scripts/typecheck.sh_
  - _Requirements: NFR (Code Quality & Performance)_
  - _Prompt: Role: 維運者 | Task: 重構以提升可讀性並跑 lint/typecheck/perf baseline | Restrictions: 不得改變行為，函式保持淺層 | Success: 靜態檢查通過且性能指標達標_

- [ ] 17. 可觀測性與告警
  - File: `src/observability/*`, `infra/monitoring/*`
  - 目的：新增結構化 log/metrics/traces 與告警
  - _Leverage: tech.md:[Observability], telemetry helpers_
  - _Requirements: NFR (Observability)_
  - _Prompt: Role: SRE | Task: 以業務 KPI 建立監控與告警 | Restrictions: 禁止記錄 PII，需說明抽樣策略 | Success: 監控面板與告警涵蓋所有關鍵流程_

- [ ] 18. 安全性、韌性與在地化
  - Scope: 威脅模型、授權/認證、節流、i18n 覆蓋
  - _Leverage: security.md, i18n.md_
  - _Requirements: NFR (Security & i18n)_
  - _Prompt: Role: 資安工程師 | Task: 驗證輸入、套用策略、確保多語訊息一致 | Restrictions: 禁止明文秘密，語系需符合規範 | Success: 安全審查通過且語系快照同步_

- [ ] 19. Regression Safety Net
  - File: `tests/regression/*`, `tests/snapshots/*`
  - _Leverage: tests/helpers/testUtils.ts, incident notes_
  - _Requirements: NFR (Regression Prevention)_
  - _Prompt: Role: QA Owner | Task: 為歷史缺陷與邊界新增回歸/變異測試 | Restrictions: CI 執行時間不可爆表，需記錄任何 flake | Success: 回歸套件在 CI 穩定綠燈_

---

## Phase 6: 驗證與發布

- [ ] 20. End-to-End 驗證
  - File: `tests/e2e/{{featureSlug}}.spec.[ts|py]`
  - _Leverage: tests/steps, tests/helpers/*_
  - _Requirements: QA-5.2_
  - _Prompt: Role: 自動化 QA | Task: 以真實 wiring 覆蓋主要/替代/失敗旅程 | Restrictions: 重用共享步驟、避免脆弱 selector | Success: E2E 套件在 CI 穩定無 flake_

- [ ] 21. 向後相容與 Rollout 計畫
  - Scope: API/CLI 契約、資料遷移、Feature Flag、Canary
  - _Leverage: design.md:[Compatibility], ops/runbook.md_
  - _Requirements: NFR (Backward Compatibility)_
  - _Prompt: Role: 發布工程師 | Task: 撰寫相容矩陣與 rollout/rollback 流程 | Restrictions: 不得影響既有使用者，flag 預設關閉 | Success: rollout checklist 經審核通過並附證據_

- [ ] 22. 文件與 Changelog 更新
  - File: `docs/features/{{featureSlug}}.md`, `CHANGELOG.md`, `tasks.md`
  - _Leverage: requirements-template.md, design-template.md_
  - _Requirements: NFR (Documentation)_
  - _Prompt: Role: 技術寫作者 | Task: 更新功能文件、變更日誌、tasks/spec-status | Restrictions: 使用 ASCII Markdown，附上 traceability 連結 | Success: 文件 PR 通過審查並無 TODO 佔位字串_

- [ ] 23. Release Readiness Review
  - Scope: CI 綠燈、Artifact 簽署、監控與通知就緒
  - _Leverage: release-checklist.md, ops/runbook.md_
  - _Requirements: NFR (Quality Assurance & Governance)_
  - _Prompt: Role: Release Manager | Task: 執行最終審查（CI、安全掃描、利害關係人確認） | Restrictions: 不得跳過檢查，需留下證據 | Success: 建立 release tag 並完成交接

---

## Traceability - Gherkin → Code → Tests

### Feature: `{{featureSlug}}`

| Gherkin Rule | Implementation | Test |
|--------------|----------------|------|
| Rule: {{ruleName}} | `src/domain/{{entity}}.{ts}` / `src/application/{{service}}Service.{ts}` | `tests/domain/{{entity}}Model.test.ts` |
| Rule: {{ruleName}} | `src/presentation/{{adapter}}.{ts}` | `tests/e2e/{{featureSlug}}.spec.ts` |

### Example: `{{exampleName}}`

| Gherkin Step | Step Definition | Assertion Source |
|--------------|-----------------|------------------|
| `Given ...` | `@Given("...")` in `{{featureSlug}}Steps` | Fixture builder |
| `When ...` | `@When("...")` in `{{featureSlug}}Steps` | Service call result |
| `Then ...` | `@Then("...")` in `{{featureSlug}}Steps` | Assertion helper |

---

## 備註與提醒

- SDD（DBML + Gherkin）是唯一真相來源，不要在程式碼中重新定義規則。
- 優先調整資料結構來消除特例，少用 `if/else` 堆積複雜度。
- 每完成任務立刻更新 `tasks.md` 與 `spec-status`，並保存驗證證據（測試輸出、截圖）。

---

## Definition of Done Checklist

- [ ] 任務狀態皆正確並連回對應 PR / Commit
- [ ] 每條 Gherkin Rule / Example 都能對應到實作與自動化測試
- [ ] Unit / Integration / E2E 套件於 CI 維持綠燈且可重複
- [ ] Lint / Format / Type-check / Security Scan 全數通過
- [ ] 文件、Changelog、Rollout 計畫、Runbook 已更新
- [ ] TODO / FIXME 已移除或建立正式 Issue
- [ ] Release Checklist 已簽核並附上 rollback 計畫

---

## 驗證指令（範例）

- `npm run lint && npm run typecheck`
- `npm test`
- `npm run test:e2e`
- `npm run spec-status && cat tasks.md`

---

## Linus 提醒

1. 任務必須解決真實問題，別為想像中的需求堆特例。
2. 先用資料結構消滅邊界情況，再考慮條件判斷。
3. Never break userspace；向後相容是鐵律。
