# Product Steering Document

## Project Vision

TravelPlanWithAccounting 是一個整合旅遊行程規劃與記帳功能的全端應用系統，旨在為旅行者提供一站式的旅遊規劃和財務管理解決方案。

### Core Value Proposition

**讓旅行規劃與財務管理無縫整合**

旅行者在規劃行程時，往往需要在多個應用程式之間切換：
- 使用地圖應用查看景點
- 使用記帳應用記錄花費
- 使用行程規劃應用安排時間表
- 使用試算表追蹤預算

本專案將這些功能整合到單一平台，提供一致的使用者體驗。

---

## Goals

### Primary Goals

1. **旅遊行程規劃**
   - 提供直覺的行程規劃介面
   - 支援景點搜尋與資訊查詢
   - 整合地圖與路線規劃
   - 支援行程分享與協作

2. **財務管理與記帳**
   - 即時記錄旅遊花費
   - 自動分類支出項目
   - 預算追蹤與提醒
   - 多幣別支援與匯率轉換

3. **整合體驗**
   - 在規劃行程時同步設定預算
   - 在記帳時關聯對應的景點或活動
   - 提供財務概覽與分析報表

### Secondary Goals

1. **多語系支援** - 服務國際旅客（zh-TW, en-US）
2. **離線功能** - 旅行時網路不穩定的情境
3. **資料匯出** - 支援多種格式匯出記錄
4. **社群功能** - 分享行程與推薦景點

---

## User Outcomes

### For Individual Travelers

**Before (Pain Points)**
- 需要在多個 app 之間切換
- 忘記記錄某些花費
- 難以追蹤總預算使用狀況
- 行程與財務資料分散

**After (Desired State)**
- 單一平台管理行程與花費
- 即時記錄，不遺漏任何支出
- 清晰的預算使用視覺化
- 所有資料集中管理

### For Group Travelers

**Before (Pain Points)**
- 難以協調分帳
- 不清楚誰付了什麼
- 需要手動計算分攤金額

**After (Desired State)**
- 自動追蹤群組花費
- 清楚的分帳記錄
- 自動計算應付/應收金額

---

## Success Metrics

### Product Metrics

1. **使用者參與度**
   - 日活躍使用者 (DAU)
   - 月活躍使用者 (MAU)
   - 平均每週使用頻率

2. **功能使用率**
   - 創建行程數量
   - 記帳筆數
   - 景點搜尋次數

3. **使用者滿意度**
   - App Store / Google Play 評分
   - 使用者回饋與建議
   - NPS (Net Promoter Score)

### Technical Metrics

1. **效能**
   - API 回應時間 < 500ms (P95)
   - 頁面載入時間 < 2s
   - 99.9% 可用性

2. **品質**
   - 測試覆蓋率 > 80%
   - 生產環境錯誤率 < 0.1%
   - 安全漏洞 = 0

---

## Scope

### In Scope

#### Phase 1: MVP (最小可行產品)
- ✅ 使用者註冊與登入
- ✅ 基本行程規劃（新增/編輯/刪除）
- ✅ 基本記帳功能（收支記錄）
- ✅ 景點搜尋（Google Places API）
- ✅ 多語系支援（zh-TW, en-US）
- ✅ 行程與記帳關聯

#### Phase 2: Enhanced Features
- 📋 預算設定與追蹤
- 📋 支出分類與分析
- 📋 行程時間軸視圖
- 📋 地圖整合與路線規劃
- 📋 群組協作（分享行程）
- 📋 多幣別與匯率轉換

#### Phase 3: Advanced Features
- 📋 AI 行程推薦
- 📋 群組分帳功能
- 📋 離線模式
- 📋 資料匯出（PDF, Excel）
- 📋 社群功能（公開行程、評論）
- 📋 整合第三方服務（訂房、訂票）

### Out of Scope

- ❌ 訂房/訂票功能（整合第三方）
- ❌ 即時通訊功能
- ❌ 支付處理（僅記錄，不處理交易）
- ❌ 社交網路功能（非旅遊相關）
- ❌ 遊戲化元素

---

## Target Audience

### Primary Users

1. **個人旅行者 (Solo Travelers)**
   - 年齡：25-45 歲
   - 特徵：喜歡自主規劃、注重預算控制
   - 需求：完整的行程與財務記錄

2. **情侶/家庭旅行者 (Couples/Families)**
   - 年齡：30-50 歲
   - 特徵：需要協作規劃、關注總花費
   - 需求：共享行程、清楚的分帳記錄

### Secondary Users

1. **背包客 (Backpackers)**
   - 年齡：20-35 歲
   - 特徵：長期旅行、極度注重預算
   - 需求：詳細的花費追蹤、多幣別支援

2. **商務旅客 (Business Travelers)**
   - 年齡：30-55 歲
   - 特徵：頻繁出差、需要報帳
   - 需求：快速記帳、匯出報表

---

## User Journey

### 旅行前 (Planning Phase)

1. 使用者建立新行程
2. 搜尋目的地景點
3. 新增景點到行程
4. 設定預算
5. 邀請旅伴（如適用）

### 旅行中 (Execution Phase)

1. 查看當日行程
2. 導航到景點
3. 記錄花費（即時或事後）
4. 檢視預算使用狀況
5. 調整行程（如需要）

### 旅行後 (Review Phase)

1. 檢視完整行程記錄
2. 查看財務分析報表
3. 匯出資料（備份或報帳）
4. 分享行程給朋友
5. 為景點評分/留言（未來功能）

---

## Design Principles

### 1. Simplicity First
- 介面簡潔直覺，減少學習成本
- 常用功能一鍵存取
- 避免過度設計

### 2. Data Integrity
- 所有記錄可追溯
- 提供編輯歷史
- 資料備份與復原

### 3. Privacy & Security
- 使用者資料加密
- 明確的隱私權政策
- 使用者掌控資料分享範圍

### 4. Accessibility
- 支援多語系
- 響應式設計（手機、平板、桌機）
- 遵循無障礙設計標準（WCAG）

### 5. Performance
- 快速載入
- 流暢的使用者體驗
- 離線優先（未來）

---

## Constraints

### Technical Constraints
- 前後端分離架構（已確立）
- 使用 Java 21 + Spring Boot（後端）
- 使用 Next.js 15 + React 19（前端）
- PostgreSQL 資料庫

### Business Constraints
- 專案時程：依功能優先級分階段交付
- 預算：開源專案，成本最小化
- 資源：小型開發團隊

### Regulatory Constraints
- 遵循 GDPR（歐盟）
- 遵循 PDPA（台灣個資法）
- 符合 App Store / Google Play 政策

---

## Risks & Mitigation

### Technical Risks

| 風險 | 影響 | 機率 | 緩解策略 |
|------|------|------|----------|
| 第三方 API 限制（Google Places） | 高 | 中 | 實作 cache 機制、考慮替代方案 |
| 效能問題（大量資料） | 中 | 中 | 分頁、虛擬滾動、索引最佳化 |
| 資料同步衝突（協作） | 中 | 低 | 實作 CRDT 或 OT 演算法 |

### Product Risks

| 風險 | 影響 | 機率 | 緩解策略 |
|------|------|------|----------|
| 使用者不願切換現有工具 | 高 | 中 | 提供資料匯入功能、強化獨特價值 |
| 功能過於複雜 | 中 | 中 | 分階段發布、持續 UX 測試 |
| 競爭對手推出類似產品 | 中 | 低 | 專注核心價值、快速迭代 |

---

## Future Vision (Long-term)

### 3 Years
- 成為台灣最受歡迎的旅遊規劃 + 記帳應用
- 支援 10+ 語言
- 100萬+ 活躍使用者
- 建立旅遊社群生態系統

### 5 Years
- 拓展至國際市場
- 整合更多旅遊服務（訂房、交通）
- 提供 AI 個人化推薦
- B2B 服務（旅行社、企業差旅管理）

---

## Changelog

- **2025-11-08**: 初版建立（Steering Document Creation）
