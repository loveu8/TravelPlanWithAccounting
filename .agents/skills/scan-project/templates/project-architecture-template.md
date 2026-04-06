# Project Architecture Snapshot

- Scan date: YYYY-MM-DD
- Repository: <repo-name>
- Scan scope: <full repo | subdirectory>
- Source commit: <git sha>

## 1) System Context (Confirmed)
- Product/domain objective:
- Primary users/actors:
- External systems/services:
- Evidence:

## 2) Containers / Deployable Units (Confirmed)
| Container | Path | Runtime/Framework | Responsibility |
|---|---|---|---|
| example-backend | backend/ | Spring Boot | API + domain services |

## 3) Component View (Confirmed + Inferred)
### 3.1 Confirmed components
- Component:
  - Path:
  - Responsibility:
  - Depends on:
  - Evidence:

### 3.2 Inferred components (mark clearly)
- Inference:
  - Why inferred:
  - What to verify:

## 4) Data and Integration Boundaries
- Datastores:
- Message/event integrations:
- Third-party APIs:
- Secrets/config boundaries:
- Evidence:

## 5) Build / Test / Run Commands (Confirmed)
| Purpose | Command | Source file/path |
|---|---|---|
| test | ./mvnw test | backend/AGENTS.md |

## 6) Agent Governance Map
- AGENTS precedence chain:
  1.
  2.
- Additional local rules (if any):

## 7) Risks / Unknowns / REQUIRES CONFIRMATION
- Unknown:
  - Why unknown:
  - How to confirm:

## 8) Suggested `AGENTS.md` Implications
- Durable rules worth keeping in AGENTS:
- Workflow details better kept in skills:

## 9) How to Update This Snapshot
1. Re-run scan-project.
2. Refresh module map and command table from manifests/scripts.
3. Update evidence links and unknowns.
4. Keep only currently true facts; remove stale statements.
