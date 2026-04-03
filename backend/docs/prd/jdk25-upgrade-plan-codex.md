# JDK 25 升級計畫（Codex Web 聯網版 v3）

> 更新日期：2026-04-03  
> 目的：修正「Codex 網頁模式執行時 agent phase 聯網被禁止，導致 Maven 依賴無法下載」問題，並讓升級流程可直接驗證 `mvn clean install` 與 `mvn test`。

---

## 0. 這版修正了什麼

根據 OpenAI 官方文件 **Agent internet access – Codex web**：

- Codex 預設會在 **agent phase 封鎖網路**。
- 只有 setup scripts 預設可上網，agent 本體仍可能被擋。
- 要在 Web 環境中手動開啟 agent internet access，並設定 allowlist 與 HTTP methods。

因此本版將流程改成：

1. 先在 Codex Web Environment 開啟 **Agent internet access = On**。
2. Domain allowlist 使用 **Common dependencies**，再補上 Maven/JDK 升級必需網域。
3. HTTP methods 限制為 `GET`, `HEAD`, `OPTIONS`（官方建議最小權限）。
4. 用固定 Gate 逐步驗證，要求 `mvn clean install` 與 `mvn test` 都必須可執行。

---

## 1. Scope / Non-Goals

### Scope（本次一定做）
- JDK：`21 -> 25`
- Maven：維持既有版本（目前專案是 3.9.x）
- 驗證：`mvn clean install`、`mvn test`
- 輸出單一決策：`GO` 或 `NO-GO`

### Non-Goals（本次不做）
- 一次升級多個依賴版本
- 無關功能重構
- 不可追溯的手動臨時修補

---

## 2. Codex Web 必要設定（先做，不可跳過）

> 位置：Codex Web → Environment → Internet Access

### 2.1 Agent internet access
- 設為：`On`

### 2.2 Domain allowlist
- Preset：`Common dependencies`
- 另外追加（建議精準補齊 Maven/JDK 下載路徑）：
  - `repo.maven.apache.org`
  - `repo1.maven.org`
  - `repo.spring.io`
  - `start.spring.io`
  - `api.adoptium.net`
  - `download.oracle.com`
  - `github.com`
  - `raw.githubusercontent.com`

> 說明：
> - `Common dependencies` 已含 `apache.org`, `maven.org`, `spring.io`, `github.com`, `java.com`, `java.net`, `oracle.com` 等常見網域。
> - 但實務上建議補「實際下載 host」如 `repo.maven.apache.org`，避免被精準網域比對擋下。

### 2.3 Allowed HTTP methods
- 只允許：`GET`, `HEAD`, `OPTIONS`
- 禁止：`POST`, `PUT`, `PATCH`, `DELETE` 等寫入型方法

---

## 3. 與本專案直接相關的網路需求（pom.xml + 升級腳本）

本專案 `pom.xml` 與升級流程需要可下載：

- Maven parent POM（Spring Boot Parent）
- Maven plugins（compiler/surefire/spring-boot plugin 等）
- 專案依賴（Spring/JJWT/PostgreSQL/OpenAPI 等）

關鍵來源站：
- `repo.maven.apache.org`（Maven Central）
- `repo.spring.io`（Spring artifacts／必要時）

若這些網域未放行，常見失敗會是：
- `Non-resolvable parent POM`
- `Could not transfer artifact ... status code: 403`

---

## 4. 執行契約（Execution Contract）

1. **Single-variable first**：先只切 JDK 與 `java.version`。
2. **Fail-fast**：任一 Gate 失敗立即停止。
3. **Evidence-based**：每一步都要有可追溯輸出。
4. **Deterministic commands**：命令可直接複製執行。

---

## 5. Gate A（JDK 21 Baseline）

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade

java -version | tee .upgrade/a-java-version.txt
mvn -version | tee .upgrade/a-mvn-version.txt
mvn -q help:evaluate -Dexpression=java.version -DforceStdout | tee .upgrade/a-java-property.txt

mvn clean install | tee .upgrade/a-clean-install.txt
mvn test | tee .upgrade/a-test.txt
```

Gate A 通過條件：
- 所有命令 exit code = 0
- `.upgrade/a-*` 證據檔存在

---

## 6. Gate B（切換 JDK 25）

1. 外部切換 shell JDK 至 25
2. 驗證：

```bash
java -version | tee .upgrade/b-java-version.txt
mvn -version | tee .upgrade/b-mvn-version.txt
```

3. 僅允許必要變更：
- `pom.xml`：`<java.version>25</java.version>`

4. 驗證 Maven model：

```bash
mvn -q help:evaluate -Dexpression=java.version -DforceStdout | tee .upgrade/b-java-property.txt
```

---

## 7. Gate C（JDK 25 驗證）

```bash
mvn -U clean compile | tee .upgrade/c-compile.txt
mvn test | tee .upgrade/c-test.txt
mvn clean install | tee .upgrade/c-clean-install.txt
```

Gate C 通過條件：
- compile/test/install 全綠

---

## 8. 失敗分類（NO-GO Root Cause）

單一主因分類：
- `ENVIRONMENT`：JDK/PATH 錯誤
- `NETWORK_POLICY`：Codex environment 沒放行對應網域/方法
- `COMPILER`：JDK 25 編譯不相容
- `TEST`：JDK 25 行為差異
- `DEPENDENCY`：依賴本身不相容

First error 擷取：

```bash
rg -n "ERROR|FATAL|BUILD FAILURE|COMPILATION ERROR|Non-resolvable|Could not transfer" .upgrade/*.txt | head -n 30
```

---

## 9. Rollback

若 Gate C 失敗：

1. 還原 `pom.xml` 的 `<java.version>` 至 `21`
2. 切回 JDK 21
3. 重跑：

```bash
java -version
mvn -version
mvn clean install
```

---

## 10. GO/NO-GO Report Template

```md
# JDK 25 Upgrade Result

## Decision
- GO | NO-GO

## Environment Snapshot
- java -version: ...
- mvn -version: ...
- java.version (maven model): ...

## Internet Access Snapshot (Codex Web)
- Agent internet access: On/Off
- Domain preset: None | Common dependencies | All
- Added domains: ...
- Allowed methods: ...

## Evidence
- Commands: ...
- Artifacts: .upgrade/a-..., .upgrade/b-..., .upgrade/c-...

## If NO-GO: Root Cause
- Category: ENVIRONMENT | NETWORK_POLICY | COMPILER | TEST | DEPENDENCY
- First failing command: ...
- First deterministic error line: ...
- Minimal fix proposal: ...

## Rollback Verification
- pom.xml java.version restored to 21: yes/no
- JDK switched back to 21: yes/no
- mvn clean install on JDK21: pass/fail
```

---

## 11. Acceptance Criteria

只有同時滿足以下條件才可判定 `GO`：

- Codex Web 已啟用 Agent internet access（且最小權限）
- Gate A/B/C 全通過
- `mvn clean install` 成功
- `mvn test` 成功
- `.upgrade/` 證據完整可追溯

任一不滿足即 `NO-GO`。
