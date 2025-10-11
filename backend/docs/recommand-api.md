# 推薦景點 API 規格（繁體中文）

## 背景說明

* 前端透過 `GET /api/recommands?country={countryCode}` 取得 10 筆景點推薦。
* 每筆推薦會依照使用者當前語系載入 `poi_i18n` 的資料；若資料庫尚未有該語系的內容，需自動補齊。
* 本次調整針對語系缺漏時的補償流程，確保仍能輸出 10 筆完整資料。

## 流程總覽

1. 服務載入對應國家的推薦組態（固定 10 筆 `poi_id`）。
2. 依照請求語系（`Accept-Language`）轉換成系統 `lang_type`，批次查詢 `poi` 與 `poi_i18n`。
3. 對於查詢不到指定語系的 `poi_id`：
   1. 以 `poi_id` 查詢 `poi.external_id`。
   2. 依序呼叫內部 API `GET /api/search/placeDetails?placeId={externalId}`。
   3. 該 API 會：
      * 優先讀取 24 小時內快取的 `infos_raw`；
      * 若快取不存在，轉呼叫 Google Place Details 並即時 `upsert` 回資料庫（`poi` / `poi_i18n`）。
   4. 取得 `PlaceDetailResponse` 後，轉換為推薦清單需要的欄位（名稱、城市、照片、座標、評分）。
4. 彙整步驟 2 與 3 的結果，依組態順序輸出 10 筆 `LocationRecommand`。

## 回傳資料欄位

| 欄位 | 型別 | 說明 |
| --- | --- | --- |
| `poiId` | UUID | 景點內部識別碼 |
| `placeId` | String | Google Place ID（對應 `poi.external_id`） |
| `name` | String | 景點名稱，對應使用者語系 |
| `city` | String | 城市名稱 |
| `photoUrl` | String | 照片 URL，優先取第一張 |
| `rating` | Double | Google 評分 |
| `lat` | Double | 緯度 |
| `lon` | Double | 經度 |

## 錯誤與例外處理

* 若組態中 `poi_id` 在資料庫不存在或無 `external_id`，會於伺服器 log 記錄警告並略過該筆。
* `placeDetails` API 若回傳錯誤或拋出例外，伺服器會記錄錯誤並略過該筆推薦（前端可得知實際回傳筆數不足 10 筆）。
* 不支援的國碼或語系會維持原本的錯誤型別（`RecommandException.InvalidCountry`、`RecommandException.UnsupportedLang`）。

## 效能考量

* 正常情況下，推薦資料皆由資料庫一次查詢完成，不需額外 API 呼叫。
* 僅在缺少語系資料時，最多額外呼叫 10 次 `placeDetails`，且每次成功後即會更新資料庫，後續請求可命中快取。
* 補償流程採同步序列化呼叫，確保回傳順序與組態一致，避免非同步造成排序錯亂。

## 後續維護建議

* 建議於排程中定期檢查推薦清單的語系涵蓋率，預先觸發 `placeDetails` 以降低線上補償的頻率。
* 若未來需支援更多語系，可將 `lang_type` 對應設定集中管理於設定檔，避免硬編碼。
