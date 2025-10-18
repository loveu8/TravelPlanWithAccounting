# AGENTS.md

## 技術與版本

```
Java: 21
Spring Boot: 3.5.6
OpenAPI: springdoc-openapi 2.8.6
JWT: jjwt 0.13.0
Database: PostgreSQL + Spring Data JPA
Apache HttpClient: 4.5.14
其他: HikariCP, Jackson Hibernate6, Gson, Spring Security, Spring Mail
```

**依賴管理原則**：新增依賴時明確指定版本號，確保與 Spring Boot BOM 相容，並同步更新 `pom.xml`。

---

## Do

### 架構與分層
- 採用 Controller-Service-Repository 分層架構
- Controller 僅處理 request/response，不包含商業邏輯
- 商業邏輯集中於 `service` 層
- 資料存取透過 `repository` 層的 Spring Data JPA
- DTO 轉換統一在 `mapper` 套件處理

### 權限驗證
- 需登入的端點加上 `@AccessTokenRequired` 注解
- 透過注入的 `AuthContext` 取得當前 `memberId`
- JWT 驗證交給 `AccessTokenAspect` + `JwtUtil` 處理
- Token 相關操作使用 `service/util/TokenUtil`

### 語系處理
- 系統自動從 `Accept-Language` header 解析語系（透過 `LocaleAspect`）
- 取得語系使用 `LocaleContextHolder.getLocale()`
- 多語訊息使用 `MessageSourceHolder.getMessage(...)`
- 預設語系為 `Locale.TAIWAN`（zh-TW），另支援 en-US

### 回應格式
- 統一使用 `RestResponse` 格式
- 透過 `RestResponseUtils.success(...)` 或 `RestResponseUtils.error(...)` 建立回應
- 交給 `ResponseBodyWrapperAdvice` 自動包裝，避免手動組裝

### 重用既有工具
優先使用 `service/util` 下的工具類別：
- `TokenUtil` - Token 產生與雜湊
- `UuidGeneratorUtils` - UUID 生成
- `RestResponseUtils` - 回應格式化
- `JsonHelper` - JSON 處理
- `EmailValidatorUtil` - Email 驗證
- `PoiTypeMapper`, `LangTypeMapper`, `LocationHelper` - 業務轉換

確認無可復用程式碼時才新增新的 Utility。

### 錯誤處理
- 例外統一使用 `ApiException` + `MessageCode`
- 新增錯誤訊息時同步更新 `src/main/resources/i18n/messages_*.properties`
- 交給 `GlobalExceptionHandler` 統一處理，不在 Controller 內捕捉

### API 文件與驗證
- 使用 `@Operation`, `@Tag`, `@Parameter` 維護 Swagger 說明
- DTO 使用 `record` 或 `@Data`，搭配 `jakarta.validation` 註解
- 確保 API 文件完整性

### 外部服務整合
- Google API 使用 `GoogleRequestFactory`
- 郵件發送使用 `MailConfig` 設定
- 快取參考 `CacheConfig` 與 `CacheConstants`
- 背景任務參考 `PoiLanguageEnrichmentPublisher`、`CacheCleanupService` 範例

### 編譯驗證
- 任何後端程式碼變更後必須執行 `mvn clean install` 或 `./mvnw clean install`
- 確保專案可成功編譯並通過測試

---

## Don't

- ❌ 不要繞過 `@AccessTokenRequired` 自行解析 `Authorization` header
- ❌ 不要手動從 header 解析語系或自建 `LocaleResolver`
- ❌ 不要回傳非 `RestResponse` 格式或直接使用 `ResponseEntity`
- ❌ 不要在 Controller 內直接操作 `ThreadLocal` 或 `HttpServletResponse`
- ❌ 不要重複實作已存在於 `service/util`、`service/dto`、`service/validator` 的功能
- ❌ 未經評估不要引入未鎖版或大型第三方依賴
- ❌ 不要在程式碼中硬編多語系字串
- ❌ 不要忽略 `ResponseBodyWrapperAdvice`、`BodyWriteHandler` 的標準回應流程
- ❌ 不要變更 Maven parent 版本或破壞既有 Spring Boot 自動配置

---

## Commands

```bash
# 完整編譯與測試（變更程式碼後必跑）
mvn clean install
# 或使用 Maven Wrapper
./mvnw clean install

# 僅執行測試
mvn test
./mvnw test
```

---

## 專案結構

```
backend/src/main/java/com/travelPlanWithAccounting/service/
├── controller/          # REST API 入口點
├── service/             # 商業邏輯層
├── repository/          # Spring Data JPA 資料存取
├── dto/                 # 請求/回應資料物件
├── mapper/              # DTO 與 Entity 轉換
├── util/                # 公用工具類別
├── validator/           # 資料驗證器
├── aspect/              # AOP（AccessTokenAspect, LocaleAspect）
├── security/            # JWT 與登入態相關工具
├── config/              # 全域設定
│   ├── advice/          # Response Wrapper, Exception Handler
│   └── I18nConfig, CacheConfig, MailConfig 等
├── constant/            # 常數定義
├── exception/           # 自訂例外類別
├── message/             # 訊息代碼與處理
└── entity/              # JPA Entity

backend/src/main/resources/
├── i18n/                # 多語訊息檔（messages_zh_TW.properties, messages_en_US.properties）
└── application-*.yml    # 環境設定檔

backend/docs/            # API 與流程文件
```

---

## 提交前檢查清單

1. ✅ 執行 `mvn clean install` 確認編譯成功
2. ✅ 新增的 API 已加上 `@Operation` 等 Swagger 註解
3. ✅ 錯誤訊息有對應的 `MessageCode` 與 i18n 檔案
4. ✅ 如有調整 API，同步更新 `docs/` 相關文件
5. ✅ 程式碼格式、命名與現有風格一致
6. ✅ 確認回應格式符合既有模式（使用 `RestResponse`）
7. ✅ 驗證新增依賴的版本相容性

---

## Safety and Permissions

### 允許操作
- 閱讀專案檔案
- 編輯程式碼
- 執行單元測試與 `mvn clean install`

### 需事前確認
- 新增或變更依賴版本
- 重大架構調整
- 刪除關鍵設定檔或修改資料表結構
- 變更安全性設定或驗證流程

---

## When Stuck

- 優先搜尋 `service/util`、`service/config`、`service/validator` 確認是否有現成實作
- 查閱 `docs/` 目錄瞭解現有 API 與流程
- 確認 `pom.xml` 中的依賴版本與配置
- 檢視既有的 Service、Controller 範例作為參考
- 必要時提出簡短問題或設計草案，確認後再實作
