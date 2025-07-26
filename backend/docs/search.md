# 搜尋引擎 API 說明

> 提供國家、地區、城市搜尋功能，以及 Google Places API 整合的附近景點搜尋。

## 流程圖

```mermaid
sequenceDiagram
    participant User as 使用者
    participant FE as 前端
    participant BE as 後端
    participant DB as 資料庫
    participant Google as Google Places API

    User->>FE: 選擇搜尋類型
    FE->>BE: 呼叫對應搜尋 API
    BE->>DB: 查詢國家/地區/城市資料
    DB-->>BE: 回傳資料
    BE-->>FE: 回傳搜尋結果
    
    Note over User,Google: 附近景點搜尋流程
    User->>FE: 輸入位置搜尋附近景點
    FE->>BE: 呼叫 searchNearbyByLocationCode(鄰近點搜尋) / searchTextByLocationCode (關鍵字搜尋) API
    BE->>Google: 呼叫 Google Places API
    Google-->>BE: 回傳景點資料
    BE->>BE: 解析景點資訊 (placeId, 名稱, 照片, 城市)
    BE-->>FE: 回傳景點搜尋結果
    
    Note over User,Google: 儲存會員景點流程
    User->>FE: 點擊儲存景點
    FE->>BE: 呼叫 saveMemberPoi API
    BE->>Google: 取得景點詳細資訊
    Google-->>BE: 回傳景點詳細資料
    BE->>DB: 儲存景點資料 (Poi + PoiI18n)
    BE->>DB: 建立會員景點關聯 (MemberPoi)
    BE-->>FE: 回傳儲存結果
```

## API 端點總覽

| 方法   | 端點                                      | 說明                     |
|--------|-------------------------------------------|--------------------------|
| GET    | `/api/search/countries/{langType}`        | 取得所有國家列表         |
| POST   | `/api/search/regions`                     | 取得指定國家的地區和城市 |
| GET    | `/api/search/allLocations`                | 取得所有地點資料         |
| POST   | `/api/search/searchNearbyByLocationCode`  | 根據 Location 代碼搜尋附近景點 |
| POST   | `/api/search/searchTextByLocationCode`    | 根據 Location 代碼和文字查詢搜尋景點 |
| GET    | `/api/search/settings/{category}`         | 根據類別查詢設定         |
| GET    | `/api/search/settings/language-types`     | 查詢所有語言類型設定     |
| GET    | `/api/search/placeDetails`                | 取得地點詳細資訊 (含照片) |
| POST   | `/api/search/saveMemberPoi`               | 儲存會員景點 ⭐          |

## API 詳細說明

### 1. 取得國家列表

- **API**: `GET /api/search/countries/{langType}`
- **描述**: 取得所有國家列表
- **請求參數**:
  - **路徑參數**:
    | 參數      | 型別   | 必填 | 說明              |
    |-----------|--------|------|-------------------|
    | `langType`| String | 是   | 語言類型 (如：zh-TW, en-US) |
- **回應**: List<國家>
- **回應範例**:
  ```json
  [
    {
      "code": "TW",
      "name": "台灣"
    },
    {
      "code": "JP",
      "name": "日本"
    }
  ]
  ```

### 2. 取得地區和城市

- **API**: `POST /api/search/regions`
- **描述**: 取得指定國家的地區和城市
- **請求參數**:
  - **請求體**:
    | 參數         | 型別   | 必填 | 說明              |
    |--------------|--------|------|-------------------|
    | `countryCode`| String | 是   | 國家代碼          |
    | `langType`   | String | 是   | 語言類型          |
  - **請求體範例**:
    ```json
    {
      "countryCode": "TW",
      "langType": "zh-TW"
    }
    ```
- **回應**: List<地區>
- **回應範例**:
  ```json
  [
    {
      "regionCode": "TW-N",
      "regionName": "北部",
      "orderIndex": 1,
      "cities": [
        {
          "code": "TPE",
          "name": "台北市"
        },
        {
          "code": "TPQ",
          "name": "新北市"
        }
      ]
    }
  ]
  ```

### 3. 取得所有地點

- **API**: `GET /api/search/allLocations`
- **描述**: 取得所有地點資料
- **請求參數**: 無
- **回應**: List<地點>
- **回應範例**:
  ```json
  [
    {
      "code": "TPE",
      "name": "台北市",
      "langType": "zh-TW"
    },
    {
      "code": "TPE",
      "name": "Taipei",
      "langType": "en-US"
    }
  ]
  ```

### 4. 根據 Location 代碼搜尋附近景點 ⭐

- **API**: `POST /api/search/searchNearbyByLocationCode`
- **描述**: 使用 Location 代碼自動取得經緯度，然後搜尋附近景點
- **請求參數**:
  - **請求體**:
    | 參數            | 型別         | 必填 | 說明                           |
    |-----------------|--------------|------|--------------------------------|
    | `code`          | String       | 是   | Location 代碼                  |
    | `langType`      | String       | 是   | 語言類型                      |
    | `includedTypes` | List<String> | 否   | 景點類型篩選 (參見景點類型)   |
    | `maxResultCount`| Integer      | 否   | 最大結果數量 (5-20，預設 5)   |
    | `rankPreference`| String       | 否   | 排序方式 (DISTANCE, RATING)   |
  - **請求體範例**:
    ```json
    {
      "code": "TPE",
      "langType": "zh-TW",
      "includedTypes": ["restaurant", "hotel", "tourist_attraction"],
      "maxResultCount": 10,
      "rankPreference": "DISTANCE"
    }
    ```
- **回應**: List<LocationSearch>
- **回應範例**:
  ```json
  [
    {
      "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
      "name": "Google Sydney",
      "city": "Sydney",
      "rating": 4.5,
      "photoUrl": "https://..."
    }
  ]
  ```

### 5. 根據 Location 代碼和文字查詢搜尋景點 ⭐

- **API**: `POST /api/search/searchTextByLocationCode`
- **描述**: 結合文字搜尋和位置偏向，在指定區域內搜尋特定景點
- **請求參數**:
  - **請求體**:
    | 參數            | 型別         | 必填 | 說明                           |
    |-----------------|--------------|------|--------------------------------|
    | `textQuery`     | String       | 是   | 要搜尋的文字查詢              |
    | `code`          | String       | 是   | Location 代碼                  |
    | `langType`      | String       | 是   | 語言類型                      |
    | `maxResultCount`| Integer      | 否   | 最大結果數量 (預設 5)         |
    | `rankPreference`| String       | 否   | 排序方式 (RELEVANCE, DISTANCE)|
    | `includedTypes` | List<String> | 否   | 景點類型篩選 (參見景點類型)   |
  - **請求體範例**:
    ```json
    {
      "textQuery": "台北101",
      "code": "TPE",
      "langType": "zh-TW",
      "maxResultCount": 10,
      "rankPreference": "RELEVANCE",
      "includedTypes": ["tourist_attraction"]
    }
    ```
- **回應**: List<LocationSearch>
- **回應範例**:
  ```json
  [
    {
      "placeId": "ChIJi73bYWusQjQRG_mf-jDN2-8",
      "name": "台北101",
      "city": "台北市",
      "rating": 4.4,
      "photoUrl": "https://..."
    }
  ]
  ```

### 6. 根據類別查詢設定 ⭐

- **API**: `GET /api/search/settings/{category}`
- **描述**: 根據類別查詢設定，目前只支援 `LANG_TYPE`
- **請求參數**:
  - **路徑參數**:
    | 參數      | 型別   | 必填 | 說明              |
    |-----------|--------|------|-------------------|
    | `category`| String | 是   | 設定類別          |
- **回應**: List<Setting>
- **回應範例**:
  ```json
  [
    {
      "category": "LANG_TYPE",
      "name": "繁體中文",
      "codeName": "zh-TW",
      "codeDesc": "繁體中文(台灣)"
    },
    {
      "category": "LANG_TYPE",
      "name": "English",
      "codeName": "en-US",
      "codeDesc": "English(USA)"
    }
  ]
  ```

### 7. 查詢所有語言類型設定 ⭐

- **API**: `GET /api/search/settings/language-types`
- **描述**: 專門用於查詢語言類型設定的快捷端點
- **請求參數**: 無
- **回應**: List<Setting>
- **回應範例**:
  ```json
  [
    {
      "category": "LANG_TYPE",
      "name": "繁體中文",
      "codeName": "zh-TW",
      "codeDesc": "繁體中文(台灣)"
    },
    {
      "category": "LANG_TYPE",
      "name": "English",
      "codeName": "en-US",
      "codeDesc": "English(USA)"
    }
  ]
  ```

### 8. 取得地點詳細資訊 (含照片)

- **API**: `GET /api/search/placeDetails`
- **描述**: 根據 Google PlaceId 取得該地點的詳細資訊
- **請求參數**:
  - **查詢參數**:
    | 參數      | 型別   | 必填 | 說明                        |
    |-----------|--------|------|-----------------------------|
    | `placeId` | String | 是   | Google Place 的唯一識別碼   |
    | `langType`| String | 否   | 語言類型 (預設 zh-TW)      |
- **回應**: 地點詳細資訊
- **回應範例**:
  ```json
  {
    "id": "ChIJN1t_tDeuEmsRUsoyG83frY4",
    "name": "Google Sydney",
    "addr": "48 Pirrama Rd, Pyrmont NSW 2009, Australia",
    "rate": 4.5,
    "cnt": 1000,
    "desc": "Google's Sydney office.",
    "tel": "+61 2 9374 4000",
    "site": "https://www.google.com.au/about/careers/locations/sydney/",
    "lat": -33.866651,
    "lon": 151.195827,
    "city": "Sydney",
    "country": "Australia",
    "photos": [
      "https://places.googleapis.com/v1/xxx/media?key=xxx&maxWidthPx=400"
    ],
    "rawHours": {
      "weekdayDescriptions": [
        "Monday: 9:00 AM – 5:00 PM",
        "Tuesday: 9:00 AM – 5:00 PM"
      ]
    }
  }
  ```

### 9. 儲存會員景點 ⭐

- **API**: `POST /api/search/saveMemberPoi`
- **描述**: 儲存會員指定的景點到個人收藏，支援多語系資料儲存
- **請求參數**:
  - **標頭**:
    | 標頭        | 型別   | 必填 | 說明              |
    |-------------|--------|------|-------------------|
    | `X-member-id`| UUID  | 是   | 會員 ID           |
  - **請求體**:
    | 參數      | 型別   | 必填 | 說明                        |
    |-----------|--------|------|-----------------------------|
    | `memberId`| UUID   | 是   | 會員 ID                     |
    | `placeId` | String | 是   | Google Place 的唯一識別碼   |
    | `langType`| String | 是   | 語言類型                   |
  - **請求體範例**:
    ```json
    {
      "memberId": "550e8400-e29b-41d4-a716-446655440000",
      "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
      "langType": "zh-TW"
    }
    ```
- **回應**: SaveMemberPoiResponse
- **回應範例**:
  ```json
  {
    "code": 1,
    "desc": "OK",
    "poiId": "550e8400-e29b-41d4-a716-446655440001",
    "poiCreated": true,
    "langInserted": true,
    "alreadySaved": false
  }
  ```
- **常見錯誤**:
  | 錯誤代碼                    | 說明                 | 解決方法                  |
  |-----------------------------|----------------------|---------------------------|
  | `unsupported langType`      | 不支援的語言類型     | 檢查 `langType` 是否正確  |
  | `place not found`           | 找不到指定景點       | 確認 `placeId` 是否有效   |
  | `place missing required fields` | 景點缺少必要欄位 | 選擇其他景點              |

## 共同資料結構

### 國家

| 欄位   | 型別   | 說明      |
|--------|--------|-----------|
| `code` | String | 國家代碼  |
| `name` | String | 國家名稱  |

### 地區

| 欄位        | 型別         | 說明      |
|-------------|--------------|-----------|
| `regionCode`| String       | 地區代碼  |
| `regionName`| String       | 地區名稱  |
| `orderIndex`| Integer      | 排序索引  |
| `cities`    | List<城市>   | 城市列表  |

### 城市

| 欄位   | 型別   | 說明      |
|--------|--------|-----------|
| `code` | String | 城市代碼  |
| `name` | String | 城市名稱  |

### 地點

| 欄位      | 型別   | 說明      |
|-----------|--------|-----------|
| `code`    | String | 地點代碼  |
| `name`    | String | 地點名稱  |
| `langType`| String | 語言類型  |

### 搜尋結果 (LocationSearch)

| 欄位      | 型別   | 說明                  |
|-----------|--------|-----------------------|
| `placeId` | String | Google Places 唯一識別碼 |
| `name`    | String | 景點名稱              |
| `city`    | String | 所在城市              |
| `rating`  | Double | 評分                  |
| `photoUrl`| String | 景點照片網址          |

### 設定 (Setting)

| 欄位      | 型別   | 說明      |
|-----------|--------|-----------|
| `category`| String | 設定類別  |
| `name`    | String | 設定名稱  |
| `codeName`| String | 設定代碼  |
| `codeDesc`| String | 設定描述  |

### 儲存會員景點回應 (SaveMemberPoiResponse)

| 欄位         | 型別    | 說明                        |
|--------------|---------|-----------------------------|
| `code`       | Integer | 回應代碼 (1=成功, 0=失敗)  |
| `desc`       | String  | 回應描述                   |
| `poiId`      | UUID    | 景點 ID                    |
| `poiCreated` | Boolean | 是否新建立景點記錄         |
| `langInserted`| Boolean| 是否新建立語系記錄         |
| `alreadySaved`| Boolean| 會員是否已收藏過此景點     |

## 景點類型

以下是常用的景點類型，可用於 `includedTypes` 參數：

| 類型               | 說明      |
|--------------------|-----------|
| `restaurant`       | 餐廳      |
| `tourist_attraction`| 觀光景點 |
| `hotel`            | 飯店      |
| `shopping_mall`    | 購物中心  |
| `museum`           | 博物館    |
| `park`             | 公園      |
| `cafe`             | 咖啡廳    |
| `bar`              | 酒吧      |

## 使用範例

### 前端 JavaScript 範例

```javascript
// 1. 取得國家列表
const countries = await fetch('/api/search/countries/zh-TW')
  .then(res => res.json());

// 2. 取得地區和城市
const regions = await fetch('/api/search/regions', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    countryCode: 'TW',
    langType: 'zh-TW'
  })
}).then(res => res.json());

// 3. 根據 Location 代碼搜尋附近景點
const nearbyByCode = await fetch('/api/search/searchNearbyByLocationCode', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    code: 'TPE',
    langType: 'zh-TW',
    includedTypes: ['restaurant', 'tourist_attraction'],
    maxResultCount: 10,
    rankPreference: 'DISTANCE'
  })
}).then(res => res.json());

// 4. 根據 Location 代碼和文字查詢搜尋景點
const textSearch = await fetch('/api/search/searchTextByLocationCode', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    textQuery: '台北101',
    code: 'TPE',
    langType: 'zh-TW',
    maxResultCount: 10,
    rankPreference: 'RELEVANCE',
    includedTypes: ['tourist_attraction']
  })
}).then(res => res.json());

// 5. 查詢語言類型設定
const languageTypes = await fetch('/api/search/settings/LANG_TYPE')
  .then(res => res.json());

// 6. 查詢所有語言類型設定
const allLanguageTypes = await fetch('/api/search/settings/language-types')
  .then(res => res.json());

// 7. 儲存會員景點
const saveResult = await fetch('/api/search/saveMemberPoi', {
  method: 'POST',
  headers: { 
    'Content-Type': 'application/json',
    'X-member-id': '550e8400-e29b-41d4-a716-446655440000'
  },
  body: JSON.stringify({
    memberId: '550e8400-e29b-41d4-a716-446655440000',
    placeId: 'ChIJN1t_tDeuEmsRUsoyG83frY4',
    langType: 'zh-TW'
  })
}).then(res => res.json());

console.log('儲存結果:', saveResult);
```

## 錯誤處理

API 會回傳標準 HTTP 狀態碼：
- `200`：成功
- `400`：請求參數錯誤
- `500`：伺服器內部錯誤

**錯誤回應範例**:
```json
{
  "error": "Invalid location coordinates",
  "message": "緯度或經度參數無效"
}
```

## 注意事項

1. **設定查詢限制**: 目前設定查詢只支援 `LANG_TYPE` 類別
2. **搜尋結果限制**: `maxResultCount` 參數限制在 5-20 之間
3. **排序偏好**: `rankPreference` 只支援特定值 (如 `RELEVANCE`, `DISTANCE`)
4. **Google Places API**: 需要有效的 Google API Key
5. **效能考量**:
   - 建議設定適當的 `maxResultCount`
   - 搜尋半徑不宜過大
   - 可快取常用搜尋結果
6. **資料來源**:
   - 國家/地區/城市資料來自本地資料庫
   - 附近景點資料來自 Google Places API
7. **會員景點儲存**:
   - 需要有效的 `X-member-id` header
   - 支援重複儲存檢查