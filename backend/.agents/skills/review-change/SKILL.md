---
name: review-change
description: Review backend-ready patches for correctness, security, maintainability, and testing risk, then provide evidence-based findings with severity and delivery recommendation.
---

# review-change

## Description
A task-specific **backend code review** skill for evaluating a delivery-ready patch/diff.

This skill is for assessment and decision support, not implementation.

## Purpose
- Produce structured, evidence-based review findings for backend changes.
- Grade findings consistently (P0–P3) by impact and delivery risk.
- Provide a clear delivery recommendation for PR / pre-merge / release gate flow.

## What This Skill Does
- Reviews changed backend code and directly impacted files.
- Evaluates four dimensions: correctness, security, maintainability, testing risk.
- Reports actionable findings with file/line evidence and fix direction.
- Explicitly reports missing evidence and uncertainty.

## What This Skill Does **Not** Do
- Does **not** implement, refactor, or rewrite code as primary output.
- Does **not** redesign architecture or rewrite requirements.
- Does **not** request unrelated large-scale cleanup.
- Does **not** convert style preference into high-severity defects.

## When to Use
Use this skill when:
- A backend patch/diff is ready for review.
- A PR is ready for pre-merge confidence check.
- A delivery decision needs structured risk assessment.
- Security/correctness confidence is required before ship.

## When Not to Use
Do **not** use this skill when:
- Requirements are not defined enough to review correctness.
- There is no concrete diff/changed file set.
- The main task is implementation, refactor, scan, or architecture planning.
- The requester expects code changes instead of review findings.

## Trigger Conditions
Trigger this skill if at least one condition is true:
1. User asks for backend review, PR review, readiness check, or delivery gate decision.
2. Backend patch is marked “ready”, “PR ready”, or “before merge”.
3. User asks for severity-based findings (P0–P3) with recommendation.

## Inputs
Required inputs:
- Patch/diff or changed file list.
- Relevant backend conventions/rules (AGENTS.md, project rules, review rules).

Strongly recommended inputs (ask if missing):
- Task requirement and acceptance criteria.
- Impacted scope/modules/endpoints.
- Test evidence (unit/integration/e2e), CI status, failing/passing context.
- Security-sensitive context (auth, permission, token, PII, external trust boundaries).
- Known constraints, non-goals, intentional trade-offs.

## Preconditions
Before reviewing, confirm:
1. Diff is accessible and readable.
2. Review scope is defined (what changed / what not changed).
3. Requirements are available or explicitly marked missing.

If preconditions are not met, do not guess; continue with limited review and mark uncertainty explicitly.

## Review Dimensions

### 1) Correctness
Check whether change behavior matches stated requirement and existing contracts.

Review rules:
- Map each meaningful code change to requirement/acceptance criteria.
- Flag logic mismatches, missing updates, partial fixes, and broken invariants.
- Check edge cases: null/empty, bounds, status transitions, time/date, concurrency/order, idempotency.
- Check data flow: input → validation → domain logic → persistence → response.
- Check contract consistency: DTO/schema/error code/response shape affected by this diff.

If requirement evidence is missing, do not assert defect as fact; mark `REQUIRES CONFIRMATION`.

### 2) Security
Check for security regressions on changed paths and trust boundaries.

Review rules:
- Authentication/authorization: missing/bypassed checks, permission drift, privilege escalation.
- Input validation: unsafe assumptions, insufficient validation, dangerous defaults.
- Sensitive data handling: PII/token/secret leakage in logs, response, exception, telemetry.
- Error handling: stack trace/internal detail exposure, security-relevant error leakage.
- Injection/deserialization risks: SQL/JPQL/command/template/serialization misuse.
- Trust boundary violations: untrusted input crossing into privileged operations.
- Auditability gaps: critical actions missing audit/security event hooks when expected.

If security impact is plausible but not provable from diff alone, mark `RISK NOT VERIFIED` with required evidence.

### 3) Maintainability
Report maintainability issues only when they add concrete future risk.

Report when:
- Change creates hidden coupling or unclear ownership.
- Complex logic lacks structure/comments/tests enough to safely maintain.
- Error handling or branching is duplicated/inconsistent in ways likely to diverge.
- New behavior violates established backend layering/conventions, increasing bug risk.

Do not report as findings:
- Personal naming/style preference without measurable maintenance risk.
- Pure formatting nits unless they block correctness/security/operability.

### 4) Testing Risk & Validation Sufficiency
Assess whether available verification is enough for change risk.

Review rules:
- Identify risk class of changed behavior: low/medium/high impact.
- Check whether tests cover changed logic and key edge/error paths.
- Verify CI/test evidence exists and matches touched scope.
- Flag regression risk when high-risk changes lack targeted verification.
- Escalate severity when high-impact path has no credible validation evidence.

If test/CI evidence is missing, mark `INSUFFICIENT EVIDENCE` and specify required checks.

## Review Steps (Execution Order)
1. **Collect context**: diff, scope, requirements, constraints, rules.
2. **Run correctness review** using explicit requirement mapping.
3. **Run security review** on changed code and trust boundaries.
4. **Run maintainability review** with anti-nitpick filter.
5. **Assess testing/CI sufficiency** and regression risk.
6. **Assign severity** using severity rules below.
7. **Generate output contract** exactly (summary, findings, recommendation, unknowns).

## Severity Rules (P0–P3)
Use impact + exploitability + delivery risk, not tone.

- **P0 – Critical / Stop-Ship**
  - Likely severe security breach, data corruption/loss, or production outage.
  - Immediate fix required before merge/release.

- **P1 – High / Fix Required Before Delivery**
  - High-confidence correctness or security defect with material user/business impact.
  - Should block delivery until fixed or explicitly accepted by owner.

- **P2 – Medium / Fix Soon (Can Ship With Explicit Acknowledgement)**
  - Real risk exists but impact/scope is limited or conditional.
  - Delivery possible only with documented risk acceptance and follow-up plan.

- **P3 – Low / Improvement**
  - Non-blocking issue with small impact; maintainability/clarity improvement.
  - Track in backlog; should not block delivery.

Severity assignment checklist (must be explicit per finding):
- What fails?
- Who/what is impacted?
- Under what condition does it occur?
- Why this severity now?

## Output Contract
Always return in this stable structure:

1. **Review Summary**
   - Scope reviewed.
   - Overall risk statement.
   - Count of findings by severity (P0/P1/P2/P3).

2. **Findings (by severity, highest first)**
   For each finding provide:
   - `ID`
   - `Severity`
   - `Category` (Correctness/Security/Maintainability/Testing)
   - `Evidence` (file + line or exact diff hunk)
   - `Risk` (what can go wrong, impact)
   - `Why this is a problem` (logic/rule basis)
   - `Recommended fix direction` (actionable, no large unrelated redesign)
   - `Confidence` (High/Medium/Low)
   - `Evidence status` (Confirmed / INSUFFICIENT EVIDENCE / REQUIRES CONFIRMATION / RISK NOT VERIFIED)

3. **Delivery Recommendation**
   - One of: `SHIP`, `SHIP WITH ACKNOWLEDGED RISKS`, `FIX REQUIRED BEFORE SHIP`.
   - Include reason tied to highest-severity findings.

4. **Known Unknowns / Missing Evidence**
   - List missing requirements, missing tests/CI, ambiguous intent, or out-of-scope risks.
   - State exactly what evidence is needed to close each gap.

5. **Verification Focus Handoff (for `backend-test-verification`)**
   - Must-run verification focus list tied to highest-risk findings.
   - Include command/check suggestions for each focus (targeted first, then broader if needed).
   - Mark each focus as `Blocking` or `Non-blocking` from review perspective.

## Boundaries
- Every finding must be evidence-based and traceable to changed code.
- Do not present speculation as confirmed defect.
- Do not demand unrelated broad refactor.
- Do not infer correctness defects without requirement/contract basis.
- Do not let style preference dominate review output.
- Do not perform unauthorized code modifications as part of this skill.

## Verification
Before finalizing review output, verify:
1. Every finding has concrete code evidence.
2. Every severity has explicit rationale.
3. Recommendation matches severity distribution.
4. Missing evidence is explicitly labeled.
5. Output is reusable in PR comments and delivery decision flow.

## Failure / Uncertainty Handling
Use these exact labels when evidence is incomplete:
- `REQUIRES CONFIRMATION`: requirement/intent is unclear; cannot assert correctness conclusively.
- `INSUFFICIENT EVIDENCE`: missing required artifacts (tests/CI/logs/design context).
- `RISK NOT VERIFIED`: risk suspected but cannot be validated from available diff/context.

If any potential `P0`/`P1` risk lacks sufficient evidence for a final ship recommendation:
- mark `REQUIRES CONFIRMATION`,
- emit explicit verification focus items for `backend-test-verification`,
- recommend `FIX REQUIRED BEFORE SHIP` until verification evidence closes the gap.

When any label appears:
- State what is unknown.
- State why it blocks certainty.
- State minimal evidence needed to resolve it.

## What Not to Do
- Do not rewrite requirements to create review findings.
- Do not convert this review skill into implementation instructions.
- Do not over-index on style nits.
- Do not hide uncertainty.
- Do not issue delivery recommendation without referencing evidence and severity.
