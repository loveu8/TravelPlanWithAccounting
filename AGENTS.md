# AGENTS.md

## Scope

This file applies to the entire repository.
For files under `backend/`, `backend/AGENTS.md` has higher priority.

## Repository Overview

- Monorepo currently centered on a Spring Boot backend service under `backend/`.
- Product/process documents are under `docs/`.
- Agent system design source of truth is `docs/agent-system-PRD.md`.

## Important Directories

- `backend/`: Java/Spring Boot application source and tests.
- `backend/docs/`: backend-specific docs and migration notes.
- `docs/`: repository-level planning docs.
- `.agents/skills/`: root reusable skills for planning and governance workflows.

## Working Conventions

1. Prefer smallest correct change; avoid broad refactors unless explicitly requested.
2. Read relevant files before editing.
3. Do not invent commands, paths, or versions. Use `REQUIRES CONFIRMATION` when uncertain.
4. Keep durable rules in `AGENTS.md`, and put reusable multi-step workflows in skills.
5. For backend tasks, follow Controller → Service → Repository layering and defer to `backend/AGENTS.md`.

## Build / Test / Validation Overview

- Backend commands should be executed from `backend/`.
- Preferred checks (when backend code changes):
  - `./mvnw test`
  - `./mvnw clean install`
- If command cannot run, report what is unverified and why.

## Do-Not Rules

- Do not overwrite existing `backend/AGENTS.md` wholesale when patching is sufficient.
- Do not auto-apply high-risk changes (DB schema, auth/security flow, API breaking contract, dependencies) without confirmation.
- Do not claim validation passed unless command was actually run.

## Routing Guidance

- Unknown repo or unclear context: use `scan-project` first.
- Requirement shaping / doc planning: use `create-prd`, then `plan-work`.
- Cross-file or docs-only checks: use `verify-change`.
- Backend implementation/testing/review/refactor: enter `backend/` scope and use backend skills.

## Definition of Done

Before handoff, always include:

1. What changed and why.
2. Files touched.
3. Commands actually run and outcomes.
4. Remaining risks, assumptions, or unverified areas.
