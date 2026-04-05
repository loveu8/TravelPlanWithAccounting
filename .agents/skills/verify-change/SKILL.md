---
name: verify-change
description: Use for high-level verification of documentation or small-scope changes to confirm completeness, consistency, and unresolved risks.
---

# verify-change

## Purpose
Perform lightweight but explicit verification before handoff.

## Trigger Conditions
- Docs/config/rules updates
- Small non-invasive code changes

## Inputs
- Changed files
- Relevant repository rules

## Steps
1. Check changed content against stated requirements.
2. Validate references, paths, and command claims.
3. Report verified vs unverified items.

## Outputs
- Verification checklist with pass/gap status
- Remaining risks and follow-up checks

## Boundaries
- Do not invent test results.
- If environment blocks a check, report limitation explicitly.

## Verification
- Ensure every conclusion maps to inspected files or executed commands.
