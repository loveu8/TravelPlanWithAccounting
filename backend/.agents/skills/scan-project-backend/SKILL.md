---
name: scan-project-backend
description: Backend scan/architecture discovery skill for the backend/ subtree. Use before backend implementation/review/refactor to build a reusable, evidence-based architecture snapshot (facts, inferences, unknowns) without changing production behavior.
---

# scan-project-backend

## Frontmatter
- **Skill type**: Backend scan / architecture discovery
- **Primary scope**: `backend/` subtree only
- **Primary artifact**: `backend/docs/backend-project-architecture.md`
- **Core method**: Repository evidence first; no unsupported claims
- **Non-goals**: Refactor design, implementation planning, code fixes

## Description
This skill performs a **backend-only repository scan** and produces a stable architecture snapshot that later backend tasks can reuse directly.

The skill must:
1. collect evidence from files/commands,
2. separate `Confirmed facts` / `Inferred facts` / `Unknowns (REQUIRES CONFIRMATION)`,
3. persist the result in a backend architecture document.

## Purpose
Build a **repeatable factual snapshot** of the backend architecture under `backend/` so future backend implementation/review/refactor work does not need full re-discovery.

## When to Use
Use this skill when at least one is true:
1. Starting backend implementation/review/refactor in an unfamiliar or partially familiar codebase.
2. Backend structure/command map is unclear or outdated.
3. A reusable architecture baseline is needed for future AI turns.
4. You need evidence-backed architecture context before giving technical recommendations.

## When Not to Use
Do **not** use this skill when:
1. Task is a tiny single-file bug fix with clear local context.
2. Task is pure code editing without architecture discovery need.
3. Task is non-backend scope (`frontend/`, infra-only, docs-only outside backend architecture context).
4. Repository does not use `backend/` as backend root and user did not ask to adapt scan scope.

If `backend/` is missing or clearly not the backend root:
- stop broad scanning,
- report `Unknowns (REQUIRES CONFIRMATION)`,
- ask for the correct backend root before continuing.

## Assumptions
1. `backend/` is intended backend subtree unless evidence disproves it.
2. Scan is evidence-collection work, not production behavior change.
3. Commands are run from repo root by default; backend mode is equivalent with adjusted paths.
4. Environment limitations may exist; failures must be recorded, not hidden.

## Required Inputs
Minimum inputs (read if present):
1. `AGENTS.md` (repo root)
2. `backend/AGENTS.md`
3. `backend/pom.xml`
4. `backend/mvnw` (or evidence it is absent)
5. `backend/src/main/**`
6. `backend/src/test/**`
7. `backend/docs/**`
8. Existing `backend/docs/backend-project-architecture.md` (if present)
9. Skill references/templates under `backend/.agents/skills/scan-project-backend/`

## Input Fallback Rules
If an expected input is missing, do not fail the whole scan by default.

- Missing `backend/AGENTS.md` or root `AGENTS.md`:
  - continue with available rules,
  - record missing governance source in `Unknowns`.
- Missing `pom.xml` or `mvnw`:
  - do not invent build commands,
  - record command-map confidence as reduced.
- Missing `src/test`:
  - do not infer “no tests exist globally”,
  - record scope-limited finding only.
- Missing `backend/docs/` or target artifact:
  - create target artifact if allowed by task; otherwise report as follow-up.
- Missing `references/` or `templates/` files:
  - continue with core scan workflow,
  - use inline default structure defined in this SKILL,
  - mark reference/template gap in `Unknowns`.

## Evidence Collection Rules
1. Every major claim must map to file-path evidence or command output.
2. If evidence is indirect, mark as `Inferred facts` and state inference basis.
3. If no evidence is available, mark as `Unknowns (REQUIRES CONFIRMATION)`.
4. Do not treat common conventions as confirmed project behavior.
5. Do not treat missing files as architecture defects without explicit evidence.
6. Evidence commands are for discovery only; avoid hidden design conclusions.

## Minimum Evidence Commands
Run minimal commands needed; choose one execution mode.

### Command Availability Rule
- Prefer `rg` for file discovery.
- If `rg` is unavailable, fallback to `find` + `sed/head` patterns.
- Record fallback usage in output.

### Mode A: Repo Root Mode (preferred)
```bash
find backend -name AGENTS.md -print
find backend -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|README|docs'
find backend/src/main/java -maxdepth 5 -type d
find backend/src/test/java -maxdepth 5 -type d
rg --files backend/src/main/java | head -n 80
rg --files backend/src/test/java | head -n 80
```

### Mode B: backend/ Mode (equivalent)
```bash
find . -name AGENTS.md -print
find . -maxdepth 3 -type f | rg 'pom.xml|mvnw|application.*yml|README|docs'
find src/main/java -maxdepth 5 -type d
find src/test/java -maxdepth 5 -type d
rg --files src/main/java | head -n 80
rg --files src/test/java | head -n 80
```

### Command Failure Handling
For each failed command, record:
1. command string,
2. failure reason (tool missing / path missing / permission / timeout),
3. impact on confidence,
4. whether a fallback command was attempted.

Do not abort entire scan unless zero meaningful evidence can be collected.

## Execution Modes
1. **Quick scan** (default): minimal evidence commands + key file reads.
2. **Deep scan** (only when task needs it): targeted deeper reads for boundaries/conventions.

Selection rule:
- If user asks architecture baseline only → Quick scan.
- If user asks review/refactor preparation across modules → Deep scan.

## Steps
Execute in order.

### Step 1: Guardrails & Scope
**Input**: AGENTS files, user request.
**Action**:
- identify applicable rules and precedence,
- confirm scan boundary is `backend/`.
**Output**:
- scope statement,
- applicable rule list.

### Step 2: Structural Evidence Collection
**Input**: backend source tree.
**Action**:
- map package/module layout from actual directories/files,
- identify major layers and infrastructure modules only when evidenced.
**Output**:
- backend module map (factual).

### Step 3: Command Map Extraction
**Input**: `pom.xml`, `mvnw`, backend docs/scripts.
**Action**:
- capture only explicit runnable commands discovered in repository,
- separate confirmed vs unconfirmed commands.
**Output**:
- command map with confidence tags.

### Step 4: Boundary & Data Surface Mapping
**Input**: controller/service/repository/entity/config evidence.
**Action**:
- identify API boundaries, data/persistence touchpoints, cross-cutting concerns.
**Output**:
- boundary map with evidence references.

### Step 5: Architecture Classification
**Input**: structure + boundaries evidence.
**Action**:
- classify architecture style using explicit rules (below),
- if evidence is insufficient, output `Insufficient evidence`.
**Output**:
- classification + rationale.

### Step 6: Convention Compliance Check
**Input**: repository layout and docs evidence.
**Action**:
- evaluate convention checks with strict statuses.
**Output**:
- compliance table (`Pass | Partial | Fail | Insufficient evidence`).

### Step 7: Persistent Artifact Update
**Input**: all scan outputs.
**Action**:
- create/update `backend/docs/backend-project-architecture.md`,
- preserve stable headings and prior useful content,
- avoid unnecessary full rewrite.
**Output**:
- updated architecture snapshot artifact.

### Step 8: Miss-Check & Final Verification
**Input**: summary + artifact draft.
**Action**:
- run verification checklist,
- ensure unknowns are explicit where confidence is limited.
**Output**:
- final chat summary + artifact consistency confirmation.

## Classification Rules
Allowed outcomes:
1. `Layered monolith`
2. `Domain-modular monolith`
3. `Microservices-oriented split`
4. `Insufficient evidence`

Decision criteria (must cite evidence):
- **Layered monolith**: dominant technical layers (controller/service/repository etc.) under one deployable backend project.
- **Domain-modular monolith**: clear domain packages/modules each containing multiple layers.
- **Microservices-oriented split**: evidence of independently structured service modules/projects or strong service-boundary separation.
- **Insufficient evidence**: any case where above cannot be proven.

Rule: never force a classification when evidence is weak.

## Convention Compliance Rules
Each check must be scored using:
- `Pass`: clear evidence meets rule.
- `Partial`: some evidence meets rule but notable gaps or inconsistencies exist.
- `Fail`: clear evidence violates rule.
- `Insufficient evidence`: cannot evaluate from available evidence.

Minimum checks:
1. Maven standard layout presence.
2. Backend package-root consistency.
3. Controller → Service → Repository layering evidence.
4. Resources/config discoverability (`src/main/resources`, config files).
5. Test layout discoverability (`src/test`).
6. Backend command discoverability (`mvnw`, docs, scripts).
7. Backend docs discoverability (`backend/docs`, backend README if present).

## Required Outputs
## 1) Chat Summary (stable headings)
Use exactly these headings for reuse stability:
- `Summary`
- `Confirmed facts`
- `Inferred facts`
- `Unknowns (REQUIRES CONFIRMATION)`
- `Command map`
- `Architecture classification`
- `Convention compliance`
- `Risks and follow-ups`
- `Confidence`

## 2) Persistent Artifact
Target: `backend/docs/backend-project-architecture.md`

If template exists (`templates/backend-project-architecture-template.md`):
- follow template structure.

If template missing:
- use the same stable headings listed above,
- include evidence references inline.

## Output Contract (for downstream AI reuse)
1. Keep section order stable.
2. Keep terminology stable (`Confirmed`, `Inferred`, `Unknowns`).
3. Include command list with execution mode and failures.
4. Include explicit confidence level (`High | Medium | Low`) + reason.
5. Keep entries concise and evidence-addressable.
6. Do not mix recommendations into factual sections; place optional suggestions under follow-ups only.

## Progressive Disclosure / Context Efficiency
- **Core output (always include)**: stable headings + key findings + command map + confidence.
- **Optional references (include only when needed)**:
  - pattern comparisons,
  - extended file inventories,
  - deep convention notes.
- Avoid dumping large raw listings unless explicitly requested.

## Boundaries
1. Do not modify production code during scan.
2. Do not infer versions/topology/profiles without direct evidence.
3. Do not convert scan into refactor or redesign proposal.
4. Do not auto-complete missing command maps from habits or memory.
5. Do not treat absent files as defects without context.
6. Keep scan minimal but sufficient for factual architecture understanding.

## Failure / Missing Evidence Handling
When evidence is incomplete:
1. continue with available evidence,
2. downgrade confidence,
3. add explicit unknowns,
4. list exact missing evidence and why missing,
5. provide targeted follow-up checks (not broad redesign suggestions).

If no meaningful evidence is collectible, output:
- summary of attempted commands/paths,
- explicit block reason,
- `Confidence: Low`,
- `REQUIRES CONFIRMATION` question for next action.

## Verification Checklist
Before handoff, verify all items:
1. Scope is backend-only and stated explicitly.
2. Every major claim is tagged as Confirmed/Inferred/Unknown.
3. No inferred item is written as confirmed fact.
4. Failed commands and impacts are documented.
5. Classification is evidence-backed or marked `Insufficient evidence`.
6. Convention checks use only allowed statuses.
7. Persistent artifact exists/updated with stable headings.
8. Existing architecture doc content was not needlessly overwritten.
9. Final output includes files changed, commands run, results, and remaining risks.
