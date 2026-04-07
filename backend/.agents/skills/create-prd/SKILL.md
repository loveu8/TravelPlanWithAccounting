---
name: create-prd
description: Use when backend requirements are ambiguous or a new backend feature is proposed; convert intent into a structured, testable PRD with explicit scope, API/data impact, and risks.
---

# create-prd

## Purpose
Turn ambiguous backend requirements into executable product requirements before implementation.

## Trigger Conditions
- New backend feature request
- Large backend change with unclear acceptance criteria
- API/data behavior needs clear definition before coding

## Inputs
- User goals
- Backend constraints from `AGENTS.md` and `backend/AGENTS.md`
- Existing backend architecture/context from scans or docs

## Steps
1. Define problem statement and desired backend outcomes.
2. Specify scope, non-goals, and affected backend boundaries (Controller/Service/Repository).
3. List user scenarios, API behaviors, and testable acceptance criteria.
4. Capture assumptions, dependencies, risks, and escalation points requiring confirmation.

## Outputs
- Structured backend PRD draft
- Open questions requiring confirmation

## Boundaries
- Do not jump into implementation details when requirements are still unclear.
- Do not propose breaking API/schema/security changes without explicitly flagging confirmation needs.

## Verification
- Ensure acceptance criteria are testable.
- Ensure assumptions and risks are clearly marked.
- Ensure backend layering and validation expectations are reflected in the PRD.
