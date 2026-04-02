# JDK 25 Upgrade Plan (Codex-Execution Locked v2)

> 目的：提供 **Codex 可穩定執行、可追溯、可回滾** 的 JDK 21 → JDK 25 升級 PRD。
>
> 原則：一次到位（single pass）、失敗即停止（No-Go）、結論必須有證據。
>
> 此檔案為增補文件，不取代 `jdk25-upgrade-plan.md`。

---

## 1) Scope / Non-Goals

### Scope（本次一定做）
- 升級執行與建置 JDK：`21 -> 25`
- 維持 Maven `3.9.14`（除非證據顯示 Maven 本身為阻塞點）
- 驗證：compile、test、package、（可選）startup
- 產出單一決策：`GO` 或 `NO-GO`

### Non-Goals（本次不做）
- 大量依賴批次升版
- 無關功能重構
- 先改 Docker 再驗證 local（流程相反）
- 模糊判定（例如「看起來差不多可行」）

---

## 2) Codex Execution Contract（避免跑掉）

1. **Single-variable first**：先只改 JDK 與 `<java.version>`。
2. **Fail-fast**：任一 Gate 失敗即停止，不繼續「順手修更多」。
3. **Evidence-based**：每個結論都需對應 command + log artifact。
4. **Deterministic commands**：所有命令可直接複製執行。
5. **No hidden context**：必要 profile / env 都寫進命令，不靠人腦記憶。
6. **One-primary-root-cause**：每次 NO-GO 僅判定一個主要根因。

---

## 3) Fixed Inputs

- Project path: `backend/`
- Baseline JDK: `21`
- Target JDK: `25`
- Build tool: `Maven 3.9.14`
- POM Java property (current): `<java.version>21</java.version>`

---

## 4) Preflight (Mandatory)

> 任一步驟不通過，立即停止。

From `backend/`:

```bash
set -euo pipefail

# 0) Git cleanliness（避免未提交變更污染證據）
git status --porcelain

# 1) Tool availability
command -v java
command -v mvn

# 2) Runtime identity
java -version
mvn -version

# 3) Effective Java property (from Maven model)
mvn -q help:evaluate -Dexpression=java.version -DforceStdout
```

Preflight Pass Criteria:
- `java` / `mvn` 都可用
- `mvn -version` 顯示的 Java 與預期一致
- `help:evaluate` 可穩定回傳 `java.version`

---

## 5) Gate Model

### Gate A — Baseline must be GREEN on JDK 21

```bash
mkdir -p .upgrade
java -version | tee .upgrade/a-java-version.txt
mvn -version | tee .upgrade/a-mvn-version.txt
mvn -q help:evaluate -Dexpression=java.version -DforceStdout | tee .upgrade/a-java-property.txt
mvn -q -DskipTests dependency:tree > .upgrade/a-deps.txt
mvn clean install | tee .upgrade/a-clean-install.txt
mvn test | tee .upgrade/a-test.txt
```

Gate A pass:
- 所有命令 exit code = 0
- `.upgrade/a-*` 證據存在

Gate A fail:
- **STOP**（不是升級回歸，是 baseline 不穩）

---

### Gate B — Switch to JDK 25 + minimal source change

1. 外部切換 shell JDK 至 25
2. 確認：

```bash
java -version | tee .upgrade/b-java-version.txt
mvn -version | tee .upgrade/b-mvn-version.txt
```

3. 僅允許以下變更：
- `pom.xml`: `<java.version>25</java.version>`

4. 確認 Maven model：

```bash
mvn -q help:evaluate -Dexpression=java.version -DforceStdout | tee .upgrade/b-java-property.txt
```

Gate B pass:
- `java -version` 為 25
- `java.version` 為 25

---

### Gate C — Validate on JDK 25

```bash
mvn -U clean compile | tee .upgrade/c-compile.txt
mvn test | tee .upgrade/c-test.txt
mvn clean install | tee .upgrade/c-clean-install.txt
```

Optional（環境完整時才執行）:

```bash
mvn spring-boot:run | tee .upgrade/c-run.txt
```

Gate C pass:
- compile/test/install 全綠
- optional run（若執行）可正常啟動

Gate C fail:
- **NO-GO + rollback**（見 Section 7）

---

## 6) Failure Taxonomy（真實原因）

失敗必須歸類到 **單一主因**：

1. `ENVIRONMENT`
   - `JAVA_HOME/PATH` 指向錯誤
   - `mvn -version` 顯示 Java 與預期不一致

2. `COMPILER`
   - `release/source/target` 或 annotation processor 相容性錯誤

3. `TEST`
   - 單元/整合測試在 JDK 25 出現行為差異

4. `RUNTIME_BOOT`
   - Spring context 啟動失敗、模組/反射限制

5. `DEPENDENCY`
   - 第三方套件與 JDK 25 不相容

### First-error extraction（避免誤判）

```bash
# 擷取第一個 ERROR/FATAL 線索（示例）
rg -n "ERROR|FATAL|BUILD FAILURE|COMPILATION ERROR" .upgrade/c-*.txt | head -n 20
```

規則：
- 以「第一個可重現失敗點」當主因
- 不以連鎖錯誤（downstream noise）當根因

---

## 7) NO-GO Rollback Procedure

當 Gate C fail：

1. 還原 `pom.xml` 的 `<java.version>` 回 `21`
2. shell 切回 JDK 21
3. 重新確認 baseline：

```bash
java -version
mvn -version
mvn clean install
```

4. 補齊 NO-GO 報告（見 Section 10）並停止

---

## 8) Minimal Remediation Policy

只有在證據支持時，才允許最小修復。

允許：
- 因編譯錯誤而補 `maven-compiler-plugin` 的最小配置
- 因明確不相容而升級「單一」依賴

禁止：
- 一次升多個依賴
- 將非根因議題混入本輪

---

## 9) Codex Runbook (Copy/Paste)

From `backend/`:

```bash
set -euo pipefail
mkdir -p .upgrade

# ---- Gate A: Baseline on JDK 21 ----
java -version | tee .upgrade/a-java-version.txt
mvn -version | tee .upgrade/a-mvn-version.txt
mvn -q help:evaluate -Dexpression=java.version -DforceStdout | tee .upgrade/a-java-property.txt
mvn -q -DskipTests dependency:tree > .upgrade/a-deps.txt
mvn clean install | tee .upgrade/a-clean-install.txt
mvn test | tee .upgrade/a-test.txt

# ---- Gate B: switch to JDK 25 externally ----
java -version | tee .upgrade/b-java-version.txt
mvn -version | tee .upgrade/b-mvn-version.txt
# edit pom.xml: <java.version>25</java.version>
mvn -q help:evaluate -Dexpression=java.version -DforceStdout | tee .upgrade/b-java-property.txt

# ---- Gate C: validate on JDK 25 ----
mvn -U clean compile | tee .upgrade/c-compile.txt
mvn test | tee .upgrade/c-test.txt
mvn clean install | tee .upgrade/c-clean-install.txt
```

---

## 10) GO/NO-GO Report Template

```md
# JDK 25 Upgrade Result

## Decision
- GO | NO-GO

## Environment Snapshot
- java -version: ...
- mvn -version: ...
- java.version (maven model): ...

## Evidence
- Commands:
  - ...
- Artifacts:
  - .upgrade/a-...
  - .upgrade/b-...
  - .upgrade/c-...

## If NO-GO: Root Cause
- Category: ENVIRONMENT | COMPILER | TEST | RUNTIME_BOOT | DEPENDENCY
- First failing command: ...
- First deterministic error line: ...
- Why this is primary cause: ...
- Minimal fix proposal: ...
- Risk: low | medium | high

## Rollback Verification
- pom.xml java.version restored to 21: yes/no
- JDK switched back to 21: yes/no
- `mvn clean install` on JDK21: pass/fail
```

---

## 11) Acceptance Criteria

Only `GO` when all true:
- Gate A/B/C 全通過
- `.upgrade/` 證據完整
- 無隱含人工步驟
- 失敗時有完整 NO-GO 報告

若任一未滿足，結果一律 `NO-GO`。
