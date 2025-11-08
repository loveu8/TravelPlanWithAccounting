# Backend 開發指南

## 架構原則

### 分層架構
採用 **Controller-Service-Repository** 三層架構：
- **Controller**: 僅處理 request/response，不包含商業邏輯
- **Service**: 商業邏輯集中處理
- **Repository**: Spring Data JPA 資料存取

### DTO 轉換
- 統一在 `mapper` 套件處理 Entity ↔ DTO 轉換

---

## 開發規範

### 權限驗證
```java
@AccessTokenRequired  // 需登入的端點加上此注解
public ResponseEntity<?> protectedEndpoint(AuthContext authContext) {
    UUID memberId = authContext.getMemberId();  // 取得當前會員 ID
    // ...
}
```
- JWT 驗證由 `AccessTokenAspect` + `JwtUtil` 自動處理
- Token 操作使用 `service/util/TokenUtil`

### 語系處理
```java
// 系統自動從 Accept-Language header 解析（透過 LocaleAspect）
Locale locale = LocaleContextHolder.getLocale();
String message = MessageSourceHolder.getMessage("message.code", locale);
```
- 預設語系: `Locale.TAIWAN` (zh-TW)
- 支援語系: zh-TW, en-US

### 回應格式
```java
// ✅ 正確：使用 RestResponseUtils
return RestResponseUtils.success(data);
return RestResponseUtils.error(MessageCode.ERROR_CODE);

// ❌ 錯誤：不要手動組裝 ResponseEntity
```
- 統一使用 `RestResponse` 格式
- 交給 `ResponseBodyWrapperAdvice` 自動包裝

### 錯誤處理
```java
// 拋出例外
throw new ApiException(MessageCode.MEMBER_NOT_FOUND);
```
- 統一使用 `ApiException` + `MessageCode`
- 新增錯誤訊息時同步更新 `i18n/messages_*.properties`
- 由 `GlobalExceptionHandler` 統一處理，不在 Controller 內捕捉

### API 文件
```java
@Tag(name = "會員管理")
@Operation(summary = "取得會員資訊")
@Parameter(name = "memberId", description = "會員 ID")
public ResponseEntity<?> getMember(...) { }
```
- 使用 `@Operation`, `@Tag`, `@Parameter` 維護 Swagger 說明

### 資料驗證
```java
public record CreateMemberRequest(
    @NotBlank @Email String email,
    @Size(min = 8) String password
) {}
```
- DTO 使用 `record` 或 `@Data`
- 搭配 `jakarta.validation` 註解

---

## 重用工具類別

優先使用 `service/util` 下的工具：

| 工具類別 | 用途 |
|---------|------|
| `TokenUtil` | Token 產生與雜湊 |
| `UuidGeneratorUtils` | UUID 生成 |
| `RestResponseUtils` | 回應格式化 |
| `JsonHelper` | JSON 處理 |
| `EmailValidatorUtil` | Email 驗證 |
| `PoiTypeMapper` | POI 類型轉換 |
| `LangTypeMapper` | 語言類型轉換 |
| `LocationHelper` | 地理位置處理 |

**原則**: 確認無可復用程式碼時才新增新的 Utility

---

## 外部服務整合

- **Google API**: 使用 `GoogleRequestFactory`
- **郵件發送**: 使用 `MailConfig` 設定
- **快取**: 參考 `CacheConfig` 與 `CacheConstants`
- **背景任務**: 參考 `PoiLanguageEnrichmentPublisher`、`CacheCleanupService`

---

## Don'ts (禁止事項)

- ❌ 不要繞過 `@AccessTokenRequired` 自行解析 `Authorization` header
- ❌ 不要手動從 header 解析語系或自建 `LocaleResolver`
- ❌ 不要回傳非 `RestResponse` 格式
- ❌ 不要在 Controller 內直接操作 `ThreadLocal` 或 `HttpServletResponse`
- ❌ 不要重複實作已存在的功能
- ❌ 不要在程式碼中硬編多語系字串
- ❌ 不要忽略標準回應流程
- ❌ 未經評估不要引入未鎖版或大型第三方依賴
- ❌ 不要變更 Maven parent 版本或破壞 Spring Boot 自動配置

---

## 依賴管理原則

- 新增依賴時**明確指定版本號**
- 確保與 Spring Boot BOM 相容
- 同步更新 `pom.xml`

---

## 程式碼結構

```
backend/src/main/java/com/travelPlanWithAccounting/service/
├── controller/          # REST API 入口點
├── service/             # 商業邏輯層
├── repository/          # Spring Data JPA 資料存取
├── dto/                 # 請求/回應資料物件
├── mapper/              # DTO 與 Entity 轉換
├── util/                # 公用工具類別
├── validator/           # 資料驗證器
├── aspect/              # AOP (AccessTokenAspect, LocaleAspect)
├── security/            # JWT 與登入態相關工具
├── config/              # 全域設定
│   ├── advice/          # Response Wrapper, Exception Handler
│   └── I18nConfig, CacheConfig, MailConfig 等
├── constant/            # 常數定義
├── exception/           # 自訂例外類別
├── message/             # 訊息代碼與處理
├── entity/              # JPA Entity
├── factory/             # Factory 類別
├── handler/             # Handler 類別
└── model/               # 資料模型

backend/src/main/resources/
├── i18n/                # 多語訊息檔
│   ├── messages_zh_TW.properties
│   └── messages_en_US.properties
└── application-*.yml    # 環境設定檔

backend/docs/            # API 與流程文件
```