# Project Architecture Snapshot

- Scan date: 2026-04-07
- Repository: TravelPlanWithAccounting
- Scan scope: full repo
- Source commit: 087ff163eb78e5decf3b862df1e021d4aa7329bf
- Scanner: GPT-5.3-Codex

## 1) Scan Scope and Evidence Policy
- Scope included:
  - Root governance/docs/config surfaces (`AGENTS.md`, `README.md`, `docs/`, `.agents/`).
  - Application modules (`backend/`, `frontend/`).
  - Build/runtime manifests and wrappers (`backend/pom.xml`, `backend/mvnw`, `frontend/package.json`, Docker-related files).
- Scope excluded:
  - Deep source-level behavior audit across all endpoints/components.
  - Runtime infrastructure outside repository (cloud/network/IAM).
  - Dependency internals under `frontend/node_modules/` (existence noted, contents not audited).
- Evidence policy:
  - `Confirmed` = directly supported by repository evidence.
  - `Inferred` = plausible from partial evidence; not yet directly proven.
  - `Unknown / REQUIRES CONFIRMATION` = missing or conflicting evidence.
- Primary evidence files:
  - `AGENTS.md`
  - `backend/AGENTS.md`
  - `README.md`
  - `backend/README.md`
  - `frontend/README.md`
  - `backend/pom.xml`
  - `frontend/package.json`
  - `backend/docker-compose.yml`
  - `backend/build.sh`, `backend/up.sh`, `backend/down.sh`
  - `frontend/src/app/api/_utils/http.ts`
  - `frontend/src/middleware.ts`

## 2) Repository Topology and Module Map

### 2.1 Top-level directory map (Confirmed)
| Path | Type | Role | Status | Evidence |
|---|---|---|---|---|
| backend/ | module | Spring Boot backend service | Confirmed | `backend/pom.xml`, `backend/README.md` |
| frontend/ | module | Next.js frontend app | Confirmed | `frontend/package.json`, `frontend/README.md` |
| docs/ | docs | Product/process/agent design docs | Confirmed | `docs/agent-system-PRD.md` |
| .agents/ | governance | Root reusable skills | Confirmed | `.agents/skills/scan-project/SKILL.md`, `.agents/skills/verify-change/SKILL.md` |

### 2.2 Module/package inventory (Confirmed + Inferred)
| Unit | Kind (app/service/package/worker/docs/infra) | Path | Depends on | Deployable | Status | Evidence |
|---|---|---|---|---|---|---|
| backend-service | service | `backend/` | PostgreSQL, Spring ecosystem dependencies | yes | Confirmed | `backend/pom.xml`, `backend/Dockerfile` |
| frontend-web | app | `frontend/` | Node.js runtime, React/Next ecosystem | yes | Confirmed | `frontend/package.json`, `frontend/Dockerfile` |
| docs-set | docs | `docs/` | n/a | no | Confirmed | `docs/agent-system-PRD.md` |
| root-skills | infra/docs | `.agents/skills/` | n/a | no | Confirmed | `.agents/skills/scan-project/SKILL.md` |
| backend-internal-packages | package | `backend/src/main/java/com/travelPlanWithAccounting/service/*` | service-local modules | unknown | Inferred | directory map under `backend/src/main/java/.../service` |

## 3) System Context and Runtime Boundaries

### 3.1 System context (Confirmed)
- Product/domain objective: travel planning with accounting support (repo title and backend description).
- Primary users/actors: end users interacting with a web UI and account/auth flows.
- External systems/services: PostgreSQL and mail/security/openapi/jwt-related libraries are present; exact external providers beyond repo are partially unknown.
- Evidence:
  - `README.md`
  - `backend/pom.xml`
  - `backend/README.md`
  - `frontend/src/app/api/_utils/http.ts`

### 3.2 Runtime/deployment boundaries (Confirmed + Inferred)
- Frontend runtime boundary: Next.js runtime (dev/build/start scripts and Docker runtime on port 3000). **Status: Confirmed** (`frontend/package.json`, `frontend/Dockerfile`).
- Backend runtime boundary: Spring Boot JAR runtime (Docker image, app mapped to port 9000 in compose). **Status: Confirmed** (`backend/Dockerfile`, `backend/docker-compose.yml`).
- Worker/batch/runtime boundary: no standalone worker module detected in top-level scan. **Status: Unknown / REQUIRES CONFIRMATION**.
- Infra/deploy boundary: Docker-based local deployment scripts are present; production orchestrator/environment is not specified in scanned files. **Status: Inferred** (`backend/build.sh`, `backend/up.sh`, `backend/down.sh`, `frontend/Dockerfile`).
- Status + evidence for each boundary:
  - Confirmed: frontend app, backend service, docker local orchestration evidence exists.
  - Inferred/Unknown: production deployment topology and ownership not explicitly documented.

## 4) Applications / Services / Packages / Deployable Units
| Unit | Path | Runtime/Framework | Responsibility | Interfaces (API/UI/Event) | Data stores | Third-party integrations | Deploy target | Status | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| frontend-web | `frontend/` | Next.js 15 + React 19 | User-facing UI + middleware/i18n + API route layer | UI routes, Next route handlers under `src/app/api` | none local in module | axios, i18next, accept-language | Node container (port 3000) | Confirmed | `frontend/package.json`, `frontend/src/middleware.ts`, `frontend/Dockerfile` |
| backend-service | `backend/` | Spring Boot 4 + Java 25 | Domain APIs for travel/account/auth-related backend logic | REST-style controllers (package layout + docs) | PostgreSQL | JJWT, Spring Security, Spring Mail, Apache HttpClient, springdoc | JVM container (port 9000) | Confirmed | `backend/pom.xml`, `backend/AGENTS.md`, `backend/docker-compose.yml` |
| root-skills/docs | `.agents/skills/`, `docs/` | Markdown governance artifacts | Agent workflows and process/PRD documentation | n/a | n/a | n/a | n/a | Confirmed | `.agents/skills/scan-project/SKILL.md`, `docs/agent-system-PRD.md` |

## 5) Interaction and Data Boundaries

### 5.1 Frontend ↔ Backend interactions
| From | To | Interface | Contract location | Auth boundary | Status | Evidence |
|---|---|---|---|---|---|---|
| frontend Next route handlers | backend-service API base URL | HTTP via axios (`NEXT_PUBLIC_API_BASE_URL`) | route handlers + backend docs (partial) | optional Bearer token from `access_token` cookie when `attachAuth=true` | Confirmed | `frontend/src/app/api/_utils/http.ts`, `backend/docs/member-auth-flow.md` |

### 5.2 Backend ↔ Data stores / third-party
| From | To | Interface | Secrets/config path | Failure boundary | Status | Evidence |
|---|---|---|---|---|---|---|
| backend-service | PostgreSQL | Spring Data JPA + JDBC driver | `.backendEnv` + Spring config files (exact keys not fully enumerated in this scan) | DB connectivity / runtime env dependent | Confirmed | `backend/pom.xml`, `backend/docker-compose.yml`, `backend/README.md` |
| backend-service | Mail/Security/JWT/OpenAPI libraries | Spring starter + dependency integration | app config + environment values (details unknown) | runtime/service config dependent | Inferred | `backend/pom.xml` |

### 5.3 Config, secrets, and environment boundaries
- Config entry points (root + module-level): `backend/src/main/resources/application.properties`, frontend env usage via `process.env.NEXT_PUBLIC_API_BASE_URL`.
- Secrets handling locations: backend scripts and compose reference `../.backendEnv`; frontend references env variable for API base URL.
- Environment split (dev/stage/prod) evidence: local/docker evidence exists; explicit stage/prod split files not found in this scan.
- Unknowns:
  - exact production secret management mechanism.
  - exact environment matrix (dev/stage/prod) operational process.

## 6) Commands Inventory (Evidence-based)

### 6.1 Confirmed runnable commands
| Scope (root/frontend/backend/shared/infra/docs) | Purpose | Command | Working directory | Source file/path | Status |
|---|---|---|---|---|---|
| backend | tests | `./mvnw test` | `backend/` | `AGENTS.md`, `backend/AGENTS.md` | Confirmed |
| backend | full build | `./mvnw clean install` | `backend/` | `AGENTS.md`, `backend/AGENTS.md` | Confirmed |
| backend | docker build helper | `sh build.sh` | `backend/` | `backend/README.md`, `backend/build.sh` | Confirmed |
| backend | docker compose up | `sh up.sh` | `backend/` | `backend/README.md`, `backend/up.sh` | Confirmed |
| backend | docker compose down | `sh down.sh` | `backend/` | `backend/README.md`, `backend/down.sh` | Confirmed |
| frontend | dev server | `npm run dev` | `frontend/` | `frontend/README.md`, `frontend/package.json` | Confirmed |
| frontend | build | `npm run build` | `frontend/` | `frontend/package.json` | Confirmed |
| frontend | start | `npm run start` | `frontend/` | `frontend/package.json` | Confirmed |
| frontend | lint | `npm run lint` | `frontend/` | `frontend/package.json` | Confirmed |

### 6.2 Unknown or conflicting commands (`REQUIRES CONFIRMATION`)
| Scope | Candidate command | Why unknown/conflicting | How to confirm |
|---|---|---|---|
| root | Monorepo unified build/test command | No root `Makefile`, root `package.json`, or CI workflow proving canonical root command | Confirm owner-preferred root command in root README or add explicit script wrapper |
| frontend | Preferred package manager command (`npm` vs `pnpm` vs `yarn`) | `frontend/README.md` shows multiple options; lockfile is `pnpm-lock.yaml` but `npm` commands are also documented | Confirm team standard and update `frontend/README.md` with single canonical command set |
| infra | Production deploy command/procedure | No CI/CD workflow manifests or deployment docs found in scan | Confirm via ops docs or add deployment runbook under `docs/` |

## 7) Governance Map (AGENTS and Local Rules)

### 7.1 AGENTS precedence chain
1. `AGENTS.md` controls repository-wide scope.
2. `backend/AGENTS.md` overrides root guidance for the `backend/` subtree.

### 7.2 Additional governance artifacts
| Artifact | Scope | Purpose | Status | Evidence |
|---|---|---|---|---|
| `.agents/skills/scan-project/SKILL.md` | root | full-repo scan workflow and output contract | Confirmed | `.agents/skills/scan-project/SKILL.md` |
| `.agents/skills/verify-change/SKILL.md` | root | lightweight cross-file/docs verification workflow | Confirmed | `.agents/skills/verify-change/SKILL.md` |
| `docs/agent-system-PRD.md` | root + backend governance planning | source-of-truth process design for AGENTS/skills evolution | Confirmed | `docs/agent-system-PRD.md` |

### 7.3 Practical implications for future agents
- Durable rules to keep in AGENTS (root-level + module-level):
  - Root: routing guidance, high-level repo conventions, cautious change policy.
  - Backend: layering rules, auth/locale/response conventions, backend validation expectations.
- Workflow/procedure content better kept in skills:
  - project scanning, planning, verification workflows requiring structured multi-step execution.

## 8) Risks, Unknowns, and Confirmation Plan
| Area | Unknown / risk | Impact | Required confirmation action | Owner/source |
|---|---|---|---|---|
| Deployment topology | production runtime/orchestration not documented | incorrect ops assumptions in future tasks | add deployment architecture doc under `docs/` or `backend/docs/` | project maintainer / ops owner |
| Frontend package-manager standard | lockfile suggests pnpm while README includes npm/yarn/pnpm/bun | inconsistent local setup and CI reproducibility risk | decide canonical tool and align README/scripts | frontend owner |
| Root command surface | no root-level unified build/test command | harder cross-module automation | define root task runner or explicit command matrix in root README | repo owner |
| Secret/env management process | `.backendEnv` usage is referenced but lifecycle/rotation not documented | environment drift and onboarding friction | document secret source and rotation policy (without committing secrets) | backend owner |

## 9) Suggested Inputs for Next Workflows
- For AGENTS updates:
  - Keep root/backend rule split; avoid duplicating backend specifics in root guidance.
- For skills updates:
  - Add missing root skills only when needed (`create-prd`, `plan-work`) with explicit trigger conditions.
- For PRD/planning:
  - Use this snapshot + `docs/agent-system-PRD.md` to anchor scope and unknown confirmations.
- For implementation/review handoff:
  - Begin from module-scoped commands in Section 6 and classify claims with status labels.

## 10) How to Update This Snapshot
1. Re-scan from repository root (topology first, then governance, then manifests/commands).
2. Refresh module inventory and deployable units table.
3. Refresh command inventory by scope from direct evidence only.
4. Reconcile root and module-level governance precedence.
5. Relabel each changed claim as Confirmed/Inferred/Unknown.
6. Remove stale facts that no longer have evidence.
7. Update unknowns with concrete `REQUIRES CONFIRMATION` actions.
