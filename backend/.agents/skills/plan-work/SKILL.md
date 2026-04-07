---
name: plan-work
description: Use after backend PRD or bug context is available to classify backend task type, define execution sequence, and set a minimal validation strategy before coding.
---

# plan-work

## Purpose
Create an implementation-ready execution plan for backend work.

## Trigger Conditions
- After backend PRD completion
- Before meaningful backend code changes
- When backend scope/risk is not yet explicitly sequenced

## Inputs
- Backend PRD or bug context
- Backend scan findings
- Constraints from `AGENTS.md` and `backend/AGENTS.md`

## Steps
1. Classify task type (feature/bug/security/refactor/docs).
2. Define impacted backend modules/layers and risk level.
3. Propose the minimal ordered sequence of skills/actions.
4. Define validation commands (prefer backend-local commands) and escalation checkpoints.

## Outputs
- Ordered backend work plan
- Validation plan
- Explicit escalation checkpoints

## Boundaries
- Do not execute risky decisions without user confirmation.
- Do not include out-of-scope refactors in the plan.

## Verification
- Confirm scope, order, and validation are consistent with backend conventions.
- Confirm planned checks are actually runnable in `backend/` when code changes are involved.
