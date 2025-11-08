# 程式碼結構

## 專案根目錄

```
TravelPlanWithAccounting/
├── .claude/                 # Claude Code 設定
├── .git/                    # Git 版本控制
├── .serena/                 # Serena MCP 資料
├── .vscode/                 # VS Code 設定
├── backend/                 # 後端服務
├── frontend/                # 前端應用
├── .gitignore
├── CODEOWNERS
└── README.md
```

---

## Backend 結構

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/travelPlanWithAccounting/service/
│   │   │   ├── ServiceApplication.java  # Spring Boot 入口
│   │   │   ├── aspect/                  # AOP 切面
│   │   │   │   ├── AccessTokenAspect.java
│   │   │   │   └── LocaleAspect.java
│   │   │   ├── config/                  # 全域配置
│   │   │   │   ├── advice/              # Response & Exception 處理
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResponseBodyWrapperAdvice.java
│   │   │   │   │   └── BodyWriteHandler.java
│   │   │   │   ├── I18nConfig.java
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── MailConfig.java
│   │   │   │   └── ...
│   │   │   ├── constant/                # 常數定義
│   │   │   │   └── CacheConstants.java
│   │   │   ├── controller/              # REST API Controllers
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   ├── entity/                  # JPA Entities
│   │   │   ├── exception/               # 自訂例外
│   │   │   │   └── ApiException.java
│   │   │   ├── factory/                 # Factory 類別
│   │   │   │   └── GoogleRequestFactory.java
│   │   │   ├── handler/                 # Handler 類別
│   │   │   ├── mapper/                  # Entity ↔ DTO 轉換
│   │   │   ├── message/                 # 訊息處理
│   │   │   │   ├── MessageCode.java
│   │   │   │   └── MessageSourceHolder.java
│   │   │   ├── model/                   # 資料模型
│   │   │   ├── repository/              # Spring Data JPA Repositories
│   │   │   ├── security/                # JWT & Security
│   │   │   │   ├── AuthContext.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── ...
│   │   │   ├── service/                 # 商業邏輯層
│   │   │   │   ├── CacheCleanupService.java
│   │   │   │   └── ...
│   │   │   ├── util/                    # 工具類別
│   │   │   │   ├── TokenUtil.java
│   │   │   │   ├── UuidGeneratorUtils.java
│   │   │   │   ├── RestResponseUtils.java
│   │   │   │   ├── JsonHelper.java
│   │   │   │   ├── EmailValidatorUtil.java
│   │   │   │   ├── PoiTypeMapper.java
│   │   │   │   ├── LangTypeMapper.java
│   │   │   │   ├── LocationHelper.java
│   │   │   │   └── ...
│   │   │   └── validator/               # 資料驗證器
│   │   └── resources/
│   │       ├── i18n/                    # 多語系訊息
│   │       │   ├── messages_zh_TW.properties
│   │       │   └── messages_en_US.properties
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/                            # 測試檔案
│       └── java/com/travelPlanWithAccounting/service/
├── docs/                                # API 文件
│   └── member-auth-flow.md
├── target/                              # Maven 建置輸出
├── .gitignore
├── AGENTS.md                            # Backend 開發指南
├── build.bat / build.sh                 # 建置腳本
├── up.bat / up.sh                       # 啟動腳本
├── down.bat / down.sh                   # 關閉腳本
├── docker-compose.yml
├── Dockerfile
├── mvnw / mvnw.cmd                      # Maven Wrapper
├── pom.xml                              # Maven 專案配置
└── README.md
```

---

## Frontend 結構

```
frontend/
├── src/                                 # 原始碼
│   ├── app/                            # Next.js App Router
│   │   ├── layout.tsx                  # Root Layout
│   │   ├── page.tsx                    # 首頁
│   │   └── ...                         # 其他頁面/路由
│   ├── components/                     # React 組件 (推測)
│   ├── lib/                            # 工具函式庫 (推測)
│   ├── styles/                         # 樣式檔案 (推測)
│   └── ...
├── public/                              # 靜態資源
│   ├── images/
│   └── ...
├── .husky/                              # Git hooks
│   └── pre-commit
├── .vscode/                             # VS Code 設定
├── .dockerignore
├── .gitignore
├── .lintstagedrc                        # lint-staged 設定
├── .npmrc                               # npm 設定
├── .nvmrc                               # Node.js 版本
├── .prettierignore
├── .prettierrc                          # Prettier 設定 (空配置)
├── Dockerfile
├── eslint.config.mjs                    # ESLint 設定
├── next.config.ts                       # Next.js 設定
├── package.json                         # npm 套件配置
├── pnpm-lock.yaml                       # pnpm 鎖定檔
├── postcss.config.mjs                   # PostCSS 設定
├── tsconfig.json                        # TypeScript 設定
└── README.md
```

---

## 關鍵檔案說明

### Backend

| 檔案 | 用途 |
|------|------|
| `ServiceApplication.java` | Spring Boot 應用程式入口 |
| `pom.xml` | Maven 依賴與建置配置 |
| `application*.yml` | Spring Boot 環境配置 |
| `AGENTS.md` | Backend 開發規範與指南 |
| `docker-compose.yml` | Docker 服務編排 |
| `i18n/messages_*.properties` | 多語系訊息檔 |

### Frontend

| 檔案 | 用途 |
|------|------|
| `package.json` | npm 套件與腳本配置 |
| `next.config.ts` | Next.js 框架配置 |
| `tsconfig.json` | TypeScript 編譯配置 |
| `eslint.config.mjs` | ESLint 規則配置 |
| `.prettierrc` | Prettier 格式化配置 |
| `.lintstagedrc` | Pre-commit lint 配置 |
| `app/layout.tsx` | Next.js Root Layout |

---

## 命名慣例

### Backend (Java)

- **Package**: 小寫，使用點號分隔
  - `com.travelPlanWithAccounting.service.controller`
- **Class**: PascalCase
  - `MemberController`, `AuthService`
- **Method**: camelCase
  - `getMemberInfo()`, `validateToken()`
- **Constant**: UPPER_SNAKE_CASE
  - `MAX_LOGIN_ATTEMPTS`, `DEFAULT_LOCALE`
- **Variable**: camelCase
  - `memberId`, `emailAddress`

### Frontend (TypeScript/JavaScript)

- **Component**: PascalCase
  - `UserProfile.tsx`, `LoginForm.tsx`
- **Function**: camelCase
  - `fetchUserData()`, `handleSubmit()`
- **Constant**: UPPER_SNAKE_CASE 或 camelCase
  - `API_BASE_URL` 或 `apiBaseUrl`
- **Type/Interface**: PascalCase
  - `type User = {...}`, `interface ApiResponse {...}`
- **File**: camelCase 或 kebab-case
  - `userService.ts`, `api-client.ts`

---

## 資料流

```
Frontend (Next.js)
    ↓ HTTP Request (with Accept-Language header)
Controller (Spring MVC)
    ↓ @AccessTokenRequired → JWT 驗證
    ↓ LocaleAspect → 語系設定
Service Layer (Business Logic)
    ↓ 
Repository (Spring Data JPA)
    ↓
PostgreSQL Database
    ↑
Response (RestResponse format)
    ↑ ResponseBodyWrapperAdvice → 包裝回應
    ↑ GlobalExceptionHandler → 錯誤處理
Frontend
```

---

## 開發目錄導航建議

### 新增功能時查看
1. `backend/src/main/java/.../controller/` - 查看現有 API
2. `backend/src/main/java/.../service/` - 查看商業邏輯
3. `backend/src/main/java/.../util/` - 查看可重用工具
4. `backend/AGENTS.md` - 確認開發規範

### 排查問題時查看
1. `backend/src/main/java/.../config/advice/GlobalExceptionHandler.java` - 錯誤處理
2. `backend/src/main/java/.../aspect/` - AOP 切面邏輯
3. `backend/docs/` - API 文件
4. `.gitignore` - 確認檔案是否被忽略