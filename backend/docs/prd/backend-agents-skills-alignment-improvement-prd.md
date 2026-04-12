# Backend AGENTS × Skills Alignment Improvement PRD

## Summary
This PRD defines a governance-focused improvement package for backend agent orchestration.  
Goal: keep existing backend technical skill identifiers unchanged while formally introducing human-facing role taxonomy (`PDM/PJM/DEV/QA`) into routing, handoff, and workflow communication.

## Problem Statement
Current backend governance and skills are mostly aligned, but operational readability and cross-skill handoff clarity can still improve:

1. Human-facing role naming is not formalized in backend governance.
2. Some skill wording uses inconsistent technical naming (`scan-project` vs `scan-project-backend`).
3. Review and verification workflows are complementary but need clearer handoff focus.

## Desired Outcomes
1. Backend governance includes a formal alias taxonomy:
   - `PDM`, `PJM-Discovery`, `PJM-Execution`, `DEV`, `DEV-Refactor`, `DEV-Review`, `QA`.
2. Routing and recommended sequences adopt alias-first display with technical name in parentheses.
3. `review-change` explicitly outputs verification focus for `backend-test-verification`.
4. Technical skill names remain authoritative execution identifiers.

## Scope (In Scope)
1. Update `backend/AGENTS.md`:
   - add formal backend service taxonomy section;
   - update routing/sequence wording to alias-first format;
   - add review → verification handoff field.
2. Update backend skills where wording consistency is required:
   - normalize `scan-project-backend` naming in `plan-work`.
   - add explicit verification-focus handoff requirement in `review-change`.

## Non-Goals (Out of Scope)
1. Renaming physical skill directories or technical identifiers.
2. Changing backend application code behavior.
3. Merging `review-change` and `backend-test-verification` into one skill.
4. Introducing new dependencies, schema/auth flow changes, or architecture redesign.

## Affected Boundaries
1. Governance docs:
   - `backend/AGENTS.md`
2. Backend skill docs:
   - `backend/.agents/skills/plan-work/SKILL.md`
   - `backend/.agents/skills/review-change/SKILL.md`
3. PRD documentation:
   - this file

## User Scenarios
1. As a maintainer, I can read backend workflow using role labels (`PDM/PJM/DEV/QA`) without losing technical precision.
2. As an operator, I can route tasks consistently because role aliases map to exact technical skills.
3. As a reviewer/QA collaborator, I can hand off risk-based verification focus without ambiguity.

## Governance Behavior Definition
1. Alias-first communication format:
   - `<Role Alias> (<Technical Skill Name>)`
2. Routing source of truth:
   - Technical skill names remain canonical for invocation.
3. Quality dual-track:
   - `DEV-Review (review-change)` handles risk judgement.
   - `QA (backend-test-verification)` handles executable evidence and merge readiness.

## Validation / Escalation Impact
### Confirmed
1. Validation honesty policy remains unchanged.
2. Existing escalation gates remain in force.

### Potential
1. Alias misuse risk if documentation omits technical names.

### Mitigation
1. Keep alias-first + technical-name format in governance text.
2. Keep explicit rule: technical names win on conflict.

## Acceptance Criteria
- **AC-001**  
  **Given** backend governance docs are read by human operators  
  **When** they inspect routing and default sequences  
  **Then** each step is shown as role alias + technical skill name.

- **AC-002**  
  **Given** planning skill references scan skill  
  **When** `plan-work` is read  
  **Then** it references `scan-project-backend` consistently.

- **AC-003**  
  **Given** review output is handed to QA verification  
  **When** `review-change` output contract is applied  
  **Then** it includes explicit verification focus items with blocking/non-blocking labeling.

- **AC-004**  
  **Given** governance and skill naming are both present  
  **When** naming conflict is encountered  
  **Then** technical skill names are explicitly defined as source of truth.

## Risks
1. Over-abstract role naming could hide technical routing precision.
2. Partial adoption may create mixed terminology temporarily.

## Dependencies
1. Backend maintainers follow alias-first communication in future docs.
2. Future skill additions include alias mapping updates.

## Open Questions
1. Should future dashboards/automation expose alias names by default while storing technical names internally?

## Requires Confirmation
1. If future governance requires machine-readable service taxonomy (JSON/YAML), confirm target format and owning document before implementation.
