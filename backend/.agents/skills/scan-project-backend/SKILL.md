---
name: scan-project-backend
description: Use when scanning the backend subtree of this repository before backend implementation/review/refactor. Build a factual architecture snapshot for Java/Spring backend modules, commands, boundaries, and risks. Also use when aligning backend structure with proven GitHub Java backend patterns and when optimizing Codex skill loading with progressive-disclosure references.
---

# scan-project-backend

## Purpose
Produce a reproducible backend-only scan for `backend/` that can be reused by future tasks without re-discovery.

## Assumptions
- Repository is a backend-centric Java/Spring project under `backend/`.
- Scan scope is documentation + architecture discovery only (no behavior change).
- Commands are executed from **repo root** by default. If current working directory is `backend/`, use the equivalent relative paths.

## Inputs
- User goal and affected backend scope.
- `backend/AGENTS.md` and root `AGENTS.md`.
- `backend/pom.xml`, `backend/mvnw`, `backend/src/main/**`, `backend/src/test/**`, `backend/docs/**`.

## Minimum Evidence Commands
### Repo root mode (preferred)
- `find backend -name AGENTS.md -print`
- `find backend -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|README|docs'`
- `find backend/src/main/java -maxdepth 4 -type d`
- `find backend/src/test/java -maxdepth 4 -type d`
- `rg --files backend/src/main/java | head -n 40`
- `rg --files backend/src/test/java | head -n 40`

### backend/ mode (equivalent)
- `find . -name AGENTS.md -print`
- `find . -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|README|docs'`
- `find src/main/java -maxdepth 4 -type d`
- `find src/test/java -maxdepth 4 -type d`
- `rg --files src/main/java | head -n 40`
- `rg --files src/test/java | head -n 40`

Use only commands supported by repository evidence. If a command fails due to environment limits, record it explicitly.

## Steps
1. **Build scope guardrails first**
   - Read root and backend AGENTS rules.
   - Record overlap and precedence rules that apply to `backend/`.
2. **Scan backend structure from evidence**
   - Map package layers: `controller`, `service`, `repository`, `dto`, `mapper`, `config`, `aspect`, `exception`, `message`.
   - Detect infrastructure modules (security, cache, mail, integration clients, schedulers).
3. **Extract real commands only**
   - Pull commands from `mvnw`, `pom.xml`, and existing docs/scripts.
   - Mark unknown commands as `REQUIRES CONFIRMATION`.
4. **Map data + boundary surfaces**
   - Identify entities, repositories, transaction boundaries, and outward API boundaries.
   - Identify security/i18n response patterns used across controllers.
5. **Classify current backend style against proven GitHub patterns**
   - Read `references/github-java-backend-patterns.md`.
   - Classify current state as:
     - Layered monolith pattern
     - Domain-modular monolith pattern
     - Microservices split pattern
   - Report mismatch and migration risk only as observations (no refactor proposal unless requested).
6. **Run convention compliance check (industry + GitHub common practice)**
   - Read `references/industry-conventions.md`.
   - Check Maven standard layout (`src/main/java`, `src/test/java`, `src/main/resources`).
   - Check Spring package-root arrangement and component scanning assumptions.
   - Check repository docs discoverability (`README`, `docs/`) and backend command discoverability.
   - Record each check as `Pass | Partial | Fail` with concrete evidence paths.
7. **Write persistent snapshot**
   - Create/update `backend/docs/backend-project-architecture.md` using `templates/backend-project-architecture-template.md`.
   - Keep claims traceable to concrete file paths.
8. **Apply Codex skill optimization checklist**
   - Read `references/codex-skill-optimization.md`.
   - Ensure scan output is concise, factual, and split into core + references for context efficiency.
9. **Run miss-check before handoff**
   - Verify there is at least one finding in each section: structure, commands, boundaries, risks.
   - Verify unknowns are not empty when scan confidence is below high.
   - Verify each major claim cites evidence file paths.

## Required Outputs
1. **Chat summary**
   - Backend module map
   - Command map
   - Architecture classification
   - Risks and unknowns
   - Confidence level + why
2. **Persistent artifact**
   - `backend/docs/backend-project-architecture.md`
3. **Stable output headings (for AI reusability)**
   - `Summary`
   - `Confirmed facts`
   - `Inferred facts`
   - `Unknowns (REQUIRES CONFIRMATION)`
   - `Command map`
   - `Convention compliance`
   - `Risks and follow-ups`

## Boundaries
- Do not guess dependency versions, runtime profiles, or deployment topology.
- Do not modify production code during scan.
- Prefer smallest factual scan; deepen only for the active backend task.

## Verification
- Re-open every cited file path to confirm existence.
- Re-run minimal evidence commands if output is ambiguous.
- Verify architecture document clearly separates: Confirmed / Inferred / REQUIRES CONFIRMATION.
- Verify final summary includes: files changed, commands run, results, remaining risks/follow-ups.
- Verify no path contradiction exists between selected execution mode and produced evidence paths.
