# 常用開發命令

## Backend 命令

### Maven 編譯與測試
```bash
# 完整編譯與測試（變更程式碼後必跑）
mvn clean install

# 使用 Maven Wrapper
./mvnw clean install

# 僅執行測試
mvn test
./mvnw test
```

### Docker 操作 (Windows)
```powershell
# 切換到 backend 目錄
cd backend

# 編譯專案
.\build.bat

# 啟動服務
.\up.bat

# 關閉服務
.\down.bat
```

### Docker 操作 (Linux)
```bash
# 切換到 backend 目錄
cd backend

# 編譯專案
sh build.sh

# 啟動服務
sh up.sh

# 關閉服務
sh down.sh
```

---

## Frontend 命令

### 開發
```bash
# 切換到 frontend 目錄
cd frontend

# 安裝依賴
pnpm install

# 啟動開發伺服器 (使用 Turbopack)
pnpm dev

# 開啟 http://localhost:3000
```

### 建置與部署
```bash
# 生產環境建置
pnpm build

# 啟動生產伺服器
pnpm start

# Lint 檢查
pnpm lint
```

### Docker 操作
```bash
# 切換到 frontend 目錄
cd frontend

# 建置 Docker image
docker build -t travelplan-frontend .

# 啟動 container
docker run -p 3000:3000 travelplan-frontend
```

---

## Windows 系統常用命令

### 檔案與目錄操作
```powershell
# 列出目錄內容
dir
ls  # PowerShell 也支援

# 切換目錄
cd path\to\directory

# 建立目錄
mkdir directory_name

# 刪除檔案
del file_name

# 刪除目錄
rmdir directory_name
rd /s directory_name  # 遞迴刪除
```

### 搜尋
```powershell
# 搜尋檔案內容
findstr "pattern" file_name
findstr /s /i "pattern" *.java  # 遞迴搜尋，忽略大小寫

# 搜尋檔案
dir /s file_name
```

### Git 操作
```bash
# 查看狀態
git status

# 查看差異
git diff

# 提交變更
git add .
git commit -m "commit message"

# 推送
git push

# 拉取
git pull

# 查看日誌
git log
```

---

## 開發流程建議

### 啟動完整開發環境
```bash
# 1. 啟動 Backend (新終端機)
cd backend
.\up.bat  # Windows
# 或 sh up.sh  # Linux

# 2. 啟動 Frontend (新終端機)
cd frontend
pnpm dev
```

### 提交前檢查
```bash
# Backend
cd backend
mvn clean install

# Frontend
cd frontend
pnpm lint
pnpm build
```

---

## 環境需求

### Backend
- **Java**: OpenJDK 21.0.6 LTS
- **Maven**: 3.9.9
- **Docker**: (for containerized deployment)

### Frontend
- **Node.js**: 參考 `.nvmrc`
- **pnpm**: 最新版本
- **Docker**: (for containerized deployment)

---

## 問題排查

### Backend
```bash
# 清理 Maven 快取
mvn clean

# 查看 Spring Boot 啟動日誌
.\up.bat
# 查看 docker logs
docker logs [container_name]
```

### Frontend
```bash
# 清理 pnpm 快取
pnpm store prune

# 重新安裝依賴
rm -rf node_modules pnpm-lock.yaml
pnpm install

# 清理 Next.js 快取
rm -rf .next
pnpm build
```