---
name: implement-backend-change
description: Use for backend code implementation after planning is clear; apply minimal safe changes following Controller-Service-Repository conventions and existing project patterns.
---

# implement-backend-change

## Purpose
Implement backend tasks safely and consistently.

## Trigger Conditions
- Task is approved and sufficiently clarified.
- Change primarily affects backend application code.

## Inputs
- Approved task plan
- `backend/AGENTS.md` rules
- Existing module patterns

## Steps
1. Locate impacted controller/service/repository/dto paths.
2. Reuse existing utilities and conventions.
3. Apply smallest correct code change.
4. Update docs/messages/annotations only when required by behavior change.

## Outputs
- Backend code patch
- Short change summary with impacted files

## Boundaries
- No silent API contract breaks.
- No DB schema/auth/dependency changes without confirmation.

## Verification
- Run at least one relevant backend validation command when feasible.
