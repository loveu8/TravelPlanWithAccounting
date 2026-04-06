# Project Architecture Snapshot

- Scan date: 2026-04-06
- Repository: TravelPlanWithAccounting
- Scan scope: full repository (frontend + backend + docs + skills)
- Source commit at scan start: 4fe8d4e

## 1) Directory and Module Distribution

### Confirmed
- Monorepo root contains three primary work areas:
  - `backend/`: Spring Boot API service.
  - `frontend/`: Next.js web app.
  - `docs/`: product/process and architecture-related docs.
- Agent governance and reusable workflows live under `.agents/skills/`.

Evidence:
- `AGENTS.md`
- `backend/pom.xml`
- `frontend/package.json`
- `docs/agent-system-PRD.md`
- `.agents/skills/scan-project/SKILL.md`

### Backend distribution (confirmed)
- Build/runtime files: `backend/pom.xml`, `backend/mvnw`, `backend/Dockerfile`, `backend/docker-compose.yml`.
- Source layout:
  - `backend/src/main/java/com/travelPlanWithAccounting/service/` (application code)
  - `backend/src/main/resources/` (i18n/static/config resources)
  - `backend/src/test/java/com/travelPlanWithAccounting/` (tests)
- Main backend packages include:
  - `controller`, `service`, `repository`, `mapper`, `dto`, `entity`
  - cross-cutting/support: `aspect`, `config`, `security`, `validator`, `message`, `exception`, `util`.

Evidence:
- `backend/src/main/java/com/travelPlanWithAccounting/service/` directory tree
- `backend/AGENTS.md`

### Frontend distribution (confirmed)
- Build/runtime files: `frontend/package.json`, `frontend/pnpm-lock.yaml`, `frontend/next.config.ts`, `frontend/Dockerfile`.
- Source layout:
  - `frontend/src/app/` uses App Router style (`layout.tsx`, route segments, API routes under `app/api/`)
  - localized route segment under `frontend/src/app/[lng]/`
  - shared UI components under `frontend/src/app/components/`
  - i18n resources under `frontend/src/app/i18n/locales/{en,zh}`
  - middleware at `frontend/src/middleware.ts`

Evidence:
- `frontend/src/app/` directory tree
- `frontend/src/middleware.ts`
- `frontend/src/app/api/_utils/http.ts`

## 2) Toolchain and Runtime Baseline

### Confirmed
- Backend:
  - Java 25 (`<java.version>25</java.version>`)
  - Spring Boot 4.0.4 parent
  - Spring Data JPA + PostgreSQL runtime dependency
  - Spring Security + JJWT
  - OpenAPI via `springdoc-openapi-starter-webmvc-ui`
- Frontend:
  - Next.js 15.3.0
  - React 19
  - TypeScript 5
  - `axios`, `@tanstack/react-query`, and `i18next` family

Evidence:
- `backend/pom.xml`
- `frontend/package.json`

## 3) Architecture and Data Boundaries

### Confirmed
- Backend architecture expectation is layered Controller → Service → Repository, with explicit backend scope guidance.
- Frontend includes server-side API route handlers (`app/api/**/route.ts`) that can act as BFF-style boundaries.
- Frontend API utility builds backend clients from `NEXT_PUBLIC_API_BASE_URL`, applies `Accept-Language`, and can attach access token from cookies.

Evidence:
- `backend/AGENTS.md`
- `frontend/src/app/api/_utils/http.ts`
- `frontend/src/app/api/auth/verify/route.ts`
- `frontend/src/app/api/members/auth-flow/route.ts`

### Inferred
- Request flow likely follows Browser → Next.js (UI + route handlers) → Spring Boot backend API.

Why inferred:
- Both Next.js route handlers and backend API service are present; no explicit deployment topology doc in this scan.

## 4) Confirmed Runnable Commands (from repository evidence)

| Area | Purpose | Command | Evidence |
|---|---|---|---|
| backend | test | `cd backend && ./mvnw test` | `AGENTS.md`, `backend/AGENTS.md` |
| backend | full build | `cd backend && ./mvnw clean install` | `AGENTS.md`, `backend/AGENTS.md` |
| backend | container build/run helper | `cd backend && sh build.sh` | `backend/README.md`, `backend/build.sh` |
| backend | compose up/down | `cd backend && sh up.sh` / `sh down.sh` | `backend/README.md`, `backend/up.sh`, `backend/down.sh` |
| frontend | dev | `cd frontend && npm run dev` | `frontend/README.md`, `frontend/package.json` |
| frontend | build | `cd frontend && npm run build` | `frontend/package.json` |
| frontend | lint | `cd frontend && npm run lint` | `frontend/package.json` |

## 5) AGENTS Scope / Governance Map

### Confirmed precedence
1. Root `AGENTS.md` applies to whole repository.
2. `backend/AGENTS.md` overrides rules for `backend/` subtree.
3. Reusable process skills are defined under `.agents/skills/`.

## 6) Unknowns and REQUIRES CONFIRMATION

- `REQUIRES CONFIRMATION`: authoritative production deployment topology (single host vs container orchestration vs cloud managed runtime).
- `REQUIRES CONFIRMATION`: whether frontend is always deployed with Next.js server mode or can be static/exported in any environment.
- `REQUIRES CONFIRMATION`: database migration ownership/process (no Flyway/Liquibase evidence found in scanned manifests).
- `REQUIRES CONFIRMATION`: CI pipeline definitions (no workflow files scanned in this pass).

## 7) Confidence and Scan Gaps

### High confidence (direct evidence)
- Module boundaries, major manifests, and technology versions.
- Backend layering intent and AGENTS precedence.
- Frontend app/api/i18n folder distribution.

### Gaps not deeply scanned in this pass
- Every individual route/page/component behavior under frontend.
- Every domain module interaction inside backend services.
- Runtime infrastructure/security hardening outside code repository files.

## 8) How to Update This Snapshot Quickly

1. Re-run structure scan for top-level and module-level directories (excluding heavy dependency folders like `frontend/node_modules`).
2. Re-read `AGENTS.md` and nested `backend/AGENTS.md` to refresh governance precedence.
3. Re-extract commands only from manifests/scripts/readmes (`package.json`, `pom.xml`, shell scripts, README docs).
4. Re-validate architecture claims against concrete file paths; classify each claim as Confirmed / Inferred / REQUIRES CONFIRMATION.
5. Update scan date and source commit hash.
