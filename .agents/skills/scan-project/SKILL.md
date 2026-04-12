---
name: scan-project
description: Use when entering an unfamiliar repo or before generating/updating AGENTS.md and skills; scan the full repository (monorepo-safe), map real structure/commands/constraints without guessing, and produce a reusable architecture snapshot for future agent workflows.
---

# scan-project

## Purpose
Produce a factual, reproducible, full-repo architecture and governance snapshot for monorepo/full-stack projects.

This skill is for **scanning and documenting**. It is **not** an implementation, review, refactor, or migration skill.

## When to Use
- First task in an unfamiliar repository.
- Before creating/updating `AGENTS.md`.
- Before creating/updating any `SKILL.md`.
- Before planning multi-module work (frontend/backend/shared/infra/docs).

## Inputs
- User request and stated scope.
- Repository files at current commit.
- Existing docs (`README*`, `docs/`, ADRs, module docs).
- Governance files (`AGENTS.md` and nested overrides).

## Required Scan Scope (Root-Level First)
Always start at repository root. Do not assume backend-only structure.

Minimum topology to check (if present):
- `backend/`
- `frontend/`
- `src/`
- `docs/`
- `.agents/`
- root `AGENTS.md`
- root `README*`

Also detect additional module roots (examples):
- `apps/`, `packages/`, `services/`, `libs/`, `workers/`, `infra/`, `deploy/`, `scripts/`

## Workflow (Ordered, Do Not Skip)
1. **Scan top-level topology (facts only)**
   - List top-level directories/files.
   - Identify candidate modules/apps/packages/deployables.
   - Record evidence paths for each detection.

2. **Build governance map (`AGENTS.md` precedence)**
   - Find root and nested `AGENTS.md` files.
   - Map scope tree: root rules → nested overrides.
   - Record which directories each governance file controls.

3. **Detect manifests and wrappers (root + module-level)**
   - Root: build/workspace/CI manifests and scripts.
   - Module-level: local manifests, wrappers, tool configs.
   - Do not infer framework/runtime unless a file proves it.

4. **Extract runnable commands from evidence**
   - Build a command inventory grouped by scope:
     - `root`
     - `frontend`
     - `backend`
     - `shared`
     - `infra/docs/other module`
   - Allowed command sources:
     - Manifests (`package.json`, `pom.xml`, `build.gradle*`, `Makefile`, etc.)
     - Wrapper scripts (`mvnw`, `gradlew`, shell scripts, task runners)
     - CI/workflow files
     - Explicit project docs (`README`, module docs, AGENTS guidance)
   - If not evidenced, mark `REQUIRES CONFIRMATION`.

5. **Map architecture surfaces (confirmed vs inferred vs unknown)**
   - System context (actors, external systems, high-level purpose).
   - Frontend apps, backend services, shared modules/packages.
   - Datastores, messaging, third-party integrations.
   - Deployment boundaries and runtime ownership boundaries.
   - Every claim must carry status:
     - `Confirmed` (direct evidence exists)
     - `Inferred` (reasonable but indirect)
     - `Unknown / REQUIRES CONFIRMATION`

6. **Generate/update persistent snapshot**
   - Create or update `docs/project-architecture.md`.
   - Use `templates/project-architecture-template.md` headings exactly.
   - Keep output concise, evidence-linked, and reusable by other skills.

7. **Final consistency checks**
   - Module map consistency: topology ↔ deployables table ↔ commands scope.
   - Governance consistency: AGENTS scope map ↔ rules summary.
   - Evidence consistency: each key claim has source paths.
   - Unknown handling: unresolved items listed as `REQUIRES CONFIRMATION`.

## Command Collection Rules (Strict)
- Never invent commands.
- Never promote common framework defaults to facts without evidence.
- Keep command rows separate by scope and source.
- If root and module docs conflict, record conflict explicitly in unknowns.

## Required Outputs
1. **Chat summary (short, structured)**
   - Topology/module map.
   - Governance precedence map.
   - Confirmed commands by scope.
   - Architecture highlights (with status labels).
   - Risks/unknowns and `REQUIRES CONFIRMATION` list.

2. **Persistent artifact**
   - `docs/project-architecture.md` updated using the template.

## `docs/project-architecture.md` Quality Bar
Must include all of the following:
- Exact scan date (`YYYY-MM-DD`).
- Scan scope (`full repo` or explicit sub-scope).
- Source commit SHA.
- Stable section headings from template (no ad-hoc renaming).
- Clear status labeling (`Confirmed` / `Inferred` / `Unknown`).
- Evidence file paths for key claims.
- Root + module command inventory with source paths.
- Governance precedence map (root + nested AGENTS).
- Explicit unknowns and confirmation actions.
- "How to Update" steps that remove stale facts.

## Boundaries
- Do not convert this skill into implementation/review/refactor work.
- Do not edit unrelated documents while producing snapshot.
- Do not guess framework, deployment topology, ownership, or dependencies.
- Do not write inferred statements as confirmed facts.

## Uncertainty Handling
When evidence is missing or conflicting:
- Mark item `Unknown / REQUIRES CONFIRMATION`.
- State why evidence is insufficient or conflicting.
- Provide one concrete confirmation method (file, command, owner, or doc).

## Verification Checklist (Before Handoff)
- [ ] Top-level topology captured from root.
- [ ] Frontend/backend/shared/docs/infra surfaces assessed.
- [ ] AGENTS root-to-nested precedence documented.
- [ ] Commands grouped by scope with source evidence.
- [ ] Architecture claims labeled Confirmed/Inferred/Unknown.
- [ ] Unknowns + confirmation methods listed.
- [ ] `docs/project-architecture.md` internally consistent and reusable.

## Design References
- GitHub Docs (repository documentation best practices): https://docs.github.com/en/repositories/creating-and-managing-repositories/best-practices-for-repositories
- arc42 (architecture documentation template): https://arc42.org/
- C4 model (software architecture visual abstraction): https://c4model.com/
- ADR practices and references: https://adr.github.io/
