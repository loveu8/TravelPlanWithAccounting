---
name: plan-work
description: Use after PRD or bug context is available to classify task type, define execution sequence, and set validation strategy before coding.
---

# plan-work

## Purpose
Create an implementation-ready execution plan.

## Trigger Conditions
- After PRD completion
- Before meaningful code changes

## Inputs
- PRD or bug context
- Repository scan findings

## Steps
1. Classify task type (feature/bug/security/refactor/docs).
2. Define impacted modules and risk level.
3. Propose minimal sequence of skills/actions.
4. Define validation commands and escalation points.

## Outputs
- Ordered work plan
- Validation plan
- Explicit escalation checkpoints

## Boundaries
- Do not execute risky decisions without user confirmation.

## Verification
- Confirm scope, order, and validation are consistent with repo conventions.
