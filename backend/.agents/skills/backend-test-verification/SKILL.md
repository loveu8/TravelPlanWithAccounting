---
name: backend-test-verification
description: Perform backend test verification for GitHub PR and CI review after backend code changes by running a deterministic Maven-based validation sequence, classifying results into blocking vs non-blocking findings, handling failed/flaky/skipped/missing-evidence cases explicitly, and producing an auditable evidence-based report.
---

# backend-test-verification

## Role
Act as a **verification-focused backend QA reviewer** for Java/Spring Boot changes.

Do not act as a feature designer or refactor driver in this skill. Focus on verification evidence, risk classification, and honest reporting.

## Objective
Produce a **clear, executable, and auditable** backend verification result that can be used directly in:
- GitHub Pull Request review comments
- CI result interpretation
- pre-merge quality gate discussions

The output must distinguish:
1. What was verified with concrete evidence
2. What failed and why
3. What is still unverified
4. What blocks merge vs what is advisory

## When to Use
Use this skill when **all** of the following are true:
1. The task includes backend source/config/test changes under `backend/`.
2. Verification status is needed before handoff, PR approval, or merge.
3. Maven-based checks are expected or already referenced by the team workflow.

Typical triggers:
- "Please verify backend tests before PR"
- "Summarize CI failures for this backend change"
- "Run minimal backend checks and report gaps"

## When Not to Use
Do **not** use this skill when:
1. Change is docs-only or non-backend-only and no backend behavior is affected.
2. The user asks for feature implementation/refactor without asking for verification.
3. No executable backend environment is available and user only wants planning.

If uncertain whether backend behavior changed, mark `REQUIRES CONFIRMATION` before running broad checks.

## Required Inputs
Collect and confirm these inputs before execution:

1. **Change Scope Evidence**
   - Touched files under `backend/`
   - Whether changes affect application code, tests, build config, or runtime config

2. **Execution Context**
   - Working directory: `backend/`
   - Available commands: `./mvnw` preferred, fallback `mvn`
   - Environment constraints (network, DB, credentials, service dependencies)

3. **Verification Target**
   - Required confidence level:
     - Minimal smoke verification
     - Standard pre-PR verification
     - Release-level confidence

If any required input is missing, state what is missing and continue with the smallest safe verifiable scope.

## Verification Workflow (Ordered, Mandatory)
Follow steps in order. Do not skip a step silently.

### Step 1: Preflight and Scope Classification
1. Confirm current repository and `backend/` path.
2. Enumerate changed backend files.
3. Classify impact level:
   - **Level A (Low):** tests/docs/internal comments only
   - **Level B (Medium):** service/controller/repository/business logic
   - **Level C (High):** security/auth, DB schema/migration, shared build config, cross-module behavior
4. Decide minimum command set based on impact level:
   - Level A: targeted verification first
   - Level B: include `test`
   - Level C: include `clean install` unless environment blocks

### Step 2: Select and Execute Smallest Relevant Command First
Execute from `backend/`:
1. targeted command if available/relevant (module/test-specific)
2. `./mvnw test` (or `mvn test` fallback)
3. `./mvnw clean install` (or `mvn clean install`) when warranted by impact/risk

Rules:
- Prefer smallest meaningful command first.
- Escalate only when needed by risk level or unclear failures.
- Record exact command text and outcome for every execution.

### Step 3: Capture Raw Evidence
For each executed command, capture:
1. Exit status
2. Key summary lines (tests run, failures, errors, skipped, build status)
3. Critical failure messages (first actionable root cause)
4. Environment blockers (timeout, missing dependency/service, permission, network)

Never infer pass/fail without command output.

### Step 4: Classify Results
Classify each command result into one of:
- **PASS**: command completed successfully with expected verification signal.
- **FAIL (Blocking)**: deterministic failure that invalidates merge confidence.
- **WARN (Non-blocking)**: verification incomplete due to environment/tooling limitation, not code defect evidence.
- **FLAKY (Needs re-run)**: non-deterministic or inconsistent outcome.
- **SKIPPED (Declared)**: intentionally not run with explicit reason.
- **MISSING EVIDENCE (Blocking)**: required verification claim without executable proof.

### Step 5: Produce Merge Decision Recommendation
Use decision matrix below to provide one recommendation:
1. **BLOCK MERGE**
2. **CONDITIONAL / NEEDS FOLLOW-UP**
3. **READY FOR MERGE (Verification Perspective Only)**

Include rationale mapped to evidence IDs.

## Decision Rules (Verifiable)

### Blocking Conditions
Recommend **BLOCK MERGE** when any of the following is true:
1. `FAIL (Blocking)` exists in required verification scope.
2. Required command(s) were not executed and no valid environment blocker evidence is provided.
3. `MISSING EVIDENCE (Blocking)` exists.
4. Flaky behavior persists after reasonable re-run attempt and impacts changed area.

### Non-Blocking Conditions
Classify as **CONDITIONAL / NEEDS FOLLOW-UP** when:
1. All executed checks pass, but some non-critical checks are `SKIPPED` with explicit reason.
2. Environment limitations prevent full confidence but do not indicate code defect.
3. Flaky behavior appears outside changed scope and is documented with mitigation plan.

### Ready Condition
Recommend **READY FOR MERGE** only when:
1. Required verification commands for the assessed risk level are executed.
2. No blocking failures remain.
3. Any skipped/unverified area is explicitly listed and accepted as non-blocking.

## Handling Special Cases

### Failed
- Identify first actionable failing unit/integration/build step.
- Separate symptom from likely root cause.
- Mark as blocking unless proven unrelated to changed scope.

### Flaky
- Re-run relevant command at least once when feasible.
- If outcomes differ, mark as `FLAKY` and avoid final pass claim.
- Provide re-run evidence and confidence downgrade.

### Skipped
- Skipped checks must include reason + expected impact.
- Distinguish intentional skip from accidental omission.

### Missing Evidence
- If a claim like "tests pass" lacks execution proof, classify as `MISSING EVIDENCE (Blocking)`.
- Require rerun or CI artifact link/log excerpt before clearing.

## Output Format (PR / CI Ready)
Use this exact structure in final report.

```markdown
## Backend Test Verification Report

### 1) Scope
- Changed files reviewed: ...
- Impact level: Level A/B/C
- Required verification scope: ...

### 2) Commands Executed
- [STATUS] `<command>`
  - Exit code: ...
  - Evidence: ...
  - Notes: ...

### 3) Result Classification
- Blocking:
  - ...
- Non-blocking:
  - ...
- Unverified:
  - ...

### 4) Decision
- Recommendation: BLOCK MERGE | CONDITIONAL / NEEDS FOLLOW-UP | READY FOR MERGE
- Rationale:
  - ...

### 5) Required Follow-ups
- [Owner] [Action] [Why]
```

Status tag mapping:
- `PASS`
- `FAIL (Blocking)`
- `WARN (Non-blocking)`
- `FLAKY (Needs re-run)`
- `SKIPPED (Declared)`
- `MISSING EVIDENCE (Blocking)`

## Guardrails (Must Follow)
1. Never claim success for commands not executed.
2. Never mix blocking and non-blocking findings in the same bullet.
3. Never hide skipped checks; list them in `Unverified`.
4. Never treat flaky as pass.
5. Never assume CI/local parity; state environment context explicitly.
6. Prefer evidence-backed statements over interpretation.
7. If uncertain, mark `REQUIRES CONFIRMATION` instead of guessing.

## Minimum Completion Checklist
A run is complete only if all items are satisfied:
- [ ] Verification scope defined from changed files
- [ ] At least one relevant backend command executed (unless blocked, with reason)
- [ ] Every command has status + exit code + evidence
- [ ] Blocking vs non-blocking separated
- [ ] Unverified scope listed explicitly
- [ ] Final recommendation provided with rationale
