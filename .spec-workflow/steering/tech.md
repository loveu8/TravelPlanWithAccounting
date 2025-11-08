# Tech Steering Document

## Tech Stack Overview

### Architecture Pattern
**前後端分離架構 (Frontend-Backend Separation)**
- Backend: RESTful API 服務
- Frontend: Single Page Application (SPA)
- Communication: HTTP/HTTPS + JSON
- Authentication: JWT (JSON Web Tokens)

---

## Backend Stack

### Core Framework
```yaml
Language: Java 21 (OpenJDK 21.0.6 LTS)
Framework: Spring Boot 3.5.6
Build Tool: Maven 3.9.9
```

### Spring Boot Modules

#### Web & API
- **Spring Boot Starter Web** - RESTful API 開發
- **Spring Boot Starter AOP** - 面向切面編程（權限、語系）
- **Spring Boot Starter Validation** - 請求參數驗證
- **SpringDoc OpenAPI 2.8.6** - API 文檔（Swagger）

#### Data & Persistence
- **Spring Data JPA** - ORM 框架
- **PostgreSQL** - 關聯式資料庫
- **HikariCP 7.0.2** - 高效能連線池

#### Security & Authentication
- **Spring Security** - 安全框架
- **JWT (jjwt 0.13.0)** - Token 驗證
  - jjwt-api
  - jjwt-impl
  - jjwt-jackson

#### Communication & Integration
- **Spring Mail** - 郵件服務
- **Apache HttpClient 4.5.14** - HTTP 客戶端（外部 API 呼叫）

#### Utilities
- **Lombok** - 減少樣板程式碼
- **Gson** - JSON 處理
- **Jackson Datatype Hibernate6** - Hibernate 序列化
- **UUID Creator 6.1.1** - UUID 生成

#### Testing
- **Spring Boot Starter Test** - 單元測試
- **Spring Security Test** - 安全測試

### Backend Architecture

```
┌─────────────────────────────────────────┐
│         Controller Layer                │  ← REST API 入口點
│  - Request/Response 處理                │
│  - @AccessTokenRequired 權限驗證        │
│  - 不包含商業邏輯                       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Service Layer                  │  ← 商業邏輯
│  - 核心業務規則                         │
│  - 資料轉換與驗證                       │
│  - 外部服務整合                         │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        Repository Layer                 │  ← 資料存取
│  - Spring Data JPA                      │
│  - 資料庫操作                           │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          PostgreSQL                     │  ← 資料庫
└─────────────────────────────────────────┘
```

### Cross-Cutting Concerns (AOP)

```java
@Aspect
class AccessTokenAspect {
  // JWT 驗證切面
  // @AccessTokenRequired 注解觸發
}

@Aspect
class LocaleAspect {
  // 語系處理切面
  // 從 Accept-Language header 解析
}
```

---

## Frontend Stack

### Core Framework
```yaml
Framework: Next.js 15.3.0 (App Router)
Runtime: React 19.0.0
Language: TypeScript 5
Build Tool: Turbopack (內建於 Next.js)
Package Manager: pnpm
```

### UI & Styling

#### Component Library
- **Radix UI** - 無障礙優先的 UI 組件
  - @radix-ui/react-icons - 圖示庫
  - @radix-ui/react-tabs - 分頁組件
  - @radix-ui/themes - 主題系統
  - radix-ui - 核心庫

#### Styling
- **Tailwind CSS 4** - Utility-first CSS 框架
- **@tailwindcss/postcss** - PostCSS 整合
- **clsx** - 條件式 className 組合
- **tailwind-merge** - Tailwind classes 合併

#### Date & Time
- **date-fns 4.1.0** - 日期處理與格式化
- **react-day-picker 9.6.7** - 日期選擇器

### Internationalization (i18n)

```yaml
Core: i18next 25.1.2
React Integration: react-i18next 15.5.1
Browser Detection: i18next-browser-languagedetector 8.1.0
Dynamic Loading: i18next-resources-to-backend 1.2.1
Language Parsing: accept-language 3.0.20
```

**支援語系**: zh-TW (繁體中文), en-US (英文)

### Development Tools

#### Code Quality
- **ESLint 9** - 程式碼檢查
  - eslint-config-next - Next.js 規則
  - eslint-config-prettier - Prettier 整合
  - eslint-plugin-prettier - Prettier 作為 ESLint 規則
  - eslint-plugin-sonarjs - 靜態程式碼分析

- **Prettier 3.5.3** - 程式碼格式化

#### Git Hooks
- **Husky 9.1.7** - Git hooks 管理
- **lint-staged 15.5.1** - Pre-commit linting

#### Asset Processing
- **@svgr/webpack 8.1.0** - SVG 轉 React 組件

### Frontend Architecture

```
┌─────────────────────────────────────────┐
│         App Router (Next.js)            │  ← 路由與頁面
│  - Server Components (預設)            │
│  - Client Components ('use client')    │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        Components Layer                 │  ← UI 組件
│  - Radix UI 組件                        │
│  - 自訂 React 組件                      │
│  - Tailwind CSS 樣式                    │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         API Client Layer                │  ← API 整合
│  - Fetch API                            │
│  - Error Handling                       │
│  - Token Management                     │
└────────────────┬────────────────────────┘
                 │
        HTTP Request (JSON)
                 │
┌────────────────▼────────────────────────┐
│         Backend REST API                │
└─────────────────────────────────────────┘
```

---

## Database

### Primary Database
```yaml
Type: PostgreSQL
ORM: Spring Data JPA (Hibernate)
Connection Pool: HikariCP 7.0.2
```

### Schema Design Principles
- 正規化設計（3NF）
- UUID 作為主鍵（使用 UUID Creator）
- 軟刪除（soft delete）支援
- 時間戳記（created_at, updated_at）
- 樂觀鎖定（version field for JPA）

---

## Infrastructure & Deployment

### Containerization
```yaml
Container: Docker
Orchestration: Docker Compose
```

### Docker Setup
- **Backend**: 獨立 Docker image (JDK 21 + Spring Boot)
- **Frontend**: 獨立 Docker image (Node.js + Next.js)
- **Database**: PostgreSQL container
- **Networking**: Docker internal network

### Environment Management
```
Backend:
├── application.yml (預設)
├── application-dev.yml (開發)
└── application-prod.yml (生產)

Frontend:
├── .env.local (本地開發)
├── .env.development (開發環境)
└── .env.production (生產環境)
```

---

## API Design

### RESTful Conventions

#### HTTP Methods
- `GET` - 查詢資源
- `POST` - 建立資源
- `PUT` - 完整更新資源
- `PATCH` - 部分更新資源
- `DELETE` - 刪除資源

#### Response Format
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2025-11-08T12:34:56Z"
}
```

#### Error Response
```json
{
  "success": false,
  "error": {
    "code": "MEMBER_NOT_FOUND",
    "message": "會員不存在",
    "details": { ... }
  },
  "timestamp": "2025-11-08T12:34:56Z"
}
```

### Authentication Flow

```
1. User Login
   POST /api/auth/login
   ↓
2. Server validates credentials
   ↓
3. Server generates JWT (AccessToken + RefreshToken)
   ↓
4. Client stores tokens
   ↓
5. Subsequent requests include:
   Header: Authorization: Bearer {accessToken}
   ↓
6. Server validates JWT via @AccessTokenRequired
   ↓
7. Request processed if valid
```

### API Versioning
- URL 版本控制: `/api/v1/...`
- 向後相容性原則
- 棄用 API 保留至少一個版本週期

---

## Security

### Authentication & Authorization
- **JWT-based** stateless authentication
- **Access Token**: 短期（15 分鐘）
- **Refresh Token**: 長期（7 天）
- **Role-based Access Control** (RBAC)

### Data Protection
- **密碼**: BCrypt 加密（Spring Security）
- **敏感資料**: AES-256 加密
- **HTTPS Only** in production
- **CORS**: 嚴格的跨域政策

### API Security
- **Rate Limiting**: 防止 API 濫用
- **Input Validation**: Jakarta Validation
- **SQL Injection**: Parameterized queries (JPA)
- **XSS Protection**: Content Security Policy (CSP)

---

## Performance Optimization

### Backend
- **Connection Pooling**: HikariCP
- **Query Optimization**: JPA 查詢最佳化、索引
- **Caching**: Spring Cache (參考 CacheConfig)
- **Async Processing**: `@Async` for background tasks

### Frontend
- **Code Splitting**: Next.js 自動分割
- **Image Optimization**: `next/image`
- **Static Generation**: SSG for static pages
- **Server Components**: 減少客戶端 JavaScript

### Database
- **Indexing**: 關鍵欄位建立索引
- **Query Optimization**: N+1 問題避免
- **Connection Pooling**: HikariCP 配置

---

## Testing Strategy

### Backend Testing
```yaml
Unit Tests: JUnit 5 + Mockito
Integration Tests: Spring Boot Test + Testcontainers
API Tests: MockMvc / RestAssured
Coverage Goal: > 80%
```

### Frontend Testing
```yaml
Unit Tests: Jest + React Testing Library (未來)
E2E Tests: Playwright / Cypress (未來)
Coverage Goal: > 70%
```

### Test Pyramid
```
        ┌─────────┐
        │  E2E    │  ← 少量（關鍵流程）
        ├─────────┤
        │ Integration │  ← 中等（API、資料庫）
        ├─────────┤
        │   Unit    │  ← 大量（商業邏輯）
        └─────────┘
```

---

## Monitoring & Logging

### Logging
- **Framework**: Logback (Spring Boot 預設)
- **Levels**: ERROR, WARN, INFO, DEBUG, TRACE
- **Format**: JSON for production (易於解析)
- **Sensitive Data**: 避免記錄密碼、Token

### Application Monitoring (未來)
- **Metrics**: Spring Boot Actuator
- **APM**: (未定，可考慮 Prometheus + Grafana)
- **Error Tracking**: (未定，可考慮 Sentry)

---

## Development Workflow

### Version Control
```yaml
VCS: Git
Branching Strategy: Git Flow
  - main: 生產環境
  - develop: 開發環境
  - feature/*: 功能分支
  - hotfix/*: 緊急修復
```

### Code Review
- Pull Request 必須經過審核
- 至少 1 位 reviewer 批准
- CI/CD 檢查通過

### CI/CD Pipeline (未來)
```
1. Push to branch
   ↓
2. Run linters (ESLint, Checkstyle)
   ↓
3. Run tests (Unit + Integration)
   ↓
4. Build (Maven, Next.js)
   ↓
5. Deploy to environment
```

---

## Third-Party Integrations

### Current
- **Google Places API** - 景點搜尋（via GoogleRequestFactory）
- **SMTP Server** - 郵件發送（via MailConfig）

### Planned
- **Google Maps API** - 地圖與路線
- **Currency Exchange API** - 匯率轉換
- **Cloud Storage** - 圖片儲存（AWS S3 / GCP Cloud Storage）

---

## Technology Decisions

### Why Java 21 + Spring Boot?
✅ **成熟穩定**: 企業級框架，生態系統完善
✅ **效能優良**: 現代 JVM 效能優異
✅ **型別安全**: 強型別語言，減少執行期錯誤
✅ **團隊熟悉**: 開發團隊已有經驗

### Why Next.js 15 + React 19?
✅ **現代化**: 最新技術棧，App Router 架構
✅ **SEO 友善**: Server Components 支援
✅ **DX 優異**: 開發者體驗極佳
✅ **生態系統**: React 生態系統龐大

### Why PostgreSQL?
✅ **開源免費**: 無授權成本
✅ **功能完整**: ACID、JSON、全文搜尋
✅ **擴展性**: 支援水平擴展（未來）
✅ **社群活躍**: 豐富的資源與支援

### Why pnpm?
✅ **速度快**: 比 npm/yarn 快 2-3 倍
✅ **節省空間**: 共享依賴，減少磁碟使用
✅ **嚴格**: 避免幽靈依賴問題

---

## Architectural Patterns

### Backend Patterns
- **Layered Architecture** (Controller-Service-Repository)
- **Dependency Injection** (Spring IoC)
- **Aspect-Oriented Programming** (AOP for cross-cutting concerns)
- **Data Transfer Object** (DTO for API contracts)
- **Repository Pattern** (Spring Data JPA)

### Frontend Patterns
- **Component-Based Architecture** (React)
- **Server Components** (Next.js default)
- **Client Components** (for interactivity)
- **Container/Presenter Pattern** (智能/展示組件分離)
- **Custom Hooks** (邏輯重用)

---

## Coding Standards

### Backend (Java)
- **Naming**: camelCase (variables, methods), PascalCase (classes)
- **Formatting**: Google Java Style Guide
- **Comments**: JavaDoc for public APIs
- **Max Line Length**: 120 characters
- **Max Method Length**: 50 lines (建議)

### Frontend (TypeScript)
- **Naming**: camelCase (variables, functions), PascalCase (components, types)
- **Formatting**: Prettier default config
- **Comments**: TSDoc for complex functions
- **Max Line Length**: 100 characters
- **Functional Components**: 優先使用 function component

### SQL
- **Keywords**: UPPERCASE
- **Tables**: snake_case
- **Columns**: snake_case
- **Indexes**: `idx_{table}_{column}`

---

## Dependency Management

### Backend (Maven)
- **版本鎖定**: 明確指定版本號
- **BOM**: Spring Boot BOM 管理版本
- **更新策略**: 定期審查、測試後更新

### Frontend (pnpm)
- **版本鎖定**: `pnpm-lock.yaml`
- **更新策略**: 使用 `pnpm update` 謹慎更新
- **安全性**: 定期運行 `pnpm audit`

---

## Changelog

- **2025-11-08**: 初版建立（Steering Document Creation）
