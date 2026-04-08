---
name: plan-work
description: Backend execution planning skill used after PRD or bug context exists and before coding. Produces an implementation-ready, risk-aware, validation-ready backend plan with explicit checkpoints.
version: 2
owner: backend
---

# plan-work

## Description
`plan-work` is a **backend execution planning skill**.

It converts existing backend requirement context into a minimal, executable plan.
It does **not** implement code, perform full review, write PRD, or run broad architecture redesign.

## Purpose
Use this skill to produce an **implementation-ready backend execution plan** before coding, including:
- task classification (primary + secondary)
- impacted backend modules/layers
- risk level and uncertainty visibility
- minimal ordered skill/action sequence
- validation strategy and concrete commands
- explicit escalation checkpoints

## When to Use
Use when all conditions are true:
1. Backend PRD exists **or** bug context exists.
2. Backend scope is known but execution order is not finalized.
3. Coding has not started, or has not passed the “meaningful implementation” stage.
4. A reusable plan is needed for implementation/review/verification handoff.

## When Not to Use
Do not use when any condition is true:
1. Requirement discovery is still open (use `create-prd` first).
2. Repository/module context is unknown (use `scan-project-backend` first).
3. Task is trivial and isolated (single-file tiny fix with obvious validation).
4. Primary request is implementation, refactor execution, or code review output.
5. Task is non-backend planning.

## Trigger Conditions
Trigger this skill when user intent matches one of these patterns:
- “plan backend work before coding”
- “sequence backend implementation steps”
- “define backend risk/validation checkpoints”
- “convert PRD/bug context into execution plan”

## Inputs
Required inputs:
1. Requirement source: backend PRD or bug context.
2. Backend scope snapshot: affected packages/modules/endpoints/components.
3. Constraints: `AGENTS.md`, `backend/AGENTS.md`, and task-specific limits.

Recommended inputs (must request/flag if missing):
1. Non-goals / out-of-scope items.
2. Known risks and open questions.
3. Test/CI context (available suites, command constraints, runtime limitations).
4. Architecture assumptions already approved.
5. Dependencies on other docs/skills (e.g., `scan-project-backend`, `verify-change`).

If required input is missing:
- Stop sequence expansion.
- Add explicit markers in output:
  - `REQUIRES CONFIRMATION`
  - `OPEN QUESTION`
  - `RISKY ASSUMPTION`

## Task Classification Rules
Classify task type using evidence from PRD/bug context and existing backend behavior.

### Allowed Types
- `feature`
- `bug`
- `security`
- `refactor`
- `docs`

### Classification Method
1. Assign **Primary Type** = type that drives acceptance criteria and core behavior change.
2. Assign **Secondary Type(s)** only if they add required work, not optional cleanup.
3. For each assigned type, cite the evidence in one line.
4. If evidence conflicts, mark `OPEN QUESTION` and do not force classification.

### Sequence/Validation Impact by Type
- `feature`: emphasize integration path, contract alignment, regression checks.
- `bug`: emphasize reproduction path, fix target, non-regression checks.
- `security`: emphasize threat boundary, auth/permission/token checks, stricter escalation.
- `refactor`: emphasize behavior parity checks and scope guardrails.
- `docs`: emphasize documentation correctness and linkage to implemented behavior.

## Impacted Modules and Layer Rules
Identify impact at layer/module level. Use only evidence-backed scope.

### Required Layer Check
Evaluate each area explicitly as: `Impacted`, `Possibly Impacted`, or `Not Impacted`:
- Controller / API route surface
- Service / business logic
- Repository / persistence/query behavior
- DTO / request-response contract
- Mapper / conversion logic
- Validation surface (`jakarta.validation`, custom validators)
- Security/auth/token/permission surface
- Integration/external API/cache/scheduler/config surface

### Scope Integrity
- Include only layers linked to requirement or bug evidence.
- Mark uncertain areas as `Possibly Impacted` + `OPEN QUESTION`.
- Do not convert uncertain areas into mandatory work without confirmation.

## Risk Rules
Assign one overall risk level: `Low` / `Medium` / `High`.

### Baseline Heuristic
- `Low`: localized change, clear behavior, no contract/security/data-flow impact.
- `Medium`: multi-layer coordination or moderate uncertainty.
- `High`: schema/API/security/dependency/external integration changes or high ambiguity.

### Risk Escalators
Any of the following raises risk and requires explicit mention:
- auth/permission/token flow changes
- public API contract changes
- DB schema/data migration/query semantic change
- dependency/version/plugin change
- external integration behavior changes
- broad cross-module refactor
- missing reproduction/acceptance criteria

If risk evidence is incomplete:
- Keep conservative level (`Medium` or `High`).
- Mark `RISKY ASSUMPTION` and checkpoint before implementation.

## Planning Steps
Follow steps in order. Keep output minimal and executable.

1. **Read context + constraints**
   - Confirm requirement source, scope, and hard constraints.
2. **Classify task**
   - Produce primary/secondary type with evidence.
3. **Map impacted scope**
   - List impacted layers/modules and uncertainty markers.
4. **Set risk level**
   - Assign level + top risk drivers.
5. **Build minimal ordered sequence**
   - Only include steps required to deliver scoped objective.
   - Each step must include: owner skill/action, goal, exit signal.
6. **Define validation strategy**
   - Map checks to task type and risk level.
   - Provide runnable command set (prefer `backend/`).
7. **Define escalation checkpoints**
   - Add explicit “stop-and-confirm” gates.
8. **Run planning verification**
   - Ensure plan is in-scope, consistent, and handoff-ready.

## Sequence Design Rules
Design sequence as shortest valid path, not idealized full lifecycle.

Use alias-first wording when describing workflow actors in plan output:
- Format: `<Role Alias> (<Technical Skill Name>)`
- Example: `PJM-Discovery (scan-project-backend)`
- If alias wording and technical skill name conflict, technical skill name is authoritative.

1. Use `scan-project-backend` only when module/flow evidence is insufficient.
2. Use `create-prd` only when requirement intent/acceptance is incomplete.
3. Schedule implementation step only after scope/risk/validation are defined.
4. Add `review`/`verify` steps when risk is `Medium`/`High` or change is cross-layer.
5. Do not insert speculative optimization or unrelated cleanup.
6. Every step must have a clear prerequisite and completion condition.

## Validation Strategy Rules
Validation plan must be concrete and risk-aligned.

### Minimum Required Content
1. Validation objective per check.
2. Exact commands (or clearly flagged non-runnable checks).
3. Scope of each check (targeted vs broader).
4. Pass criteria.
5. Unverified items (if any).

### Minimal Validation Strategy
Use smallest check set that can detect likely regressions for current scope.
- Prefer targeted tests first.
- Expand to broader checks when risk or impact increases.

### Escalate Validation Intensity
Use stronger validation when:
- risk is `High`
- multiple layers are impacted
- auth/security/contract/persistence is involved

### Command Reliability Rule
If command runnability cannot be confirmed:
- label `REQUIRES CONFIRMATION`
- provide reason (missing tool/env/path)
- provide fallback candidate command if known

## Escalation Rules
Insert explicit checkpoint and stop progression when any condition is met:
1. API contract-breaking risk.
2. Schema/migration/data semantic change.
3. Auth/security/token/permission flow change.
4. New dependency or version change.
5. Scope expansion beyond stated objective.
6. Requirement ambiguity blocking reliable implementation order.
7. Any `High` risk with unresolved assumptions.

Checkpoint format:
- `CHECKPOINT: <decision>`
- `WHY: <risk/evidence>`
- `NEEDS CONFIRMATION FROM: user/reviewer`

## Output Contract
Output must be directly reusable by implementation/review/verification skills.

Provide sections in this exact order:
1. `Task Classification Summary`
   - Primary type, secondary type(s), evidence.
2. `Impacted Scope`
   - Layer/module list with impacted status.
3. `Risk Summary`
   - risk level, top drivers, uncertainty markers.
4. `Ordered Execution Plan`
   - numbered minimal steps with rationale and exit signal.
5. `Validation Plan`
   - commands/checks, objectives, pass criteria, unverified items.
6. `Escalation Checkpoints`
   - explicit stop-and-confirm items.
7. `Assumptions and Unknowns`
   - `REQUIRES CONFIRMATION`, `OPEN QUESTION`, `RISKY ASSUMPTION`, `OUT OF SCOPE`.

## Boundaries
Strict boundaries for this skill:
1. Do not implement code.
2. Do not execute refactor/redesign work.
3. Do not expand scope with opportunistic cleanup.
4. Do not treat speculative work as required steps.
5. Do not make risky technical decisions silently.
6. Do not classify uncertain impact as confirmed without evidence.

## Verification
Before finalizing the plan, verify all checks below:
1. Sequence is logically ordered by dependency and risk.
2. Validation intensity matches task type and risk level.
3. Escalation points cover all high-impact decision boundaries.
4. No step pushes work outside stated scope/non-goals.
5. Commands are backend-local where possible and syntactically runnable.
6. Unknowns are explicitly labeled, not implied.

## Failure / Uncertainty Handling
When information is incomplete, use strict labels:

- `REQUIRES CONFIRMATION`: blocking decision requiring user/reviewer confirmation.
- `OPEN QUESTION`: missing fact that affects plan quality but is not yet resolved.
- `RISKY ASSUMPTION`: assumption that can alter design/validation materially.
- `OUT OF SCOPE`: tempting but excluded work.

Rules:
1. Never hide uncertainty.
2. Never continue to detailed sequencing past unresolved blocking items.
3. Offer at most one conservative default path when needed, marked as assumption-based.

## What Not to Do
1. Do not rewrite PRD.
2. Do not perform code review scoring.
3. Do not run implementation or migration commands as part of planning output.
4. Do not propose architecture redesign unless explicitly requested and confirmed.
5. Do not provide generic advice without mapping to actual backend layers and checks.
