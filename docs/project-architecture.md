# Project Architecture Snapshot

- Scan date: 2026-04-05
- Repository: TravelPlanWithAccounting
- Scan scope: full repository
- Source commit: 0599ee0

## 1) System Context

### Confirmed
- Repository is positioned as a travel itinerary + accounting project (`TravelPlanWithAccounting`).
- It contains a Spring Boot backend service and a Next.js frontend application.
- Main backend domain package is `com.travelPlanWithAccounting.service`.

Evidence:
- `README.md`
- `backend/pom.xml`
- `frontend/package.json`
- `backend/src/main/java/com/travelPlanWithAccounting/service/*`

### Inferred
- External actors likely include end users through Web UI and REST API clients.

### REQUIRES CONFIRMATION
- Exact production deployment topology and external runtime dependencies (cloud services, ingress, observability stack).

## 2) Containers / Deployable Units

| Container | Path | Runtime/Framework | Responsibility |
|---|---|---|---|
| backend-service | `backend/` | Java 25, Spring Boot 4.0.4, Spring Data JPA | Core API and domain/business logic |
| frontend | `frontend/` | Next.js 15, React 19, TypeScript | Web UI and client-side integration |

Evidence:
- `backend/pom.xml`
- `frontend/package.json`

## 3) Component View

### 3.1 Confirmed components
- Backend layered components exist under `service/` package:
  - `controller/`
  - `service/`
  - `repository/`
  - `mapper/`
  - `dto/`
  - `validator/`
  - plus cross-cutting areas (`aspect/`, `config/`, `security/`, `exception/`, `message/`).

Evidence:
- `backend/src/main/java/com/travelPlanWithAccounting/service/` directory tree
- `backend/AGENTS.md`

### 3.2 Inferred components
- Frontend likely follows Next.js app-router conventions and consumes backend APIs via `axios`.

Why inferred:
- Dependency list includes `next`, `react`, and `axios`; detailed route/component mapping not fully scanned.

What to verify:
- `frontend/src` routing layout and API client boundaries.

## 4) Data and Integration Boundaries

### Confirmed
- Backend uses PostgreSQL through Spring Data JPA.
- Security/auth infrastructure exists (`spring-boot-starter-security`, JWT dependencies).
- OpenAPI UI support is included through `springdoc-openapi-starter-webmvc-ui`.

Evidence:
- `backend/pom.xml`
- `backend/AGENTS.md`

### REQUIRES CONFIRMATION
- Actual DB schema ownership and migration mechanism (Flyway/Liquibase/manual).
- Third-party API endpoints and outbound integration contracts.
- Cache/message broker runtime topology.

## 5) Build / Test / Run Commands (Confirmed)

| Purpose | Command | Source |
|---|---|---|
| backend test | `cd backend && ./mvnw test` | `AGENTS.md` |
| backend build | `cd backend && ./mvnw clean install` | `AGENTS.md` |
| frontend dev | `cd frontend && npm run dev` | `frontend/README.md` + `frontend/package.json` |
| frontend build | `cd frontend && npm run build` | `frontend/package.json` |
| frontend lint | `cd frontend && npm run lint` | `frontend/package.json` |

## 6) Agent Governance Map

AGENTS precedence chain:
1. `AGENTS.md` (repository-wide)
2. `backend/AGENTS.md` (overrides root rules under `backend/`)

Additional note:
- Root repository defines reusable skills in `.agents/skills/`.

## 7) Risks / Unknowns / REQUIRES CONFIRMATION

- Unknown: production deployment architecture.
  - Why unknown: no infra/deployment manifest scanned in this pass.
  - How to confirm: inspect CI/CD pipelines, IaC, or ops docs.
- Unknown: authoritative architecture decision history.
  - Why unknown: no ADR folder convention explicitly found in this pass.
  - How to confirm: search for `adr` or decision logs under `docs/`.

## 8) Suggested `AGENTS.md` Implications

Durable rules suitable for AGENTS:
- Keep backend layering (Controller → Service → Repository).
- Require evidence-based command extraction from manifests/docs.
- Keep unknowns explicitly labeled as `REQUIRES CONFIRMATION`.

Workflow details better kept in skills:
- Exact scan command sequence.
- Architecture snapshot generation checklist.
- Refresh cadence and diff review process for architecture docs.

## 9) How to Update This Snapshot

1. Re-run `scan-project` skill.
2. Refresh module map from real directories and manifests.
3. Update command table from scripts/wrappers.
4. Keep only confirmed facts and mark unknowns clearly.
5. Reconcile with current `AGENTS.md` precedence and repository conventions.
