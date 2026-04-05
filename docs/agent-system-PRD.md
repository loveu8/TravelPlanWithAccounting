# Agent System PRD（初稿）

- 文件名稱：Agent System PRD
- 文件版本：v0.1-draft
- 文件狀態：Draft
- 建議儲存路徑：`/docs/agent-system-PRD.md`
- 適用範圍：`project-root`、`backend`
- 本次範圍：只產出 PRD，不建立任何 `AGENTS.md`、`SKILL.md`、`.codex/config.toml` 或 `.codex/agents/*.toml` 實作檔

---

## 1. 背景與問題定義

### 1.1 背景
目前專案希望建立一套可重用的 Agent 指令系統，讓 Codex 在進入專案後，能依照專案脈絡與固定規則，逐步完成以下工作：

1. 理解現有專案狀態
2. 釐清需求本質與邊界
3. 產出 PRD 或任務執行計畫
4. 在明確條件下進行實作、驗證、審查與重構
5. 將重複流程沉澱為可持續使用的技能（skills）

本系統的核心目的，不是單純「讓 AI 幫忙寫 code」，而是降低以下常見問題：

- 需求還沒定義清楚，就直接開始產出內容
- PRD、任務拆解、實作、測試、Review 之間缺乏一致輸入輸出
- AI 在不同回合的行為不一致，導致反覆修正
- 長期規則、工作流程、環境設定、角色邏輯混在一起，越做越亂
- 遇到高風險改動時，AI 不知道何時該停、何時該回報

### 1.2 目前要解決的核心問題
本 PRD 要解決的真正問題不是「怎麼寫一組 prompt」，而是：

> 如何把專案中的長期規則、重複工作流、環境設定、可選角色邏輯拆分成清楚分層，讓 Codex 在 backend 專案中有一致、可驗證、可擴充的工作方式。

### 1.3 目標使用情境
本系統主要支援以下情境：

- 新功能規劃與 PRD 產出
- Bug 修復前的任務拆解
- 安全性修補前的風險辨識
- 技術債重構前的專案理解與驗證規劃
- 後續將常見流程沉澱為 reusable skills

### 1.4 問題本質反思
本系統若只從「我要有幾個 agent」開始設計，容易犯三個錯：

1. **先定角色，再硬塞責任**：會把 skill 寫得過大，觸發條件模糊。
2. **先想自動化，再忽略邊界**：會把很多高風險決策錯誤地交給 Agent。
3. **先寫規則，再忽略執行環境**：最後發現很多設定其實應該放在 `.codex/config.toml`，而不是 `AGENTS.md`。

因此，本 PRD 先以「分層設計」為核心，而不是先定義固定的多角色團隊。

---

## 2. 系統設計目標與核心理念

### 2.1 設計目標
本系統的設計目標如下：

1. **分層清楚**：讓 `AGENTS.md`、skills、config、optional custom agents 各自負責不同層級問題。
2. **先理解、再產出**：不讓 Agent 在資訊不足時直接產出定稿內容。
3. **流程可重用**：把重複工作流沉澱為 skill，而不是每次重新 prompt。
4. **驗證可執行**：每一步都要有明確輸出與完成條件。
5. **高風險可升級處理**：明確定義哪些情況不能自動決策。
6. **逐步落地**：先有 PRD，之後再拆成 root/backend `AGENTS.md`、repo skills、config、optional subagents。

### 2.2 核心理念

#### 原則 A：`AGENTS.md` 只放常駐規則
`AGENTS.md` 應該用來保存每次都要遵守的 durable guidance，例如 repo 結構、build/test 指令、工程慣例、驗證方式與禁止事項。

#### 原則 B：Skill 只做可重用工作流
Skill 應該聚焦在「什麼時候觸發」「做哪些步驟」「產出什麼」，而不是扮演一個模糊的大角色。

#### 原則 C：環境設定不硬塞進指令
sandbox、approval、model、MCP、profiles、subagent 設定屬於環境層問題，應優先放在 `.codex/config.toml`。

#### 原則 D：角色是設計視角，不一定是實作單位
PDM、PJM、Reviewer 等概念可作為思考視角，但不必然直接實作成 skills。若未來需要獨立角色代理，才考慮 `.codex/agents/*.toml`。

#### 原則 E：先解決問題，再決定怎麼自動化
若需求本身、驗收標準或風險邊界尚未釐清，不應優先追求自動觸發與多代理並行。

---

## 3. 官方規格對齊與設計依據

> 本章將「官方硬規格」與「設計建議」分開說明，避免混淆。

### 3.1 官方硬規格（應優先遵守）

1. **專案指令檔名稱以 `AGENTS.md` 為主**。
2. **Codex 會在開始工作前讀取 `AGENTS.md`**，並依工作目錄做分層疊加。
3. **Skills 的標準格式是資料夾 + `SKILL.md`**。
4. **`SKILL.md` 至少需有 `name` 與 `description` metadata**。
5. **Skills 採 progressive disclosure**：啟動時主要依 skill metadata 做 discovery；只有真的要用到 skill，才會讀完整內容與 scripts/references。
6. **專案層級設定可使用 `.codex/config.toml`**。
7. **Subagents 不會自動生成，也不會自動啟用**；只有在明確要求或顯式工作流中才適合使用。

### 3.2 官方設計方向（強烈建議遵守）

1. `AGENTS.md` 應保持短、準、可執行。
2. 若 `AGENTS.md` 開始太長，應把細節拆到 task-specific docs 或 skills。
3. 專案型工作應先 plan，再實作。
4. 應把重複出錯的地方寫回 `AGENTS.md`，形成持續迭代的規則。
5. 配置應該盡量 durable，不要每次都靠 prompt 重講。

### 3.3 社群慣例（可當輔助參考）

社群對 `agents.md` 的實務觀察普遍偏向以下方向：

- 應提供具體可執行命令，而不是抽象原則
- 要清楚寫出 boundary 與禁做事項
- 應明確說明專案結構與重要路徑
- 過度寬泛的 agent 容易失效， specialist 較穩

### 3.4 本 PRD 的對齊策略

本 PRD 採用以下優先序：

1. **官方規格**
2. **官方最佳實踐**
3. **社群實務慣例**
4. **專案自訂設計**

若原始想法與官方規格衝突，一律以官方規格優先。

---

## 4. 範圍、非目標與成功指標

### 4.1 本次範圍（In Scope）

本 PRD 要定義：

- 系統分層結構
- root vs backend 的規則責任分工
- skill 命名策略與責任切分
- 主要工作流程情境
- 各 skill 的輸入、輸出、邊界、驗證方式
- 高風險決策的升級條件
- 驗證與評估機制
- 後續落地實作的分階段路線

### 4.2 本次非目標（Out of Scope）

本 PRD 不處理：

- 前端 `AGENTS.md` / skills 設計
- 直接建立任何 `AGENTS.md`、`SKILL.md`、config 檔案
- 直接實作 multi-agent orchestration
- 直接接入外部 MCP server
- 直接調整 CI/CD
- 直接實作 code review pipeline

### 4.3 成功指標（Success Criteria）

本 PRD 產出成功，應滿足以下條件：

1. 團隊可依此文件建立 root/backend 兩層 `AGENTS.md`
2. 團隊可依此文件拆出 skills 目錄與 `SKILL.md` 草稿
3. 團隊可辨識哪些規則該放 `AGENTS.md`、哪些該放 skills、哪些該放 config
4. 團隊可辨識哪些情況不可自動決策
5. 未來實作時，不需再從零重想整體架構

---

## 5. 目錄分層與作用域設計

### 5.1 建議未來落地結構

```text
/project-root
├── AGENTS.md
├── .codex/
│   ├── config.toml
│   └── agents/                  # optional
│       ├── pdm.toml
│       ├── pjm.toml
│       └── reviewer.toml
├── .agents/
│   └── skills/
│       ├── scan-project/
│       │   └── SKILL.md
│       ├── create-prd/
│       │   └── SKILL.md
│       ├── plan-work/
│       │   └── SKILL.md
│       ├── review-change/
│       │   └── SKILL.md
│       └── verify-change/
│           └── SKILL.md
└── backend/
    ├── AGENTS.md
    └── .agents/
        └── skills/
            ├── implement-backend-change/
            │   └── SKILL.md
            ├── backend-test-verification/
            │   └── SKILL.md
            ├── api-contract-check/
            │   └── SKILL.md
            ├── db-migration-check/
            │   └── SKILL.md
            └── refactor-backend/
                └── SKILL.md
```

### 5.2 各層責任

#### `project-root/AGENTS.md`
負責：

- 全專案通用規則
- 專案整體結構導覽
- 跨模組共同約束
- 全域 build/test/review expectation
- 規則總入口

不負責：

- backend 專屬細節
- 太長的流程手冊
- 大量 task-specific SOP

#### `backend/AGENTS.md`
負責：

- backend 子系統專屬規則
- backend 主要目錄與模組說明
- backend build/test/run 命令
- API / DB / service 層約束
- backend 專屬 routing guidance

不負責：

- 所有 skill 詳細步驟
- 環境層設定
- 可選 custom agent 設定

#### `.agents/skills/`
負責：

- 重複工作流
- 明確觸發與輸出
- 工具性或流程性任務
- 必要時搭配 deterministic scripts

#### `.codex/config.toml`
負責：

- model
- reasoning effort
- sandbox mode
- approval policy
- profiles
- MCP
- subagent 與其他 durable configuration

#### `.codex/agents/*.toml`（optional）
負責：

- specialized custom agent 定義
- 特定角色的 instructions 與 model / tool 配置

不應作為第一階段必做項。

---

## 6. 載入邏輯與作用域

### 6.1 指令載入邏輯
未來系統應依 Codex 的分層載入機制設計：

1. 從使用者層或全域層設定開始
2. 進入 repo 後，從 project root 往目前工作目錄逐層讀取 `AGENTS.md`
3. 越接近目前工作目錄的規則，優先度越高
4. 若有 override 機制，應由更特定層覆蓋通用層
5. Backend 工作以 `project-root/AGENTS.md` + `backend/AGENTS.md` 為主要組合

### 6.2 設計意義
這代表：

- root 應放長期共通規則
- backend 應放 backend 專屬規則
- 不應把所有細節塞到 root
- 不應把高層規則與低層實作說明混寫

### 6.3 風險提醒
若未來在多層目錄中重複寫相似規則，容易造成：

- 規則互相打架
- 維護成本升高
- Agent 不知該採信哪一層說法

因此本系統要求：

- 共通規則只寫在最接近共通作用域的位置
- 專屬規則只寫在最接近專屬工作目錄的位置

---

## 7. 命名策略與邏輯角色設計

### 7.1 為什麼不直接用 `skill-dev`、`skill-qa` 這種命名
這種命名方式的問題是：

1. 責任太大，不利於穩定觸發
2. 很容易混淆「角色」與「流程」
3. 很難定義清楚輸入、輸出與邊界
4. 後續一旦想細分，重構成本很高

### 7.2 命名策略原則
Skill 命名應優先反映：

- 工作流
- 任務目的
- 何時觸發
- 產出物

因此較建議使用：

- `scan-project`
- `create-prd`
- `plan-work`
- `implement-backend-change`
- `backend-test-verification`
- `review-change`
- `refactor-backend`

### 7.3 邏輯角色保留方式
以下角色可作為思考視角，但不直接等於 skill：

| 邏輯角色 | 責任定位 | 預設實作方式 |
|---|---|---|
| Product Analyst | 釐清需求與 PRD 結構 | `create-prd` |
| Task Planner | 任務分類、流程決策、技能選擇 | `plan-work` |
| Backend Implementer | 後端變更實作 | `implement-backend-change` |
| Verifier | 測試、build、驗證 | `backend-test-verification` / `verify-change` |
| Reviewer | correctness / security / maintainability review | `review-change` |
| Refactor Specialist | 純重構與回歸驗證 | `refactor-backend` |

若未來有強需求需要明確角色代理，再考慮 `.codex/agents/*.toml`。

---

## 8. 工作流程設計

### 8.1 標準工作流程矩陣

| 情境 | 建議流程 |
|---|---|
| 全新功能開發 | `scan-project` → `create-prd` → `plan-work` → `implement-backend-change` → `backend-test-verification` → `review-change` |
| Bug 修復 | `scan-project`（必要時） → `plan-work` → `implement-backend-change` → `backend-test-verification` → `review-change` |
| 安全性修補 | `scan-project`（必要時） → `plan-work` → `implement-backend-change` → `review-change` → `backend-test-verification` |
| 技術債重構 | `scan-project` → `plan-work` → `refactor-backend` → `backend-test-verification` → `review-change` |
| 只產文件 | `scan-project` → `create-prd` 或 `plan-work` |

### 8.2 為什麼安全性修補的順序不同
安全性修補常見風險是：

- 修補方向可能本身有新漏洞
- 權限/驗證機制改動容易波及邏輯
- 需要先做 correctness/security review，再看 regression

因此安全性情境下，允許 `review-change` 提前，避免用錯方向跑完整測試後才發現設計本身有問題。

### 8.3 自動觸發與半自動觸發原則
本系統在第一階段不追求「所有步驟完全自動串接」。

比較合理的設計是：

- `AGENTS.md` 定義何時必須先 plan / verify / review
- skill 定義工作流細節
- 真正複雜的 orchestrated multi-agent 行為留到後期再評估

---

## 9. Skills 完整規格

> 以下為 PRD 階段的完整 skill 規格定義，不代表本次就要實作檔案。

### 9.1 Skill：`scan-project`

#### 目的
快速理解目前 backend 專案狀態，建立後續 PRD、規劃、實作的基礎上下文。

#### 觸發條件

- 進入陌生專案
- 需要先理解 backend 結構
- 使用者要求先掃描專案
- 要產 PRD、拆任務、重構前

#### 輸入

- 專案工作目錄
- 使用者指定的任務範圍（若有）

#### 執行步驟

1. 盤點主要目錄與檔案
2. 找出 build/test/run 相關指令
3. 找出主要模組、框架、語言、規範
4. 找出與任務最相關的入口檔與依賴鏈
5. 總結已知風險與資訊缺口

#### 輸出

- 專案掃描摘要
- 重要目錄說明
- 技術棧摘要
- 主要執行命令
- 已知風險與待確認事項

#### 不做什麼

- 不改程式
- 不直接做需求猜測
- 不延伸到前端範圍

#### 完成定義

- 能說明 backend 的主要結構與流程入口
- 能列出至少一組合理的 build/test/run 指令
- 能指出目前任務的主要影響範圍

---

### 9.2 Skill：`create-prd`

#### 目的
將模糊需求整理為可驗證、可拆解、可討論的 PRD。

#### 觸發條件

- 使用者提出新功能
- 使用者提出模糊構想
- 使用者要求整理 PRD

#### 輸入

- 使用者需求敘述
- 專案掃描資訊（若已存在）
- 既有產品或技術限制

#### 執行步驟

1. 先釐清問題本質與目標
2. 區分目標、限制、假設、非目標
3. 補齊使用者場景與成功標準
4. 補齊驗收條件與風險
5. 檢查是否可拆解與可實作
6. 輸出正式 PRD 草稿

#### 輸出

- PRD 文件
- 功能範圍
- 非功能需求
- 驗收條件
- 風險與待確認事項

#### 不做什麼

- 不直接寫程式碼
- 不把未驗證假設當成定案

#### 完成定義

- PRD 可被後續 `plan-work` 直接使用
- 文件中具備目標、範圍、非目標、驗收與風險欄位

---

### 9.3 Skill：`plan-work`

#### 目的
把 PRD 或任務描述轉為可執行計畫，並決定後續技能順序。

#### 觸發條件

- 接收到 PRD
- 接收到 bug/security/refactor 類任務
- 使用者要求任務拆解

#### 輸入

- PRD 或任務敘述
- 專案掃描摘要

#### 執行步驟

1. 判斷任務類型
2. 界定影響範圍
3. 選擇技能組合與順序
4. 定義驗證方法
5. 標出高風險決策點

#### 輸出

- 任務執行計畫
- 推薦 skill 流程
- 驗證策略
- 需回報使用者的決策點

#### 不做什麼

- 不直接實作
- 不假設需求已完全清楚

#### 完成定義

- 後續執行人可依計畫直接開始工作
- 已明確指出哪些點需升級決策

---

### 9.4 Skill：`implement-backend-change`

#### 目的
根據任務計畫完成 backend 程式變更。

#### 觸發條件

- 已有足夠清楚的任務計畫
- 實作方向無重大未決事項

#### 輸入

- 任務執行計畫
- 相關程式碼上下文
- backend 專屬規範

#### 執行步驟

1. 確認影響模組
2. 按既有架構修改最小必要範圍
3. 補上必要測試或驗證點
4. 整理變更摘要
5. 將結果交給驗證 skill

#### 輸出

- 可交付程式碼
- 變更摘要
- 必要測試補充說明

#### 不做什麼

- 不自行引入高風險 dependency
- 不自行改 public contract / DB schema / security policy 而不回報
- 不為了重構而重構

#### 完成定義

- 變更與任務目標一致
- 已準備好進入驗證階段

---

### 9.5 Skill：`backend-test-verification`

#### 目的
驗證 backend 變更是否可編譯、可執行、無明顯 regression。

#### 觸發條件

- 實作完成
- 重構完成
- 使用者要求驗證

#### 輸入

- 變更內容
- 專案 build/test/lint 命令
- 任務驗收標準

#### 執行步驟

1. 執行 build
2. 執行相關 test
3. 執行 lint/format/static check（若專案有）
4. 對照完成定義檢查
5. 回報結果與缺口

#### 輸出

- 驗證報告
- 實際執行過的命令
- 通過項目
- 失敗項目
- 未覆蓋風險

#### 不做什麼

- 不捏造結果
- 無法執行時不能假裝已通過

#### 完成定義

- 有具體、可信、可回溯的驗證紀錄
- 能清楚區分「已驗證」與「未驗證」

---

### 9.6 Skill：`review-change`

#### 目的
從 correctness、security、maintainability、test risk 角度評估變更品質。

#### 觸發條件

- 實作完成後
- 重大變更前
- 使用者要求 review

#### 輸入

- 程式碼變更
- 任務目標
- 驗證報告

#### 執行步驟

1. 以任務目標確認 correctness
2. 檢查安全性與風險點
3. 檢查維護性與設計負債
4. 檢查測試覆蓋與回歸風險
5. 分級輸出 review 結果

#### 輸出

- Review Report
- 問題等級
- 問題位置
- 風險原因
- 修改建議
- 是否阻擋交付

#### 建議分級

| 等級 | 定義 |
|---|---|
| P0 / Critical | 重大安全、資料毀損、系統不可用 |
| P1 / High | 功能正確性或穩定性受影響 |
| P2 / Medium | 維護性、潛在風險、測試不足 |
| P3 / Low | 風格、命名、可讀性建議 |

#### 不做什麼

- 不用模糊語氣堆積建議
- 不做沒有依據的猜測性評論

#### 完成定義

- 結論清楚：可交付 / 需修復
- 若無 blocking issue，要明講

---

### 9.7 Skill：`refactor-backend`

#### 目的
在不新增功能的前提下，改善結構、可讀性、可維護性。

#### 觸發條件

- 使用者要求重構
- review 指出可重構區塊
- 技術債整理情境

#### 輸入

- 重構目標
- 既有程式碼與測試狀況
- 已知風險

#### 執行步驟

1. 確認重構目標與非目標
2. 限縮改動範圍
3. 執行重構
4. 交由驗證 skill 做回歸檢查
5. 輸出重構摘要

#### 輸出

- 重構摘要
- 影響範圍
- 風險降低說明
- 驗證結果

#### 不做什麼

- 不擴張成新功能
- 不在邊界不清楚時硬做大改動

#### 完成定義

- 結構有改善
- 功能未被改變
- 已通過必要回歸驗證

---

## 10. 行為邊界與升級條件

### 10.1 必須回報使用者的情況
以下情況不得由 Agent 自行決策，必須先回報使用者：

1. DB schema 或 migration 變更
2. API contract 破壞性變更
3. 新增或更換重要 dependency
4. 權限、驗證、授權流程變更
5. 部署、infra、CI/CD 方式變更
6. 大範圍 refactor 或跨模組改寫
7. 測試無法執行或結果不可信
8. 有多個技術方向且 trade-off 明顯
9. 掃描結果與使用者敘述不一致
10. 需要 subagents、MCP 或外部工具時，超出既有環境規則

### 10.2 升級條件矩陣

| 類型 | 可自動處理 | 需先回報 |
|---|---|---|
| 小範圍純文件調整 | 是 | 否 |
| 小範圍 backend bug fix | 視情況 | 若影響 API/DB 則是 |
| 新功能規劃 | 否，先 plan | 是 |
| DB schema 變更 | 否 | 是 |
| 權限/安全策略調整 | 否 | 是 |
| 大型 refactor | 否 | 是 |
| 單元測試補寫 | 多數可 | 若需要改既有設計則是 |
| 新 dependency 引入 | 否 | 是 |

### 10.3 設計原則

- 低風險、局部、可驗證的事可自動處理
- 高風險、難回復、需產品決策的事必須升級

---

## 11. Output Contract 與完成定義

### 11.1 Output Contract 總覽

| Skill | 主要輸出 | 下一步可直接使用者 |
|---|---|---|
| `scan-project` | 專案掃描摘要 | `create-prd` / `plan-work` |
| `create-prd` | PRD 文件 | `plan-work` |
| `plan-work` | 任務執行計畫 | `implement-backend-change` / `refactor-backend` |
| `implement-backend-change` | 程式碼變更與摘要 | `backend-test-verification` |
| `backend-test-verification` | 驗證報告 | `review-change` |
| `review-change` | Review Report | 使用者決策 / 修復回合 |
| `refactor-backend` | 重構摘要與驗證結果 | `review-change` / 使用者確認 |

### 11.2 Output Contract 規則
每個 skill 的輸出必須：

1. 可讀
2. 可驗證
3. 可被下一個 skill 直接使用
4. 不依賴大量隱含上下文
5. 清楚區分「已確認」與「待確認」

### 11.3 Definition of Done
每條 workflow 至少要滿足：

1. 目標已清楚達成或清楚說明未完成原因
2. 已列出影響範圍
3. 已執行必要驗證
4. 已註明未驗證風險
5. 若存在高風險決策點，已升級回報
6. 輸出可供下一階段直接使用

---

## 12. Review 分級標準與輸出格式

### 12.1 Review 分級標準

| 等級 | 說明 |
|---|---|
| P0 / Critical | 重大安全、資料遺失、系統不可用、破壞性錯誤 |
| P1 / High | 會影響功能正確性、穩定性、重要邏輯 |
| P2 / Medium | 維護性差、潛在風險、測試不足、設計不一致 |
| P3 / Low | 命名、風格、可讀性、細節優化 |
| None | 無 blocking 或明顯問題 |

### 12.2 建議輸出格式

```markdown
## Code Review Report

### 檔案：<file-path>

#### [P1] 問題標題
- 位置：第 N 行
- 風險：說明這個問題會造成什麼影響
- 原因：為什麼這樣判定
- 建議：具體修法
- 是否阻擋：是 / 否

### 總結
- P0：N 項
- P1：N 項
- P2：N 項
- P3：N 項
- 結論：可交付 / 需修復後再審
```

### 12.3 使用原則

- 要有證據或推理依據
- 要盡量給具體修法
- 若無阻擋問題，要直接說明

---

## 13. 驗證策略與 Skill 評估機制

### 13.1 為什麼需要 Skill Evaluation
Skill 是否有價值，不是看檔案有沒有建立，而是看：

- 是否在正確時機被觸發
- 是否真的縮短工作流程
- 是否能穩定產出符合規範的輸出
- 是否降低重工與來回修正次數

### 13.2 評估維度
未來 skill 落地後，至少用以下四個維度評估：

1. **Outcome**：有沒有完成任務
2. **Process**：有沒有走對流程
3. **Format**：輸出是否符合規格
4. **Efficiency**：是否避免多餘步驟與過度擴張

### 13.3 建議初始評估集
建議每個 skill 至少準備 10～20 個代表性 prompt 或案例：

- 新功能 PRD
- Bug 修復規劃
- 小範圍 backend 修改
- 大範圍 refactor 升級判斷
- API 變更風險識別
- DB migration 邊界判斷

### 13.4 驗證輸出要檢查的項目

#### `scan-project`
- 是否正確抓到目錄與主要命令
- 是否指出核心模組與影響範圍

#### `create-prd`
- 是否先講問題本質
- 是否有非目標與驗收條件
- 是否把假設與事實分開

#### `plan-work`
- 是否有正確分類任務
- 是否有指出高風險決策點

#### `implement-backend-change`
- 是否遵守範圍
- 是否沒有偷偷擴張需求

#### `backend-test-verification`
- 是否誠實列出測試與失敗點
- 是否清楚說明未驗證缺口

#### `review-change`
- 是否有證據導向
- 是否分級合理

---

## 14. 實作分階段路線圖

### Phase 0：PRD 定稿
產出本文件，完成設計共識。

### Phase 1：建立 root / backend `AGENTS.md`
先把 durable guidance 建起來，避免還沒沉澱規則就急著做 skills。

### Phase 2：建立最小必要 skills
優先做：

1. `scan-project`
2. `create-prd`
3. `plan-work`
4. `backend-test-verification`

原因：這四個最直接改善「先亂做、後回頭修」的問題。

### Phase 3：加入執行與審查技能
再加入：

1. `implement-backend-change`
2. `review-change`
3. `refactor-backend`

### Phase 4：補 config 與 optional custom agents
當規則與流程穩定後，再整理：

- `.codex/config.toml`
- optional `.codex/agents/*.toml`
- optional MCP / orchestration

### Phase 5：建立評估與回饋閉環
把 skill 錯誤案例、常見誤判、重複 review feedback 反向寫回 `AGENTS.md` 或 skill 文件。

---

## 15. 風險、限制與未來擴充

### 15.1 風險

1. **規則寫太多**：造成 `AGENTS.md` 膨脹，反而難維護。
2. **skill 寫太廣**：觸發條件模糊，使用不穩定。
3. **太早角色化**：在流程尚未穩定前就引入 custom agents，會增加複雜度。
4. **環境規則與 repo 規則混寫**：導致後續設定難以管理。
5. **過度追求自動化**：忽略高風險升級與人工判斷。

### 15.2 限制
本 PRD 仍有以下限制：

- 尚未對應到實際專案檔案名稱與真實命令
- 尚未驗證各技能在本 repo 的實際觸發穩定性
- 尚未建立真實 eval dataset
- 尚未整合 MCP 或多代理 orchestrator

### 15.3 未來擴充方向

1. 為 backend 補 `api-contract-check` 與 `db-migration-check`
2. 補 `PLANS.md` 或 execution plan 範本
3. 補 repo-specific review checklist
4. 補 CI 中的 skills / review automation
5. 需要時再引入 subagents 做 bounded parallel work

---

## 16. 決策摘要

### 16.1 本 PRD 的關鍵決策

1. 使用 `AGENTS.md`，不使用 `AGENT.md`
2. skills 採 `.agents/skills/<skill>/SKILL.md`
3. 把角色與工作流拆開
4. `AGENTS.md` 放 durable guidance，skills 放重複流程
5. `.codex/config.toml` 作為環境設定層
6. subagents 為 optional，而非預設機制
7. 第一階段先做單代理可穩定工作的系統，再考慮並行與 orchestration

### 16.2 為什麼這樣設計
因為目前真正的瓶頸不是「沒有很多角色」，而是：

- 規則沒分層
- 技能沒定義清楚
- 輸入輸出沒對齊
- 驗證與升級條件不夠清楚

先把這四件事做好，後續不論是 Codex、Claude、GitHub Copilot 或其他 agent 架構，都比較容易對接。

---

## 17. 後續落地建議

1. 先根據本 PRD 寫出 **root `AGENTS.md` 初稿**
2. 再寫 **backend `AGENTS.md` 初稿**
3. 接著只先做 2～4 個最小必要 skill
4. 用真實任務測試，記錄失敗案例
5. 再把反覆出錯點寫回規則或 skill

建議不要一開始就同時做：

- 全部 skills
- custom agents
- MCP
- multi-agent orchestration
- review pipeline 自動化

那樣太容易在規則還沒穩時就把系統做複雜。

---

## 18. 參考來源與設計依據

### OpenAI 官方文件
1. OpenAI Developers — Custom instructions with AGENTS.md  
   https://developers.openai.com/codex/guides/agents-md/
2. OpenAI Developers — Agent Skills  
   https://developers.openai.com/codex/skills/
3. OpenAI Developers — Best practices  
   https://developers.openai.com/codex/learn/best-practices/
4. OpenAI Developers — Config basics  
   https://developers.openai.com/codex/config-basic/
5. OpenAI Developers — Customization  
   https://developers.openai.com/codex/concepts/customization/
6. OpenAI Developers — Subagents  
   https://developers.openai.com/codex/subagents/
7. OpenAI Developers — Use Codex with the Agents SDK  
   https://developers.openai.com/codex/guides/agents-sdk/
8. OpenAI Developers — Codex Prompting Guide  
   https://developers.openai.com/cookbook/examples/gpt-5/codex_prompting_guide/
9. OpenAI Developers — Using skills to accelerate OSS maintenance  
   https://developers.openai.com/blog/skills-agents-sdk/

### 社群與輔助參考
10. GitHub Blog — How to write a great agents.md: Lessons from over 2,500 repositories  
    https://github.blog/ai-and-ml/github-copilot/how-to-write-a-great-agents-md-lessons-from-over-2500-repositories/

---

## 19. 附錄：未來 `AGENTS.md` 應包含的最小欄位

### root `AGENTS.md` 建議最小欄位
- Repo overview
- Important directories
- How to run
- Build/test/lint commands
- Core engineering conventions
- Do-not rules
- Done definition
- Routing guidance to backend-specific rules

### backend `AGENTS.md` 建議最小欄位
- Backend overview
- Main modules / directory map
- Backend run/build/test commands
- API / DB / service layer conventions
- Validation rules
- High-risk escalation rules
- Suggested skills to use first

### `SKILL.md` 建議最小欄位
- `name`
- `description`
- Trigger conditions
- Inputs
- Steps
- Outputs
- Boundaries
- Verification

---

## 20. 一句話結論

這份 PRD 的核心不是「建立很多 agent」，而是：

> 先把規則層、工作流層、環境層、可選角色層拆乾淨，讓 Codex 在 backend 專案中能穩定地先理解、再規劃、再執行、再驗證。
