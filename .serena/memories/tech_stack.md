# 技術棧

## Backend

### 核心框架
- **Java**: 21 (OpenJDK 21.0.6 LTS)
- **Spring Boot**: 3.5.6
- **Maven**: 3.9.9

### 主要依賴
- **Spring Data JPA** - ORM 框架
- **Spring Security** - 安全框架
- **Spring Mail** - 郵件服務
- **Spring Boot Starter Web** - Web 框架
- **Spring Boot Starter AOP** - 面向切面編程
- **Spring Boot Starter Validation** - 資料驗證

### 資料庫
- **PostgreSQL** (runtime)
- **HikariCP 7.0.2** - 連線池

### 工具庫
- **Lombok** - 減少樣板程式碼
- **SpringDoc OpenAPI 2.8.6** - API 文件 (Swagger)
- **JWT (jjwt 0.13.0)** - Token 驗證
- **Gson** - JSON 處理
- **Jackson Datatype Hibernate6** - Hibernate 序列化
- **Apache HttpClient 4.5.14** - HTTP 客戶端
- **UUID Creator 6.1.1** - UUID 生成

### 測試
- **Spring Boot Starter Test**
- **Spring Security Test**

---

## Frontend

### 核心框架
- **Next.js**: 15.3.0 (使用 Turbopack)
- **React**: 19.0.0
- **TypeScript**: 5

### 包管理器
- **pnpm**

### UI 庫
- **Radix UI** - 無障礙 UI 組件庫
  - @radix-ui/react-icons
  - @radix-ui/react-tabs
  - @radix-ui/themes
- **Tailwind CSS**: 4 - Utility-first CSS 框架
- **React Day Picker 9.6.7** - 日期選擇器

### 國際化
- **i18next 25.1.2**
- **react-i18next 15.5.1**
- **i18next-browser-languagedetector 8.1.0**
- **i18next-resources-to-backend 1.2.1**
- **accept-language 3.0.20**

### 工具庫
- **date-fns 4.1.0** - 日期處理
- **clsx 2.1.1** - 條件式 className
- **tailwind-merge 3.2.0** - Tailwind className 合併

### 開發工具
- **ESLint 9** - 程式碼檢查
  - eslint-config-next
  - eslint-config-prettier
  - eslint-plugin-prettier
  - eslint-plugin-sonarjs (靜態程式碼分析)
- **Prettier 3.5.3** - 程式碼格式化
- **Husky 9.1.7** - Git hooks
- **lint-staged 15.5.1** - Pre-commit linting
- **@svgr/webpack 8.1.0** - SVG 轉 React 組件

---

## 容器化
- **Docker**
- **Docker Compose**