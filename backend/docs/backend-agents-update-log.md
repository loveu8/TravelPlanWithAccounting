# Backend AGENTS Update Log

## 2026-04-07 — Skill-aware governance optimization for `backend/AGENTS.md`

### Update goal
Optimize backend governance so `backend/AGENTS.md` stays durable-rule focused while adding clear, executable routing/handoff/escalation guidance aligned with existing backend skills.

### Files changed
- `backend/AGENTS.md`
- `backend/docs/backend-agents-update-log.md` (new)

### Why this update was needed
- Existing backend governance already had strong durable rules (source-of-truth, validation honesty, layering, risk escalation), but lacked explicit routing policy for backend skills.
- Skill ordering and cross-skill handoff expectations were implicit, which could lead to inconsistent execution between requirement shaping, planning, implementation, review, and verification.
- Escalation checkpoints existed as risk categories, but lacked explicit routing guidance when risks are discovered mid-workflow.

### New capabilities added to backend AGENTS
1. **Backend Skill Routing Policy**
   - Defines when to start with each backend skill:
     - `scan-project-backend`
     - `create-prd`
     - `plan-work`
     - `implement-backend-change`
     - `refactor-backend`
     - `review-change`
     - `backend-test-verification`
   - Adds routing guardrails to prevent premature implementation when context/requirements are unclear.

2. **Recommended Default Sequences**
   - Adds default (non-SOP) sequences for:
     - new feature with ambiguous requirements
     - new feature with clear PRD
     - bug fix in familiar module
     - bug fix in unfamiliar module
     - refactor-only work
     - high-risk change with rollback/escalation likelihood

3. **Skill Handoff Contract**
   - Defines minimum artifacts to hand off between skills (module map, PRD outputs, plan outputs, changed files, invariants, findings, verification classifications).
   - Makes skill-to-skill transitions auditable and reusable.

4. **Escalation Routing Clarification**
   - Adds routing actions for risk discovered during:
     - requirement shaping
     - planning
     - implementation/refactor
     - review/verification
   - Standardizes `REQUIRES CONFIRMATION` pause behavior.

### Deliberately kept out of AGENTS (left in skills)
To preserve AGENTS/skills boundaries, the following were intentionally **not** moved into `backend/AGENTS.md`:
- detailed multi-step execution procedures per skill
- full output templates/rubrics for review or verification
- command-by-command scan/report mechanics
- deep PRD drafting format details

These remain in skill definitions under `backend/.agents/skills/`.

### Compatibility and scope notes
- No backend skill core purpose was changed.
- No production code, dependency, schema, API, or security flow changes were made.
- Update is governance/documentation only and scoped to backend workflow consistency.
