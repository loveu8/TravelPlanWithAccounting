# Tasks Document

> **開發流程（SDD + BDD + TDD + Hardening + Release）**：
> **本專案採用規格驅動開發（Specification-Driven Development），結合行為驅動開發（BDD）與測試驅動開發（TDD）方法，並加強非功能需求（Cross-Cutting Concerns）與發佈驗證。必須嚴格遵循以下順序：**
> 1. **SDD**：規格模型已完成（DBML + Gherkin）
> 2. **BDD Red**：實作 Step Definitions（測試失敗）
> 3. **TDD Red**：撰寫單元測試（測試失敗）
> 4. **Green**：實作功能（測試通過）
> 5. **Refactor**：重構程式碼品質（測試持續通過）
> 6. **Hardening**：強化非功能需求（觀測性、安全性、向後相容性）
> 7. **Release**：驗證與發佈前檢查

---

## 📋 Task Status Legend

- `[ ]` - Pending（待執行）
- `[-]` - In-Progress（執行中）
- `[x]` - Completed（已完成）

**重要提醒**：
- 每個任務開始前標記為 `[-]`
- 每個任務完成後**立即**標記為 `[x]`
- Section 標題（純數字如 1, 2, 3）不包含元數據，僅為組織用途
- Sub-tasks（如 1.1, 2.3）包含完整元數據（File, _Prompt:, _Leverage:, _Requirements:）

---

## 📝 Task Detail Fields（元數據格式規範）

每個子任務可包含以下可選欄位：

- **File:**（檔案路徑）- 需要建立或修改的檔案
  - 格式：`File: path/to/file.ext`

- **_Prompt:**（AI 提示詞）- **必須使用單行管道分隔格式**
  - 格式：`_Prompt: Role: [角色定位] | Task: [詳細任務描述] | Restrictions: [限制條件] | Success: [完成標準]_`
  - ❌ 錯誤：不可使用多行程式碼區塊（```）或多行格式
  - ✅ 正確：所有內容在同一行，使用 `|` 分隔四個部分

- **_Leverage:**（參考資源）- 單行或多行列舉
  - 格式：`_Leverage: 資源1、資源2、資源3_`

- **_Requirements:**（需求引用）- 引用對應的需求編號
  - 格式：`_Requirements: Req X (對應需求編號)_`

---

## ✅ Phase 1: SDD - Specification Model (Already Done)

**Status**: ✅ Completed by Formulation

**規格文件**:
- **資料模型（Data Model）**: `spec/erm.dbml`
  - 實體定義（Entities）
  - 屬性約束（Constraints）
  - 關聯關係（Relationships）

- **功能模型（Functional Model）**: `spec/features/[功能名稱].feature`
  - 功能描述（Feature）
  - 業務規則（Rules）
  - 驗收範例（Examples - Given/When/Then）

**Scenarios**:
[列出此 Feature 包含的所有 Scenario/Rule]
- Rule: [Rule 描述]
  - Example: [Example 標題]

**Next Step**: 開始 Phase 2（實作 BDD Step Definitions）

---

## 🔴 Phase 2: BDD - Step Definitions (Red)

> **目標**: 實作 Cucumber/Gherkin Step Definitions，將 Gherkin 場景轉換為可執行的測試程式碼。測試會失敗（因功能尚未實作）。

- [ ] 1. BDD Step Definitions Implementation
  建立 BDD 測試框架，實作 Given/When/Then step methods，執行測試確認紅燈狀態。

- [ ] 1.1 建立 Step Definitions 測試框架設定
  - File: [測試目錄]/steps/[功能名稱]Steps.[副檔名]
  - 範例路徑：`features/step_definitions/game_steps.rb`（Ruby/Cucumber）、`tests/steps/test_game_steps.py`（Python/Behave）、`src/test/java/.../steps/GameSteps.java`（Java/Cucumber）、`tests/features/step_definitions/game.steps.ts`（TypeScript/Cucumber）
  - 建立 Step Definitions 框架設定
  - 載入測試上下文（Test Context）與共享狀態（Shared State）
  - 注入待測元件（Controller/Service）或建立測試 API Client
  - 建立共享測試資料容器（World/Context Object）用於步驟間資料傳遞
  - 設定測試前置與清理（Before/After hooks）
  - _Leverage: spec/features/[功能名稱].feature, 專案測試框架設定指南_
  - _Requirements: BDD-2.1_
  - _Prompt: Role: BDD 測試工程師，精通 Cucumber/Gherkin 與測試框架 | Task: 建立 Step Definitions 測試框架設定，載入測試上下文、注入待測元件、建立共享測試資料容器 | Restrictions: 使用專案既有的測試基礎設施，遵循測試框架的最佳實踐，確保測試隔離性 | Success: 測試框架正確載入，共享狀態容器可在步驟間傳遞資料，測試上下文設定完成_

- [ ] 1.2 實作所有 @Given step methods，設定測試前置條件
  - 實作所有 @Given step methods，準備測試資料與初始狀態
  - 從 Gherkin Feature 提取所有 Given 步驟（如：Given 棋盤為空、Given 使用者已登入）
  - 建立測試資料建構器（Test Data Builder）或 Fixture
  - 設定初始狀態（資料庫、API、記憶體狀態）
  - 驗證前置條件已正確設定
  - _Leverage: spec/features/[功能名稱].feature (Given steps), Test Data Builder pattern_
  - _Requirements: BDD-2.2_
  - _Prompt: Role: BDD 測試工程師 | Task: 實作所有 @Given step methods，設定測試前置條件 | Restrictions: Given 步驟僅設定狀態不執行業務邏輯，使用 Test Data Builder 建立測試資料，確保資料隔離 | Success: 所有 Given 步驟實作完成，測試資料正確建立，初始狀態驗證通過_

- [ ] 1.3 實作所有 @When step methods，觸發待測功能
  - 實作所有 @When step methods，呼叫待測功能
  - 從 Gherkin Feature 提取所有 When 步驟（如：When 玩家下棋於 (0, 0)）
  - 呼叫待測 API、Controller 或 Service 方法
  - 捕捉回應結果或例外，儲存至共享狀態
  - 不驗證結果（驗證在 Then 步驟）
  - _Leverage: spec/features/[功能名稱].feature (When steps), API 介面定義_
  - _Requirements: BDD-2.3_
  - _Prompt: Role: BDD 測試工程師 | Task: 實作所有 @When step methods，觸發待測功能 | Restrictions: When 步驟僅執行動作不驗證結果，捕捉回應與例外儲存至共享狀態，處理非同步操作 | Success: 所有 When 步驟實作完成，待測功能正確呼叫，回應結果正確儲存_

- [ ] 1.4 實作所有 @Then step methods，驗證預期結果
  - 實作所有 @Then step methods，驗證預期結果
  - 從 Gherkin Feature 提取所有 Then 步驟（如：Then 棋盤狀態為 [[X, _, _], ...]）
  - 使用斷言庫驗證回應資料、狀態、錯誤訊息
  - 驗證成功案例與異常案例（錯誤碼、例外類型）
  - 驗證副作用（資料庫變更、事件發送）
  - _Leverage: spec/features/[功能名稱].feature (Then steps), 斷言庫文檔_
  - _Requirements: BDD-2.4_
  - _Prompt: Role: BDD 測試工程師 | Task: 實作所有 @Then step methods，驗證預期結果 | Restrictions: Then 步驟僅驗證不執行業務邏輯，使用斷言庫清晰表達預期，涵蓋成功與異常案例 | Success: 所有 Then 步驟實作完成，斷言清晰且完整，成功與異常案例都涵蓋_

- [ ] 1.5 執行 BDD 測試，確認測試失敗（紅燈）
  - File: N/A（執行命令）
  - 執行 BDD 測試套件，確認測試失敗（因功能尚未實作）
  - 執行測試命令範例：Ruby: `cucumber`、Python: `behave`、Java: `mvn test`、Node.js: `npm test`
  - 預期結果：🔴 測試失敗，錯誤訊息指出功能尚未實作
  - 驗證：測試能正確執行（無語法錯誤）、失敗原因為功能未實作而非測試程式碼錯誤、錯誤訊息清晰易懂
  - _Leverage: Step Definitions from tasks 1.1-1.4_
  - _Requirements: BDD-2.5_
  - _Prompt: Role: BDD 測試工程師 | Task: 執行 BDD 測試套件，確認測試失敗狀態（紅燈）符合預期 | Restrictions: 確認無編譯/語法錯誤，確認失敗原因為功能未實作而非測試程式碼錯誤 | Success: BDD 報告顯示所有測試失敗且失敗訊息清晰指出缺少的功能，無編譯或語法錯誤_

---

## 🔴 Phase 3: TDD - Unit Tests (Red)

> **目標**: 撰寫單元測試，測試會失敗（因 Domain/Service 尚未實作）。遵循 TDD 的 Red-Green-Refactor 循環。

- [ ] 2. Unit Test Implementation
  建立單元測試框架，實作 Domain Entity 和 Service 的單元測試，執行測試確認紅燈狀態。

- [ ] 2.1 建立 Domain Entity 的單元測試，測試業務邏輯與驗證規則
  - File: [測試目錄]/domain/[實體名稱]Test.[副檔名]
  - 範例路徑：`tests/test_board.py`（Python）、`src/__tests__/domain/Board.test.ts`（TypeScript）、`src/test/.../domain/BoardTest.java`（Java）
  - 建立 Domain Entity 的單元測試，驗證業務邏輯與約束條件
  - 測試案例從 DBML note 與 Gherkin Rule 提取：測試建構子驗證（如：價格必須 >= 0）、測試業務方法（如：updateStatus, calculateTotal）、測試邊界條件（如：邊界值、null 值、空集合）
  - 每個測試使用測試框架的測試語法（如：`test...`、`describe/it`、`@Test`）
  - 使用 AAA 模式（Arrange-Act-Assert）
  - 測試失敗原因為類別或方法不存在
  - _Leverage: spec/erm.dbml:[實體名稱], spec/features/[功能名稱].feature:[相關 Rules], DBML note (約束條件)_
  - _Requirements: TDD-3.1_
  - _Prompt: Role: TDD 開發工程師，精通單元測試與測試框架 | Task: 建立 Domain Entity [實體名稱] 的單元測試，測試業務邏輯與驗證規則 | Restrictions: 使用專案定義的單元測試框架，使用斷言庫驗證結果，測試命名清晰反映測試意圖，僅測試 Domain Entity 不依賴外部框架或資料庫，僅測試 DBML 明確定義的屬性與規格中提到的業務方法（無腦補假設原則），保持測試簡潔（≤ 20 行，避免深層巢狀） | Success: 測試編譯執行失敗（類別不存在，預期），測試涵蓋所有約束條件與邊界案例，測試命名清晰且有意義_

- [ ] 2.2 建立 Service 的單元測試，測試協調邏輯
  - File: [測試目錄]/application/[服務名稱]ServiceTest.[副檔名]
  - 建立 Service 的單元測試，測試業務流程協調邏輯
  - 測試案例從 Gherkin Rule 提取：測試前置條件驗證（如：Rule: 數量必須 > 0）、測試業務流程協調（如：Rule: 新局棋盤為空）、測試例外處理（如：Rule: 格子已佔用時拋出例外）
  - 使用 Mock 工具模擬 Repository 依賴
  - 使用測試框架的 Mock 功能（如：Mockito/unittest.mock/Jest mock）
  - 測試 Service 協調邏輯，不測試 Repository 內部實作
  - 測試失敗原因為 Service 類別或方法不存在
  - _Leverage: spec/features/[功能名稱].feature, [Repository 名稱]Repository (需 mock)_
  - _Requirements: TDD-3.2_
  - _Prompt: Role: TDD 開發工程師，精通單元測試與 Mock 工具 | Task: 建立 Service [服務名稱]Service 的單元測試，測試協調邏輯 | Restrictions: 使用專案定義的測試框架與 Mock 工具，使用 Mock 模擬 Repository 依賴，測試 Service 協調邏輯不測試 Repository 內部實作，遵循 AAA 模式（Arrange-Act-Assert），保持測試簡潔（≤ 20 行） | Success: 測試編譯執行失敗（類別不存在，預期），測試涵蓋所有 Gherkin Rule 的驗證邏輯，Mock 行為設定正確_

- [ ] 2.3 執行單元測試，確認測試失敗（紅燈）
  - File: N/A（執行命令）
  - 執行測試命令範例：Python: `pytest tests/`、JavaScript/TypeScript: `npm test` 或 `jest`、Java: `mvn test`
  - 執行單元測試，確認測試失敗
  - 預期結果：🔴 測試失敗，錯誤訊息指出類別或方法不存在
  - 驗證：Domain Tests 失敗（Entity 不存在）、Service Tests 失敗（Service 不存在）、測試失敗訊息清晰（如：class/function not found）
  - _Leverage: Unit Tests from tasks 2.1-2.2_
  - _Requirements: TDD-3.3_
  - _Prompt: Role: TDD 開發工程師 | Task: 執行單元測試，確認測試失敗狀態（紅燈）符合預期 | Restrictions: 確認無編譯/語法錯誤，確認失敗原因為類別/方法不存在而非測試程式碼錯誤 | Success: 測試報告顯示所有測試失敗且失敗訊息清晰指出缺少的類別/方法，無編譯或語法錯誤_

---

## 🟢 Phase 4: Implementation (Green)

> **目標**: 實作功能程式碼，讓 BDD 與單元測試通過（綠燈）。採用垂直切片（Vertical Slice）方式，由內而外實作完整功能。

- [ ] 3. Feature Implementation
  實作完整功能，包含 Domain Model、Repository、Service、Presentation Layer 與 Error Handling，讓所有測試通過。

- [ ] 3.1 實作 Domain Entities，對應 DBML 定義
  - File: [原始碼目錄]/domain/entities/[實體名稱].[副檔名]
  - 範例路徑：`src/domain/entities/Board.py`（Python）、`src/domain/entities/Board.ts`（TypeScript）、`src/main/java/.../domain/entity/Board.java`（Java）
  - 實作 Domain Entity，對應 DBML 定義的實體
  - 實作所有屬性（對應 DBML Column）：原始型別、列舉型別、關聯物件
  - 實作建構子與驗證邏輯（對應 DBML note 約束條件）：參數驗證（如：價格 >= 0）、不變量檢查（如：狀態轉換規則）、提早返回（Early Return）避免深層巢狀
  - 實作業務方法（對應 Gherkin Rule）：業務邏輯封裝、狀態轉換、計算方法
  - 實作測試輔助方法（如需要）：equals/hashCode（用於測試比較）、toString（用於除錯）
  - _Leverage: spec/erm.dbml:[實體名稱], spec/features/[功能名稱].feature:[相關 Rules], Domain Tests from task 2.1_
  - _Requirements: Impl-4.1_
  - _Prompt: Role: 領域建模專家，精通 Domain-Driven Design | Task: 實作 Domain Entity [實體名稱]，對應 DBML 定義 | Restrictions: 嚴格遵循 DBML 定義不可偏離，實作 DBML note 中的所有約束條件，使用提早返回避免深層巢狀（≤ 3 層縮排），僅實作規格明確提到的業務方法（無腦補假設原則），保持類別簡潔（單一職責） | Success: Domain Entity 實作完成，所有屬性與約束對應 DBML，單元測試通過（從 task 2.1），程式碼簡潔無深層巢狀_

- [ ] 3.2 實作 Domain Exceptions，對應 Gherkin 錯誤處理規則
  - File: [原始碼目錄]/domain/exceptions/[例外名稱].[副檔名]
  - 實作自訂例外類別，對應 Gherkin 的錯誤處理規則
  - 從 Gherkin Scenario 提取錯誤案例（如：Then 系統回應錯誤「格子已被佔用」）
  - 定義例外階層（BaseException → DomainException → SpecificException）
  - 實作錯誤碼與錯誤訊息（支援 i18n）
  - 實作例外建構子與輔助方法
  - _Leverage: spec/features/[功能名稱].feature (Then ... error/exception 步驟), 錯誤碼規範_
  - _Requirements: Impl-4.2_
  - _Prompt: Role: 例外處理專家 | Task: 實作 Domain Exceptions，對應 Gherkin 錯誤處理規則 | Restrictions: 從 Gherkin Then 步驟提取所有錯誤案例，定義清晰的例外階層，錯誤訊息清晰且支援 i18n，避免使用通用例外（如：RuntimeException） | Success: 所有 Gherkin 錯誤案例都有對應例外類別，例外階層清晰，錯誤訊息有意義_

- [ ] 3.3 定義 Repository 介面並實作資料存取邏輯
  - File: [原始碼目錄]/domain/repositories/[實體名稱]Repository.[副檔名]（介面）、[原始碼目錄]/infrastructure/repositories/[實體名稱]RepositoryImpl.[副檔名]（實作）
  - 定義 Repository 介面，宣告資料存取方法
  - 介面方法對應業務需求（如：findById, save, findByStatus）
  - 實作 Repository（使用 ORM、SQL、或 In-Memory）
  - 實作基本 CRUD 操作
  - 實作自訂查詢方法（對應 Gherkin 查詢需求）
  - 處理資料庫例外並轉換為 Domain Exception
  - _Leverage: spec/erm.dbml:[實體名稱], spec/features/[功能名稱].feature (查詢需求), Domain Entity from task 3.1_
  - _Requirements: Impl-4.3_
  - _Prompt: Role: 資料存取層專家，精通 Repository Pattern | Task: 定義 Repository 介面並實作資料存取邏輯 | Restrictions: 介面僅宣告業務所需方法不過度設計，實作使用專案既有的資料存取技術（ORM/SQL），處理資料庫例外並轉換為 Domain Exception，保持資料存取邏輯與業務邏輯分離 | Success: Repository 介面清晰簡潔，實作正確處理所有查詢需求，資料庫例外正確轉換_

- [ ] 3.4 實作 Service，協調 Domain Model 與 Repository
  - File: [原始碼目錄]/application/services/[服務名稱]Service.[副檔名]
  - 實作 Service，協調 Domain Model 與 Repository
  - 每個 Service 方法對應一個 Gherkin Rule 或 Use Case
  - 實作業務流程協調：驗證輸入參數 → 載入 Domain Entity → 執行業務邏輯 → 持久化結果 → 返回結果
  - 使用 Domain Exception 處理錯誤
  - 實作交易管理（如需要）
  - _Leverage: spec/features/[功能名稱].feature (Rules), Domain Entity from task 3.1, Repository from task 3.3, Service Tests from task 2.2_
  - _Requirements: Impl-4.4_
  - _Prompt: Role: 應用服務層專家，精通業務流程協調 | Task: 實作 Service，協調 Domain Model 與 Repository | Restrictions: 每個方法對應一個 Gherkin Rule，遵循「驗證 → 載入 → 執行 → 持久化 → 返回」流程，使用 Domain Exception 處理錯誤，Service 不包含業務邏輯（業務邏輯在 Domain Entity），保持方法簡潔（≤ 20 行） | Success: Service 實作完成，單元測試通過（從 task 2.2），每個 Gherkin Rule 都有對應方法，協調邏輯清晰_

- [ ] 3.5 實作 Presentation Layer，處理外部請求並呼叫 Service
  - File: [原始碼目錄]/presentation/controllers/[控制器名稱].[副檔名]（REST API）或 [原始碼目錄]/presentation/cli/[命令名稱].[副檔名]（CLI）或 [原始碼目錄]/presentation/views/[視圖名稱].[副檔名]（Web UI）
  - 範例路徑：`src/api/controllers/GameController.py`（REST）、`src/cli/commands/game_command.py`（CLI）
  - 實作 Presentation Layer，處理外部請求並呼叫 Service
  - 定義 API 端點或 CLI 命令（對應 Gherkin When 步驟）
  - 解析請求參數與驗證（格式驗證、必填檢查）
  - 呼叫 Service 方法
  - 處理 Service 回應並格式化輸出（JSON/XML/HTML/CLI 輸出）
  - 處理例外並返回適當的錯誤回應（HTTP 狀態碼、錯誤訊息）
  - _Leverage: spec/features/[功能名稱].feature (When 步驟), Service from task 3.4, API 設計規範_
  - _Requirements: Impl-4.5_
  - _Prompt: Role: API/UI 開發專家 | Task: 實作 Presentation Layer，處理外部請求並呼叫 Service | Restrictions: 僅處理輸入/輸出與格式轉換不包含業務邏輯，使用專案既有的框架（如：Flask/Express/Spring），遵循 RESTful API 設計原則（或 CLI 設計原則），處理例外並返回適當錯誤回應，保持控制器簡潔（薄層） | Success: Presentation Layer 實作完成，API/CLI 正確呼叫 Service，請求/回應格式正確，錯誤處理適當_

- [ ] 3.6 實作全域錯誤處理器，統一處理錯誤回應
  - File: [原始碼目錄]/presentation/middlewares/error_handler.[副檔名]
  - 實作全域錯誤處理器，統一處理所有層級的例外
  - 捕捉 Domain Exception 並轉換為適當的 HTTP 狀態碼（或 CLI 錯誤訊息）
  - 捕捉系統例外（如：資料庫連線失敗）並返回通用錯誤
  - 記錄錯誤日誌（包含 stack trace、request context）
  - 返回一致的錯誤回應格式（包含錯誤碼、錯誤訊息、timestamp）
  - _Leverage: Domain Exceptions from task 3.2, 錯誤回應格式規範_
  - _Requirements: Impl-4.6_
  - _Prompt: Role: 錯誤處理專家 | Task: 實作全域錯誤處理器，統一處理錯誤回應 | Restrictions: 使用框架提供的錯誤處理機制，區分 Domain Exception 與系統例外，不洩漏敏感資訊（如：資料庫結構），記錄詳細錯誤日誌供除錯，返回一致的錯誤回應格式 | Success: 全域錯誤處理器正確捕捉所有例外，錯誤回應格式一致，錯誤日誌完整，不洩漏敏感資訊_

- [ ] 3.7 執行所有測試，確認測試通過（綠燈）
  - File: N/A（執行命令）
  - 執行所有測試（BDD + 單元測試），確認所有測試通過
  - 執行 BDD 測試：確認所有 Scenario 通過（綠燈）
  - 執行單元測試：確認所有 Domain 與 Service 測試通過
  - 執行整合測試（如有）：確認端到端流程正確
  - 檢查測試覆蓋率：確認核心業務邏輯覆蓋率 >= 80%
  - _Leverage: BDD Tests from Phase 2, Unit Tests from Phase 3, Implementation from tasks 3.1-3.6_
  - _Requirements: Impl-4.7_
  - _Prompt: Role: QA 工程師 | Task: 執行所有測試，確認測試通過（綠燈） | Restrictions: 確認所有測試類型都執行（BDD、單元、整合），檢查測試覆蓋率是否達標，確認無測試被跳過或忽略 | Success: 🟢 所有測試通過（BDD、單元、整合），測試覆蓋率達標，測試報告清晰_

---

## 🔧 Phase 5: Refactor & Hardening

> **目標**: 重構程式碼以提升品質，並強化非功能需求（觀測性、安全性、向後相容性等）。

- [ ] 4. Code Quality Refactoring
  審查並重構程式碼，消除複雜度、改善命名、提升可讀性，確保程式碼符合 "Good Taste" 原則。

- [ ] 4.1 審查程式碼簡潔性與可讀性
  - 審查所有新增/修改的程式碼
  - 檢查項目：函數長度（≤ 20 行）、縮排深度（≤ 3 層）、圈複雜度（≤ 10）、重複程式碼（DRY 原則）
  - 重構目標：消除深層巢狀（使用 Early Return、Guard Clauses）、消除特殊情況（重新設計資料結構）、提取複雜邏輯為獨立函數、刪除死程式碼與註解掉的程式碼
  - Linus 原則：「如果需要超過 3 層縮排，你就完蛋了，應該修復你的程式。」
  - _Requirements: NFR (Code Quality)_
  - _Prompt: Role: 程式碼審查專家，精通 Clean Code 與 Refactoring | Task: 審查程式碼簡潔性與可讀性，重構複雜程式碼 | Restrictions: 每次重構後立即執行測試確保功能未破壞，遵循 Linus 的 3 層縮排原則，優先消除特殊情況而非增加 if/else，保持單一職責原則 | Success: 所有函數 ≤ 20 行，縮排 ≤ 3 層，無重複程式碼，特殊情況已消除，測試持續通過_

- [ ] 4.2 檢查程式碼結構與命名約定
  - 檢查類別、函數、變數命名是否清晰且一致
  - 檢查項目：命名是否表達意圖（Intention-Revealing Names）、是否避免誤導（Avoid Disinformation）、是否遵循專案命名規範（如：camelCase/snake_case）、是否使用領域術語（Ubiquitous Language）
  - 重構目標：重新命名模糊或誤導的名稱、統一命名風格、使用領域術語而非技術術語
  - Linus 原則：「C 是斯巴達式語言，命名也應如此。」（簡潔但清晰）
  - _Requirements: NFR (Code Quality)_
  - _Prompt: Role: 程式碼審查專家 | Task: 檢查程式碼結構與命名約定 | Restrictions: 命名應清晰表達意圖且簡潔，遵循專案命名規範，使用領域術語（從 DBML 與 Gherkin 提取），避免技術術語污染業務層 | Success: 所有命名清晰且一致，遵循專案規範，使用領域術語，測試持續通過_

- [ ] 4.3 檢查所有 public API 是否有文件註解
  - 檢查所有對外公開的類別、方法、函數是否有文件註解
  - 檢查項目：是否有類別/方法摘要、參數說明、返回值說明、例外說明、使用範例（如需要）
  - 補充文件註解（使用專案規範格式，如：Javadoc/JSDoc/Docstring）
  - 不要為私有方法寫多餘註解（程式碼應自我說明）
  - _Requirements: NFR (Documentation)_
  - _Prompt: Role: 技術文件撰寫專家 | Task: 檢查所有 public API 是否有文件註解，補充缺少的註解 | Restrictions: 僅為 public API 撰寫註解，使用專案規範的文件格式，註解應簡潔且有意義（避免廢話），包含參數、返回值、例外說明 | Success: 所有 public API 都有清晰的文件註解，註解格式一致，內容有意義_

- [ ] 4.4 每次重構後執行測試，確保功能未被破壞
  - File: N/A（執行命令）
  - 每次重構後立即執行測試套件
  - 確認所有測試持續通過（BDD + 單元 + 整合）
  - 如有測試失敗，立即回退重構並修正
  - Linus 原則：「Never break userspace!」（向後相容性是鐵律）
  - _Requirements: NFR (Regression Prevention)_
  - _Prompt: Role: QA 工程師 | Task: 每次重構後執行測試，確保功能未被破壞 | Restrictions: 重構與測試必須交替進行，不可累積大量重構後才測試，如有失敗立即回退並修正，確保向後相容性 | Success: 每次重構後測試都通過，無功能退化，向後相容性保持_

---

- [ ] 5. Cross-Cutting Concerns
  實作與驗證橫切關注點，包含觀測性、安全性、錯誤處理與 i18n，確保系統符合所有非功能需求。

- [ ] 5.1 實作觀測性（日誌、指標、追蹤）
  - 實作結構化日誌（Structured Logging）：使用 JSON 格式、包含 context（requestId、userId、timestamp）、記錄關鍵業務事件（如：訂單建立、支付完成）
  - 實作應用指標（Metrics）：請求計數、回應時間、錯誤率、業務指標（如：每日訂單數）
  - 實作分散式追蹤（Distributed Tracing，如需要）：使用 OpenTelemetry 或類似工具、追蹤跨服務呼叫鏈
  - 驗證日誌輸出：確認關鍵路徑都有日誌、日誌等級正確（INFO/WARN/ERROR）、日誌不包含敏感資訊
  - _Leverage: 專案 Observability 規範、日誌與指標框架文檔_
  - _Requirements: NFR (Observability)_
  - _Prompt: Role: SRE/DevOps 工程師，精通可觀測性工程 | Task: 實作觀測性（日誌、指標、追蹤） | Restrictions: 使用專案既有的觀測性工具，日誌使用 JSON 格式，包含完整 context，記錄關鍵業務事件，不記錄敏感資訊，指標命名遵循規範 | Success: 結構化日誌正確輸出，應用指標可查詢，分散式追蹤正常運作（如有），日誌不包含敏感資訊_

- [ ] 5.2 實作安全性（認證、授權、資料保護）
  - 實作認證（Authentication，如需要）：驗證 API Token/JWT、處理認證失敗
  - 實作授權（Authorization，如需要）：檢查使用者權限、基於角色的存取控制（RBAC）
  - 實作資料保護：敏感資料加密（如：密碼、信用卡號）、敏感資料遮罩（日誌中隱藏）、輸入驗證與清理（防止 Injection 攻擊）
  - 實作速率限制（Rate Limiting，如需要）：防止濫用 API
  - 審查安全風險：檢查 OWASP Top 10 常見漏洞
  - _Leverage: 專案 Security 規範、OWASP Top 10_
  - _Requirements: NFR (Security)_
  - _Prompt: Role: 應用安全專家，精通 OWASP Top 10 | Task: 實作安全性（認證、授權、資料保護） | Restrictions: 使用專案既有的安全框架，敏感資料必須加密或雜湊，日誌中遮罩敏感資料，實作輸入驗證防止 Injection，遵循最小權限原則 | Success: 認證與授權正確實作，敏感資料已加密/遮罩，輸入驗證完整，無 OWASP Top 10 漏洞_

- [ ] 5.3 實作錯誤處理與 i18n
  - 完善錯誤訊息多語系支援：建立錯誤訊息資源檔（如：messages_en.properties、messages_zh_TW.properties）、使用錯誤碼對應多語系訊息
  - 完善業務資料多語系支援（如需要）：資料庫欄位支援多語系（如：JSON 欄位）、API 根據 Accept-Language header 返回對應語系
  - 驗證 i18n 功能：切換語系測試、確認所有錯誤訊息都有翻譯
  - _Leverage: 專案 i18n 規範、錯誤碼定義_
  - _Requirements: NFR (i18n)_
  - _Prompt: Role: i18n 專家 | Task: 實作錯誤處理與 i18n | Restrictions: 使用專案既有的 i18n 框架，所有錯誤訊息都必須支援多語系，使用錯誤碼而非硬編碼訊息，根據 Accept-Language 返回對應語系 | Success: 錯誤訊息資源檔完整，所有錯誤碼都有翻譯，API 正確返回對應語系，語系切換測試通過_

- [ ] 5.4 審查複雜度與程式碼品味（Good Taste）
  - 最終審查所有程式碼，應用 Linus 的「好品味」原則
  - 檢查項目：是否消除了所有特殊情況（Special Cases）、是否有更簡單的資料結構可以簡化邏輯、是否有不必要的 if/else 分支、是否有過度設計的抽象
  - 重構目標：找出可以「重新看問題」的地方、將 10 行帶條件的程式碼重構為 4 行無條件程式碼（Linus 經典案例）、消除邊界情況
  - Linus 原則：「有時你可以從不同角度看問題，重寫它讓特殊情況消失，變成正常情況。」
  - _Requirements: NFR (Code Quality - Good Taste)_
  - _Prompt: Role: 首席架構師，精通 Linus Torvalds 的程式碼品味哲學 | Task: 審查複雜度與程式碼品味，應用 Good Taste 原則重構 | Restrictions: 找出所有特殊情況並嘗試消除，重新設計資料結構以簡化邏輯，避免增加 if/else（重新思考問題），每次重構後執行測試 | Success: 特殊情況已消除或最小化，資料結構最佳化，邏輯簡潔優雅，測試持續通過_

---

## ✅ Phase 6: Verification & Release

> **目標**: 執行完整驗證，確保向後相容性，準備發佈。遵循 Linus 的「Never break userspace」鐵律。

- [ ] 6. Verification & Release
  執行完整測試套件，檢查 API 向後相容性，更新文檔，完成發佈前檢查。

- [ ] 6.1 執行完整測試套件
  - File: N/A（執行命令）
  - 執行所有測試類型：BDD 測試（`cucumber`/`behave`/`mvn test`）、單元測試（`pytest`/`jest`/`mvn test`）、整合測試（如有）、效能測試（如需要）、安全測試（如需要）
  - 檢查測試覆蓋率：核心業務邏輯覆蓋率 >= 80%、整體覆蓋率 >= 70%
  - 檢查測試報告：確認無測試被跳過或忽略、確認無 flaky tests（不穩定測試）
  - 執行程式碼品質檢查：Linter（語法檢查）、Formatter（格式檢查）、Static Analysis（靜態分析）
  - _Leverage: All tests from previous phases_
  - _Requirements: NFR (Quality Assurance)_
  - _Prompt: Role: QA Lead | Task: 執行完整測試套件，確保所有測試通過且覆蓋率達標 | Restrictions: 確認所有測試類型都執行，檢查覆蓋率是否達標，執行程式碼品質工具，確認無 flaky tests | Success: 🟢 所有測試通過，覆蓋率達標，程式碼品質檢查通過，測試報告清晰_

- [ ] 6.2 檢查 API 向後相容性（Never break userspace!）
  - 檢查所有 API 變更（REST API、CLI、SDK）
  - 檢查項目：新增 API 端點（✅ Additive Change，安全）、修改現有 API 回應格式（🔴 Breaking Change，危險）、移除 API 端點或欄位（🔴 Breaking Change，禁止）、修改 API 語義（🔴 Breaking Change，禁止）
  - 如有 Breaking Change：提供遷移指南、實作版本控制（如：`/api/v2/...`）、保留舊版 API 一段時間（Deprecation 策略）、警告使用者
  - 使用 API 契約測試工具（如：Pact、OpenAPI Diff）驗證相容性
  - Linus 鐵律：「我們不破壞使用者空間！任何導致現有程式崩潰的改動都是 bug，無論多麼『理論正確』。」
  - _Leverage: API 規範文件、OpenAPI/契約定義_
  - _Requirements: NFR (Backward Compatibility)_
  - _Prompt: Role: API 設計專家 | Task: 檢查 API 向後相容性，確保不破壞現有使用者 | Restrictions: 遵循 Linus 的「Never break userspace」原則，Additive Changes 允許但需文件化，Breaking Changes 必須提供遷移路徑與版本控制，禁止移除現有 API 而不提供替代方案 | Success: 所有 API 變更都是 Additive 或有適當遷移策略，API 契約測試通過，使用者不會因升級而崩潰_

- [ ] 6.3 更新文檔與發佈說明
  - 更新 README.md：功能描述、安裝指南、使用範例、變更摘要
  - 更新 API 文檔：OpenAPI/Swagger 定義、API 使用範例、錯誤碼說明
  - 撰寫 CHANGELOG.md：新增功能（Added）、變更功能（Changed）、棄用功能（Deprecated）、移除功能（Removed）、修復錯誤（Fixed）、安全更新（Security）
  - 撰寫遷移指南（如有 Breaking Change）：說明變更影響、提供程式碼範例、列出遷移步驟
  - 更新專案規格文檔：`requirements.md`、`design.md`、`tasks.md` 標記完成狀態
  - _Leverage: 所有實作成果、API 定義、測試案例_
  - _Requirements: NFR (Documentation)_
  - _Prompt: Role: 技術文件撰寫專家 | Task: 更新文檔與發佈說明 | Restrictions: 文檔應清晰簡潔且有範例，CHANGELOG 遵循 Keep a Changelog 格式，API 文檔包含完整的請求/回應範例，遷移指南提供具體程式碼範例 | Success: README、CHANGELOG、API 文檔都已更新，遷移指南清晰（如有），規格文檔已標記完成_

- [ ] 6.4 程式碼品質與安全性檢查（Code Quality & Security Check）
  - 程式碼品質檢查：所有測試通過、測試覆蓋率達標、Linter/Formatter 檢查通過、Static Analysis 無重大問題
  - 安全檢查：安全掃描通過（如：dependency check）、敏感資訊未硬編碼、輸入驗證完整
  - 文檔完整性：README、CHANGELOG、API 文檔已更新、遷移指南已撰寫（如有 Breaking Change）
  - 向後相容性：Breaking Changes 已記錄、遷移路徑已提供、API 契約測試通過
  - _Requirements: NFR (Code Quality, Security, Documentation)_
  - _Prompt: Role: QA Lead | Task: 完成程式碼品質與安全性檢查 | Restrictions: 逐項檢查所有項目，確認測試與覆蓋率達標，確認文檔完整，確認向後相容性 | Success: ✅ 所有檢查項目完成，程式碼品質達標，文檔完整，向後相容性確認_

---

## 🎯 Definition of Done

**此規格完成的標準**：

- ✅ 所有 Gherkin Scenario 測試通過（BDD 綠燈）
- ✅ 所有單元測試與整合測試通過（TDD 綠燈）
- ✅ 測試覆蓋率達標（核心業務邏輯 >= 80%）
- ✅ 程式碼品質符合標準（≤ 3 層縮排、≤ 20 行/函數、無特殊情況）
- ✅ 非功能需求完成（觀測性、安全性、i18n）
- ✅ API 向後相容性確認（Never break userspace!）
- ✅ 文檔完整（README、CHANGELOG、API 文檔）
- ✅ 發佈前檢查表完成

---

## 🚀 執行驗證命令

完成後執行以下命令驗證：

```bash
# 1. 執行完整測試套件
# Python
pytest tests/ --cov=src --cov-report=html
behave features/

# JavaScript/TypeScript
npm test
npm run test:coverage

# Java
mvn clean test
mvn verify

# 2. 執行程式碼品質檢查
# Python
flake8 src/
black --check src/
mypy src/

# JavaScript/TypeScript
npm run lint
npm run format:check

# Java
mvn spotless:check
mvn checkstyle:check

# 3. 執行安全掃描（可選）
# Python
safety check
bandit -r src/

# JavaScript/TypeScript
npm audit

# Java
mvn dependency-check:check

# 4. 確認所有任務都標記為 [x]
grep -E "^- \[[ -x]\]" tasks.md | grep -v "\[x\]"
# 應該無輸出（所有任務都完成）
```

---

## 📌 注意事項

1. **嚴格遵循流程順序**：SDD → BDD Red → TDD Red → Green → Refactor → Hardening → Release
2. **每個任務開始前標記 `[-]`，完成後立即標記 `[x]`**
3. **Section 標題（純數字）不包含元數據，僅為組織用途**
4. **每次重構後立即執行測試，確保功能不被破壞**
5. **向後相容性是鐵律（Never break userspace!）**
6. **Linus 原則**：好品味 > 理論完美、簡潔 > 抽象、消除特殊情況 > 增加 if/else

---

## 📚 範例：修改既有元件（非 Entity 新增場景）

> **使用時機**：當需求是修改既有工具類、Service、或其他元件，而非建立新的 Entity 時，使用此範例格式。

### 場景範例：修改 QueryBuilderUtils 支援不分大小寫查詢

**正確格式**（✅ 可被 Dashboard 解析）：

```markdown
## 🔴 Phase 2: BDD Red - Integration Tests

> **目標**: 建立 BDD 整合測試，確認測試失敗狀態（功能尚未實作）。

- [ ] 1. BDD Framework Setup (if needed)
  建立或確認 BDD 測試框架已就緒。

- [ ] 1.1 引入測試框架依賴
  - File: module-name/pom.xml
  - 加入必要的測試框架依賴（如：Cucumber, JUnit, Spring Test）
  - 建立測試配置檔
  - 建立測試 Runner 類別
  - _Leverage: 測試框架官方文檔_
  - _Requirements: 測試基礎設施_
  - _Prompt: Role: 測試框架專家 | Task: 設定測試框架，確保可執行測試 | Restrictions: 使用專案標準測試框架版本，所有依賴 scope=test | Success: 測試框架正常啟動，無編譯錯誤_

- [ ] 2. BDD Test Cases Creation
  建立整合測試案例，驗證功能行為。

- [ ] 2.1 建立 Feature 檔案或整合測試類別
  - File: module-name/src/test/resources/features/feature-name.feature (Cucumber) 或 module-name/src/test/java/.../IntegrationTest.java (Spring Test)
  - 撰寫測試場景，涵蓋所有 requirements.md 的驗收標準
  - 定義 Given/When/Then 步驟或 @Test 方法
  - _Leverage: requirements.md (Gherkin Rules or Acceptance Criteria)_
  - _Requirements: Req 1-N_
  - _Prompt: Role: BDD/整合測試工程師 | Task: 建立測試案例，涵蓋所有需求場景 | Restrictions: 每個需求至少一個測試案例，測試場景清晰無歧義 | Success: 測試檔案已建立，涵蓋所有驗收標準_

- [ ] 2.2 實作測試程式碼（Step Definitions 或 Test Methods）
  - File: module-name/src/test/java/.../TestSteps.java 或 IntegrationTest.java
  - 實作測試邏輯，呼叫待測元件
  - 準備測試資料
  - 驗證測試結果
  - _Leverage: design.md (元件介面設計)_
  - _Requirements: BDD/整合測試實作_
  - _Prompt: Role: 測試實作專家 | Task: 實作測試程式碼，呼叫待測元件並驗證結果 | Restrictions: 測試隔離性，使用 Mock/Stub 處理外部依賴，驗證邏輯清晰 | Success: 測試程式碼已實作，執行後失敗（預期），失敗原因為功能尚未實作_

- [ ] 2.3 執行整合測試，確認紅燈狀態
  - File: N/A（執行命令）
  - 執行測試命令：`mvn test` 或 `mvn test -Dtest=TestClassName`
  - 確認所有測試失敗（預期）
  - 失敗原因：待修改元件尚未實作新功能
  - 驗證失敗訊息清晰
  - _Leverage: 測試程式碼_
  - _Requirements: BDD Red 確認_
  - _Prompt: Role: 測試執行者 | Task: 執行測試，確認紅燈狀態 | Restrictions: 確認失敗原因為功能缺失而非測試錯誤 | Success: 測試執行完成，所有測試失敗，失敗原因明確_

---

## 🔴 Phase 3: TDD Red - Unit Tests

> **目標**: 撰寫單元測試，測試待修改元件的新功能，確認測試失敗狀態。

- [ ] 3. Unit Tests for Modified Component
  建立單元測試，測試待修改元件的新行為。

- [ ] 3.1 建立單元測試類別
  - File: module-name/src/test/java/.../ComponentNameTest.java
  - 建立 JUnit 測試類別
  - 設定測試 Fixture
  - 建立測試輔助方法
  - _Leverage: JUnit 5 文檔_
  - _Requirements: TDD 單元測試框架_
  - _Prompt: Role: JUnit 測試專家 | Task: 建立單元測試類別，設定測試環境 | Restrictions: 使用 JUnit 5，純單元測試不依賴外部資源 | Success: 測試類別已建立，Fixture 設定完成_

- [ ] 3.2 測試新增或修改的方法行為
  - File: module-name/src/test/java/.../ComponentNameTest.java
  - 為每個修改的方法撰寫單元測試
  - 測試正常情況和邊界條件
  - 測試異常處理
  - _Leverage: design.md (修改的方法設計)_
  - _Requirements: Req 1-N（對應修改的方法）_
  - _Prompt: Role: TDD 開發工程師 | Task: 撰寫單元測試，測試修改後的方法行為 | Restrictions: 每個修改至少 3 個測試案例（正常/邊界/異常），測試命名清晰，保持測試簡潔（≤ 20 行） | Success: 測試已撰寫，執行後失敗（預期），失敗原因為方法尚未修改_

- [ ] 3.3 執行單元測試，確認紅燈狀態
  - File: N/A（執行命令）
  - 執行 `mvn test -Dtest=ComponentNameTest`
  - 確認所有測試失敗（預期）
  - 失敗原因：方法尚未修改
  - 驗證失敗訊息清晰
  - _Leverage: ComponentNameTest.java_
  - _Requirements: TDD Red 確認_
  - _Prompt: Role: TDD 測試執行者 | Task: 執行單元測試，確認紅燈狀態 | Restrictions: 確認失敗原因為功能缺失而非測試錯誤 | Success: 測試執行完成，所有測試失敗_

---

## 🟢 Phase 4: Implementation (Green)

> **目標**: 實作元件修改，讓所有測試通過（綠燈）。

- [ ] 4. Component Modification Implementation
  實作元件修改，讓所有測試通過。

- [ ] 4.1 實作修改點 1 - [修改的方法或邏輯描述]
  - File: module-name/src/main/java/.../ComponentName.java
  - 修改 Line XXX-YYY
  - 實作新的邏輯或行為
  - 執行對應的單元測試，確認通過
  - _Leverage: design.md (Phase 1 設計), Task 3.2 測試_
  - _Requirements: Req 1（對應需求）_
  - _Prompt: Role: Java 開發工程師 | Task: 實作修改點 1，讓對應測試通過 | Restrictions: 遵循設計文件，保持程式碼簡潔，符合 Checkstyle 規範 | Success: 修改已完成，對應測試通過_

- [ ] 4.2 實作修改點 2 - [修改的方法或邏輯描述]
  - File: module-name/src/main/java/.../ComponentName.java
  - 修改 Line XXX-YYY
  - 實作新的邏輯或行為
  - 執行對應的單元測試，確認通過
  - _Leverage: design.md (Phase 2 設計), Task 3.2 測試_
  - _Requirements: Req 2（對應需求）_
  - _Prompt: Role: Java 開發工程師 | Task: 實作修改點 2，讓對應測試通過 | Restrictions: 遵循設計文件，保持程式碼簡潔，符合 Checkstyle 規範 | Success: 修改已完成，對應測試通過_

- [ ] 4.3 執行所有單元測試驗證
  - File: N/A（執行命令）
  - 執行 `mvn test -Dtest=ComponentNameTest`
  - 確認所有測試通過（綠燈）
  - 檢查測試覆蓋率 ≥ 80%
  - _Leverage: Task 3.1-3.2 的所有測試_
  - _Requirements: TDD Green 確認_
  - _Prompt: Role: 測試驗證工程師 | Task: 執行所有單元測試，確認綠燈狀態 | Restrictions: 所有測試必須通過，檢查覆蓋率報告 | Success: 所有單元測試通過，覆蓋率 ≥ 80%_

- [ ] 4.4 執行整合測試驗證
  - File: N/A（執行命令）
  - 執行 `mvn test` 或 BDD 測試
  - 確認所有整合測試通過（綠燈）
  - 驗證功能行為符合需求
  - _Leverage: Task 2.1-2.3 的所有整合測試_
  - _Requirements: BDD Green 確認_
  - _Prompt: Role: 整合測試驗證工程師 | Task: 執行整合測試，確認所有測試通過 | Restrictions: 所有測試必須通過，功能行為符合需求 | Success: 所有整合測試通過_

---

## 🔵 Phase 5: Refactor & Hardening

> **目標**: 重構程式碼品質，強化非功能需求。

- [ ] 5. Code Quality Enhancement
  重構與強化程式碼品質。

- [ ] 5.1 程式碼重構與優化
  - File: module-name/src/main/java/.../ComponentName.java
  - 提取常數（Magic Numbers/Strings）
  - 簡化複雜邏輯
  - 消除程式碼重複（DRY 原則）
  - 執行 Checkstyle 和 SonarLint 檢查
  - 確認所有測試仍然通過
  - _Leverage: Clean Code 原則, Effective Java_
  - _Requirements: 程式碼品質_
  - _Prompt: Role: 程式碼重構專家 | Task: 重構程式碼，提升可維護性 | Restrictions: 不改變功能行為，所有測試持續通過，符合 Checkstyle 規範 | Success: 程式碼已重構，Checkstyle 0 violations，所有測試通過_

- [ ] 5.2 新增日誌與可觀測性
  - File: module-name/src/main/java/.../ComponentName.java
  - 在關鍵流程加入 DEBUG 日誌
  - 記錄重要決策點
  - 確認日誌不洩露敏感資訊
  - _Leverage: SLF4J 日誌最佳實踐_
  - _Requirements: 可觀測性_
  - _Prompt: Role: 可觀測性專家 | Task: 在關鍵流程加入日誌 | Restrictions: 日誌 level 為 DEBUG，不洩露敏感資訊，日誌訊息清晰 | Success: 日誌已加入，DEBUG level 正確_

- [ ] 5.3 安全性檢查
  - File: N/A（執行檢查）
  - 執行安全性掃描（OWASP Dependency Check）
  - 測試輸入驗證與錯誤處理
  - 驗證無安全漏洞
  - _Leverage: OWASP Top 10_
  - _Requirements: 安全性_
  - _Prompt: Role: 資安專家 | Task: 執行安全性檢查 | Restrictions: 測試各種異常輸入，確認無安全漏洞 | Success: 安全性檢查通過，無已知漏洞_

---

## ✅ Phase 6: Verification & Release

> **目標**: 向後相容性驗證、效能測試、文檔更新、部署準備。

- [ ] 6. Release Preparation
  準備發布，確保品質與相容性。

- [ ] 6.1 向後相容性驗證
  - File: N/A（執行檢查）
  - 執行完整的迴歸測試套件
  - 搜尋所有使用修改元件的程式碼
  - 驗證破壞性變更影響範圍
  - 記錄相容性報告
  - _Leverage: grep 搜尋, Maven Surefire 測試報告_
  - _Requirements: 向後相容性_
  - _Prompt: Role: 迴歸測試工程師 | Task: 驗證向後相容性 | Restrictions: 執行所有既有測試，記錄破壞性變更 | Success: 迴歸測試通過，相容性報告已完成_

- [ ] 6.2 效能測試（視專案類型決定）
  - File: N/A（執行測試）
  - **適用場景**：API 專案、微服務、Web 應用
  - **不適用場景**：Library 專案（無獨立部署能力）→ 標記為 [x]（跳過），由使用端專案驗證
  - 執行效能測試
  - 比對改動前後的效能指標
  - 確認效能符合需求
  - _Leverage: JMeter, Gatling, K6 等效能測試工具_
  - _Requirements: 效能需求_
  - _Prompt: Role: 效能測試工程師 | Task: 執行效能測試 | Restrictions: 比對改動前後指標，確認符合需求 | Success: 效能測試通過，符合需求_

- [ ] 6.3 文檔更新
  - File: 相關文檔檔案
  - 更新 API 文檔或使用指南
  - 撰寫 Release Note
  - 更新測試文檔
  - _Leverage: 現有文檔範本_
  - _Requirements: 文檔完整性_
  - _Prompt: Role: 技術文件工程師 | Task: 更新相關文檔 | Restrictions: 文檔清晰易懂，Release Note 包含變更說明 | Success: 所有文檔已更新_

```

**錯誤格式**（❌ Dashboard 無法解析）：

```markdown
## 🔴 Phase 2: BDD Red (Integration Tests)

### Task 2.1: 建立 BDD Feature 檔案
- [ ] 建立 `src/test/resources/features/query/case-insensitive-query.feature`
- [ ] 撰寫 Scenario: 使用 CONTAIN 查詢不分大小寫
- [ ] 撰寫 Scenario: 使用 LIKE 查詢不分大小寫

**檔案位置**:
- `path/to/features/...`

**Definition of Done**:
- [ ] Feature 檔案已建立
- [ ] 包含至少 4 個 Scenario
```

### 格式差異對比

| 元素     | ❌ 錯誤格式           | ✅ 正確格式                                                                |
| -------- | -------------------- | ------------------------------------------------------------------------- |
| 任務標題 | `### Task 2.1: 標題` | `- [ ] 2.1 標題`                                                          |
| 任務描述 | 列在標題下方         | 縮排在任務標記下                                                          |
| 檔案路徑 | `**檔案位置**: ...`  | `- File: path/to/file`                                                    |
| 提示詞   | 無或使用多行         | `- _Prompt: Role: ... \| Task: ... \| Restrictions: ... \| Success: ..._` |
| 子項目   | `- [ ] 子項`         | 整合到任務描述中                                                          |

### 關鍵原則

1. **使用列表格式**：`- [ ] 編號 標題`，不使用 Markdown 標題（`###`）
2. **元數據縮排**：File, _Prompt:, _Leverage:, _Requirements: 都要縮排在任務標記下
3. **_Prompt 單行**：必須使用管道分隔（`|`），不可換行
4. **階層清晰**：主任務（1, 2, 3）→ 子任務（1.1, 1.2, 2.1）

---

## 🎓 Linus 的智慧

> "Bad programmers worry about the code. Good programmers worry about data structures and their relationships."
> （糟糕的程式設計師擔心程式碼。優秀的程式設計師擔心資料結構及其關係。）

> "Never break userspace!"
> （永遠不要破壞使用者空間！）

> "If you need more than 3 levels of indentation, you're screwed anyway, and should fix your program."
> （如果你需要超過 3 層縮排，你就完蛋了，應該修復你的程式。）

> "Talk is cheap. Show me the code."
> （空談無益。給我看程式碼。）
