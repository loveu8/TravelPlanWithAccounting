# JDK 25 升級計畫（Codex Web 聯網版 v6）

> 更新日期：2026-04-03
> 目的：在 Codex Web 環境下，以可追溯、可重複、可明確判斷 `GO / NO-GO` 的方式，完成 JDK `21 -> 25` 升級驗證，並排除 agent phase 網路限制、Maven 執行模式、plugin 相容性、parent POM 假設錯誤與 Maven local repo 不一致等問題。
> 本版補強：plugin 判斷門檻、無 parent POM 容錯（修正 `null` 字串問題）、`rg` fallback 至 `grep`、Setup / agent 共用同一 local repo、`mvnw` / `mvn` 選擇機制、全命令 `-B`、Surefire 檢查、Setup Script log 保留、`.upgrade/` 與 `.m2/` 管理建議。

---

## 0. 這版修正了什麼

相較前版（v5），v6 補強以下重點：

1. **plugin 檢查補上可判斷門檻**
   - `maven-compiler-plugin`：建議 `3.13.0+`
   - `maven-surefire-plugin`：建議 `3.2.0+`
   - 不再只是收集證據，而是有明確判斷基準

2. **`project.parent.version` 改成容錯模式（修正 `null` 字串）**
   - 沒有 parent POM 時，`help:evaluate` 實際回傳字串 `null`，不是空字串
   - 原本的 `${VAR:-fallback}` 對 `null` 字串無效，現已改成明確判斷 `[ "$VAR" = "null" ]`

3. **失敗擷取命令加上 `rg` / `grep` fallback**
   - Codex 環境不保證有安裝 ripgrep
   - 改成先偵測 `rg` 是否存在，有則用 `rg`，無則退回 `grep -En`

4. **Setup 與 agent phase 強制使用同一個 Maven local repo**
   - 統一使用專案內固定路徑：`"$PWD/.m2/repository"`
   - 避免不同 phase 的 HOME 或預設路徑不同，導致 Setup 預熱 cache 實際沒被用到

5. **延續 v5 的完整防呆**
   - `./mvnw` 優先
   - 所有 Maven 命令加 `-B`
   - Setup Script 保留 log
   - Surefire 檢查
   - `.upgrade/` 管理說明
   - 保留 `test` 與 `clean install` 的雙重驗證

---

## 1. Scope / Non-Goals

### 1.1 Scope（本次一定做）

- JDK：`21 -> 25`
- Maven：維持專案既有版本或 wrapper 指定版本
- 驗證：
  - `mvn test`
  - `mvn clean install`
- 輸出單一決策：
  - `GO`
  - `NO-GO`

### 1.2 Non-Goals（本次不做）

- 一次升級多個無必要依賴版本
- 與 JDK 25 無直接關聯的功能重構
- 無紀錄、不可追溯的臨時修補
- 未經說明的 repo 結構調整
- 把 `.upgrade/` 或 `.m2/` 驗證產物當成正式交付內容

---

## 2. Codex Web 必要設定（先做，不可跳過）

> 位置：Codex Web → Environment → Internet Access

### 2.1 Agent internet access

- 設為：`On`

### 2.2 Domain allowlist

- Preset：`Common dependencies`
- 額外追加精準 host：
  - `repo.maven.apache.org`
  - `repo1.maven.org`
  - `repo.spring.io`
  - `start.spring.io`
  - `api.adoptium.net`
  - `download.oracle.com`
  - `github.com`
  - `raw.githubusercontent.com`

> 說明：`Common dependencies` 雖然可能已包含常見網域，但實務上仍建議補上實際 artifact/download host，避免精準網域比對造成阻擋。

### 2.3 Allowed HTTP methods

- 只允許：`GET`、`HEAD`、`OPTIONS`
- 禁止：`POST`、`PUT`、`PATCH`、`DELETE`

---

## 3. 執行前共通約定（所有 Gate 都適用）

### 3.1 Maven 指令選擇規則

1. 若專案存在 `./mvnw`，優先使用 `./mvnw`
2. 若不存在 `./mvnw`，退回使用系統 `mvn`

### 3.2 統一使用 Batch Mode

所有 Maven 命令一律加上 `-B`，目的：
- 關閉互動式模式
- 降低 agent 卡住風險
- 讓輸出更適合自動分析

### 3.3 統一 Maven local repo 路徑

所有 Setup 與 Gate 內的 Maven 命令，統一使用：

```bash
MAVEN_REPO="$PWD/.m2/repository"
```

> 說明：故意不用 `${HOME}/.m2/repository`，而是改用專案工作目錄內的固定路徑。
> 原因是 Codex 不同 phase 的 HOME 或預設 Maven local repo 路徑可能不同，但 `backend/.m2/repository` 在同一個 workspace 內通常更穩定，能確保 Setup 預熱真的被後續 Gate 用到。

### 3.4 `.upgrade/` 與 `.m2/` 管理建議

以下目錄屬於臨時驗證產物，建議不要納入正式版本控制：
- `.upgrade/`
- `.m2/`

可使用以下任一方式忽略：
- 加入 `.gitignore`
- 或加入 `.git/info/exclude`

> 本計畫不強制要求修改 tracked 的 `.gitignore`，避免造成額外 repo diff。但執行者應確保這兩個目錄不被誤 commit。

### 3.5 測試重複執行的設計說明

本計畫**刻意保留**：
- `mvn -B test`：驗證純 test phase 是否正常
- `mvn -B clean install`：驗證完整 lifecycle（compile → test → package → install）是否正常

這是故意的雙重驗證，不是多餘重複。

---

## 4. Setup Script（建議強制使用）

> 目的：利用 setup phase 可連網的特性，預先下載依賴與 plugins，降低 agent phase 因網路限制而失敗的機率。此步驟是防呆與預熱，不是取代後續 Gate 驗證。

### 4.1 Setup Script 內容

在 Codex Web 的 Setup Script 使用：

```bash
set -euo pipefail

cd backend
mkdir -p .upgrade .m2/repository

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
  echo "using Maven Wrapper: ./mvnw" | tee .upgrade/00-maven-command.txt
else
  MVN=mvn
  echo "using system Maven: mvn" | tee .upgrade/00-maven-command.txt
fi

MAVEN_REPO="$PWD/.m2/repository"
echo "$MVN" | tee .upgrade/00-maven-command-value.txt
echo "$MAVEN_REPO" | tee .upgrade/00-maven-repo-path.txt

$MVN -B -Dmaven.repo.local="$MAVEN_REPO" dependency:go-offline | tee .upgrade/setup-go-offline.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" dependency:resolve-plugins | tee .upgrade/setup-resolve-plugins.txt
```

### 4.2 Setup Script 驗收條件

- Setup Script exit code = 0
- `.upgrade/setup-go-offline.txt` 存在
- `.upgrade/setup-resolve-plugins.txt` 存在
- `.upgrade/00-maven-repo-path.txt` 存在
- 無 dependency / plugin resolution error

---

## 5. 與本專案直接相關的網路需求

本專案升級與 build 驗證過程中，至少需要能存取以下類型資源：
- Maven parent POM
- Maven plugins
- Spring Boot 相關 artifacts
- 專案依賴（如 JJWT、PostgreSQL、OpenAPI 等）

常見來源站包含：
- `repo.maven.apache.org`
- `repo.spring.io`
- 其他專案 pom 或 settings 實際定義的 repository host

常見失敗訊號：
- `Non-resolvable parent POM`
- `Could not transfer artifact`
- `status code: 403`
- `Connection timed out`
- `PKIX path building failed`

---

## 6. 執行契約（Execution Contract）

1. **Single-variable first**：預設只改兩件事：執行環境 JDK、`pom.xml` 的 `<java.version>`
2. **Fail-fast**：任一 Gate 失敗，立即停止，不繼續下一階段
3. **Evidence-based**：每個關鍵步驟都要有 log 檔
4. **Deterministic commands**：命令需可直接複製執行
5. **One root cause only**：`NO-GO` 報告只標示一個主因分類

---

## 7. Preflight（Maven 指令與 local repo 確認）

> 目的：在正式進 Gate 0 前，先確認這次流程要用 `./mvnw` 還是 `mvn`，以及共用哪個 local repo。

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade .m2/repository

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
  echo "wrapper exists; using ./mvnw" | tee .upgrade/00-maven-command.txt
else
  MVN=mvn
  echo "no wrapper; using system mvn" | tee .upgrade/00-maven-command.txt
fi

MAVEN_REPO="$PWD/.m2/repository"
echo "$MVN" | tee .upgrade/00-maven-command-value.txt
echo "$MAVEN_REPO" | tee .upgrade/00-maven-repo-path.txt
```

### 7.1 Preflight 通過條件

- `.upgrade/00-maven-command.txt` 存在
- `.upgrade/00-maven-command-value.txt` 存在
- `.upgrade/00-maven-repo-path.txt` 存在

---

## 8. Gate 0（網路驗證）

> 目的：先確認 agent phase 的網路是否真的可用。若此 Gate 失敗，直接判定 `NETWORK_POLICY`，不進入後續 build 驗證。

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade

curl -sS -L -o /dev/null -w "%{http_code}\n" https://repo.maven.apache.org/maven2/ | tee .upgrade/0-network-central.txt
curl -sS -L -o /dev/null -w "%{http_code}\n" https://repo.spring.io/release/ | tee .upgrade/0-network-spring.txt
```

### 8.1 Gate 0 通過條件

- `.upgrade/0-network-central.txt` 內容為 `200`
- `.upgrade/0-network-spring.txt` 內容為 `200`，或專案明確未使用 Spring repository 並在報告中註明原因

### 8.2 Gate 0 失敗處理

- 立即停止
- 判定 `NO-GO`
- Root Cause = `NETWORK_POLICY`

---

## 9. Gate A（JDK 21 Baseline）

> 目的：確認升級前 baseline 本身健康。若 baseline 本來就 build/test 失敗，則不應進入 JDK 25 驗證。

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade .m2/repository

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
else
  MVN=mvn
fi

MAVEN_REPO="$PWD/.m2/repository"

java -version 2>&1 | tee .upgrade/a-java-version.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" -version 2>&1 | tee .upgrade/a-mvn-version.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" -q help:evaluate -Dexpression=java.version -DforceStdout \
  | tee .upgrade/a-java-property.txt

# 容錯處理：沒有 parent POM 時，help:evaluate 回傳字串 "null"，不是空字串
SPRING_PARENT_VERSION="$(
  $MVN -B -Dmaven.repo.local="$MAVEN_REPO" -q help:evaluate \
    -Dexpression=project.parent.version -DforceStdout 2>/dev/null || true
)"
if [ -z "$SPRING_PARENT_VERSION" ] || [ "$SPRING_PARENT_VERSION" = "null" ]; then
  echo "no parent POM" | tee .upgrade/a-spring-boot-version.txt
else
  echo "$SPRING_PARENT_VERSION" | tee .upgrade/a-spring-boot-version.txt
fi

$MVN -B -Dmaven.repo.local="$MAVEN_REPO" test | tee .upgrade/a-test.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" clean install | tee .upgrade/a-clean-install.txt
```

### 9.1 Gate A 通過條件

- 所有必要命令 exit code = 0
- `.upgrade/a-*` 證據檔存在
- JDK 21 baseline 下：`test` 成功、`clean install` 成功

### 9.2 Gate A 補充說明

- `.upgrade/a-spring-boot-version.txt` 若為 `no parent POM`，**不構成 Gate A 失敗**
- 這代表專案可能不是用 parent POM 管理版本，而不是 baseline 壞掉

### 9.3 Gate A 失敗處理

若 Gate A 失敗：
- 直接停止，判定 `NO-GO`
- **不執行 rollback**
- 原因：升級前 baseline 本身就有問題，應先修復 baseline，再重新啟動整個升級計畫

---

## 10. Gate B（切換 JDK 25 + 相容性前置檢查）

> 目的：將執行環境切至 JDK 25，並在正式 build 前，先確認最容易造成誤判的相容性問題。

### 10.1 JDK 25 切換方式

執行者應依 Codex Web 當前可用能力擇一，並在報告中說明採用哪一種。

#### 選項 A：使用 SDKMAN（若有安裝）

```bash
sdk install java 25-tem
sdk use java 25-tem
```

#### 選項 B：設定 JAVA_HOME

```bash
export JAVA_HOME=/path/to/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
hash -r
```

#### 選項 C：使用 Codex Environment 的 runtime 切換

- 若 UI 提供 JDK runtime 切換功能，可直接切至 JDK 25
- 切換後仍必須在 shell 驗證 `java -version`

> 注意：本文件不假設 Codex Web 一定支援哪一種切換法。核心要求只有一個：**最終 shell 內的 `java` 與 Maven 實際使用的 Java，都必須是 JDK 25。**

### 10.2 JDK 切換後驗證

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade .m2/repository

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
else
  MVN=mvn
fi

MAVEN_REPO="$PWD/.m2/repository"

java -version 2>&1 | tee .upgrade/b-java-version.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" -version 2>&1 | tee .upgrade/b-mvn-version.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" -q help:evaluate -Dexpression=java.version -DforceStdout \
  | tee .upgrade/b-java-property.txt
```

### 10.3 允許的程式變更

本計畫預設僅允許最小變更：

- `pom.xml`：`<java.version>21</java.version>` → `<java.version>25</java.version>`

修改後執行：

```bash
git diff -- pom.xml | tee .upgrade/pom-diff.txt
```

### 10.4 Maven / Spring / JVM 前置檢查

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade .m2/repository

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
else
  MVN=mvn
fi

MAVEN_REPO="$PWD/.m2/repository"

$MVN -B -Dmaven.repo.local="$MAVEN_REPO" help:effective-pom \
  -Doutput=.upgrade/b-effective-pom.xml | tee .upgrade/b-effective-pom-command.txt

grep -n -A5 -B2 "maven-compiler-plugin" .upgrade/b-effective-pom.xml \
  | tee .upgrade/b-compiler-plugin.txt || true
grep -n -A5 -B2 "maven-surefire-plugin" .upgrade/b-effective-pom.xml \
  | tee .upgrade/b-surefire-plugin.txt || true

if [ -f .mvn/jvm.config ]; then
  cat .mvn/jvm.config | tee .upgrade/b-jvm-config.txt
else
  echo "no jvm.config" | tee .upgrade/b-jvm-config.txt
fi

echo "MAVEN_OPTS=${MAVEN_OPTS:-}" | tee .upgrade/b-maven-opts.txt
```

### 10.5 Gate B 檢查重點（含判斷門檻）

1. **JDK 25 是否真的生效**
   - `java -version` 顯示 JDK 25
   - Maven 使用的 Java 也顯示 JDK 25

2. **`pom.xml` 是否僅有最小變更**
   - 預期 diff 為 `java.version 21 -> 25`
   - 若有其他依賴改動，必須在報告中明確列出

3. **Spring Boot / parent 資訊**
   - 若有 parent POM，需記錄版本
   - 若為 `no parent POM`，僅表示沒有 parent，不是失敗

4. **`maven-compiler-plugin` 判斷門檻**
   - **建議基準線：`3.13.0+`**（此為本 PRD 實務基準，非 Apache 官方硬門檻）
   - 若低於 `3.13.0`：標記為高風險，優先歸類為 `DEPENDENCY`
   - 若無法明確辨識版本：報告中標示 `UNKNOWN`，不直接算通過

5. **`maven-surefire-plugin` 判斷門檻**
   - **建議基準線：`3.2.0+`**（此為本 PRD 實務基準，非 Apache 官方硬門檻）
   - 若低於 `3.2.0`：標記為高風險，若後續 test 失敗優先歸類為 `DEPENDENCY`
   - 若無法明確辨識版本：報告中標示 `UNKNOWN`，不直接算通過

6. **`.mvn/jvm.config` / `MAVEN_OPTS`**
   - 若包含 JDK 25 不支援的 JVM 旗標，優先列為 `ENVIRONMENT` 或 `DEPENDENCY` 問題來源

### 10.6 Gate B 通過條件

- JDK 25 已確認生效
- `pom.xml` diff 符合最小變更原則
- compiler / surefire plugin 已完成檢查
- plugin 版本不得為明確低於基準線的版本
- 若版本 `UNKNOWN`，不得直接判 `GO`，需在報告中列為未解風險

---

## 11. Gate C（JDK 25 Build / Test 驗證）

> 目的：在 JDK 25 下，驗證專案是否能正常執行 test 與完整 install。本版保留 `test` 與 `clean install` 的雙重驗證，屬刻意設計。

在 `backend/` 目錄執行：

```bash
set -euo pipefail
mkdir -p .upgrade .m2/repository

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
else
  MVN=mvn
fi

MAVEN_REPO="$PWD/.m2/repository"

$MVN -B -Dmaven.repo.local="$MAVEN_REPO" clean compile | tee .upgrade/c-compile.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" test | tee .upgrade/c-test.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" clean install | tee .upgrade/c-clean-install.txt
```

### 11.1 Gate C 通過條件

- `clean compile` 成功
- `test` 成功
- `clean install` 成功

### 11.2 可選補充驗證：dependency refresh

若前述都成功，可再額外執行：

```bash
set -euo pipefail

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
else
  MVN=mvn
fi

MAVEN_REPO="$PWD/.m2/repository"
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" -U dependency:resolve | tee .upgrade/c-dependency-resolve.txt
```

> 這是補充驗證，不作為第一層 compile/test 判定依據。若此步失敗但 Gate C 前三步皆成功，通常較接近 repository / snapshot / 網路政策問題，而不是 JDK 25 原始碼相容性問題。

---

## 12. 失敗分類（NO-GO Root Cause）

若任一 Gate 失敗，只選擇一個主因：

- `ENVIRONMENT`
  - JDK 未正確切換
  - `JAVA_HOME` / `PATH` 錯誤
  - 不相容 JVM flags 導致 Maven 無法啟動
  - Maven 實際使用的 Java 與預期不符

- `NETWORK_POLICY`
  - agent internet access 未正確啟用
  - allowlist 缺漏
  - HTTP methods 限制過嚴
  - repository host 被擋

- `DEPENDENCY`
  - Spring Boot / library / plugin 與 JDK 25 不相容
  - `maven-compiler-plugin` 低於基準線
  - `maven-surefire-plugin` 低於基準線
  - parent POM / artifact 無法解析

- `COMPILER`
  - 原始碼在 JDK 25 下出現明確編譯錯誤
  - API 移除、語法或型別推導行為改變

- `TEST`
  - compile 通過，但測試在 JDK 25 下失敗
  - **且已排除 plugin / dependency / environment 問題後**，才歸到此類

---

## 13. First Error 擷取方式

發生失敗時，可執行：

```bash
# 自動偵測 rg 或退回 grep（Codex 不保證有安裝 ripgrep）
SEARCH_CMD="grep -En"
command -v rg >/dev/null 2>&1 && SEARCH_CMD="rg -n"

$SEARCH_CMD \
  "ERROR|FATAL|BUILD FAILURE|COMPILATION ERROR|Non-resolvable|Could not transfer|UnsupportedClassVersionError|Unrecognized VM option" \
  .upgrade/*.txt \
  | head -n 30 | tee .upgrade/failure-summary.txt
```

---

## 14. Rollback

### 14.1 何時需要 rollback

只有在以下情況才需要 rollback：
- Gate A 已通過
- 已修改 `pom.xml`
- 已切換到 JDK 25
- 之後在 Gate B 或 Gate C 失敗

### 14.2 何時不需要 rollback

若 **Gate A 失敗**：
- 不需 rollback
- 因為還沒進入正式升級變更，baseline 本身就已不健康

### 14.3 Rollback 步驟

```bash
set -euo pipefail
mkdir -p .upgrade .m2/repository

git checkout -- pom.xml

# 依實際環境切回 JDK 21，例如：
# sdk use java 21-tem
# 或 export JAVA_HOME=/path/to/jdk-21

if [ -f ./mvnw ]; then
  chmod +x ./mvnw
  MVN=./mvnw
else
  MVN=mvn
fi

MAVEN_REPO="$PWD/.m2/repository"

java -version 2>&1 | tee .upgrade/r-java-version.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" -version 2>&1 | tee .upgrade/r-mvn-version.txt
$MVN -B -Dmaven.repo.local="$MAVEN_REPO" clean install | tee .upgrade/r-clean-install.txt
```

### 14.4 Rollback 驗證條件

- `pom.xml` 已回到 JDK 21 設定
- Java runtime 已切回 JDK 21
- `clean install` 成功

---

## 15. GO / NO-GO Report Template

```md
# JDK 25 Upgrade Result

## Decision
- GO | NO-GO

## Environment Snapshot
- java -version: ...
- mvn command used: ./mvnw | mvn
- maven local repo: ...
- mvn -version: ...
- java.version (maven model): ...

## Codex Web Internet Access Snapshot
- Agent internet access: On/Off
- Domain preset: None | Common dependencies | All
- Added domains: ...
- Allowed methods: GET, HEAD, OPTIONS

## Setup Script Result
- go-offline: pass/fail
- resolve-plugins: pass/fail
- logs:
  - .upgrade/setup-go-offline.txt
  - .upgrade/setup-resolve-plugins.txt

## Baseline Snapshot
- Gate A passed: yes/no
- parent / spring boot info: ...
- Baseline test: pass/fail
- Baseline clean install: pass/fail

## JDK Switch Method
- Method used: SDKMAN | JAVA_HOME | Codex runtime switch
- Evidence:
  - .upgrade/b-java-version.txt
  - .upgrade/b-mvn-version.txt

## Changes Made
- pom.xml diff: .upgrade/pom-diff.txt
- Summary: java.version 21 -> 25 only / or explain additional changes

## Compatibility Checks
- compiler plugin: .upgrade/b-compiler-plugin.txt
- compiler plugin assessment: PASS (>=3.13.0) | WARN | FAIL (<3.13.0) | UNKNOWN
- surefire plugin: .upgrade/b-surefire-plugin.txt
- surefire plugin assessment: PASS (>=3.2.0) | WARN | FAIL (<3.2.0) | UNKNOWN
- jvm.config: .upgrade/b-jvm-config.txt
- MAVEN_OPTS: .upgrade/b-maven-opts.txt

## Evidence
- Maven command selection:
  - .upgrade/00-maven-command.txt
  - .upgrade/00-maven-command-value.txt
- Maven local repo:
  - .upgrade/00-maven-repo-path.txt
- Network:
  - .upgrade/0-network-central.txt
  - .upgrade/0-network-spring.txt
- Gate A:
  - .upgrade/a-*
- Gate B:
  - .upgrade/b-*
- Gate C:
  - .upgrade/c-*

## If NO-GO: Root Cause
- Category: ENVIRONMENT | NETWORK_POLICY | DEPENDENCY | COMPILER | TEST
- First failing command: ...
- First deterministic error line: ...
- Minimal fix proposal: ...

## Rollback Verification
- rollback required: yes/no
- pom.xml restored to 21: yes/no
- JDK switched back to 21: yes/no
- clean install on JDK21: pass/fail
```

---

## 16. Acceptance Criteria

只有同時滿足以下條件，才可判定 `GO`：

1. Codex Web 已啟用 agent internet access
2. Domain allowlist 與 HTTP methods 符合最小權限要求
3. Setup Script 成功完成 dependency / plugin 預熱
4. Preflight 已明確記錄使用 `./mvnw` 或 `mvn`
5. Preflight 已明確記錄共用的 Maven local repo 路徑
6. Gate 0 通過
7. Gate A 通過
8. Gate B 已證明 JDK 25 生效
9. `maven-compiler-plugin` 不得明確低於 `3.13.0`
10. `maven-surefire-plugin` 不得明確低於 `3.2.0`
11. Gate C 通過
12. `mvn -B test` 成功
13. `mvn -B clean install` 成功
14. `.upgrade/` 證據完整可追溯
15. `pom.xml diff` 能清楚說明實際變更

任一不滿足，即判定 `NO-GO`。

---

## 17. 執行順序總覽

```text
[Section 2] Codex Web Internet Access 設定
        ↓
[Section 4] Setup Script 預熱依賴與 plugins（共用固定 local repo）
        ↓
[Section 7] Preflight：確認使用 ./mvnw 或 mvn，並確認 local repo 路徑
        ↓
[Gate 0] 網路連線驗證
        ↓
[Gate A] JDK 21 baseline 驗證
        ↓
[Gate B] 切換 JDK 25 + 相容性前置檢查
        ↓
[Gate C] JDK 25 compile / test / install 驗證
        ↓
[GO / NO-GO Report]
        ↓
[必要時] Rollback
```

---

## 18. 補充說明（避免誤判）

### 18.1 若 Gate 0 失敗

先檢查網路策略，不要直接懷疑 Maven 或原始碼。

### 18.2 若 Gate A 失敗

代表 baseline 本身就壞，不是 JDK 25 的問題。應先修 baseline，再重新啟動整個升級流程。

### 18.3 若 Gate A 的 `a-spring-boot-version.txt` 顯示 `no parent POM`

這不是失敗，只代表這個專案不是靠 parent POM 管理版本。

### 18.4 若 Gate B 中 plugin 版本低於基準線

優先視為 `DEPENDENCY` 風險，而不是等 Gate C 爆掉後才回頭猜。

### 18.5 若 Setup 成功但 Gate C 還是大量重新下載依賴

優先檢查：
- 是否真的有使用同一個 `-Dmaven.repo.local`
- 是否路徑被後續命令覆蓋
- 是否 workspace 在不同 phase 被重建

### 18.6 若 Gate C compile 成功但 test 失敗

先看：
- `maven-surefire-plugin` 版本
- JUnit / test framework 版本
- 反射、模組、時區、序列化等 runtime 差異

只有在排除 plugin / dependency / environment 問題後，才把 root cause 定為 `TEST`。