# Backend Project Architecture Snapshot

- Scan date: YYYY-MM-DD
- Scope: `backend/`
- Scanner: Codex (`scan-project-backend`)
- Execution mode: `repo-root | backend-dir`
- Evidence path base: `backend/... | ./...`

## 1. Summary
- Backend style classification: `Layered monolith | Domain-modular monolith | Microservices | Hybrid`
- Confidence: `High | Medium | Low`
- Notes:

## 2. Confirmed facts
- Build/runtime baseline:
  - Evidence files:
  - Evidence commands:
- Package/layer map:
  - Evidence files:
  - Evidence commands:
- API/security/i18n/response conventions:
  - Evidence files:
  - Evidence commands:

## 3. Inferred facts
- Inference:
- Why inferred:
- Evidence files:
- Evidence commands:

## 4. Unknowns (`REQUIRES CONFIRMATION`)
- Item:
- Why unknown:
- How to verify:

## 5. Command map (confirmed only)
- Build:
- Test:
- Run:
- Tooling:
- Evidence files:

## 5.1 Command execution notes
- Successful commands:
- Failed commands and reasons:
- Unverified areas caused by command limitations:

## 6. Risks and constraints
- Contract/DB/security/dependency risks:
- Operational risks:

## 7. Backend pattern comparison (GitHub baselines)
- Closest baseline:
- Similarities:
- Differences:
- Potential implications:

## 8. Convention compliance check
- Maven standard layout: `Pass | Partial | Fail`
  - Evidence:
- Spring package/root scanning convention: `Pass | Partial | Fail`
  - Evidence:
- GitHub discoverability (README/docs/commands): `Pass | Partial | Fail`
  - Evidence:

## 9. How to update this snapshot
1. Re-run backend scan steps from skill.
2. Refresh changed sections only.
3. Keep Confirmed/Inferred/Unknown separation strict.
4. Update scan date.

## 10. Citation format requirement
- Use repository-relative file path citations when reporting scan results.
- Recommended format: `【F:<path>†Lx-Ly】`.

## 11. AI usability checklist
- Keep headings unchanged for downstream parsing.
- Keep status values deterministic: `Pass | Partial | Fail`.
- Keep unknowns explicit with `REQUIRES CONFIRMATION`.
