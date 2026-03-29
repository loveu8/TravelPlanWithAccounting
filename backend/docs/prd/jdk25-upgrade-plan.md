# JDK 21 to JDK 25 Upgrade Plan

## Simplified PRD for Windows 10 + Git Bash

## 0. Preflight Checks (Run Before Anything Else)

This PRD must **fail fast** if Java or Maven is not available in the current Windows 10 Git Bash session.

Before any migration step, first verify:

* `java` exists on `PATH`
* `mvn` exists on `PATH`
* the active Java executable path
* the active Java home
* the active install root
* the active Maven executable path
* the active Maven home
* `java -version`
* `mvn -version`

If any of these checks fail, stop immediately and fix the local shell environment before continuing.

If pasting directly into an interactive Git Bash session, use the function-wrapped blocks below.
They return failure without closing the terminal session.

### 0.1 Verify Java and Maven exist

```bash
preflight_exists() {
  command -v java >/dev/null 2>&1 || { echo "java not found on PATH"; return 1; }
  command -v mvn  >/dev/null 2>&1 || { echo "mvn not found on PATH"; return 1; }
}

preflight_exists
```

### 0.2 Detect active runtime paths

```bash
detect_active_paths() {
  command -v java >/dev/null 2>&1 || { echo "java not found on PATH"; return 1; }
  command -v mvn  >/dev/null 2>&1 || { echo "mvn not found on PATH"; return 1; }

  JAVA_BIN_UNIX="$(readlink -f "$(command -v java)")" || return 1
  JAVA_BIN_WIN="$(cygpath -w "$JAVA_BIN_UNIX")" || return 1
  JAVA_HOME_UNIX="$(dirname "$(dirname "$JAVA_BIN_UNIX")")" || return 1
  JAVA_HOME_WIN="$(cygpath -w "$JAVA_HOME_UNIX")" || return 1
  INSTALL_ROOT_UNIX="$(dirname "$JAVA_HOME_UNIX")" || return 1
  INSTALL_ROOT_WIN="$(cygpath -w "$INSTALL_ROOT_UNIX")" || return 1

  MVN_BIN_UNIX="$(readlink -f "$(command -v mvn)")" || return 1
  MVN_BIN_WIN="$(cygpath -w "$MVN_BIN_UNIX")" || return 1
  MAVEN_HOME_WIN="$(mvn -version 2>&1 | sed -n 's/^Maven home: //p' | tr -d '\r')"

  echo "Active Java executable: $JAVA_BIN_WIN"
  echo "Active Java home: $JAVA_HOME_WIN"
  echo "Active install root: $INSTALL_ROOT_WIN"
  echo "Active Maven executable: $MVN_BIN_WIN"
  echo "Active Maven home: $MAVEN_HOME_WIN"
}

detect_active_paths
```

### 0.3 Confirm runtime versions

```bash
confirm_runtime_versions() {
  java -version || return 1
  mvn -version || return 1
}

confirm_runtime_versions
```

### 0.4 Current confirmed baseline on this machine

The current tested baseline on this machine is:

* Active Java executable: `D:\project\java\microsoft-jdk-21.0.10-windows-x64\bin\java.exe`
* Active Java home: `D:\project\java\microsoft-jdk-21.0.10-windows-x64`
* Active install root: `D:\project\java`
* Active Maven executable: `D:\project\java\apache-maven-3.9.14\bin\mvn`
* Active Maven home: `D:\project\java\apache-maven-3.9.14`
* Java version: `21.0.10`
* Maven version: `3.9.14`

This baseline was verified successfully in the current Windows 10 + Git Bash environment.

### 0.5 Detect candidate installation directories (Approach B)

If candidate JDK / Maven versions need to be discovered dynamically, derive the installation roots from the active tools and scan nearby directories.

This follows the same path derivation logic shown in **Section 0.2**:

* `java` executable → `JAVA_HOME`
* `JAVA_HOME` → install root

```bash
detect_candidate_installations() {
  command -v java >/dev/null 2>&1 || { echo "java not found on PATH"; return 1; }
  command -v mvn  >/dev/null 2>&1 || { echo "mvn not found on PATH"; return 1; }

  JAVA_BIN_UNIX="$(readlink -f "$(command -v java)")" || return 1
  JAVA_HOME_UNIX="$(dirname "$(dirname "$JAVA_BIN_UNIX")")" || return 1
  JAVA_INSTALL_ROOT_UNIX="$(dirname "$JAVA_HOME_UNIX")" || return 1

  MAVEN_HOME_WIN="$(mvn -version 2>&1 | sed -n 's/^Maven home: //p' | tr -d '\r')"
  MAVEN_HOME_UNIX="$(cygpath -u "$MAVEN_HOME_WIN")" || return 1
  MAVEN_INSTALL_ROOT_UNIX="$(dirname "$MAVEN_HOME_UNIX")" || return 1

  printf '%s\n%s\n' "$JAVA_INSTALL_ROOT_UNIX" "$MAVEN_INSTALL_ROOT_UNIX" | sort -u | while read -r ROOT; do
    echo "Scanning install root: $(cygpath -w "$ROOT")"
    find "$ROOT" -maxdepth 1 -type d \( \
      -name 'microsoft-jdk-*' -o \
      -name 'apache-maven-*' \
    \) | sort -V | while read -r p; do
      echo "  $(cygpath -w "$p")"
    done
  done
}

detect_candidate_installations
```

This candidate scan is for discovery only.
Only the **active paths detected from the current shell** should be treated as the execution baseline.

---

## 1. Objective

This PRD defines a **minimal-risk upgrade path** for moving the backend project from **JDK 21** to **JDK 25**.

The primary goal is:

* make the upgrade succeed first
* keep problem attribution simple
* avoid introducing extra moving parts during the first migration pass

This PRD is intentionally optimized for:

* **Windows 10**
* **Git Bash**
* **local Maven build/test first**
* **Docker migration later**

This PRD does **not** treat Maven Wrapper repair, Maven Toolchains adoption, or broad dependency cleanup as mandatory first-pass upgrade steps. Those are considered **post-upgrade hardening tasks**, unless they are required by an actual failure during migration.

---

## 2. Current Repository Baseline

Current repository findings from the latest PRD snapshot are as follows. 

### 2.1 Build and runtime baseline

* Build tool: Maven
* Java version in `pom.xml`: `21`
* Spring Boot parent version: `4.0.4`
* `Dockerfile` currently uses Temurin 21 runtime images
* `build.sh` and `build.bat` currently use Maven + Temurin 21
* Maven Wrapper is incomplete because `.mvn/wrapper/maven-wrapper.properties` is missing
* The repository does not explicitly configure:

  * `maven-compiler-plugin`
  * `maven-toolchains-plugin`
  * any Maven toolchain file
  * explicit `maven.compiler.release`, `source`, or `target`

### 2.2 Version-sensitive dependencies to observe

These current dependency versions should be treated as **watch items**, not automatic first-pass upgrade targets. 

* `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1`
* `com.fasterxml.jackson.datatype:jackson-datatype-hibernate7:2.21.0`
* `io.jsonwebtoken:jjwt-*:0.13.0`
* `org.apache.httpcomponents:httpclient:4.5.14`

These should be **revalidated during testing**, but should not all be changed at the same time unless a failure proves they need to move.

---

## 3. Local Environment Baseline (Windows 10 + Git Bash)

### 3.1 Source-of-truth policy

This PRD does **not** hardcode versioned JDK or Maven directory names as execution baselines.

Instead, before any upgrade step:

* detect the **active** Java and Maven paths from the current Git Bash session
* derive the local installation root from the detected active paths
* scan that installation root for candidate JDK and Maven directories when needed

Only the **detected active paths** should be treated as the execution baseline.

### 3.2 Local environment policy

For the first-pass migration:

* keep **JDK 21** as the known-good baseline
* switch to **JDK 25** only when explicitly testing migration
* keep Maven fixed to the intended local Maven installation
* do not assume Docker follows local `JAVA_HOME`
* after every JDK switch, rerun the **Section 0 Preflight Checks**

### 3.3 Command consistency rule

Across all migration phases, use the **same Maven command style, same profile, same test scope, and same required environment inputs**.

Examples:

* if baseline uses `mvn clean install` and `mvn test`, keep using those
* if baseline requires `-DskipITs`, use the same flag in later phases
* if startup verification depends on a specific env file or profile, use the same one in each comparable phase

Do not compare results across phases if the command scope changed.

---

## 4. Minimal-Risk Upgrade Principles

This PRD follows these rules:

1. **One main control plane only**
   For first-pass migration, use **Git Bash `JAVA_HOME` switching** as the main control plane.
   Do not introduce Maven Toolchains as another active control plane during the initial migration unless necessary.

2. **Baseline first**
   If JDK 21 baseline is not green, do not diagnose JDK 25 yet.

3. **Smallest useful change first**
   Do not change Docker, Wrapper, Toolchains, and multiple dependencies at the same time.

4. **Local build/test before Docker**
   First prove the project builds and tests on local JDK 25.
   Docker migration comes later.

5. **Dependency upgrades are not automatic**
   First revalidate. Only change a dependency when a failure or compatibility reason requires it.

---

## 5. Required Report Back to AI

After completing the **Preflight Checks in Section 0**, report these values back exactly:

* Active Java executable
* Active Java home
* Active install root
* Active Maven executable
* Active Maven home
* `java -version` output
* `mvn -version` output

AI must use these values as the current execution baseline.

If the detected Java or Maven path is not the intended one, do not continue until the shell environment is corrected.

---

## 6. Recommended Upgrade Order

### Phase 0: Confirm preflight baseline

Re-run the **Section 0 Preflight Checks** and confirm:

* active Java path
* active Java home
* active install root
* active Maven path
* active Maven home
* `java -version`
* `mvn -version`

This phase is required before everything else.

---

### Phase 1: Establish JDK 21 baseline

Without changing any project files, run the agreed baseline commands.

Example:

```bash
mvn clean install
mvn test
```

Also verify current local startup expectations as applicable:

* application startup
* database connection
* Swagger/OpenAPI access
* auth/JWT flow

**Gate:**
If Phase 1 is not green, do not continue.
Fix JDK 21 baseline first.

This gate is required so that later JDK 25 failures can be attributed to the upgrade instead of pre-existing issues.

---

### Phase 2: Patch-upgrade Spring Boot only

After JDK 21 baseline is green:

* upgrade Spring Boot from `4.0.4` to the latest `4.0.x` patch

To find the latest available `4.0.x` patch, check:

```text
https://spring.io/projects/spring-boot#learn
https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent
```

Do **not** do these yet:

* do not change Java target to 25
* do not change Docker images
* do not introduce Maven Toolchains
* do not repair Maven Wrapper unless it blocks execution
* do not broadly refresh dependencies

Then rerun the same agreed baseline commands, and also generate the effective POM.

Use a path **outside `target/`** so `mvn clean` does not delete it:

```bash
mvn -q help:effective-pom -Doutput=effective-pom.xml
mvn clean install
mvn test
```

Purpose:

* isolate framework patch risk from JDK migration risk
* confirm inherited managed versions changed as expected after the Spring Boot patch upgrade

At this stage, pay special attention to managed versions that may shift through Spring Boot patching, such as Lombok, AspectJ, PostgreSQL driver, and related managed dependencies.

**Gate:**
If `mvn test` is not green after the Spring Boot patch upgrade, diagnose and fix before proceeding.
Do **not** attribute the failure to JDK 25 — the shell is still on JDK 21 at this stage.

**Note:**
Do not commit `effective-pom.xml` into the repository.
Add it to `.gitignore` if you keep using this output path.

---

### Phase 3: Switch Git Bash to JDK 25

After Phase 2 is green, switch only the local shell environment to JDK 25.

Use the **detected install root approach**, not a hardcoded version path:

```bash
switch_to_jdk25() {
  command -v java >/dev/null 2>&1 || { echo "java not found on PATH"; return 1; }
  command -v mvn  >/dev/null 2>&1 || { echo "mvn not found on PATH"; return 1; }

  JAVA_BIN_UNIX="$(readlink -f "$(command -v java)")" || return 1
  JAVA_HOME_UNIX="$(dirname "$(dirname "$JAVA_BIN_UNIX")")" || return 1
  INSTALL_ROOT_UNIX="$(dirname "$JAVA_HOME_UNIX")" || return 1

  JDK25_HOME_UNIX="$(find "$INSTALL_ROOT_UNIX" -maxdepth 1 -type d -name 'microsoft-jdk-25*' | sort -V | tail -n 1)"
  [ -n "$JDK25_HOME_UNIX" ] || { echo "JDK 25 not found under detected install root"; return 1; }

  export JAVA_HOME="$JDK25_HOME_UNIX"
  export PATH="$JAVA_HOME/bin:$PATH"
  hash -r

  echo "Using JAVA_HOME=$(cygpath -w "$JAVA_HOME")"
  java -version || return 1
  mvn -version || return 1
}

switch_to_jdk25
```

Then rerun the **Section 0 Preflight Checks**.

Purpose:

* confirm the local shell is truly using JDK 25
* keep the control mechanism simple
* avoid hardcoding versioned paths in the PRD
* reuse the already active Maven installation instead of duplicating Maven paths in `PATH`

---

### Phase 3.5: Optional runtime-first transition check

This phase is **optional**, but useful when you want cleaner failure attribution.

At this point:

* shell is already on JDK 25
* `pom.xml` still keeps `java.version=21`
* the project was already built successfully in Phase 2

If practical, run the **artifact built in Phase 2 under JDK 21** on a **JDK 25 JVM** before changing the compiler target.

Example patterns:

```bash
java -jar target/<your-artifact>.jar
```

or use the project’s normal startup command under the JDK 25 shell.

The artifact used here is the one built in **Phase 2 under JDK 21**.
The purpose is to run a **JDK 21-compiled binary** on a **JDK 25 JVM**, which isolates runtime compatibility from compiler target changes.

Interpretation:

* if this fails before you change `java.version`, the problem is more likely in **runtime behavior**, dependency compatibility, startup assumptions, or environment integration
* not yet in the Java compiler target change itself

Important:

* this is only useful if your project has a practical local startup path
* if your application depends on external infrastructure and this check is hard to run cleanly, skip it rather than forcing complexity

---

### Phase 4: Change `pom.xml` Java target to 25

After local shell is confirmed on JDK 25:

* change `<java.version>21</java.version>` to `25`

At this stage:

* keep Docker unchanged
* keep Wrapper unchanged unless blocked
* keep Toolchains disabled
* keep dependency churn minimal

Then run the same agreed baseline commands plus effective POM generation.

Again, use a path **outside `target/`**:

```bash
mvn -q help:effective-pom -Doutput=effective-pom.xml
mvn clean install
mvn test
```

Purpose:

* prove the project can build and test locally on JDK 25 with the fewest moving parts

**Gate:**
If `mvn test` is not green, diagnose and fix before proceeding.
Do **not** run **Phase 5 diagnostics** while builds are still failing.

**Note:**
Do not commit `effective-pom.xml` into the repository.
Add it to `.gitignore` if needed.

---

### Phase 5: Run focused diagnostics only after local JDK 25 build/test is green

After Phase 4 succeeds, run static migration diagnostics.

#### Preferred first-pass scan

```bash
jdeps -jdkinternals target/classes
jdeprscan --release 25 target/classes
```

#### Optional broader scan on fat JAR

```bash
jdeps -jdkinternals --ignore-missing-deps target/backend-service-0.0.1-SNAPSHOT.jar
jdeprscan --release 25 target/backend-service-0.0.1-SNAPSHOT.jar
```

#### Optional broader dependency scan

```bash
jdeps --multi-release 25 --recursive target/backend-service-0.0.1-SNAPSHOT.jar
```

This PRD intentionally starts with `target/classes` to reduce noise and make findings easier to interpret.

---

### Phase 6: Audit strong encapsulation workarounds

After local JDK 25 build/test is green, audit runtime flags:

```bash
grep -R -n -E \
  --include='Dockerfile' \
  --include='build.sh' \
  --include='build.bat' \
  --include='*.properties' \
  --include='*.yml' \
  --include='*.yaml' \
  -- '--add-opens|--add-exports|--add-reads' . 2>/dev/null
```

If any are found:

* document where they are used
* identify what library or runtime path needs them
* verify whether they are still necessary on JDK 25
* do not carry them forward silently

This is a diagnostic phase, not an early migration gate.

---

### Phase 7: Migrate Docker last

Only after local JDK 25 build/test is green.

Before updating image tags, verify the target tags are currently available on the official Docker Hub pages for Eclipse Temurin and Maven:

```text
https://hub.docker.com/_/eclipse-temurin
https://hub.docker.com/_/maven
```

Do **not** assume tag availability. Check directly before changing the files.

For this repository, use the **minimal-change target tags**:

* `Dockerfile` runtime image:

  * `eclipse-temurin:25-jre-alpine`
* `build.sh` / `build.bat` build image:

  * `maven:3.9-eclipse-temurin-25`

This choice keeps the runtime image aligned with the current Alpine-based runtime pattern, while keeping the Maven build image aligned with the project’s current non-Alpine Maven image pattern. The currently available official image pages expose Temurin 25 runtime images and Maven 3.9 + Temurin 25 tags, and your latest PRD baseline confirms the repo is currently on `eclipse-temurin:21-jre-alpine` plus `maven:3.9.9-eclipse-temurin-21`. 

Then verify:

```bash
docker build -t backend-service:jdk25-test .
```

And validate:

* application startup
* database connection
* Swagger/OpenAPI endpoint
* auth/JWT flow
* locale-sensitive behavior

**Gate:**
If Docker build or Docker startup fails here, treat it as a **container / image / environment layer issue first**.
Do **not** immediately change Java target or dependency versions again until the Docker-specific failure is isolated.

This order keeps Docker migration as a separate failure domain.

---

### Phase 8: Dependency adjustments only if failures remain

If failures still exist after Phases 2 to 7, or if a compatibility review clearly shows drift that should be corrected:

* revalidate `springdoc-openapi-starter-webmvc-ui:3.0.1`
* bump to `3.0.2` if the upgrade path confirms it is needed
* revalidate `jjwt`
* revalidate `httpclient`
* revalidate `jackson-datatype-hibernate7`

Only change what is necessary.

Do **not** start with broad dependency churn.

---

## 7. Verification Plan

### 7.1 Before any build or test

Always re-run the **Section 0 Preflight Checks** and confirm the active Java/Maven paths match the intended phase.

### 7.2 Local build verification

For each major phase, use the agreed baseline commands consistently.

Example:

```bash
mvn clean install
mvn test
```

For Java target changes and Spring Boot patch confirmation:

```bash
mvn -q help:effective-pom -Doutput=effective-pom.xml
```

Use a path outside `target/` so the file is preserved after `mvn clean`.

### 7.3 Runtime verification

When relevant, verify:

* application startup
* DB connection
* Swagger/OpenAPI
* auth/JWT
* AOP behavior
* JSON serialization behavior
* outbound HTTP behavior
* locale/date formatting behavior

### 7.4 Success criteria

Upgrade is considered successful when all of the following are true:

* JDK 21 baseline was green before migration
* Spring Boot patch upgrade is green
* local JDK 25 build is green
* local JDK 25 test suite is green
* Docker build and startup are green on JDK 25 images
* no critical `jdeps` / `jdeprscan` findings remain unexplained
* any `--add-opens` / `--add-exports` usage is reviewed and justified

---

## 8. Optional Hardening Tasks (Not First-Pass Requirements)

These tasks are useful, but they should not complicate the first successful migration unless required by an actual blocker.

### 8.1 Maven Wrapper repair

Current repo state shows the Maven Wrapper is incomplete. 

Recommended command:

```bash
mvn wrapper:wrapper -Dmaven=3.9.14
```

Then verify:

* `.mvn/wrapper/maven-wrapper.properties`
* `mvnw`
* `mvnw.cmd`

This is recommended as a cleanup / hardening step after the main upgrade path is stable.

### 8.2 Maven Toolchains adoption

For first-pass migration, Toolchains are **optional**.

They may be adopted later if you want:

* cleaner multi-JDK team workflows
* stronger build reproducibility
* less reliance on shell-based JDK switching

If adopted later, first detect the concrete JDK installation directories from the current machine, then populate `toolchains.xml` with the detected Windows paths.

Example detection commands:

```bash
detect_toolchain_paths() {
  command -v java >/dev/null 2>&1 || { echo "java not found on PATH"; return 1; }

  JAVA_BIN_UNIX="$(readlink -f "$(command -v java)")" || return 1
  JAVA_HOME_UNIX="$(dirname "$(dirname "$JAVA_BIN_UNIX")")" || return 1
  INSTALL_ROOT_UNIX="$(dirname "$JAVA_HOME_UNIX")" || return 1

  JDK21_HOME_UNIX="$(find "$INSTALL_ROOT_UNIX" -maxdepth 1 -type d -name 'microsoft-jdk-21*' | sort -V | tail -n 1)"
  JDK25_HOME_UNIX="$(find "$INSTALL_ROOT_UNIX" -maxdepth 1 -type d -name 'microsoft-jdk-25*' | sort -V | tail -n 1)"

  echo "JDK21_HOME=$(cygpath -w "$JDK21_HOME_UNIX")"
  echo "JDK25_HOME=$(cygpath -w "$JDK25_HOME_UNIX")"
}

detect_toolchain_paths
```

Then use the detected Windows paths in `toolchains.xml`.

Suggested template:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>25</version>
      <vendor>microsoft</vendor>
    </provides>
    <configuration>
      <jdkHome>DETECTED_JDK25_HOME</jdkHome>
    </configuration>
  </toolchain>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
      <vendor>microsoft</vendor>
    </provides>
    <configuration>
      <jdkHome>DETECTED_JDK21_HOME</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

Important:

* do not enable Toolchains and shell-based JDK switching as two active control planes during the first migration pass unless necessary
* if Toolchains are introduced, simplify the process and document which mechanism is authoritative

---

## 9. Things Not to Do During First-Pass Migration

Do **not** do these at the same time unless required:

* large refactoring
* replacing HTTP client libraries
* broad Jackson configuration redesign
* unrelated dependency refreshes
* Toolchains rollout plus Wrapper repair plus Docker migration plus JDK change in one step

The first successful migration should stay focused.

---

## 10. Rollback Plan

Keep the upgrade in a dedicated branch.

Prefer separate commits or PRs for:

1. Spring Boot patch upgrade
2. JDK 25 local migration
3. Docker migration
4. optional hardening tasks

If migration fails, revert in this order:

1. restore `pom.xml` Java target from `25` back to `21`
2. restore Docker runtime image tags from `25` back to `21`
3. restore Dockerized Maven image tags from `25` back to `21`
4. revert Spring Boot patch upgrade only if needed
5. revert optional hardening changes separately if they were introduced

After rollback, rerun:

```bash
mvn clean install
mvn test
```

And verify the normal JDK 21 baseline again.

---

## 11. Final Execution Rule

For this PRD, the real execution order is:

1. complete the Section 0 Preflight Checks
2. confirm JDK 21 baseline is green
3. patch-upgrade Spring Boot only
4. switch Git Bash to JDK 25
5. optionally run runtime-first validation using the existing Phase 2 artifact
6. change `pom.xml` Java target to 25
7. prove local `build / test` works on JDK 25
8. run diagnostics
9. migrate Docker
10. apply optional hardening only after the main path is stable

If step 2 is skipped, later failures are hard to attribute.
If step 7 is not green, do not continue to diagnostics.
If step 9 fails, isolate Docker-specific causes before changing other layers again.
If step 10 is moved too early, the migration becomes more complex than necessary.

---

## 12. Summary of Improvements in This Revision

This revision keeps the simplified migration strategy and incorporates the final stability refinements:

* **Preflight checks are at the top**
* **Java / Maven existence is validated before anything else**
* **function-wrapped fail-fast blocks avoid closing an interactive Git Bash terminal**
* **the tested current-machine baseline is recorded near the top**
* **Approach B candidate detection is integrated into the Preflight section**
* **Section 0.2 exposes the active install root**
* **`MAVEN_HOME_WIN` stripping now removes Windows CR characters**
* **Phase 3 uses `sort -V | tail -n 1` to select the highest JDK 25 version**
* **Phase 3 adds `hash -r` after changing `JAVA_HOME`**
* **Phase 3 no longer duplicates Maven in `PATH`**
* **Phase 4 gate correctly points to Phase 5 diagnostics**
* **Phase 2 uses the correct Spring Boot `#learn` URL**
* **Phase 7 includes concrete recommended Docker image tags**
* **hardcoded versioned JDK/Maven paths are removed from the mainline workflow**
* **the overall strategy remains “upgrade first, harden later”**