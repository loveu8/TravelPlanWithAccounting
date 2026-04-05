# Agent System PRD（可執行修正版）

- 文件名稱：Agent System PRD
- 文件版本：v1.0-executable
- 文件狀態：Ready for implementation
- 建議儲存路徑：`/docs/agent-system-PRD.md`
- 適用範圍：`project-root`、`backend`
- 主要對象：Codex / 其他 coding agent 的 repo 內持久化工作規則與技能設計
- 本文件角色：**設計與產檔母文件**。未來可依本文件直接生成對應的 `AGENTS.md`、`SKILL.md`，並在條件具備時再生成 `.codex/config.toml`。

---

## 1. 文件目的與本次修正結論

### 1.1 為什麼要修正
原始版本的方向正確，但有一個實作層級的矛盾：

- 一方面希望未來由 Codex 生成 `AGENTS.md`、`SKILL.md`、`.codex/config.toml`
- 另一方面又把本次任務定義成「只產出 PRD，不建立任何實作檔」

這會導致真正執行時，模型很容易停在「寫完 PRD 就結束」，而不會自然進入後續的產檔階段。

### 1.2 本次修正目標
本修正版要解決兩件事：

1. 讓架構設計與 OpenAI / Codex 目前的官方慣例對齊
2. 讓這份 PRD **不只是說明文件，而是能被後續任務直接拿來產出對應檔案**

### 1.3 本次修正後的核心決策
1. 使用 `AGENTS.md`，不使用 `AGENT.md`
2. skills 採 `.agents/skills/<skill>/SKILL.md` 形式
3. `AGENTS.md` 負責 durable guidance，skills 負責 reusable workflow
4. `.codex/config.toml` 屬於環境層，不在第一輪強制生成
5. subagents / custom agents 只保留為可選擴充，不列為第一輪預設輸出
6. 第一輪實作的目標是：**先讓單代理工作流穩定可用，再擴充角色化與並行化**

---

## 2. 背景與問題定義

### 2.1 背景
目前專案希望建立一套可重用的 Agent 指令系統，讓 Codex 在進入專案後，能依照專案脈絡與固定規則，逐步完成以下工作：

1. 理解現有專案狀態
2. 釐清需求本質與邊界
3. 產出 PRD 或任務執行計畫
4. 在明確條件下進行實作、驗證、審查與重構
5. 將重複流程沉澱為可持續使用的 skills

### 2.2 真正要解決的核心問題
本 PRD 要解決的問題不是「怎麼寫一組 prompt」，而是：

> 如何把專案中的長期規則、重複工作流、環境設定、可選角色邏輯拆分成清楚分層，讓 Codex 在 backend 專案中有一致、可驗證、可擴充，而且能落地產檔的工作方式。

### 2.3 目標使用情境
本系統主要支援以下情境：

- 新功能規劃與 PRD 產出
- Bug 修復前的任務拆解
- 安全性修補前的風險辨識
- 技術債重構前的專案理解與驗證規劃
- 後續將常見流程沉澱為 reusable skills

### 2.4 問題本質反思
若只從「我要有幾個 agent」開始設計，容易犯四個錯：

1. **先定角色，再硬塞責任**：skill 會寫得過大，觸發條件模糊。
2. **先想自動化，再忽略邊界**：高風險決策會被錯誤交給 agent。
3. **先寫規則，再忽略執行環境**：很多設定其實應放在 `.codex/config.toml`。
4. **先想產檔，再忽略真實 repo**：最後產出一組看起來完整、但實際命令與路徑都不對的檔案。

因此，本 PRD 的核心是：**先掃描真實 repo，再按分層規則生成最小必要檔案。**

---

## 3. 官方規格對齊與設計依據

> 本章把「官方硬規格」與「設計建議」分開說明，避免混淆。

### 3.1 官方硬規格（應優先遵守）
1. Codex 會在開始工作前讀取 `AGENTS.md`，並依目錄分層疊加。  
2. 指令 discovery 會由較高層一路到目前工作目錄，越接近目前工作目錄的規則優先。  
3. skill 的標準格式是：**一個資料夾，裡面放 `SKILL.md`**。  
4. `SKILL.md` 至少必須有 `name` 與 `description`。  
5. skill 採 progressive disclosure：先看 metadata，再在需要時讀取完整 `SKILL.md` 與 scripts / references。  
6. 專案層級設定可放在 `.codex/config.toml`，而且 config 也會按專案根目錄到子目錄分層套用。  
7. 自訂 subagents / custom agents 要放在 `.codex/agents/*.toml` 或 `~/.codex/agents/*.toml`，且不會自行啟用。  

### 3.2 官方最佳實踐（強烈建議遵守）
1. `AGENTS.md` 要短、準、可執行。
2. repo-specific guidance 應放在最接近作用域的位置。
3. skills 應設計成小而明確的 workflow，不是過度寬泛的大角色。
4. 應把重複出錯或重複 review feedback 的內容回寫到 `AGENTS.md`。
5. config 適合放 durable settings，例如 sandbox / approvals / profiles / MCP / feature flags。

### 3.3 對本專案的設計含義
這代表：

- `AGENTS.md` 不是 SOP 大全，而是 repo 的長期工作規則
- skill 不是角色介紹，而是可被重複觸發的任務流程
- `.codex/config.toml` 不是第一輪一定要產出的檔案，因為它涉及環境與權限策略
- subagents 不是這份設計的預設基礎，而是後續可選擴充

---

## 4. 本文件的設計範圍與成功指標

### 4.1 In Scope
本 PRD 要定義：

- 系統分層結構
- root vs backend 的規則責任分工
- skill 命名策略與責任切分
- 主要工作流程情境
- 各 skill 的輸入、輸出、邊界、驗證方式
- 高風險決策的升級條件
- 產檔順序與最小必要輸出物
- 後續落地實作的分階段路線

### 4.2 Out of Scope
本 PRD 不直接處理：

- 前端 `AGENTS.md` / skills 設計
- multi-agent orchestration 的正式實作
- MCP server 整合
- CI / GitHub Action 版自動化
- 雲端 task routing
- 真實 repo 以外的全域個人設定

### 4.3 Success Criteria
本文件算成功，應同時滿足：

1. 可依本文件直接建立 root / backend 兩層 `AGENTS.md`
2. 可依本文件直接建立第一輪最小必要 skill 檔案
3. 可辨識哪些規則應放 `AGENTS.md`、哪些應放 skill、哪些應延後到 `.codex/config.toml`
4. 可明確知道哪些檔案第一輪要生成、哪些不要
5. 後續執行時，不會因任務描述模糊而卡在「只寫 PRD 不產檔」

---

## 5. 建議落地結構

```text
/project-root
├── AGENTS.md
├── .agents/
│   └── skills/
│       ├── scan-project/
│       │   └── SKILL.md
│       ├── create-prd/
│       │   └── SKILL.md
│       ├── plan-work/
│       │   └── SKILL.md
│       └── verify-change/
│           └── SKILL.md
├── .codex/
│   ├── config.toml                  # optional, phase 2+
│   └── agents/                      # optional, phase 3+
│       ├── pdm.toml
│       └── reviewer.toml
└── backend/
    ├── AGENTS.md
    └── .agents/
        └── skills/
            ├── implement-backend-change/
            │   └── SKILL.md
            ├── backend-test-verification/
            │   └── SKILL.md
            ├── review-change/
            │   └── SKILL.md
            ├── refactor-backend/
            │   └── SKILL.md
            ├── api-contract-check/
            │   └── SKILL.md         # optional, phase 2+
            └── db-migration-check/
                └── SKILL.md         # optional, phase 2+
```

### 5.1 各層責任

#### `project-root/AGENTS.md`
負責：
- 全專案通用規則
- 專案整體結構導覽
- 共通 build/test/review expectation
- 目錄導引與 routing guidance
- 高層 done definition

不負責：
- backend 專屬命令細節
- 每個 skill 的完整流程
- 使用者個人偏好設定

#### `backend/AGENTS.md`
負責：
- backend 專屬規則
- backend 主要模組與目錄導覽
- backend build/test/run 命令
- API / DB / service layer 約束
- backend 高風險變更升級規則

不負責：
- 全專案共通規則
- `.codex/config.toml` 配置
- custom agent 定義

#### `.agents/skills/`
負責：
- 可重複的任務工作流
- 觸發條件、步驟、輸出、驗證
- 必要時可帶 deterministic scripts / references

#### `.codex/config.toml`
負責：
- model
- reasoning effort
- sandbox mode
- approval policy
- profiles
- feature flags
- MCP / agent settings

#### `.codex/agents/*.toml`
負責：
- 自訂 agent 的說明與設定
- 需要專門 spawn 的角色型代理

---

## 6. 載入邏輯與作用域

### 6.1 `AGENTS.md` 載入原則
未來系統應依下列邏輯設計：

1. Codex 啟動時會先讀全域層 guidance
2. 進入 repo 後，從 project root 往目前工作目錄逐層讀取 `AGENTS.md`
3. 後面的層級會覆蓋較上層的一般規則
4. root 與 backend 必須職責明確，不可互相重複大段內容

### 6.2 設計約束
這代表：

- root 只放跨模組共通規則
- backend 只放 backend 專屬規則
- 不可把所有細節都塞到 root
- 不可讓 root 與 backend 對同一件事給出互相衝突的命令

### 6.3 產檔時的實務要求
在生成 `AGENTS.md` 之前，必須先掃描 repo，找出：

- 真實目錄結構
- 真實 build / test / run 指令
- 真實 backend 入口位置
- 真實模組切分方式

**禁止憑空猜測命令或路徑。**
若找不到真實命令，應使用 `TODO:` 或 `REQUIRES CONFIRMATION` 標示，而不是捏造內容。

---

## 7. 命名策略與角色處理方式

### 7.1 為什麼不直接用 `skill-dev`、`skill-qa`
這種命名的問題是：

1. 責任太大，不利穩定觸發
2. 混淆角色與流程
3. 難以定義清楚輸入、輸出與邊界
4. 未來擴充時容易拆不開

### 7.2 命名策略原則
skill 命名應優先反映：

- 任務目的
- 觸發情境
- 預期輸出
- 是否為特定子系統專用

建議使用：
- `scan-project`
- `create-prd`
- `plan-work`
- `verify-change`
- `implement-backend-change`
- `backend-test-verification`
- `review-change`
- `refactor-backend`

### 7.3 邏輯角色如何保留
以下角色可保留為設計視角，但不直接等於 skill：

| 邏輯角色 | 功能 | 預設落地方式 |
|---|---|---|
| Product Analyst | 釐清需求與 PRD 結構 | `create-prd` |
| Task Planner | 任務分類與流程決策 | `plan-work` |
| Backend Implementer | 後端實作 | `implement-backend-change` |
| Verifier | 驗證與回報 | `verify-change` / `backend-test-verification` |
| Reviewer | correctness / security / maintainability review | `review-change` |
| Refactor Specialist | 純重構 | `refactor-backend` |

只有在這些邏輯角色需要獨立 spawn、使用不同配置、不同工具邊界時，才應考慮 `.codex/agents/*.toml`。

---

## 8. 工作流程設計

### 8.1 標準流程矩陣

| 情境 | 建議流程 |
|---|---|
| 全新功能開發 | `scan-project` → `create-prd` → `plan-work` → `implement-backend-change` → `backend-test-verification` → `review-change` |
| Bug 修復 | `scan-project`（必要時） → `plan-work` → `implement-backend-change` → `backend-test-verification` → `review-change` |
| 安全性修補 | `scan-project`（必要時） → `plan-work` → `implement-backend-change` → `review-change` → `backend-test-verification` |
| 技術債重構 | `scan-project` → `plan-work` → `refactor-backend` → `backend-test-verification` → `review-change` |
| 純文件產出 | `scan-project` → `create-prd` 或 `plan-work` |
| 生成 repo 規則檔 | `scan-project` → 生成 `AGENTS.md` → 生成第一輪 skills → 自我驗證 |

### 8.2 安全修補為什麼 review 在前
安全修補的風險常在於「修補方向本身可能錯」。
因此在這種情境，允許先做 correctness / security review，再做完整 regression 驗證，比先全部跑完測試才發現方向錯誤更合理。

### 8.3 第一輪不追求完全自動串接
本系統第一輪設計採：

- `AGENTS.md` 定義強制規則與 routing guidance
- skills 定義可重複流程
- 重要決策點仍由使用者確認

不要求第一輪就完成：
- 自動多代理協作
- 自動 PR review pipeline
- 自動外部工具協作

---

## 9. Skills 完整規格

> 以下為第一輪與第二輪 skill 設計規格。

### 9.1 `scan-project`
**目的**：理解 backend 專案結構、技術棧、慣例與主要命令。  
**觸發條件**：進入陌生 repo、要產規則檔、要產 PRD、要拆任務。  
**輸出**：
- 目錄摘要
- 主要技術棧
- build / test / run 命令
- backend 入口與重要模組
- 已知風險與待確認事項

**關鍵要求**：
- 找不到命令時不可亂猜
- 必須區分「觀察到」與「推測」

### 9.2 `create-prd`
**目的**：將模糊需求整理為可拆解、可驗證的 PRD。  
**觸發條件**：新需求、新構想、需要定義問題本質。  
**輸出**：
- 目標
- 範圍與非目標
- 使用情境
- 驗收條件
- 風險與待確認事項

**關鍵要求**：
- 要先講問題本質，不可直接跳解法
- 假設與事實必須分開

### 9.3 `plan-work`
**目的**：把 PRD 或任務轉為執行計畫。  
**觸發條件**：收到 PRD、收到 bug / refactor / security fix 任務。  
**輸出**：
- 任務類型判定
- 影響範圍
- skill 順序
- 驗證策略
- 升級決策點

### 9.4 `verify-change`
**目的**：做高層驗證檢查，可跨文件或小範圍修改。  
**用途定位**：適合 root 層文件、說明文件、通用產物的檢查。  
**輸出**：
- 已檢查項目
- 未檢查項目
- 明顯缺漏

### 9.5 `implement-backend-change`
**目的**：根據任務計畫完成 backend 程式變更。  
**觸發條件**：任務已規劃清楚，且無重大未決事項。  
**輸出**：
- 程式碼變更
- 變更摘要
- 必要測試補充

**邊界**：
- 不自行改 public contract / DB schema / security policy
- 不自行引入重大 dependency

### 9.6 `backend-test-verification`
**目的**：驗證 backend 變更是否可編譯、可執行、無明顯 regression。  
**輸出**：
- 實際執行命令
- 通過與失敗項目
- 未覆蓋風險

**關鍵要求**：
- 不得捏造測試結果
- 無法執行時要明講

### 9.7 `review-change`
**目的**：從 correctness、security、maintainability、test risk 角度做 review。  
**輸出**：
- 問題等級
- 問題位置
- 風險原因
- 具體建議
- 是否阻擋交付

### 9.8 `refactor-backend`
**目的**：在不新增功能的前提下改善結構。  
**輸出**：
- 重構摘要
- 影響範圍
- 驗證結果

### 9.9 第二輪 optional skills
以下建議延後到流程穩定後再做：
- `api-contract-check`
- `db-migration-check`
- `pr-draft-summary`
- `release-checklist`

---

## 10. 行為邊界與升級條件

### 10.1 必須先回報使用者的情況
以下情況不得由 agent 自行決策：

1. DB schema / migration 變更
2. API contract 破壞性變更
3. 新增或更換重要 dependency
4. 權限、驗證、授權流程變更
5. 部署、infra、CI/CD 方式變更
6. 大範圍 refactor 或跨模組改寫
7. 測試無法執行或結果不可信
8. 有多個技術方向且 trade-off 明顯
9. 掃描結果與使用者敘述不一致
10. 需要 subagents、MCP 或額外外部工具時

### 10.2 升級矩陣

| 類型 | 可自動處理 | 需先回報 |
|---|---|---|
| 小範圍純文件調整 | 是 | 否 |
| root / backend `AGENTS.md` 草稿生成 | 是，但須先 scan repo | 若關鍵命令無法確認 |
| 第一輪 skill 檔案生成 | 是 | 若命名或作用域不明 |
| `.codex/config.toml` 生成 | 否，預設延後 | 是 |
| DB schema 相關規則 | 否 | 是 |
| 安全策略調整 | 否 | 是 |
| 新 dependency 引入 | 否 | 是 |

### 10.3 設計原則
- 低風險、局部、可回溯的內容可自動產出
- 高風險、環境敏感、影響權限的內容必須升級

---

## 11. Output Contract 與 Definition of Done

### 11.1 Output Contract 總覽

| 產物 | 輸出內容 | 下一步 |
|---|---|---|
| `scan-project` | 專案掃描摘要 | 產 PRD / 產 `AGENTS.md` / 規劃 |
| `create-prd` | PRD 文件 | `plan-work` |
| `plan-work` | 任務執行計畫 | 產 skills / 實作 |
| `AGENTS.md` | durable guidance | 後續 agent session |
| `SKILL.md` | reusable workflow | 後續 agent routing |
| `backend-test-verification` | 驗證報告 | review / 修復 |
| `review-change` | Review report | 使用者決策 / 修復 |

### 11.2 所有輸出的共同規則
每個輸出都必須：

1. 可讀
2. 可驗證
3. 可被下一步直接使用
4. 明確區分「已確認」與「待確認」
5. 不依賴大量隱含上下文

### 11.3 Definition of Done
每條 workflow 至少要滿足：

1. 目標已達成，或未完成原因已被清楚說明
2. 已列出影響範圍
3. 已列出實際檢查或驗證內容
4. 未驗證缺口已被保留
5. 高風險決策點已升級回報
6. 產物可供下一階段直接使用

---

## 12. 第一輪最小必要輸出物（真正可執行版本）

> 本章是本次修正版最重要的新章節。它定義：**當 Codex 被要求依本 PRD 建檔時，第一輪到底要生成哪些檔案。**

### 12.1 第一輪必生成檔案

```text
/project-root/AGENTS.md
/project-root/.agents/skills/scan-project/SKILL.md
/project-root/.agents/skills/create-prd/SKILL.md
/project-root/.agents/skills/plan-work/SKILL.md
/project-root/.agents/skills/verify-change/SKILL.md
/backend/AGENTS.md
/backend/.agents/skills/implement-backend-change/SKILL.md
/backend/.agents/skills/backend-test-verification/SKILL.md
/backend/.agents/skills/review-change/SKILL.md
/backend/.agents/skills/refactor-backend/SKILL.md
```

### 12.2 第一輪不生成檔案
以下檔案在第一輪預設不生成：

```text
/.codex/config.toml
/.codex/agents/*.toml
/backend/.agents/skills/api-contract-check/SKILL.md
/backend/.agents/skills/db-migration-check/SKILL.md
```

原因：
- `config.toml` 涉及 approval、sandbox、profile 等環境選擇
- `agents/*.toml` 涉及 agent 角色拆分與 spawn 策略
- `api-contract-check` / `db-migration-check` 適合在 repo 規則與第一輪流程穩定後再補

### 12.3 第一輪產檔順序
生成順序必須如下：

1. `scan-project`：掃描 repo 並整理觀察結果
2. 產生 `project-root/AGENTS.md`
3. 產生 `backend/AGENTS.md`
4. 產生 root 層 4 個 skills
5. 產生 backend 層 4 個 skills
6. 執行一次自我檢查，確認：
   - 路徑正確
   - `AGENTS.md` 不過長
   - `SKILL.md` 有 `name` / `description`
   - root / backend 規則沒有重複打架

---

## 13. 每個要生成檔案的最小欄位要求

### 13.1 root `AGENTS.md`
至少要有：
- Repo overview
- Important directories
- Build / test / lint overview（若可確認）
- Core conventions
- Do-not rules
- Definition of done
- Routing guidance：什麼情況應先進 backend 規則、什麼情況應先用哪個 root skill

### 13.2 backend `AGENTS.md`
至少要有：
- Backend overview
- Main modules / directory map
- Real backend commands（若已確認）
- API / DB / service layer conventions
- High-risk escalation rules
- Verification expectations
- Suggested backend skills to route into first

### 13.3 每個 `SKILL.md`
至少要有：
- YAML frontmatter：`name`、`description`
- Purpose
- Trigger conditions
- Inputs
- Steps
- Outputs
- Boundaries
- Verification

### 13.4 `description` 的品質要求
`description` 不能只寫「run tests」或「implement backend changes」。
應該包含：
- 何時要用
- 在什麼範圍用
- 主要做什麼
- 是否屬 mandatory workflow

---

## 14. 生成檔案時的實作規範

### 14.1 一律先掃描，再生成
生成任何 `AGENTS.md` 或 `SKILL.md` 之前，必須先掃描：
- 真實目錄
- 真實命令
- 真實 backend 子系統範圍
- 是否已有現成 docs / scripts 可引用

### 14.2 不得捏造 repo 事實
若 repo 中找不到：
- build 命令
- test 命令
- backend 啟動方式
- 目錄用途

則應採用：
- `TODO:`
- `REQUIRES CONFIRMATION:`
- `Observed:` / `Not yet confirmed:`

禁止直接幻想一套通用命令塞進文件。

### 14.3 文件長度控制
- root `AGENTS.md` 以短小為原則
- backend `AGENTS.md` 只寫 backend 需要的規則
- 太長的流程應下放到 skills
- 太細的 repo 特例可在 skill 內說明或引用額外檔案

### 14.4 先穩定，再擴充
第一輪只做最小必要輸出，不要同時做：
- custom agents
- MCP
- multi-agent orchestration
- CI automation
- 過多 specialized skills

---

## 15. Review 分級標準與輸出格式

### 15.1 分級標準

| 等級 | 說明 |
|---|---|
| P0 / Critical | 重大安全、資料遺失、系統不可用 |
| P1 / High | 影響功能正確性、穩定性、重要邏輯 |
| P2 / Medium | 維護性差、潛在風險、測試不足 |
| P3 / Low | 命名、風格、可讀性建議 |
| None | 無 blocking issue |

### 15.2 建議輸出格式

```markdown
## Code Review Report

### 檔案：<file-path>

#### [P1] 問題標題
- 位置：第 N 行
- 風險：說明影響
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

---

## 16. 驗證策略與 Skill 評估

### 16.1 為什麼需要 eval
skill 的價值不在「有檔案」，而在：
- 是否在正確情境被觸發
- 是否真的縮短流程
- 是否穩定產出符合規範的內容
- 是否降低重工

### 16.2 評估維度
至少從以下四個面向評估：
1. Outcome：任務是否完成
2. Process：流程是否走對
3. Format：輸出是否合規
4. Efficiency：是否避免多餘步驟

### 16.3 第一輪最小驗證集
至少準備以下案例：
- 新功能 PRD
- bug 修復規劃
- root / backend `AGENTS.md` 生成
- 小範圍 backend 變更
- 純文件修改檢查
- refactor 升級判斷

### 16.4 產檔驗證清單
生成完第一輪檔案後，至少檢查：

1. 檔案路徑是否全部正確
2. 所有 `SKILL.md` 是否都有 `name` 與 `description`
3. root `AGENTS.md` 與 backend `AGENTS.md` 是否各守其責
4. 是否有捏造命令或路徑
5. 是否有明確寫出高風險升級規則
6. 是否有過度寬泛、難以觸發的 skill 描述

---

## 17. 分階段落地路線圖

### Phase 0：PRD 定稿
完成本文件。

### Phase 1：生成第一輪最小必要輸出物
生成：
- root `AGENTS.md`
- backend `AGENTS.md`
- root 4 個 skills
- backend 4 個 skills

### Phase 2：根據真實使用回饋修正 routing 與邊界
把重複錯誤、重複 review feedback 寫回 `AGENTS.md` 與 `SKILL.md`。

### Phase 3：補第二輪 specialized skills
視需求增加：
- `api-contract-check`
- `db-migration-check`
- release / checklist 類技能

### Phase 4：再考慮 `.codex/config.toml`
只有在以下前提成立時才進入此階段：
- 已清楚知道 approval policy 需求
- 已清楚知道 sandbox 邊界
- 已確定是否要用 profiles / MCP / subagents

### Phase 5：再考慮 `.codex/agents/*.toml`
只有在以下前提成立時才進入此階段：
- 單代理工作流已穩定
- 角色拆分確實帶來好處
- 願意承擔更高 token 與流程複雜度

---

## 18. 直接可用的執行指令模板（給後續 Codex 任務）

> 本章是給未來真正執行產檔時使用的任務模板。

### 18.1 目標
根據 `/docs/agent-system-PRD.md`，掃描目前 repo，並生成第一輪最小必要輸出物。

### 18.2 執行要求
1. 先掃描 repo，確認真實目錄、命令、backend 邊界
2. 根據 PRD 只生成第一輪必生成檔案
3. 不生成 `.codex/config.toml`、`.codex/agents/*.toml`、optional skills
4. 若找不到真實命令，使用 `TODO:` 或 `REQUIRES CONFIRMATION:` 標示
5. 生成後自我檢查：
   - 路徑是否正確
   - `name` / `description` 是否齊全
   - root / backend 是否有重複衝突
   - 是否有捏造資訊
6. 最後輸出變更檔案清單與每個檔案一句摘要

### 18.3 預期生成檔案
```text
/project-root/AGENTS.md
/project-root/.agents/skills/scan-project/SKILL.md
/project-root/.agents/skills/create-prd/SKILL.md
/project-root/.agents/skills/plan-work/SKILL.md
/project-root/.agents/skills/verify-change/SKILL.md
/backend/AGENTS.md
/backend/.agents/skills/implement-backend-change/SKILL.md
/backend/.agents/skills/backend-test-verification/SKILL.md
/backend/.agents/skills/review-change/SKILL.md
/backend/.agents/skills/refactor-backend/SKILL.md
```

---

## 19. 風險、限制與未來擴充

### 19.1 風險
1. **規則寫太多**：`AGENTS.md` 會膨脹，影響可維護性。
2. **skill 寫太廣**：觸發條件模糊，routing 不穩。
3. **太早角色化**：在流程不穩時引入 custom agents，只會加複雜度。
4. **環境規則與 repo 規則混寫**：config 與 repo guidance 難以分工。
5. **過度追求自動化**：忽略高風險升級與人工判斷。
6. **捏造 repo 事實**：是第一輪產檔最常見、也最危險的錯誤。

### 19.2 限制
本 PRD 仍有以下限制：
- 尚未對應到某個真實 repo 的實際命令
- 尚未建立真實 eval dataset
- 尚未驗證所有 skills 的觸發穩定性
- 尚未處理多人團隊共享 config 的治理問題

### 19.3 未來擴充方向
1. 為 backend 補 `api-contract-check` 與 `db-migration-check`
2. 補 `PLANS.md` / execution plan 模板
3. 補 repo-specific review checklist
4. 規則穩定後再接 CI / GitHub Action
5. 明確需要時再引入 subagents 做 bounded parallel work

---

## 20. 最終結論

這份修正版 PRD 的核心不是「建立很多 agent」，而是：

> 先把規則層、工作流層、環境層、可選角色層拆乾淨，再明確定義第一輪真正要生成哪些檔案、以什麼順序生成、哪些檔案暫時不生成，讓 Codex 可以從 repo 掃描出發，穩定產出可用的 `AGENTS.md` 與 `SKILL.md`。