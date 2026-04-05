---
name: review-change
description: Use to review backend changes for correctness, security, maintainability, and testing risk before delivery.
---

# review-change

## Purpose
Perform structured code review with severity grading.

## Trigger Conditions
- A backend patch is ready for review.
- Security or correctness confidence is needed.

## Inputs
- Diff/changed files
- Backend rules and conventions

## Steps
1. Check correctness against requirements.
2. Check security-sensitive surfaces and error handling.
3. Check maintainability and style consistency.
4. Check validation sufficiency and regressions.
5. Classify issues by severity (P0-P3).

## Outputs
- Review findings with file/line references
- Delivery recommendation (ship / fix required)

## Boundaries
- Keep findings actionable and evidence-based.

## Verification
- Ensure each finding maps to concrete code evidence.
