# 推薦景點 API 說明

## 概述

推薦景點 API 提供各國家/地區的熱門景點推薦，支援多語系顯示。系統會根據預設的景點配置檔案返回精選的旅遊景點資訊。

---

## API 端點

### 取得推薦景點

- **API**：`GET /api/recommands/{country}`
- **說明**：取得指定國家的推薦景點清單

#### 請求參數

| 參數名稱 | 類型 | 必填 | 說明 | 範例 |
|---------|------|------|------|------|
| country | String | 是 | 國家代碼（路徑參數） | TW, JP, KR, HK |

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

#### 回應格式

**成功回應**：
```json
[
  {
    "poiId": "42061ab7-174a-476f-85fd-23b6f89d360f",
    "placeId": "ChIJLZgN8z6tQjQR3kK3hKZJwJg",
    "name": "台北 101",
    "city": "台北市",
    "photoUrl": "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=...",
    "rating": 4.5,
    "lat": 25.0330,
    "lon": 121.5654
  },
  {
    "poiId": "e1985ee4-8d5a-45a5-9b34-60dffe2d8e3c",
    "placeId": "ChIJLZgN8z6tQjQR3kK3hKZJwJg",
    "name": "國立故宮博物院",
    "city": "台北市",
    "photoUrl": "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=...",
    "rating": 4.7,
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
| city | String | 所在城市名稱 |
| photoUrl | String | 景點照片 URL（第一張照片） |
| rating | Double | 景點評分（1.0-5.0） |
| lat | Double | 緯度 |
| lon | Double | 經度 |

**錯誤回應欄位**：

| 欄位名稱 | 類型 | 說明 |
|----------|------|------|
| data | Object | 成功時為資料內容，錯誤時為 null |
| meta | Object | 輔助資訊，錯誤時為 null |
| error | Object | 錯誤資訊，成功時為 null |
| error.code | String | 錯誤代碼（如 RC-001、RC-002、RC-003） |
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
    "message": "country 僅支援 TW/JP/KR/HK，傳入值: XX",
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

---

## 使用範例

### JavaScript 範例

```javascript
// 取得台灣推薦景點
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

// 取得日本推薦景點（英文）
async function getJapanRecommendations() {
  try {
    const response = await fetch('/api/recommands/JP', {
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
    
    // 顯示在 UI 上
    displayRecommendations(taiwanSpots, '台灣');
    displayRecommendations(japanSpots, '日本');
    
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
# 取得台灣推薦景點（繁體中文）
curl -X GET "http://localhost:8080/api/recommands/TW" \
  -H "Accept-Language: zh-TW"

# 取得日本推薦景點（英文）
curl -X GET "http://localhost:8080/api/recommands/JP" \
  -H "Accept-Language: en-US"

# 取得韓國推薦景點（預設語系）
curl -X GET "http://localhost:8080/api/recommands/KR"
```

---

## 技術實作細節

### 資料來源

推薦景點資料來自預設的 JSON 配置檔案：

- `backend/src/main/resources/recommand/TW.json` - 台灣景點
- `backend/src/main/resources/recommand/JP.json` - 日本景點
- `backend/src/main/resources/recommand/KR.json` - 韓國景點
- `backend/src/main/resources/recommand/HK.json` - 香港景點

### 資料處理流程

1. **配置載入**：從 JSON 檔案載入預設的景點 ID 清單
2. **語系處理**：根據 `Accept-Language` header 決定顯示語系
3. **資料庫查詢**：查詢景點的本地化資訊
4. **資料補充**：如果資料庫中缺少景點資訊，會透過 Google Places API 即時補充
5. **結果回傳**：返回標準化的景點資訊

### 快取機制

- 配置檔案會在服務啟動時載入並快取
- 景點資料會根據語系進行快取
- 支援即時資料補充和更新

### 錯誤處理

- **國家代碼驗證**：只接受 TW、JP、KR、HK
- **語系驗證**：檢查語系是否支援
- **資料完整性**：確保每個國家有 10 個推薦景點
- **容錯機制**：部分景點資料缺失時仍會返回可用的景點

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

3. **資料展示**：
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
- **RC-001**: 不支援的國家代碼，只接受 TW、JP、KR、HK
- **RC-002**: 不支援的語系，只接受 zh-TW 和 en-US
- **RC-003**: 推薦配置檔案錯誤，可能是 JSON 格式問題或景點數量不符

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

- **v1.0.0** (2024-01-15)
  - 初始版本發布
  - 支援 TW、JP、KR、HK 四個國家/地區
  - 支援多語系顯示
  - 整合 Google Places API 資料補充機制
