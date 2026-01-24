# PRD：推薦景點回傳 Google Maps 實際評論人數

## 一、背景與動機

目前 `GET /api/recommands/{country}` 回傳的推薦景點資訊包含評分（`rating`），但缺少「實際評論人數」。使用者在評估景點可信度時，需要同時看評分與評論人數以提升決策信心。因此需要在推薦景點回傳中加入 Google Maps `userRatingCount` 對應欄位。

---

## 二、目標

1. **在推薦景點 API 回傳中新增 `reviewCount` 欄位**，呈現 Google Maps 實際評論人數。
2. **保持現有 API 不破壞相容性**（新增欄位不影響既有欄位）。
3. **確保資料來源一致**：從資料庫已存在的 `poi.review_count` 取得，若需要補刷資料時仍可回傳。

---

## 三、非目標（Out of Scope）

- 不新增新端點或 API 版本。
- 不變更推薦邏輯（隨機抽樣、ALL 模式）。
- 不新增前端 UI 改版或使用者端功能。

---

## 四、使用者故事

> 作為使用者，我希望在推薦景點列表中看到每個景點的評論人數，這樣我能更清楚判斷評分的可信度。

---

## 五、功能需求

### 5.1 API 行為

**API**：`GET /api/recommands/{country}`  
**新增回傳欄位**：

| 欄位名稱 | 類型 | 說明 |
|----------|------|------|
| reviewCount | Integer | Google Maps 實際評論人數（userRatingCount） |

### 5.2 回傳範例（新增欄位）

```json
[
  {
    "poiId": "42061ab7-174a-476f-85fd-23b6f89d360f",
    "placeId": "ChIJLZgN8z6tQjQR3kK3hKZJwJg",
    "name": "台北 101",
    "country": "台灣",
    "city": "台北市",
    "photoUrl": "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=...",
    "rating": 4.5,
    "reviewCount": 31245,
    "lat": 25.0330,
    "lon": 121.5654
  }
]
```

---

## 六、資料來源與流程

1. **主要資料來源**：`poi.review_count`  
2. **生成與更新**：由既有 Place Details 流程更新（`userRatingCount` → `reviewCount`）。
3. **回傳方式**：
   - 一般情境：透過 repository projection 直接讀取 `reviewCount`。
   - fallback（資料缺漏時）：由 `PlaceDetailResponse.reviewCount` 取得並回傳。

---

## 七、相容性與風險

### 7.1 相容性
- 新增欄位不影響現有 API 呼叫方。
- 若使用者端不認識該欄位，仍可正常解析既有資料。

### 7.2 風險與對策
- **風險**：資料庫中 reviewCount 可能為 null（尚未更新或未收錄）。  
  **對策**：允許 null，並保持欄位可為空。

---

## 八、驗收標準（Acceptance Criteria）

1. 呼叫 `GET /api/recommands/{country}` 時，回傳 JSON 中包含 `reviewCount` 欄位。
2. `reviewCount` 與 Google Maps `userRatingCount` 值一致（如資料庫已有）。
3. API 仍維持既有回傳格式與欄位。

---

## 九、文件更新

- 更新 `backend/docs/recommand-api.md` 增加 `reviewCount` 回傳欄位說明與範例。

