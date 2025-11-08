# Frontend 開發指南

## 技術棧
- **Next.js 15.3.0** (App Router)
- **React 19**
- **TypeScript 5**
- **Tailwind CSS 4**
- **Radix UI**

---

## 專案結構

```
frontend/
├── src/                 # 原始碼目錄
│   ├── app/            # Next.js App Router 頁面
│   └── ...             # 其他原始碼
├── public/              # 靜態資源
├── .husky/              # Git hooks
├── .vscode/             # VS Code 設定
├── package.json         # 依賴管理
├── pnpm-lock.yaml       # pnpm 鎖定檔
├── tsconfig.json        # TypeScript 設定
├── next.config.ts       # Next.js 設定
├── eslint.config.mjs    # ESLint 設定
├── .prettierrc          # Prettier 設定
├── .lintstagedrc        # lint-staged 設定
└── Dockerfile           # Docker 建置檔
```

---

## 程式碼風格

### ESLint 設定
使用以下 ESLint 配置：
- **Next.js core-web-vitals** - Next.js 最佳實踐
- **Next.js TypeScript** - TypeScript 規則
- **SonarJS recommended** - 靜態程式碼分析
- **Prettier integration** - 自動格式化整合

覆寫規則：
```javascript
rules: {
  "sonarjs/todo-tag": "warn",  // TODO 標籤降為警告
}
```

### Prettier 設定
使用 Prettier 預設配置（空配置檔案）

### Pre-commit Hooks
- **Husky** - 管理 Git hooks
- **lint-staged** - 只檢查暫存的檔案

---

## 國際化 (i18n)

### 使用的套件
- `i18next` - 核心國際化庫
- `react-i18next` - React 整合
- `i18next-browser-languagedetector` - 自動偵測語言
- `i18next-resources-to-backend` - 動態載入語系檔

### 支援語系
- zh-TW (繁體中文)
- en-US (英文)

---

## UI 組件

### Radix UI
使用無障礙優先的 UI 組件庫：
- `@radix-ui/react-icons` - 圖示
- `@radix-ui/react-tabs` - 分頁
- `@radix-ui/themes` - 主題系統

### 樣式工具
- **Tailwind CSS 4** - Utility-first CSS
- **clsx** - 條件式 className 組合
- **tailwind-merge** - 合併 Tailwind classes

### 日期處理
- **date-fns** - 日期格式化與計算
- **react-day-picker** - 日期選擇器組件

---

## 開發規範

### TypeScript
- 嚴格模式啟用（參考 `tsconfig.json`）
- 優先使用型別推斷
- 避免使用 `any`

### Next.js 最佳實踐
- 使用 App Router (Next.js 15)
- Server Components 優先
- 適時使用 Client Components (`'use client'`)
- 圖片使用 `next/image`
- 字型使用 `next/font`

### SVG 處理
- 使用 `@svgr/webpack` 將 SVG 轉為 React 組件
- 可直接 import SVG 作為組件使用

---

## 開發工具

### Package Manager
使用 **pnpm** 作為包管理器
- 更快的安裝速度
- 更少的磁碟空間
- 嚴格的依賴解析

### Node.js 版本
參考 `.nvmrc` 檔案確認所需的 Node.js 版本

---

## Docker 相關

### Dockerfile
前端提供獨立的 Dockerfile 用於容器化部署

### 建置注意事項
- 使用 `.dockerignore` 排除不必要的檔案
- 建置時使用 production mode