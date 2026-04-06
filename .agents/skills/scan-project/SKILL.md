---
name: scan-project
description: Use when entering an unfamiliar repo or before generating/updating AGENTS.md and skills; map real structure, commands, and constraints without guessing, then produce a reusable architecture snapshot for future agent guidance.
---

# scan-project

## Purpose
Produce a factual, reproducible project scan that supports planning, AGENTS.md updates, and future onboarding.

## Why this design
- Keep docs close to code and maintain them in version control (GitHub repository documentation guidance).
- Use lightweight architecture documentation that teams can actually maintain (arc42 + C4 model).
- Capture important trade-off decisions as ADR links when present.

## Trigger Conditions
- First task in an unfamiliar repository.
- Before creating/updating `AGENTS.md` or any `SKILL.md`.
- Before proposing broad refactors across modules.

## Inputs
- User request and current goal
- Existing repository files
- Existing architecture docs (`docs/`, `README*`, ADR folders, diagrams)

## Steps
1. **Scan structure (facts only)**
   - List top-level directories and identify code modules.
   - Detect major manifests/build files and infer toolchain from those files only.
2. **Detect governance and scope rules**
   - Find all `AGENTS.md` files.
   - Build scope precedence map (root to nested overrides).
3. **Extract runnable commands from evidence**
   - Read scripts/wrappers/manifests to collect build/test/lint/run commands.
   - Mark unknowns as `REQUIRES CONFIRMATION` instead of guessing.
4. **Summarize architecture at practical depth**
   - Identify system context, main containers/apps, key components, and data boundaries.
   - Link each architecture claim to specific files.
5. **Generate architecture snapshot document**
   - Create or update `docs/project-architecture.md` using the template in `templates/project-architecture-template.md`.
   - Ensure this file is concise and usable as input for future `AGENTS.md` maintenance.
6. **Report confidence and gaps**
   - Separate confirmed facts vs assumptions.
   - Add unknowns and validation follow-ups.

## Required Outputs
1. **Scan summary in response**
   - Directory/module summary
   - AGENTS scope hierarchy
   - Confirmed commands
   - Constraints and risks
   - `REQUIRES CONFIRMATION` list
2. **Persistent artifact**
   - `docs/project-architecture.md` (new or updated)

## `docs/project-architecture.md` quality bar
- Must include exact date (`YYYY-MM-DD`) and scan scope.
- Must cite concrete file paths for key claims.
- Must distinguish:
  - Confirmed
  - Inferred
  - Unknown / `REQUIRES CONFIRMATION`
- Must include a "How to update" section so future agents can refresh quickly.

## Boundaries
- Never fabricate commands, ownership, architecture, or dependencies.
- Prefer shortest accurate scan first; deepen only where task requires.
- Do not edit unrelated docs while generating the architecture snapshot.

## Verification
- Re-check every cited file path exists.
- Re-run minimal commands used for evidence collection when output is ambiguous.
- Confirm `docs/project-architecture.md` is internally consistent with the scan summary.

## External references used by this skill design
- GitHub Docs, repository documentation best practices: https://docs.github.com/en/repositories/creating-and-managing-repositories/best-practices-for-repositories
- arc42 template overview: https://arc42.org/
- C4 model (official): https://c4model.com/
- ADR hub and rationale references: https://adr.github.io/
