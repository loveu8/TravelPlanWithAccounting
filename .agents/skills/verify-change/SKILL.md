---
name: verify-change
description: Root-level lightweight verification for docs/config/rules/templates/AGENTS/skills and small-scope low-risk non-invasive changes; confirms completeness, consistency, references, paths, command claims, and unresolved risks with explicit VERIFIED/UNVERIFIED/GAP status.
---

# verify-change

## Description
Root-level, reusable **lightweight verification** skill for full-repo/monorepo handoff checks.

Use this skill to verify delivery readiness of:
- docs
- config
- rules/instructions
- templates
- AGENTS files
- skills
- small-scope, low-risk, non-invasive code changes

This is **not** an implementation skill and **not** a deep review skill.

## Purpose
Produce an evidence-based verification result that is clear, bounded, and handoff-ready.

Core goals:
1. Check completeness against stated intent.
2. Check consistency across changed artifacts and referenced sources.
3. Validate reference/path/command claims.
4. Separate verified facts from unverified assumptions.
5. Surface unresolved risks and required follow-up checks.

## When to Use
Use when you need high-level pre-handoff verification for:
- documentation updates
- repository instructions/rules/governance updates
- AGENTS or skill updates
- template/config updates
- small-scope code edits that do not change architecture, contracts, or security posture

## When Not to Use
Do **not** use this skill as the primary workflow for:
- high-risk backend patches
- security-sensitive/auth/payment/secret handling changes
- large refactors or cross-system rewrites
- new feature implementation
- deep correctness/performance/concurrency/security review

Escalate to a deeper review/implementation/testing workflow when risk or scope exceeds lightweight verification.

## Trigger Conditions
Trigger this skill when any of the following is true:
1. The request explicitly asks for verification/check/handoff-readiness.
2. The change set is primarily docs/config/rules/templates/AGENTS/skills.
3. The change set is small, low-risk, and non-invasive, but still needs explicit verification status.

## Inputs
Required inputs:
1. **Stated requirements / intended change** (what should be true after change).
2. **Changed files list** (actual touched files).
3. **Affected scope** (root-level, module-level, cross-module).
4. **Applicable rules/instructions** (AGENTS.md, repo instructions, task constraints).
5. **Claims to verify**:
   - path/file existence claims
   - section/heading/link/reference claims
   - command usage/execution claims
   - template/skill/AGENTS reference claims
6. **Known limitations** (environment, permissions, unavailable tools).

If inputs are missing:
- mark missing parts as `REQUIRES CONFIRMATION`
- continue only with verifiable checks
- do not invent assumptions as facts

## Verification Scope
### In Scope (high-level)
1. **Completeness**: changed content covers stated intent.
2. **Consistency**: no obvious conflict between changed files and related instructions/references.
3. **Reference validity**: referenced files/sections/skills/templates exist and are correctly named.
4. **Path validity**: referenced paths are real and reachable in current repo layout.
5. **Command-claim validity**:
   - confirmed only if actually executed successfully in this run
   - otherwise mark `UNVERIFIED` or `BLOCKED BY ENVIRONMENT`
6. **Risk surfacing**: unresolved risks and follow-up checks are explicitly listed.

### Out of Scope
- deep code correctness proof
- full regression testing
- architecture redesign
- refactor planning
- broad risk modeling across unrelated modules

If a “small” code change shows deeper risk (security, schema, API contract, cross-module impact), mark `OUT OF SCOPE` and recommend deeper review.

## Verification Steps
1. **Set Objective and Boundary**
   - Restate intended change and verification goal.
   - Confirm this request fits lightweight verification.
   - Flag `OUT OF SCOPE` items immediately.

2. **Map Change Set to Intent**
   - Inspect changed files.
   - Compare against stated requirements.
   - Identify missing required updates, extra unintended edits, or mismatch.

3. **Validate References and Claims**
   - Verify referenced file paths exist.
   - Verify referenced headings/sections/anchors if applicable.
   - Verify AGENTS/skill/template references point to valid targets.
   - Verify command claims only through executed evidence.

4. **Assign Status Per Item**
   - Label each check item with one status only:
     - `VERIFIED`
     - `GAP / INCONSISTENCY`
     - `UNVERIFIED`
     - `BLOCKED BY ENVIRONMENT`
     - `REQUIRES CONFIRMATION`
     - `OUT OF SCOPE`

5. **Summarize Risks and Follow-up**
   - List unresolved risks.
   - List exact follow-up checks needed.
   - Provide handoff recommendation based on evidence.

## Status Definitions
- `VERIFIED`: Confirmed by inspected files/content and/or successfully executed commands.
- `GAP / INCONSISTENCY`: Evidence shows missing requirement, contradiction, broken reference, or mismatch.
- `UNVERIFIED`: Not enough evidence collected yet; check not executed or not inspectable with current inputs.
- `BLOCKED BY ENVIRONMENT`: Could not verify due to environment/tool/access/runtime limitation.
- `REQUIRES CONFIRMATION`: Missing or ambiguous requirement/intent/scope needs human clarification.
- `OUT OF SCOPE`: Item requires deeper review/implementation workflow beyond this skill.

## Output Contract
Always output in this structure:

1. **Summary**
   - What was verified at high level.

2. **Verification Scope**
   - In-scope targets checked.
   - Explicit out-of-scope boundaries.

3. **Verified Items (`VERIFIED`)**
   - Bullet list with evidence source (file path and/or command).

4. **Gaps / Inconsistencies (`GAP / INCONSISTENCY`)**
   - What failed and why.

5. **Unverified Items (`UNVERIFIED`)**
   - What remains unproven.

6. **Environment Limitations (`BLOCKED BY ENVIRONMENT`)**
   - What was blocked and concrete reason.

7. **Requires Confirmation (`REQUIRES CONFIRMATION`)**
   - Missing decisions/inputs needed from user or owner.

8. **Follow-up Checks**
   - Exact next checks/commands/review needed.

9. **Handoff Recommendation**
   - `Ready with noted limitations` or `Not ready`.
   - Must align with statuses above.

## Boundaries
1. Do not implement fixes as part of verification unless explicitly requested.
2. Do not perform deep review while presenting it as lightweight verification.
3. Do not infer successful execution without command evidence.
4. Do not treat style preference as a verification gap unless it violates explicit rules.
5. Do not give optimistic “ready” conclusions for high-risk changes.
6. Do not hide uncertainty; label it with required status.

## Verification (Evidence and Traceability)
For every conclusion:
1. Link to concrete evidence (inspected file content, path check, executed command output).
2. Ensure each claimed command result has execution evidence.
3. Ensure requirement-to-change mapping is explicit.
4. Ensure unresolved risks are listed, not omitted.
5. If evidence is partial, downgrade status (`UNVERIFIED`/`REQUIRES CONFIRMATION`/`BLOCKED BY ENVIRONMENT`).

## Failure / Uncertainty / Environment Limitation Handling
- Command cannot run -> `BLOCKED BY ENVIRONMENT` with reason.
- Path exists but target content not fully validated -> `UNVERIFIED`.
- Reference target exists but semantic correctness unclear -> `UNVERIFIED` + follow-up.
- Requirement unclear or conflicting -> `REQUIRES CONFIRMATION`.
- Small code change appears riskier than expected -> `OUT OF SCOPE` + escalate to deeper review.

## What Not to Do
- Do not fabricate test, build, lint, or runtime outcomes.
- Do not mark “verified” based only on plausibility.
- Do not silently skip unresolved items.
- Do not expand into implementation/refactor/feature delivery work.
- Do not claim comprehensive correctness/security guarantees.
