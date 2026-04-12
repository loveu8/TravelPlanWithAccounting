# Project Architecture Snapshot

- Scan date: YYYY-MM-DD
- Repository: <repo-name>
- Scan scope: <full repo | subdirectory>
- Source commit: <git sha>
- Scanner: <agent/version>

## 1) Scan Scope and Evidence Policy
- Scope included:
- Scope excluded:
- Evidence policy:
  - `Confirmed` = directly supported by repository evidence.
  - `Inferred` = plausible from partial evidence; not yet directly proven.
  - `Unknown / REQUIRES CONFIRMATION` = missing or conflicting evidence.
- Primary evidence files:

## 2) Repository Topology and Module Map

### 2.1 Top-level directory map (Confirmed)
| Path | Type | Role | Status | Evidence |
|---|---|---|---|---|
| backend/ | module | backend service(s) | Confirmed | <path> |
| frontend/ | module | frontend app(s) | Confirmed | <path> |
| docs/ | docs | product/technical docs | Confirmed | <path> |
| .agents/ | governance | agent skills/rules | Confirmed | <path> |

### 2.2 Module/package inventory (Confirmed + Inferred)
| Unit | Kind (app/service/package/worker/docs/infra) | Path | Depends on | Deployable | Status | Evidence |
|---|---|---|---|---|---|---|
| <unit-name> | <kind> | <path> | <deps or unknown> | <yes/no/unknown> | <Confirmed/Inferred/Unknown> | <path> |

## 3) System Context and Runtime Boundaries

### 3.1 System context (Confirmed)
- Product/domain objective:
- Primary users/actors:
- External systems/services:
- Evidence:

### 3.2 Runtime/deployment boundaries (Confirmed + Inferred)
- Frontend runtime boundary:
- Backend runtime boundary:
- Worker/batch/runtime boundary:
- Infra/deploy boundary:
- Status + evidence for each boundary:

## 4) Applications / Services / Packages / Deployable Units
| Unit | Path | Runtime/Framework | Responsibility | Interfaces (API/UI/Event) | Data stores | Third-party integrations | Deploy target | Status | Evidence |
|---|---|---|---|---|---|---|---|---|---|
| <frontend-app> | <path> | <runtime or unknown> | <summary> | <summary> | <summary> | <summary> | <summary> | <Confirmed/Inferred/Unknown> | <path> |
| <backend-service> | <path> | <runtime or unknown> | <summary> | <summary> | <summary> | <summary> | <summary> | <Confirmed/Inferred/Unknown> | <path> |
| <shared-package> | <path> | <runtime or unknown> | <summary> | <summary> | n/a | <summary> | n/a | <Confirmed/Inferred/Unknown> | <path> |

## 5) Interaction and Data Boundaries

### 5.1 Frontend ↔ Backend interactions
| From | To | Interface | Contract location | Auth boundary | Status | Evidence |
|---|---|---|---|---|---|---|
| <frontend> | <backend> | <REST/GraphQL/RPC/Event/unknown> | <path/unknown> | <boundary/unknown> | <Confirmed/Inferred/Unknown> | <path> |

### 5.2 Backend ↔ Data stores / third-party
| From | To | Interface | Secrets/config path | Failure boundary | Status | Evidence |
|---|---|---|---|---|---|---|
| <backend> | <db/queue/external api> | <driver/protocol> | <path/unknown> | <summary/unknown> | <Confirmed/Inferred/Unknown> | <path> |

### 5.3 Config, secrets, and environment boundaries
- Config entry points (root + module-level):
- Secrets handling locations:
- Environment split (dev/stage/prod) evidence:
- Unknowns:

## 6) Commands Inventory (Evidence-based)

### 6.1 Confirmed runnable commands
| Scope (root/frontend/backend/shared/infra/docs) | Purpose | Command | Working directory | Source file/path | Status |
|---|---|---|---|---|---|
| root | <build/test/lint/run/etc> | <command> | <dir> | <path> | Confirmed |

### 6.2 Unknown or conflicting commands (`REQUIRES CONFIRMATION`)
| Scope | Candidate command | Why unknown/conflicting | How to confirm |
|---|---|---|---|
| <scope> | <command> | <reason> | <file/owner/command> |

## 7) Governance Map (AGENTS and Local Rules)

### 7.1 AGENTS precedence chain
1. `<path>` controls `<scope>`
2. `<path>` overrides `<parent path>` for `<scope>`

### 7.2 Additional governance artifacts
| Artifact | Scope | Purpose | Status | Evidence |
|---|---|---|---|---|
| <docs/template/skill/policy> | <scope> | <purpose> | <Confirmed/Inferred/Unknown> | <path> |

### 7.3 Practical implications for future agents
- Durable rules to keep in AGENTS (root-level + module-level):
- Workflow/procedure content better kept in skills:

## 8) Risks, Unknowns, and Confirmation Plan
| Area | Unknown / risk | Impact | Required confirmation action | Owner/source |
|---|---|---|---|---|
| <module> | <unknown> | <impact> | <next step> | <file/person/team> |

## 9) Suggested Inputs for Next Workflows
- For AGENTS updates:
- For skills updates:
- For PRD/planning:
- For implementation/review handoff:

## 10) How to Update This Snapshot
1. Re-scan from repository root (topology first, then governance, then manifests/commands).
2. Refresh module inventory and deployable units table.
3. Refresh command inventory by scope from direct evidence only.
4. Reconcile root and module-level governance precedence.
5. Relabel each changed claim as Confirmed/Inferred/Unknown.
6. Remove stale facts that no longer have evidence.
7. Update unknowns with concrete `REQUIRES CONFIRMATION` actions.
