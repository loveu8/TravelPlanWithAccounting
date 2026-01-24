# 推薦景點 API 說明

## 概述

推薦景點 API 提供各國家/地區的熱門景點推薦，支援多語系顯示。系統會根據預設的景點配置檔案返回精選的旅遊景點資訊，並可透過 `limit` 參數控制回傳筆數，也支援跨國整合查詢。

---

## API 端點

### 取得推薦景點

- **API**：`GET /api/recommands/{country}`
- **說明**：取得指定國家的推薦景點清單

#### 請求參數

| 參數名稱 | 類型 | 必填 | 位置 | 說明 | 範例 |
|---------|------|------|------|------|------|
| country | String | 是 | Path | 國家代碼 | TW、JP、KR、HK、ALL |
| limit | Integer | 否 | Query | 回傳景點筆數，範圍 4-10，預設為 4。僅接受整數。 | 4、6、10 |

#### Headers

```
Accept-Language: zh-TW
```

> **語系支援**：支援 `zh-TW`、`en-US` 等語系，未提供時預設為 `zh-TW`。

#### 支援的國家代碼

| 代碼 | 國家/地區 | 說明 |
|------|-----------|------|
| TW | 台灣 | 台灣熱門景點 |
| JP | 日本 | 日本熱門景點 |
| KR | 韓國 | 韓國熱門景點 |
| HK | 香港 | 香港熱門景點 |
| ALL | 全部 | 整合 TW/JP/KR/HK 四國資料，確保每國至少 1 筆，再依 `limit` 補齊剩餘筆數 |

#### 回傳筆數與隨機策略

- **預設筆數**：未指定 `limit` 時預設回傳 4 筆資料。
- **筆數範圍**：`limit` 僅接受 4-10 的整數，超出範圍視為無效請求。
- **單一國家**：若可用景點超過 `limit`，以亂數挑選符合筆數的景點；不足時回傳所有可用景點。
- **ALL 模式**：
  - 先從 HK/JP/KR/TW 各抽 1 筆景點，確保四國皆有呈現。
  - 若 `limit` 大於 4，剩餘筆數從仍有剩餘景點的國家池中隨機抽取。
  - 若總可用景點小於 `limit`，以所有可用景點為主並記錄警示以供監控。

#### 回應格式

**成功回應**：
```json
[
  {
    "poiId": "42061ab7-174a-476f-85fd-23b6f89d360f",
    "placeId": "ChIJLZgN8z6tQjQR3kK3hKZJwJg",
    "name": "台北 101",
    "country" : "台灣",
    "city": "台北市",
    "photoUrl": "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=...",
    "rating": 4.5,
    "reviewCount": 31245,
    "lat": 25.0330,
    "lon": 121.5654
  },
  {
    "poiId": "e1985ee4-8d5a-45a5-9b34-60dffe2d8e3c",
    "placeId": "ChIJLZgN8z6tQjQR3kK3hKZJwJg",
    "name": "國立故宮博物院",
    "country" : "台灣",
    "city": "台北市",
    "photoUrl": "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=...",
    "rating": 4.7,
    "reviewCount": 18321,
    "lat": 25.1024,
    "lon": 121.5485
  }
]
```

#### 回應欄位說明

**成功回應欄位**：

| 欄位名稱 | 類型 | 說明 |
|----------|------|------|
| poiId | UUID | 內部景點 ID |
| placeId | String | Google Places API 的 place ID |
| name | String | 景點名稱（根據語系顯示） |
| country | String | 所在國家名稱 |
| city | String | 所在城市名稱 |
| photoUrl | String | 景點照片 URL（第一張照片） |
| rating | Double | 景點評分（1.0-5.0） |
| reviewCount | Integer | Google Maps 實際評論人數 |
| lat | Double | 緯度 |
| lon | Double | 經度 |

**錯誤回應欄位**：

| 欄位名稱 | 類型 | 說明 |
|----------|------|------|
| data | Object | 成功時為資料內容，錯誤時為 null |
| meta | Object | 輔助資訊，錯誤時為 null |
| error | Object | 錯誤資訊，成功時為 null |
| error.code | String | 錯誤代碼（如 RC-001、RC-002、RC-003、RC-004） |
| error.message | String | 錯誤訊息（根據語系顯示） |
| error.timestamp | String | 錯誤發生時間（ISO 8601 格式） |
| error.details | Object | 額外錯誤詳情，通常為 null |

#### 錯誤回應

**不支援的國家代碼**：
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "RC-001",
    "message": "country 僅支援 TW/JP/KR/HK/ALL，傳入值: XX",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

**不支援的語系**：
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "RC-002",
    "message": "Accept-Language 僅支援 zh-TW 與 en-US，傳入值: xx-XX",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

**配置錯誤**：
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "RC-003",
    "message": "推薦清單設定錯誤：TW",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

**不支援的 limit 數值**：
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "RC-004",
    "message": "limit 僅接受 4-10 的整數，傳入值: 2",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

---

## 使用範例

### JavaScript 範例

```javascript
// 取得台灣推薦景點，預設回傳 4 筆
async function getTaiwanRecommendations() {
  try {
    const response = await fetch('/api/recommands/TW', {
      method: 'GET',
      headers: {
        'Accept-Language': 'zh-TW'
      }
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const recommendations = await response.json();
    console.log('台灣推薦景點:', recommendations);
    return recommendations;
  } catch (error) {
    console.error('取得推薦景點失敗:', error);
    throw error;
  }
}

// 取得日本推薦景點（英文），自訂回傳 6 筆
async function getJapanRecommendations() {
  try {
    const response = await fetch('/api/recommands/JP?limit=6', {
      method: 'GET',
      headers: {
        'Accept-Language': 'en-US'
      }
    });
    
    const recommendations = await response.json();
    console.log('Japan recommendations:', recommendations);
    return recommendations;
  } catch (error) {
    console.error('Failed to get Japan recommendations:', error);
    throw error;
  }
}

// 使用範例
async function loadRecommendations() {
  try {
    // 載入台灣景點
    const taiwanSpots = await getTaiwanRecommendations();

    // 載入日本景點
    const japanSpots = await getJapanRecommendations();

    // 跨國整合，每個國家至少 1 筆
    const allResponse = await fetch('/api/recommands/ALL?limit=8', {
      headers: {
        'Accept-Language': 'zh-TW'
      }
    });
    const allSpots = await allResponse.json();

    // 顯示在 UI 上
    displayRecommendations(taiwanSpots, '台灣');
    displayRecommendations(japanSpots, '日本');
    displayRecommendations(allSpots, '跨國精選');
    
  } catch (error) {
    console.error('載入推薦景點失敗:', error);
  }
}

function displayRecommendations(recommendations, country) {
  const container = document.getElementById('recommendations-container');
  
  recommendations.forEach(spot => {
    const card = document.createElement('div');
    card.className = 'recommendation-card';
    card.innerHTML = `
      <img src="${spot.photoUrl}" alt="${spot.name}" />
      <h3>${spot.name}</h3>
      <p>${spot.city} • 評分: ${spot.rating}</p>
      <button onclick="viewDetails('${spot.placeId}')">查看詳情</button>
    `;
    container.appendChild(card);
  });
}
```

### cURL 範例

```bash
# 取得台灣推薦景點（繁體中文），預設 4 筆
curl -X GET "http://localhost:8080/api/recommands/TW" \
  -H "Accept-Language: zh-TW"

# 取得日本推薦景點（英文），限定 6 筆
curl -X GET "http://localhost:8080/api/recommands/JP?limit=6" \
  -H "Accept-Language: en-US"

# 取得韓國推薦景點（預設語系），限定 5 筆
curl -X GET "http://localhost:8080/api/recommands/KR?limit=5"

# 取得跨國精選，每國至少 1 筆，共 8 筆
curl -X GET "http://localhost:8080/api/recommands/ALL?limit=8" \
  -H "Accept-Language: zh-TW"
```

---

## 技術實作細節

### 資料來源

推薦景點資料來自預設的 JSON 配置檔案：

- `backend/src/main/resources/recommand/TW.json` - 台灣景點
- `backend/src/main/resources/recommand/JP.json` - 日本景點
- `backend/src/main/resources/recommand/KR.json` - 韓國景點
- `backend/src/main/resources/recommand/HK.json` - 香港景點

> `ALL` 參數會依序載入 `HK.json` → `JP.json` → `KR.json` → `TW.json`，並在合併後進行隨機抽樣。

### 資料處理流程

1. **配置載入**：依 `country` 參數從對應 JSON 檔案載入預設景點 ID；若為 `ALL` 則合併 HK/JP/KR/TW 四份配置。
2. **limit 驗證**：確認 `limit` 是否為 4-10 的整數，未提供時以 4 代入。
3. **語系處理**：根據 `Accept-Language` header 決定顯示語系。
4. **資料庫查詢**：查詢景點的本地化資訊。
5. **資料補充**：如果資料庫中缺少景點資訊，會透過 Google Places API 即時補充。
6. **隨機篩選**：
   - 單一國家：若可用景點筆數 ≥ `limit`，隨機挑選 `limit` 筆；若不足則回傳所有可用筆數。
   - `ALL`：
     1. 先為 HK/JP/KR/TW 各挑選 1 筆（若某國可用筆數為 0 則視為配置錯誤）。
     2. 剩餘筆數從尚有餘量的國家池中隨機挑選，直到達到 `limit` 或無更多可用資料。
7. **結果回傳**：返回標準化的景點資訊，保持原有欄位結構。

### 快取機制

- 配置檔案會在服務啟動時載入並快取
- 景點資料會根據語系進行快取
- 支援即時資料補充和更新

### 錯誤處理

- **國家代碼驗證**：只接受 TW、JP、KR、HK、ALL
- **limit 驗證**：僅接受 4-10 的整數，超出範圍或非數值時回傳 RC-004。
- **語系驗證**：檢查語系是否支援
- **資料完整性**：確保每個國家有足夠景點供 `limit` 使用；`ALL` 模式需保證每國至少 1 筆
- **容錯機制**：部分景點資料缺失時仍會返回可用的景點

---

## 邊界情境與注意事項

1. **limit 小於國家數量**：由於 `limit` 最小值為 4，恰好等於支援的國家數量，`ALL` 模式始終能為每個國家保留至少 1 筆。
2. **單一國家資料不足**：若某國家配置檔案中可用景點少於 `limit`，則回傳所有可用景點並於系統監控中記錄警示，方便後續補齊資料。
3. **跨國景點不足**：`ALL` 模式若合併後的可用景點少於 `limit`，回傳全部可用景點且維持每國至少一筆的原則；同時產生警示以利營運人員調整配置。
4. **亂數重複性**：採用安全亂數來源（如 `SecureRandom`）避免固定排序造成相同結果，可視需求設定快取或熔斷機制確保性能。
5. **語系回退**：若特定景點缺少請求語系的本地化資料，延續既有邏輯回退至預設語系（`zh-TW`）。

---

## 最佳實踐

### 前端整合建議

1. **語系處理**：
   ```javascript
   // 根據使用者設定決定語系
   const language = getUserLanguage(); // 取得使用者偏好的語系
   const headers = {
     'Accept-Language': language
   };
   ```

2. **錯誤處理**：
   ```javascript
   try {
     const response = await fetch('/api/recommands/TW', {
       headers: { 'Accept-Language': 'zh-TW' }
     });
     
     const result = await response.json();
     
      if (result.error) {
        // 處理錯誤
        switch (result.error.code) {
          case 'RC-001':
            console.error('不支援的國家代碼:', result.error.message);
            break;
          case 'RC-002':
            console.error('不支援的語系:', result.error.message);
            // 切換到預設語系
            break;
          case 'RC-003':
            console.error('配置錯誤:', result.error.message);
            break;
          case 'RC-004':
            console.error('不支援的 limit 數值:', result.error.message);
            break;
          default:
            console.error('未知錯誤:', result.error.message);
        }
      } else {
        // 處理成功回應
        const recommendations = result.data || result; // 直接返回資料時
        console.log('推薦景點:', recommendations);
      }
   } catch (error) {
     console.error('請求失敗:', error);
   }
   ```

3. **動態控制 limit**：
   ```javascript
   const limit = Math.min(Math.max(userPreferredCount, 4), 10); // 確保在允許範圍內
   const response = await fetch(`/api/recommands/ALL?limit=${limit}`);
   ```

4. **資料展示**：
   ```javascript
   // 顯示景點卡片
   recommendations.forEach(spot => {
     // 使用 spot.photoUrl 顯示照片
     // 使用 spot.rating 顯示評分
     // 使用 spot.lat, spot.lon 顯示地圖位置
   });
   ```

### 效能優化

1. **預載入**：在應用啟動時預載入常用國家的推薦景點
2. **快取策略**：在前端實作適當的快取機制
3. **圖片優化**：使用適當的圖片尺寸和格式

---

## 常見問題

### Q: 為什麼某些景點沒有照片？
A: 如果景點在 Google Places API 中沒有照片資料，`photoUrl` 欄位會是 `null`。

### Q: 如何新增或修改推薦景點？
A: 需要修改對應國家的 JSON 配置檔案，並重新啟動服務。

### Q: 支援哪些語系？
A: 目前支援繁體中文（zh-TW）和英文（en-US），語系支援取決於資料庫中的 i18n 資料。

### Q: 推薦景點會自動更新嗎？
A: 不會自動更新，需要手動修改配置檔案。但景點的基本資訊（評分、照片等）會透過 Google Places API 即時取得。

### Q: 如果某個景點資料庫中找不到怎麼辦？
A: 系統會嘗試透過 Google Places API 即時取得該景點的詳細資訊。

### Q: 錯誤代碼代表什麼意思？
A: 系統使用標準化的錯誤代碼：
- **RC-001**: 不支援的國家代碼，只接受 TW、JP、KR、HK、ALL
- **RC-002**: 不支援的語系，只接受 zh-TW 和 en-US
- **RC-003**: 推薦配置檔案錯誤，可能是 JSON 格式問題或景點數量不符
- **RC-004**: `limit` 參數驗證失敗，僅接受 4-10 的整數

### Q: 錯誤回應格式是什麼？
A: 所有錯誤都使用統一的 `RestResponse` 格式：
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "RC-001",
    "message": "具體錯誤訊息",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

---

## 更新日誌

- **v1.1.0** (2025-10-12)
  - 新增 `limit` 查詢參數，支援 4-10 筆的動態回傳筆數
  - 新增 `ALL` 國家代碼，提供跨國隨機推薦並確保每國至少 1 筆
  - 補充 RC-004 錯誤代碼及相關驗證邏輯
  - 更新前端整合範例與錯誤處理指引
- **v1.0.0** (2025-10-11)
  - 初始版本發布
  - 支援 TW、JP、KR、HK 四個國家/地區
  - 支援多語系顯示
  - 整合 Google Places API 資料補充機制
