# Requirements Document

> **原則**：所有內容必須忠實引用 `spec/erm.dbml`、`spec/features/*.feature` 或 SDD。遇到模糊敘述時，不寫腦補；先釐清後再填。

---

## Workflow（產生 `requirements.md` 前必做）
1. **讀取來源**：拉取 ERM、Feature、SDD 與 steering docs（product / tech / structure）。
2. **Discovery 掃描**：以 A1~D2 檢查清單逐項檢查，標記 Clear / Partial / Missing。
3. **釐清**：將 Partial / Missing 轉為 Clarification Questions，指定優先級與預設值。
4. **撰寫文件**：依序填寫 Introduction → Data Model → Functional → Non-Functional → Traceability；引用來源，保持可追溯。

---

## Discovery 掃描精要

### A. 資料模型（spec/erm.dbml / SDD）
- **A1 實體**：核心概念都有實體？命名唯一？是否遺漏衍生實體？
- **A2 屬性**：每個欄位型別、note、單位、允許值是否明確？
- **A3 邊界**：數值、字串、枚舉、特殊值（空、負、零）是否量化？
- **A4 不變條件**：跨欄位計算、依賴是否具體寫出公式？
- **A5 關聯 / 唯一性**：PK、Unique、FK、關聯類型是否完整？
- **A6 狀態**：狀態集合、轉換合法性、初始/終止狀態是否列明？

### B. 功能（spec/features/*.feature / SDD）
- **B1 功能識別**：每個交互都有 Feature？功能邊界是否清楚？
- **B2 規則**：Rule 是否原子、可驗證、區分前後置條件？
- **B3 例子**：每條 Rule 是否有 Example？缺少時標示 `#TODO`。
- **B4 邊界**：臨界值、時間、狀態、組合情境是否各有 Example？
- **B5 錯誤**：錯誤/異常流程是否有 Rule + Example？訊息定義清晰？

### C. 術語一致
- 是否有詞彙表？同義詞/同名異義是否淘汰？棄用術語是否標記？

### D. 其他品質
- **D1 待決 / TODO**：列出影響範圍與後續動作。
- **D2 模糊語**：像「快速」「適當」必須量化或轉成條件。

只要出現 Partial / Missing，就建立釐清問題或回填來源文件，直到所有 High 優先都解決。

---

## Introduction

### Feature Overview
[以 2~3 句描述此功能的目的、使用者價值、觸發情境。]

### Source Specifications
- **資料模型**：`spec/erm.dbml`（列出涉入的 Table）；若無則指向 SDD 區段。
- **功能模型**：`spec/features/<feature>.feature`；若無則指向 SDD。
- **輔助文件**：product.md / tech.md / structure.md（列出章節）。

---

## Clarification Questions

> 問題來自 Discovery 掃描。Dashboard 回答 A/B/C/Short；未答則採預設（⭐）。答案需同步回 ERM / Feature / SDD。

### 📌 釐清問題格式（必留）

[若無歧義可整段刪除；若有，依此格式新增。]

#### Q1: [實體或功能] - [問題摘要]

**Question**: [完整敘述，需白話、可測試]

**Context**:
- **定位**：`spec/erm.dbml:<Table>.<Column>` 或 `spec/features/<feature>.feature:<Rule>` 或 `SDD:<section>`
- **歧義點**：為何需要釐清？缺了哪個條件/值/結果？

**Options**:
| 選項 | 描述 | 預設 |
|------|------|------|
| A | [選項 A 描述] | |
| B | [選項 B 描述] | ⭐ |
| C | [選項 C 描述] | |
| Short | 自填 ≤ 5 字 | |

**Impact**: 說明影響的實體 / 規則 / 測試 / 架構決策。

**Priority**: High / Medium / Low  
**Discovery Check**: 對應 A1~D2 編號  
**Status**: ⏳ Pending / ✅ Answered  
**Answer**: `B - [描述]` 或 `Short - ...`

（可複製上方模板新增 Q2/Q3…）

---

## Alignment with Product Vision
- **Product Principles**：引用 product.md 中與此功能最相關的 2~3 條，並描述具體對齊方式。
- **Success Metrics**：定義量化成功指標（例如活躍率、完成率、錯誤率）。

---

## Data Model
為每個涉入實體填表；若無 ERM，直接在此定義（需標註來源）。

### Entity: `<Name>`
**來源**：`spec/erm.dbml:<table>` / `SDD:<section>`

```dbml
Table <Name> {
  id string [pk, note: "…"]
  ...
}
```
- **用途**：此實體要解決什麼商業問題？
- **關鍵屬性**：`<field> (<type>)` — 限制 / 預設 / 單位
- **跨欄位約束**：等式 / 不變條件
- **關聯**：指出一對一/多對多與擁有者

（依需要新增更多實體）

---

## Functional Requirements

### Feature Reference
**來源**：`spec/features/<feature>.feature` / `SDD:<section>`

```gherkin
Feature: <名稱>
  <描述>
```

### Rules & Acceptance Criteria
對每個 Rule 建一節；若無 Rule，先補 Rule 再寫需求。

#### Requirement `<n>`: <Rule 摘要>
**Gherkin Rule**
```gherkin
Rule: <摘要>
  <說明>
```

**Example**
```gherkin
Example: <標題>
  Given ...
  When ...
  Then ...
```

**Acceptance Criteria**
1. **GIVEN** … **WHEN** … **THEN** … **SHALL** …
   - Example：`spec/features/<feature>.feature:<line>`
   - Source：`SDD:<section>`（若適用）

**邊界 / 異常**：列出臨界值、非法輸入、失敗流程。  
**Discovery 備註**：記錄對應 B3/B4/B5 結果（✅/⚠️）。

---

## Non-Functional Requirements
- **Architecture / Modularity**（structure.md）：層次責任、依賴方向、SRP/DI/Testability 原則。
- **Performance**（tech.md）：延遲、吞吐、資源上限；必須量化（如 API P95 < 100ms）。
- **Security**（tech.md）：輸入驗證、錯誤遮蔽、認證/授權、Domain Exceptions。
- **Reliability & Observability**：重試策略、指標/告警、資料持久化策略。
- **Usability**：界面響應、可及性或其它 UX 要求。
- **Testability**：BDD/TDD/整合測試位置、框架、覆蓋率門檻；引用 testing_strategy 或 structure.md。

---

## Dependencies & Integration
- **Internal**：列出需要呼叫的 Service / Repository / DTO / Utility。
- **External**：框架、第三方服務、佇列、快取等；引用 tech.md。
- **Constraints & Risks**：技術限制、部署條件、已知風險（以 product / tech 為來源）。

---

## Traceability Matrix

| Requirement | Gherkin Rule/Line | DBML Entity | Acceptance Criteria | Test (BDD / Unit) |
|-------------|-------------------|-------------|---------------------|-------------------|
| REQ-1 | `spec/features/...:<n>` | `<Entity>` | AC-1.1, AC-1.2 | `tests/...` |

（至少包含所有 Requirement）

---

## Discovery 摘要

| 分類 | Clear | Partial | Missing | Clarification |
|------|-------|---------|---------|---------------|
| 資料模型 A1-A6 |   |   |   |   |
| 功能 B1-B5 |   |   |   |   |
| 術語 C1-C2 |   |   |   |   |
| 其他 D1-D2 |   |   |   |   |

- **主要議題**：條列最影響實作的 2~3 個歧義。
- **高優先釐清**：列出尚未回答的問題 ID。

---

## 審核檢查表
- [ ] 已完整跑 Discovery，並同步結果至 Clarification 區。
- [ ] 所有 High 優先釐清已回答或採預設值。
- [ ] 每個 Requirement 都有對應 Rule + Example + AC。
- [ ] 非功能需求均引用 product / tech / structure。
- [ ] 資料模型與 `spec/erm.dbml`（或 SDD）完全對齊。
- [ ] Traceability Matrix 可追溯到規格與測試。
- [ ] 文件中無模糊形容詞或未量化敘述。

---

**備註**：若要新增章節，先問自己三件事——「真問題嗎？有更簡單的做法嗎？會不會破壞既有使用者？」若答案站不住腳，就不要寫。
