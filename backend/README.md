# Backend Service

## 環境需求

- **OpenJDK 21.0.6 LTS**  
  [下載連結](https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-21)
- **Maven 3.9.9**  
  [下載連結](https://maven.apache.org/download.cgi)

---

## Docker 啟動方式

### Windows 10/11 用戶（Powershell）

1. 切換到後端目錄
   ```powershell
   cd backend
   ```
2. 編譯專案
   ```powershell
   .\build.bat
   ```
3. 啟動專案
   ```powershell
   .\up.bat
   ```
4. 關閉專案
   ```powershell
   .\down.bat
   ```

### Linux 用戶

1. 切換到後端目錄
   ```bash
   cd backend
   ```
2. 編譯專案
   ```bash
   sh build.sh
   ```
3. 啟動專案
   ```bash
   sh up.sh
   ```
4. 關閉專案
   ```bash
   sh down.sh
   ```

---

## 多語系訊息回傳

本專案支援多語系錯誤與提示訊息，依據 API 請求 Header 的 `Accept-Language` 自動回傳對應語言內容。

- **支援語言**：
  - zh-TW（繁體中文）
  - en（英文）
- **設定方式**：
  - 在 API 請求 Header 加入 `Accept-Language`，如：
    - `Accept-Language: zh-TW`
    - `Accept-Language: en`
- **預設語言**：
  - 若未指定或語言不支援，預設回傳英文訊息。

API 回應錯誤時，會根據語系自動顯示對應語言的友善訊息。

---

## 其他說明

- **API 文件**：請參考 `/docs/member-auth-flow.md` 取得完整 API 串接與流程說明。
- **環境變數**：請依照 `.env` 或 `application.properties` 設定資料庫、郵件等連線資訊。
- **常見問題**：如遇啟動或串接問題，請先檢查環境變數與依賴安裝。
