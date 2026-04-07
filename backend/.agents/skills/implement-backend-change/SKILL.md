---
name: implement-backend-change
description: Implement approved backend code changes in existing Spring Boot modules with minimal safe edits. Use only after task scope and acceptance criteria are clear. Reuse current controller/service/repository/dto/mapper/config patterns, preserve API contracts unless explicitly approved, run relevant backend verification commands, and report evidence, risks, and unresolved items.
---

# implement-backend-change

## Role
Act as a **backend implementation executor**.

This skill is for writing and updating backend code only. It is **not** a scan/discovery skill, architecture redesign skill, or generic review-only skill.

## Purpose
Deliver the approved backend change with the smallest safe patch that:
1. follows existing project patterns,
2. avoids silent behavior drift,
3. includes executable verification evidence,
4. is ready for PR/review handoff.

## When to Use
Use this skill only when all are true:
1. Task plan is already approved.
2. Scope and acceptance criteria are explicit.
3. Main work is backend code change under `backend/`.
4. Required module boundaries are known (controller/service/repository/dto/mapper/config/test/docs).

## When Not to Use
Do not use this skill when any is true:
1. Requirements are ambiguous or still being shaped.
2. Work is mainly discovery, planning, or architecture evaluation.
3. Change requires unapproved DB schema/migration/auth/dependency/framework redesign.
4. Requested work is mostly broad refactor/cleanup/renaming unrelated to the task.

If these conditions appear, stop and mark `REQUIRES CONFIRMATION`.

## Trigger Conditions
Trigger when user intent matches phrases such as:
- "implement backend change"
- "apply approved backend task"
- "code the backend plan"
- "make minimal backend patch with tests"

Do not trigger for scan-only, PRD-only, or review-only requests.

## Required Inputs
Confirm or derive these before editing:
1. **Approved task context**: ticket/PR/issue link or accepted plan summary.
2. **Exact scope**: affected module(s), endpoint(s), and package/file targets.
3. **Acceptance criteria**: expected behavior and non-goals.
4. **Constraints**: forbidden changes (schema/auth/dependency/architecture), compatibility requirements.
5. **Verification target**: which tests/commands prove completion.
6. **Reference locations**: related tests/docs/message files.

If any required input is missing, do not guess. Mark `REQUIRES CONFIRMATION` with the missing item.

## Preconditions
Before implementation:
1. Read `AGENTS.md` and `backend/AGENTS.md`.
2. Inspect nearby code in affected module to identify established patterns.
3. Confirm this is an implementation task, not redesign.
4. Confirm no blocked high-risk category is implicitly required.

## Implementation Rules

### A. Minimal Safe Change Rules (Mandatory)
1. Change only files directly required by accepted scope.
2. Reuse existing service/util/mapper/validator/exception/message patterns first.
3. Do not add new abstraction/class/helper when an existing one can be extended safely.
4. Do not perform unrelated cleanup, renaming, formatting sweeps, or opportunistic refactors.
5. Split broad edits into small, traceable commits/patch segments when risk grows.

### B. Layering and Structure Rules
1. Keep Controller thin; place business logic in Service.
2. Keep persistence behavior in Repository.
3. Keep DTO mapping in existing mapper/location pattern.
4. Follow current package naming and annotation conventions.

### C. API Contract and Behavior Safety Rules
Treat these as contract-sensitive and never change silently:
1. Endpoint paths/methods/request fields/response fields.
2. Validation rules and error payload semantics.
3. HTTP status codes and exception mapping behavior.
4. Security annotations/rules and auth-required behavior.
5. User-visible message codes/keys/localized text paths.

If change to any contract-sensitive item is required:
- mark `REQUIRES CONFIRMATION`,
- describe old vs new behavior,
- list impacted clients/tests/docs.

### D. Restricted Change Categories
Without explicit approval, do not change:
1. DB schema/migrations/entity persistence contract,
2. auth/token/permission/security flow,
3. dependency versions or new production dependencies,
4. major architecture/module boundaries.

## Steps (Execute in Order)
1. **Confirm Scope and Guardrails**
   - Restate task scope, non-goals, and blocked categories.
   - Mark `REQUIRES CONFIRMATION` if scope conflicts with boundaries.

2. **Locate Impacted Files**
   - Identify concrete files for controller/service/repository/dto/mapper/config/test/docs.
   - Include message/i18n/exception files when behavior or messages change.

3. **Match Existing Patterns**
   - Inspect nearest similar implementation.
   - Reuse same response/error/validation/annotation style.

4. **Implement Smallest Correct Patch**
   - Apply only required logic updates.
   - Keep signatures/contracts stable unless explicitly approved.

5. **Update Dependent Artifacts Required by Behavior Change**
   - Update tests, API annotations, message keys, docs only where behavior changed.
   - Do not add speculative updates.

6. **Run Verification**
   - Run smallest relevant command first.
   - Escalate only if needed for confidence or task requirement.

7. **Report Results for Handoff**
   - Provide output contract exactly (see below).
   - Explicitly list unverified areas and reasons.

## Verification Rules

### What counts as relevant validation
Choose the smallest command that directly validates changed behavior, in this order:
1. Targeted test(s) for touched package/class (if available).
2. `./mvnw test` (or `mvn test` fallback) when code behavior changed.
3. `./mvnw clean install` (or `mvn clean install`) for higher-risk/shared/build-impact changes.

### Minimum requirement
- Run at least one executable backend validation command for backend code changes, unless blocked by environment.

### If validation cannot run
Report explicitly:
1. command not run,
2. blocker reason,
3. exactly what remains unverified.

Never claim pass without command output.

## Output Contract (Required)
Return a structured handoff containing:
1. **Change Summary**: what changed and why.
2. **Files Changed**: explicit path list by layer.
3. **Behavior/Contract Impact**:
   - unchanged, or
   - changed with approved confirmation reference and impact list.
4. **Verification Evidence**:
   - exact command,
   - pass/fail/blocked,
   - key result.
5. **Risks and Follow-ups**:
   - known risks,
   - deferred checks,
   - required next actions.
6. **Assumptions / Unresolved Items**:
   - include `REQUIRES CONFIRMATION`, `BLOCKED`, or `OUT OF SCOPE` tags where applicable.

## Failure / Uncertainty Handling
Use these exact tags:

- `REQUIRES CONFIRMATION`: missing input, contract-impacting decision, or restricted category change needed.
- `BLOCKED`: cannot proceed due to environment/tool/access/runtime blocker.
- `OUT OF SCOPE`: request exceeds approved plan or this skill boundary.

When tagged, include:
1. reason,
2. impact,
3. minimal next action needed to unblock.

## Boundaries
1. Do not turn implementation into redesign.
2. Do not introduce new patterns without evidence existing patterns are insufficient.
3. Do not apply unrelated code cleanup.
4. Do not hide behavior changes behind “minor refactor”.
5. Do not expand scope beyond approved task plan.

## What Not to Do
1. Do not start with broad scan/planning workflows.
2. Do not silently change API contract, validation behavior, or status codes.
3. Do not modify schema/auth/dependencies without explicit approval.
4. Do not claim tests passed without execution evidence.
5. Do not leave uncertainty implicit; tag it explicitly.
