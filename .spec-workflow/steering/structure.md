# Structure Steering Document

## Project Root Structure

```
TravelPlanWithAccounting/
├── .claude/                 # Claude Code 設定與 prompts
├── .git/                    # Git 版本控制
├── .gitignore               # Git 忽略清單
├── .serena/                 # Serena MCP 資料（memories, indexes）
├── .spec-workflow/          # Spec-Workflow MCP 資料
│   ├── steering/            # 專案導向文件
│   └── specs/               # 功能規格文件
├── .vscode/                 # VS Code 工作區設定
├── backend/                 # 後端服務（Java + Spring Boot）
├── frontend/                # 前端應用（Next.js + React）
├── CODEOWNERS               # GitHub code owners
└── README.md                # 專案說明文件
```

### Directory Ownership

| Directory | Owner | Purpose |
|-----------|-------|---------|
| `/backend` | Backend Team | Java Spring Boot API |
| `/frontend` | Frontend Team | Next.js React App |
| `/.spec-workflow` | Product Team | 規格管理與追蹤 |
| `/.claude` | All Teams | AI 協作設定 |

---

## Backend Structure

### Overview
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/travelPlanWithAccounting/service/
│   │   │   ├── ServiceApplication.java      # 📍 應用程式入口
│   │   │   ├── aspect/                      # 🎯 AOP 切面
│   │   │   ├── config/                      # ⚙️ 全域配置
│   │   │   ├── constant/                    # 📌 常數定義
│   │   │   ├── controller/                  # 🌐 REST API Controllers
│   │   │   ├── dto/                         # 📦 Data Transfer Objects
│   │   │   ├── entity/                      # 🗄️ JPA Entities
│   │   │   ├── exception/                   # ⚠️ 自訂例外
│   │   │   ├── factory/                     # 🏭 Factory 類別
│   │   │   ├── handler/                     # 🎛️ Handler 類別
│   │   │   ├── mapper/                      # 🔄 Entity ↔ DTO 轉換
│   │   │   ├── message/                     # 💬 訊息處理
│   │   │   ├── model/                       # 📐 資料模型
│   │   │   ├── repository/                  # 💾 Spring Data JPA
│   │   │   ├── security/                    # 🔐 JWT & Security
│   │   │   ├── service/                     # 🧠 商業邏輯層
│   │   │   ├── util/                        # 🛠️ 工具類別
│   │   │   └── validator/                   # ✅ 資料驗證器
│   │   └── resources/
│   │       ├── i18n/                        # 🌍 多語系訊息
│   │       │   ├── messages_zh_TW.properties
│   │       │   └── messages_en_US.properties
│   │       ├── application.yml              # 基礎設定
│   │       ├── application-dev.yml          # 開發環境設定
│   │       └── application-prod.yml         # 生產環境設定
│   └── test/                                # 🧪 測試檔案
│       └── java/com/travelPlanWithAccounting/service/
├── docs/                                    # 📚 API 文件
│   ├── member-auth-flow.md
│   └── ...
├── target/                                  # 🎯 Maven 建置輸出（.gitignore）
├── .gitignore
├── AGENTS.md                                # 🤖 Backend 開發指南
├── build.bat / build.sh                     # 🔨 建置腳本
├── up.bat / up.sh                           # ▶️ 啟動腳本
├── down.bat / down.sh                       # ⏹️ 關閉腳本
├── docker-compose.yml                       # 🐳 Docker 編排
├── Dockerfile                               # 🐳 Docker 映像檔
├── mvnw / mvnw.cmd                          # Maven Wrapper
├── pom.xml                                  # Maven 專案配置
└── README.md
```

### Package Organization

#### Core Packages

##### 1. `controller/` - REST API Controllers
**Purpose**: 處理 HTTP 請求與回應

**規範**:
- 類別名稱: `{Resource}Controller`（例: `MemberController`）
- 只處理 request/response，不包含商業邏輯
- 使用 `@RestController` + `@RequestMapping`
- 回應統一使用 `RestResponse` 格式
- 需登入端點加上 `@AccessTokenRequired`

**範例**:
```java
@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "會員管理")
public class MemberController {

    @GetMapping("/{memberId}")
    @Operation(summary = "取得會員資訊")
    @AccessTokenRequired
    public ResponseEntity<RestResponse<?>> getMember(
        @PathVariable UUID memberId,
        AuthContext authContext
    ) {
        // 呼叫 service 層處理
        return RestResponseUtils.success(memberService.getMember(memberId));
    }
}
```

##### 2. `service/` - 商業邏輯層
**Purpose**: 實作核心業務規則

**規範**:
- 類別名稱: `{Resource}Service`（例: `MemberService`）
- 使用 `@Service` 註解
- 商業邏輯集中處理
- 拋出 `ApiException` 處理錯誤

**範例**:
```java
@Service
public class MemberService {

    public MemberDto getMember(UUID memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ApiException(MessageCode.MEMBER_NOT_FOUND));
        return memberMapper.toDto(member);
    }
}
```

##### 3. `repository/` - 資料存取層
**Purpose**: 資料庫操作

**規範**:
- 介面名稱: `{Entity}Repository`
- 繼承 `JpaRepository<Entity, ID>`
- 自訂查詢使用 `@Query` 或 method naming

**範例**:
```java
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.createdAt > :date")
    List<Member> findRecentMembers(@Param("date") LocalDateTime date);
}
```

##### 4. `dto/` - Data Transfer Objects
**Purpose**: API 請求/回應資料結構

**規範**:
- 使用 `record` 或 `@Data` class
- 命名: `{Purpose}{Type}` (例: `CreateMemberRequest`, `MemberResponse`)
- 加上 `jakarta.validation` 驗證註解

**範例**:
```java
public record CreateMemberRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String name
) {}

@Data
public class MemberResponse {
    private UUID id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
}
```

##### 5. `entity/` - JPA Entities
**Purpose**: 資料庫表格映射

**規範**:
- 使用 `@Entity` 註解
- 主鍵使用 UUID（透過 `UuidGeneratorUtils`）
- 包含 `createdAt`, `updatedAt` 時間戳記
- 使用 Lombok `@Data`, `@Builder`

**範例**:
```java
@Entity
@Table(name = "members")
@Data
@Builder
public class Member {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

##### 6. `mapper/` - Entity ↔ DTO 轉換
**Purpose**: 在 Entity 和 DTO 之間轉換

**規範**:
- 類別名稱: `{Entity}Mapper`
- 使用 `@Component` 註解
- 方法: `toDto()`, `toEntity()`

**範例**:
```java
@Component
public class MemberMapper {
    public MemberResponse toDto(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setEmail(member.getEmail());
        response.setName(member.getName());
        response.setCreatedAt(member.getCreatedAt());
        return response;
    }
}
```

##### 7. `aspect/` - AOP 切面
**Purpose**: 橫切關注點（權限、語系、日誌）

**現有切面**:
- `AccessTokenAspect` - JWT 驗證
- `LocaleAspect` - 語系處理

##### 8. `config/` - 全域配置
**Purpose**: Spring Boot 配置類別

**子套件**:
- `config/advice/` - Response wrapper, Exception handler
- 其他: `I18nConfig`, `CacheConfig`, `MailConfig`, `SecurityConfig`

##### 9. `util/` - 工具類別
**Purpose**: 可重用的工具方法

**現有工具**:
- `TokenUtil` - Token 產生與雜湊
- `UuidGeneratorUtils` - UUID 生成
- `RestResponseUtils` - 回應格式化
- `JsonHelper` - JSON 處理
- `EmailValidatorUtil` - Email 驗證
- `PoiTypeMapper`, `LangTypeMapper` - 業務轉換
- `LocationHelper` - 地理位置處理

**規範**:
- 類別名稱: `{Purpose}Util` 或 `{Purpose}Helper`
- 方法使用 `static`
- 避免狀態（stateless）

##### 10. `exception/` - 自訂例外
**Purpose**: 業務例外定義

**核心例外**:
- `ApiException` - API 業務例外

**使用方式**:
```java
throw new ApiException(MessageCode.MEMBER_NOT_FOUND);
```

##### 11. `message/` - 訊息處理
**Purpose**: 錯誤訊息與多語系

**核心類別**:
- `MessageCode` - 訊息代碼枚舉
- `MessageSourceHolder` - 訊息取得工具

---

## Frontend Structure

### Overview
```
frontend/
├── src/                                     # 📁 原始碼
│   ├── app/                                # 🎯 Next.js App Router
│   │   ├── [locale]/                      # 🌍 語系路由
│   │   ├── layout.tsx                     # Root Layout
│   │   ├── page.tsx                       # 首頁
│   │   └── ...                            # 其他頁面/路由
│   ├── components/                         # 🧩 React 組件
│   │   ├── ui/                            # 基礎 UI 組件
│   │   ├── features/                      # 功能組件
│   │   └── layouts/                       # 佈局組件
│   ├── lib/                                # 📚 工具函式庫
│   │   ├── api/                           # API 客戶端
│   │   ├── utils/                         # 通用工具
│   │   └── hooks/                         # Custom React Hooks
│   ├── types/                              # 📐 TypeScript 型別定義
│   ├── styles/                             # 🎨 全域樣式
│   └── i18n/                               # 🌍 國際化設定
├── public/                                  # 🌐 靜態資源
│   ├── images/
│   ├── icons/
│   └── ...
├── .husky/                                  # 🎣 Git hooks
│   └── pre-commit
├── .vscode/                                 # VS Code 設定
├── .dockerignore
├── .gitignore
├── .lintstagedrc                            # lint-staged 設定
├── .npmrc                                   # npm 設定
├── .nvmrc                                   # Node.js 版本
├── .prettierignore
├── .prettierrc                              # Prettier 設定
├── Dockerfile
├── eslint.config.mjs                        # ESLint 設定
├── next.config.ts                           # Next.js 設定
├── package.json                             # npm 套件配置
├── pnpm-lock.yaml                           # pnpm 鎖定檔
├── postcss.config.mjs                       # PostCSS 設定
├── tsconfig.json                            # TypeScript 設定
└── README.md
```

### Directory Organization

#### 1. `app/` - Next.js App Router
**Purpose**: 檔案系統路由

**規範**:
- 使用 Server Components（預設）
- Client Components 標記 `'use client'`
- 檔案命名: `page.tsx`, `layout.tsx`, `loading.tsx`, `error.tsx`

**結構範例**:
```
app/
├── layout.tsx                    # Root layout
├── page.tsx                      # 首頁 (/)
├── login/
│   └── page.tsx                 # 登入頁 (/login)
├── dashboard/
│   ├── layout.tsx               # Dashboard layout
│   ├── page.tsx                 # Dashboard 首頁 (/dashboard)
│   └── trips/
│       ├── page.tsx             # 行程列表 (/dashboard/trips)
│       └── [tripId]/
│           └── page.tsx         # 行程詳情 (/dashboard/trips/:tripId)
└── api/
    └── auth/
        └── route.ts             # API routes
```

#### 2. `components/` - React 組件
**Purpose**: 可重用的 UI 組件

**組織方式**:
```
components/
├── ui/                          # 基礎 UI 組件（按鈕、輸入框等）
│   ├── Button.tsx
│   ├── Input.tsx
│   ├── Card.tsx
│   └── ...
├── features/                    # 功能組件（特定業務邏輯）
│   ├── TripList/
│   │   ├── TripList.tsx
│   │   ├── TripCard.tsx
│   │   └── index.ts
│   └── ExpenseTracker/
│       ├── ExpenseForm.tsx
│       ├── ExpenseList.tsx
│       └── index.ts
└── layouts/                     # 佈局組件
    ├── Header.tsx
    ├── Footer.tsx
    └── Sidebar.tsx
```

**命名規範**:
- 檔案名稱: PascalCase（`TripCard.tsx`）
- 組件名稱: PascalCase（`function TripCard() {}`）
- 一個檔案一個主要組件

#### 3. `lib/` - 工具函式庫
**Purpose**: 業務邏輯與工具函式

**組織方式**:
```
lib/
├── api/                         # API 客戶端
│   ├── client.ts               # Fetch wrapper
│   ├── auth.ts                 # 認證 API
│   ├── trips.ts                # 行程 API
│   └── expenses.ts             # 記帳 API
├── utils/                       # 通用工具
│   ├── date.ts                 # 日期處理
│   ├── currency.ts             # 貨幣格式化
│   └── validation.ts           # 驗證邏輯
└── hooks/                       # Custom React Hooks
    ├── useAuth.ts
    ├── useTrips.ts
    └── useLocalStorage.ts
```

#### 4. `types/` - TypeScript 型別定義
**Purpose**: 共用的型別定義

**組織方式**:
```
types/
├── api.ts                       # API 回應型別
├── models.ts                    # 資料模型
├── components.ts                # 組件 Props 型別
└── index.ts                     # 匯出所有型別
```

**範例**:
```typescript
// types/models.ts
export interface Trip {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
  budget: number;
}

export interface Expense {
  id: string;
  tripId: string;
  amount: number;
  category: string;
  description: string;
  date: string;
}
```

---

## Naming Conventions

### Backend (Java)

#### Package Names
- 全部小寫
- 使用點號分隔
- 範例: `com.travelPlanWithAccounting.service.controller`

#### Class Names
- **PascalCase**
- Controller: `{Resource}Controller` (例: `MemberController`)
- Service: `{Resource}Service` (例: `TripService`)
- Repository: `{Entity}Repository` (例: `MemberRepository`)
- Entity: `{TableName}` (例: `Member`, `Trip`)
- DTO: `{Purpose}{Type}` (例: `CreateMemberRequest`, `MemberResponse`)
- Exception: `{Purpose}Exception` (例: `ApiException`)
- Util: `{Purpose}Util` 或 `{Purpose}Helper`

#### Method Names
- **camelCase**
- 動詞開頭: `getMember()`, `createTrip()`, `validateToken()`
- 布林方法: `isValid()`, `hasPermission()`

#### Variable Names
- **camelCase**
- 有意義的名稱: `memberId`, `emailAddress`, `totalExpense`

#### Constant Names
- **UPPER_SNAKE_CASE**
- 範例: `MAX_LOGIN_ATTEMPTS`, `DEFAULT_LOCALE`, `TOKEN_EXPIRY_HOURS`

#### Database Schema
- **Table Names**: snake_case, 複數 (例: `members`, `trips`, `expenses`)
- **Column Names**: snake_case (例: `member_id`, `created_at`, `email_address`)
- **Indexes**: `idx_{table}_{column}` (例: `idx_members_email`)
- **Foreign Keys**: `fk_{table}_{referenced_table}` (例: `fk_expenses_trips`)

### Frontend (TypeScript/JavaScript)

#### File Names
- **camelCase** 或 **kebab-case** (統一使用一種)
- Component: PascalCase（`TripCard.tsx`）
- Utility: camelCase（`formatCurrency.ts`）
- API: camelCase（`authApi.ts`）

#### Component Names
- **PascalCase**
- 範例: `TripList`, `ExpenseForm`, `UserProfile`

#### Function Names
- **camelCase**
- 範例: `fetchUserData()`, `handleSubmit()`, `formatDate()`

#### Variable Names
- **camelCase**
- 範例: `userId`, `totalAmount`, `isLoading`

#### Constant Names
- **UPPER_SNAKE_CASE** 或 **camelCase**（根據用途）
- 全域常數: `API_BASE_URL`, `MAX_FILE_SIZE`
- 本地常數: `defaultOptions`, `initialState`

#### Type/Interface Names
- **PascalCase**
- 範例: `User`, `Trip`, `ApiResponse`

#### CSS Classes (Tailwind)
- 使用 Tailwind utility classes
- 自訂 class 使用 kebab-case（極少使用）

---

## File Organization Patterns

### Backend

#### Controller Pattern
```
controller/
├── MemberController.java         # 會員相關 API
├── TripController.java           # 行程相關 API
├── ExpenseController.java        # 記帳相關 API
└── AuthController.java           # 認證相關 API
```

#### Service Pattern
```
service/
├── member/
│   ├── MemberService.java
│   └── MemberValidationService.java
├── trip/
│   ├── TripService.java
│   └── TripSearchService.java
└── expense/
    ├── ExpenseService.java
    └── ExpenseCalculationService.java
```

#### Repository Pattern
```
repository/
├── MemberRepository.java
├── TripRepository.java
└── ExpenseRepository.java
```

### Frontend

#### Feature-based Organization
```
components/features/
├── auth/
│   ├── LoginForm.tsx
│   ├── RegisterForm.tsx
│   └── index.ts
├── trips/
│   ├── TripList.tsx
│   ├── TripCard.tsx
│   ├── TripForm.tsx
│   └── index.ts
└── expenses/
    ├── ExpenseList.tsx
    ├── ExpenseForm.tsx
    └── index.ts
```

#### API Client Pattern
```
lib/api/
├── client.ts                     # Base fetch wrapper
├── auth.ts                       # Authentication APIs
├── trips.ts                      # Trip APIs
└── expenses.ts                   # Expense APIs
```

---

## Code Organization Rules

### Backend Rules

1. **Single Responsibility**
   - 每個類別只負責一個職責
   - Controller 不包含商業邏輯
   - Service 不直接操作資料庫

2. **Dependency Direction**
   - Controller → Service → Repository
   - 不要反向依賴

3. **Package Cohesion**
   - 相關類別放在同一個 package
   - 避免跨 package 的強耦合

4. **Configuration Separation**
   - 環境相關設定放在 `application-{env}.yml`
   - 不要硬編設定值

5. **Test Organization**
   - 測試類別與被測類別同名加 `Test` 後綴
   - 測試檔案結構與 main 一致

### Frontend Rules

1. **Component Composition**
   - 優先使用組合而非繼承
   - 小型、可重用的組件

2. **Server/Client Components**
   - 預設使用 Server Components
   - 需要互動才使用 Client Components

3. **State Management**
   - 本地狀態: `useState`
   - 跨組件狀態: Context API 或狀態管理庫
   - Server state: React Query (未來)

4. **Import Order**
   ```typescript
   // 1. React imports
   import React from 'react';

   // 2. Third-party imports
   import { format } from 'date-fns';

   // 3. Internal imports (絕對路徑)
   import { Button } from '@/components/ui/Button';

   // 4. Relative imports
   import { formatCurrency } from './utils';

   // 5. Type imports
   import type { Trip } from '@/types';
   ```

5. **File Size Limits**
   - Component: < 300 行（建議）
   - Utility: < 200 行（建議）
   - 超過則考慮拆分

---

## Configuration Management

### Backend Environment Files

```
backend/src/main/resources/
├── application.yml              # 基礎設定（所有環境共用）
├── application-dev.yml          # 開發環境專用
└── application-prod.yml         # 生產環境專用
```

**啟用方式**:
```bash
# 開發環境
java -jar app.jar --spring.profiles.active=dev

# 生產環境
java -jar app.jar --spring.profiles.active=prod
```

### Frontend Environment Files

```
frontend/
├── .env.local               # 本地開發（不提交 Git）
├── .env.development         # 開發環境
└── .env.production          # 生產環境
```

**使用方式**:
```typescript
// Next.js 自動載入對應環境的 .env
const apiUrl = process.env.NEXT_PUBLIC_API_URL;
```

---

## Version Control Patterns

### Git Ignore

#### Backend `.gitignore`
```
target/
*.class
*.jar
*.war
.DS_Store
application-local.yml
```

#### Frontend `.gitignore`
```
node_modules/
.next/
out/
.env.local
.DS_Store
```

#### Root `.gitignore`
```
.serena/
.spec-workflow/
.claude/
```

### Branch Naming

```
main                          # 生產環境
develop                       # 開發環境
feature/{spec-name}           # 功能分支
hotfix/{issue-description}    # 緊急修復
```

---

## Documentation Structure

### Backend Documentation
```
backend/docs/
├── api/
│   ├── member-api.md
│   ├── trip-api.md
│   └── expense-api.md
├── architecture/
│   └── system-design.md
└── development/
    └── setup-guide.md
```

### Frontend Documentation
```
frontend/docs/
├── components/
│   └── component-guide.md
├── styling/
│   └── tailwind-guide.md
└── development/
    └── setup-guide.md
```

---

## Changelog

- **2025-11-08**: 初版建立（Steering Document Creation）
