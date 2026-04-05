---
name: refactor-backend
description: Use for backend structure/maintainability improvements without changing externally observable behavior; require clear scope and validation.
---

# refactor-backend

## Purpose
Improve backend code quality while preserving behavior.

## Trigger Conditions
- Refactor request with no new feature intent.
- Technical debt cleanup in bounded scope.

## Inputs
- Refactor goal and boundaries
- Existing tests and architecture patterns

## Steps
1. Confirm non-functional scope and invariants.
2. Apply incremental refactor in small commits.
3. Preserve public contracts and runtime behavior.
4. Run targeted validation for touched areas.

## Outputs
- Refactor patch
- Behavior-preservation notes
- Validation summary

## Boundaries
- Do not mix feature work into refactor changes.
- Escalate if API/schema/security impact appears.

## Verification
- Confirm no intended behavior change through tests/checks.
