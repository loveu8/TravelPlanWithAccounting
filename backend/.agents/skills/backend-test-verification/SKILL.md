---
name: backend-test-verification
description: Use after backend code changes to run and report concrete Maven validation commands with honest pass/fail and explicit unverified gaps.
---

# backend-test-verification

## Purpose
Provide trustworthy verification status for backend changes.

## Trigger Conditions
- Backend code has been modified.
- Pre-handoff validation is needed.

## Inputs
- Changed backend files
- Available Maven commands/environment

## Steps
1. Run smallest relevant test/build command first.
2. Escalate to broader command when needed (e.g., `./mvnw test`, `./mvnw clean install`).
3. Capture failures, warnings, and environment blockers.

## Outputs
- Exact commands executed
- Pass/fail status and error summary
- Unverified areas

## Boundaries
- Never claim checks passed unless actually executed.

## Verification
- Ensure report reflects real terminal output only.
