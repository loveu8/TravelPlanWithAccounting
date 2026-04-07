---
name: refactor-backend
description: Backend refactoring workflow for improving structure, readability, maintainability, duplication, module boundaries, and testability without changing externally observable behavior. Use when scope is bounded and behavior-preservation can be verified; do not use for feature implementation, API/schema/security redesign, dependency upgrades, or broad system redesign.
---

# refactor-backend

## Description
Execute safe backend refactors that improve internal code quality while preserving public contracts and runtime semantics.

## Purpose
- Improve internal structure, readability, maintainability, and testability.
- Remove duplication and clarify module boundaries.
- Keep changes bounded, reviewable, and reversible.
- Preserve externally observable behavior.

## When to Use
Use this skill when the request is primarily about:
- technical debt cleanup
- code structure cleanup
- duplication removal
- module/package re-organization inside a bounded area
- readability improvement
- testability improvement

## When Not to Use
Do **not** use this skill as the primary workflow when the request is about:
- feature implementation or requirement completion
- changing API contract, validation rules, auth/security behavior, DB schema, event contract, or business semantics
- dependency/framework upgrade
- cross-system redesign or architecture rewrite
- unresolved product requirements

Use another skill/workflow first when discovery, planning, or requirement shaping is the main task.

## Trigger Conditions
Trigger this skill only if all are true:
1. Refactor intent is explicit (or strongly implied) and feature intent is absent.
2. Scope can be bounded to specific modules/packages/files.
3. Behavior-preservation invariants can be listed.
4. A validation path exists (tests/checks/manual verification notes).

If any condition fails, mark `REQUIRES CONFIRMATION` before changing code.

## Inputs
Required inputs:
- Refactor goal (what to improve internally).
- Scope boundaries (allowed files/modules/packages).
- Non-functional invariants (what must remain unchanged).
- Public contracts to preserve (API shapes, DTO fields, error mapping, events, DB-facing assumptions).
- Existing architecture/layering constraints.
- Validation plan for touched areas.
- Explicit non-goals and forbidden changes.

Optional but recommended:
- Known pain points / code smells.
- Related existing tests and known test gaps.
- Risk notes from prior incidents.

If required inputs are missing, do not guess. Mark `REQUIRES CONFIRMATION` and list the missing items.

## Preconditions
Before editing:
1. Identify touched files/modules and confirm they are in scope.
2. Identify preserved contracts and behavior invariants.
3. Identify forbidden changes (out-of-scope items).
4. Identify closest existing tests/checks for touched paths.
5. Confirm baseline architecture patterns in the target module.

If current behavior is unclear or cannot be confirmed, mark `BLOCKED` or `RISKY` before refactoring.

## Refactor Rules
- Keep refactor-only intent. Do not add feature behavior.
- Use incremental, low-risk steps.
- Keep each change logically atomic and easy to rollback.
- Prefer existing project patterns over introducing new abstractions.
- Preserve layering and module boundaries unless boundary cleanup is explicitly in scope.
- Avoid mixed-intent edits (e.g., rename + logic change + new behavior in one step).

### Incremental / Small-Step Policy
- A small step changes one concern at a time (example: extract method, deduplicate helper, move class within same layer).
- Split work into batches when refactor crosses multiple modules or concerns.
- Do not “while here” expand scope to unrelated cleanup.
- If rename-only refactor is needed, keep it separate from semantic logic changes.

## Invariants (Must Preserve)
Treat the following as unchanged unless explicitly approved:
- API routes, request/response fields, status codes, and documented error contracts.
- Validation outcomes and exception-to-response mapping.
- AuthN/AuthZ checks, permission boundaries, and security-sensitive flows.
- DB observable behavior (writes/reads, transaction boundaries, query semantics, migration/schema assumptions).
- Event publishing/consumption contracts and side-effect triggers.
- Runtime semantics visible to callers (success/failure conditions, retry semantics, idempotency expectations).

### Public Contract Definition
Public contract includes:
- HTTP API signatures and payload schemas.
- External message/event schemas.
- Persisted data expectations relied on by other components.
- Stable service interfaces consumed across module boundaries.

### Acceptable Internal Cleanup
Allowed without extra approval when invariants remain preserved:
- method extraction/inlining
- internal class decomposition
- duplication consolidation
- package/file moves within existing architecture boundaries
- naming cleanup limited to internal/private scope
- test-only refactor that preserves test intent

## Steps
1. **Confirm scope and invariants**
   - Write explicit in-scope modules/files.
   - Write preserved contracts/invariants and non-goals.
2. **Map impacted code and patterns**
   - Locate entry points, call paths, and integration boundaries.
   - Capture existing local patterns to follow.
3. **Design minimal refactor slices**
   - Break work into small logical steps.
   - Define expected no-behavior-change outcome per slice.
4. **Apply one slice at a time**
   - Keep each edit focused.
   - Avoid unrelated cleanup.
5. **Run targeted validation after each meaningful slice**
   - Execute nearest tests/checks for touched code paths.
   - Record pass/fail and uncovered gaps.
6. **Run final verification for touched scope**
   - Re-run key checks to confirm no drift.
   - Compare results against stated invariants.
7. **Publish preservation and risk notes**
   - Summarize what was preserved, what was not fully verified, and why.

## Verification
### Targeted Validation Rules
- Start with tests/checks closest to touched files/modules.
- Add integration-level checks if refactor crosses layer boundaries.
- Prefer existing tests; add minimal focused tests only when needed to verify preservation.

### Minimum Verification Output
Report:
- commands actually run
- which touched areas each command validates
- results (pass/fail)
- unverified invariants and reason

If validation cannot be executed, mark `BLOCKED` and state exact cause.

## Output Contract
Provide outputs in a review-ready format:
1. **Refactor Summary**
   - goal, scope, non-goals
2. **Files Changed**
   - touched files grouped by concern/slice
3. **Preserved Invariants**
   - explicit checklist of preserved contracts/behaviors
4. **Validation Results**
   - commands, outcomes, coverage notes, gaps
5. **Risk & Follow-up**
   - residual risks, assumptions, unresolved items
6. **Escalations**
   - items marked `REQUIRES CONFIRMATION`, `RISKY`, `BLOCKED`, or `OUT OF SCOPE`

## Boundaries
- Do not implement new features.
- Do not change public contracts without explicit confirmation.
- Do not modify schema/migrations/auth/security behavior in this workflow.
- Do not add/upgrade dependencies as part of routine refactor.
- Do not perform unrelated bug fixes or repository-wide naming/style rewrites.
- Do not introduce large abstractions solely for aesthetic cleanup.
- Do not replace established patterns without evidence and confirmation.

## Failure / Uncertainty Handling
Use explicit status tags:
- `REQUIRES CONFIRMATION`: missing critical inputs or possible contract/behavior impact.
- `BLOCKED`: cannot proceed due to missing code context, missing tests, failing baseline, or execution constraints.
- `RISKY`: refactor may cause behavior drift or touches fragile paths with weak coverage.
- `OUT OF SCOPE`: requested change is feature/design/security/schema/dependency work outside this skill.

When any tag is used:
1. state trigger condition
2. state impact if continued without confirmation
3. propose the smallest safe next step

## What Not to Do
- Do not treat refactor as a hidden redesign.
- Do not assume behavior preservation without evidence.
- Do not claim validation passed unless commands were executed.
- Do not silently continue when contract-impact risk appears.
- Do not broaden scope because “the code nearby is messy”.
