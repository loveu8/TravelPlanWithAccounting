---
name: create-prd
description: Use this skill when backend requirements are ambiguous before implementation. Convert user intent into a structured, testable backend PRD with explicit scope, non-goals, API/data/auth impact, assumptions, risks, and open questions.
---

# create-prd

## Description
A requirement-clarification skill for backend work. It turns ambiguous requests into a reusable backend PRD draft that implementation, review, and testing can execute against.

## Purpose
- Clarify backend problem statements before coding starts.
- Separate confirmed requirements from assumptions and speculative ideas.
- Produce a PRD draft with explicit scope, boundaries, and testable acceptance criteria.
- Surface unknowns and high-risk impacts early (`REQUIRES CONFIRMATION`).

## Role
This skill **is**:
- PRD drafting
- requirement clarification
- scope and risk definition

This skill **is not**:
- implementation
- refactor planning
- code review
- architecture redesign

## When to Use
Use when at least one of these is true:
- New backend feature request with incomplete details.
- Large backend change spans multiple modules or layers.
- API behavior, data behavior, or authorization behavior is unclear.
- Acceptance criteria are missing, vague, or not testable.
- Stakeholders disagree on expected backend outcomes.

## When Not to Use
Do not use when:
- Requirements are already explicit and implementation-ready.
- Task is a small bugfix with clear expected behavior.
- Task is pure refactor/review with no requirement ambiguity.
- You only need architecture scanning (`scan-project-backend`) or implementation planning (`plan-work`).

## Trigger Conditions
Trigger this skill if request text includes signals like:
- "需求還不清楚", "先整理需求", "先寫 PRD"
- "新功能", "跨模組", "先定義 API/data 行為"
- "驗收標準不明確", "先釐清 scope / non-goals"

## Required Inputs
Collect these inputs before drafting:
1. **User intent and business outcome**
   - User goal, expected value, target actor/stakeholder.
2. **Repository rules and constraints**
   - `AGENTS.md`
   - `backend/AGENTS.md`
3. **Backend architecture context**
   - `backend/docs/backend-project-architecture.md` (preferred architecture snapshot)
   - Relevant module/package files if request is module-specific.
4. **Existing contract/constraint context** (if available)
   - Current API/DTO behavior
   - DB/schema/migration constraints
   - Validation, auth, audit, permission constraints
5. **Known limits and non-goals**
   - Explicit exclusions from user/stakeholder.

If any required input is missing, continue with PRD drafting but explicitly mark unknown parts using:
- `OPEN QUESTION`
- `ASSUMPTION`
- `REQUIRES CONFIRMATION`

## Discovery Rules
Before writing final PRD sections, run this sequence:
1. **Normalize request**
   - Rewrite user request into one-sentence problem statement.
2. **Separate requirement types**
   - Label each key statement as `PROBLEM`, `OUTCOME`, or `PROPOSED SOLUTION`.
   - Do not treat `PROPOSED SOLUTION` as confirmed requirement by default.
3. **Detect ambiguity**
   - Find missing actor, trigger, input, output, error behavior, and constraints.
4. **Detect conflicts**
   - Check for contradictions between user intent, existing constraints, and architecture snapshot.
5. **Classify uncertainty**
   - Unknown but plausible = `ASSUMPTION`
   - External dependency not controlled here = `DEPENDENCY`
   - Potential negative impact = `RISK`
   - Cannot proceed safely without answer = `REQUIRES CONFIRMATION`

## Scope Rules
Define clear boundaries in PRD:
- `In Scope`: exact behaviors/outcomes delivered by this PRD.
- `Out of Scope`: explicitly excluded behaviors and adjacent requests.
- `Affected Boundaries`:
  - Controller/API boundary
  - Service/business boundary
  - Repository/data access boundary
  - External integration boundary

Anti-scope-creep rules:
- Every in-scope item must map to at least one acceptance criterion.
- Any item without acceptance criteria must be moved to `OPEN QUESTION` or `OUT OF SCOPE`.
- Do not add architecture redesign goals unless user explicitly requests them.

## PRD Sections (Output Structure)
Output must include all sections below in this order:
1. `Summary`
2. `Problem Statement`
3. `Desired Backend Outcomes`
4. `Scope (In Scope)`
5. `Non-Goals (Out of Scope)`
6. `Affected Backend Boundaries`
7. `User Scenarios`
8. `API Behavior Definition`
9. `Data / Schema Impact`
10. `Validation / Auth / Permission / Audit Impact`
11. `Acceptance Criteria (Testable)`
12. `Assumptions`
13. `Dependencies`
14. `Risks`
15. `Open Questions`
16. `Requires Confirmation`

## API / Data / Auth Impact Rules
For each impact section, explicitly label status:
- `Confirmed`
- `Potential`
- `Unknown`

Mandatory checks:
- API contract impact: endpoint, request/response shape, status/error behavior, backward compatibility.
- Data impact: entity/table/schema/index/migration expectation, write/read path impact.
- Validation impact: input rules, field constraints, error signaling.
- Auth/permission impact: who can call, role/policy checks, token/session expectations.
- Audit/logging impact: whether action must be traceable.

If breaking change is possible and unconfirmed, add to `REQUIRES CONFIRMATION`.

## Acceptance Criteria Rules
Each acceptance criterion must be:
- Observable (can be checked via API response, DB state, log/event, or explicit output).
- Deterministic (clear expected result).
- Bounded (clear precondition and trigger).
- Testable (maps to at least one test idea).

Format each criterion as:
- `AC-<id>`
- `Given` (precondition)
- `When` (action/trigger)
- `Then` (expected result)

Coverage minimum:
- Success path
- Failure path
- Validation failure
- Authorization/permission behavior (if endpoint is protected)
- At least one edge case

Reject criteria that are vague (example of invalid wording: "system should work correctly").

## Assumptions / Risks / Open Questions Handling
Use strict definitions:
- `ASSUMPTION`: temporary working statement used due to missing confirmation.
- `DEPENDENCY`: external team/system/policy/date needed to deliver outcome.
- `RISK`: uncertainty that may cause failure, delay, security issue, or contract break.
- `OPEN QUESTION`: question that improves PRD quality but does not fully block drafting.
- `REQUIRES CONFIRMATION`: blocker or high-risk item that must be answered before safe implementation.
- `OUT OF SCOPE`: explicitly not delivered in this PRD.

Do not hide uncertainty inside narrative text; list it in explicit sections.

## Output Contract
The final PRD draft must:
- Follow all section headings from `PRD Sections`.
- Use explicit labels for unknown/high-risk items.
- Include testable acceptance criteria IDs (`AC-001`, `AC-002`, ...).
- Be implementation-ready at requirement level, without implementation design details.
- Be reusable by downstream skills (`plan-work`, implementation, review, test verification).

## Boundaries
- Do not write code.
- Do not provide low-level implementation design unless user explicitly asks in a separate task.
- Do not convert unverified assumptions into confirmed facts.
- Do not smuggle architecture redesign into requirement text.
- Do not propose API/schema/security breaking changes without `REQUIRES CONFIRMATION`.
- Do not ignore repository constraints in `AGENTS.md`, `backend/AGENTS.md`, and architecture snapshot.

## Verification Checklist
Before finalizing PRD, verify:
1. Scope/non-goals do not conflict.
2. Each in-scope item maps to scenario + acceptance criteria.
3. Acceptance criteria are testable and cover success/failure/validation/auth/edge cases.
4. API/data/auth impacts are explicitly marked (`Confirmed/Potential/Unknown`).
5. Unknowns are surfaced in `Open Questions` or `Requires Confirmation`.
6. No unconfirmed breaking change is presented as decided.
7. PRD stays at requirement level (not implementation planning).

## Failure / Uncertainty Handling
If information is insufficient:
1. Continue with best-effort PRD draft.
2. Mark every unknown using required labels.
3. Add focused follow-up questions under `Open Questions`.
4. Promote blockers/high-risk unknowns to `Requires Confirmation`.
5. State what cannot be validated from current repository evidence.

## What Not to Do
- Do not skip problem clarification and jump to solution details.
- Do not collapse scope/non-goals into a single vague paragraph.
- Do not output acceptance criteria without measurable expected results.
- Do not omit API/data/auth impact analysis.
- Do not hide major risks behind generic wording.
- Do not treat this skill as implementation planning or code task execution.
