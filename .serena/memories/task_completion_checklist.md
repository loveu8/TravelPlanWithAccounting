# 任務完成檢查清單

## Backend 變更檢查清單

### 1. 程式碼品質
- [ ] 遵循 Controller-Service-Repository 分層架構
- [ ] 使用既有的 util 工具類別，避免重複實作
- [ ] 錯誤處理使用 `ApiException` + `MessageCode`
- [ ] API 端點使用 `@Operation`, `@Tag`, `@Parameter` 註解
- [ ] DTO 使用 `record` 或 `@Data` + `jakarta.validation` 驗證

### 2. 權限與安全
- [ ] 需登入的端點加上 `@AccessTokenRequired`
- [ ] 透過 `AuthContext` 取得 `memberId`，不手動解析 JWT
- [ ] 敏感資料不硬編在程式碼中

### 3. 多語系
- [ ] 新增錯誤訊息時同步更新 `i18n/messages_zh_TW.properties`
- [ ] 同步更新 `i18n/messages_en_US.properties`
- [ ] 使用 `MessageSourceHolder.getMessage()` 取得訊息
- [ ] 不在程式碼中硬編多語系字串

### 4. 回應格式
- [ ] 統一使用 `RestResponseUtils.success()` 或 `RestResponseUtils.error()`
- [ ] 不手動組裝 `ResponseEntity`
- [ ] 確保回應格式符合 `RestResponse` 標準

### 5. 編譯與測試
- [ ] 執行 `mvn clean install` 確認編譯成功
- [ ] 所有單元測試通過
- [ ] 新功能有對應的測試案例（如適用）

### 6. 文件更新
- [ ] 如調整 API，同步更新 `backend/docs/` 相關文件
- [ ] Swagger 文件完整且正確

### 7. 程式碼風格
- [ ] 命名風格與現有程式碼一致
- [ ] 無不必要的註解或 debug 程式碼
- [ ] import 整理完成，無未使用的 import

---

## Frontend 變更檢查清單

### 1. 程式碼品質
- [ ] 遵循 Next.js 最佳實踐
- [ ] TypeScript 型別正確，無使用 `any`
- [ ] 組件拆分合理，可重用性高

### 2. 樣式
- [ ] 使用 Tailwind CSS utility classes
- [ ] RWD 響應式設計正確
- [ ] 無內聯樣式（除非必要）

### 3. 多語系
- [ ] 使用 i18next 處理多語系文字
- [ ] 新增文字有對應的翻譯
- [ ] 支援 zh-TW 和 en-US

### 4. 效能
- [ ] 使用 `next/image` 載入圖片
- [ ] 適當使用 Server Components
- [ ] Client Components 標記 `'use client'`
- [ ] 避免不必要的 re-render

### 5. Lint 與格式化
- [ ] 執行 `pnpm lint` 無錯誤
- [ ] Prettier 格式化完成
- [ ] ESLint 規則全數通過

### 6. 建置
- [ ] 執行 `pnpm build` 成功
- [ ] 無 TypeScript 編譯錯誤
- [ ] 無 Next.js 警告

### 7. 測試
- [ ] 手動測試功能正常
- [ ] 測試不同語系切換
- [ ] 測試 RWD 在不同螢幕尺寸

---

## 全端整合檢查清單

### 1. API 整合
- [ ] Frontend 正確呼叫 Backend API
- [ ] 錯誤處理完整（網路錯誤、業務錯誤）
- [ ] Loading 狀態顯示正確

### 2. 權限與 Token
- [ ] JWT Token 正確傳遞
- [ ] Token 過期處理正確
- [ ] 未授權狀態導向正確頁面

### 3. 多語系一致性
- [ ] Frontend 與 Backend 語系切換同步
- [ ] Accept-Language header 正確設定

### 4. Docker 部署
- [ ] Backend Docker 建置成功
- [ ] Frontend Docker 建置成功
- [ ] Docker Compose 啟動正常

---

## Git 提交前檢查

### 1. 程式碼審查
- [ ] 移除 console.log / System.out.println
- [ ] 移除註解掉的程式碼
- [ ] 移除未使用的檔案

### 2. Commit Message
- [ ] 使用清楚的 commit message
- [ ] 說明變更的目的與內容
- [ ] 遵循專案的 commit 規範

### 3. 分支管理
- [ ] 在正確的分支上開發
- [ ] 合併前確認無衝突
- [ ] Pull Request 描述完整

---

## 發布前最終檢查

### 1. 功能驗證
- [ ] 所有功能端到端測試通過
- [ ] 邊界情況處理正確
- [ ] 錯誤訊息友善且正確

### 2. 效能
- [ ] 無明顯效能問題
- [ ] API 回應時間合理
- [ ] 頁面載入速度正常

### 3. 安全性
- [ ] 無 SQL Injection 風險
- [ ] 無 XSS 風險
- [ ] 敏感資料已加密
- [ ] CORS 設定正確

### 4. 文件
- [ ] README 更新（如需要）
- [ ] API 文件更新
- [ ] 版本號更新（如需要）