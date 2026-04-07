# Codex Skill Optimization Notes (for Backend Scan Skills)

Use these rules to keep skill execution accurate and token-efficient.

## Trigger quality
- Put trigger conditions in SKILL frontmatter `description`.
- Include both capability and context keywords (e.g., backend scan, architecture snapshot, command discovery).
- Avoid vague trigger text.

## Progressive disclosure
- Keep `SKILL.md` focused on workflow only.
- Move heavy detail to `references/`.
- Link reference files directly from `SKILL.md` (one-hop discovery).

## Context budget discipline
- Load only files needed for current task scope.
- Prefer concise checklist output over long prose.
- Avoid duplicating the same rule across AGENTS + SKILL + references.

## Determinism
- Use evidence-first wording: "from file X" and "command output shows".
- Mark unknowns as `REQUIRES CONFIRMATION`.
- Never infer commands not present in repo manifests/wrappers.
- Prefer fixed output skeletons/checklists to reduce omission risk.

## Codex operational alignment
- Reference: OpenAI Harness Engineering post (repository-embedded skills + agent workflows)
  - https://openai.com/index/harness-engineering/
- Local skill guidance source:
  - `/opt/codex/skills/.system/skill-creator/SKILL.md`

## Skill maintenance
- Keep SKILL under ~500 lines.
- Update references when backend architecture changes significantly.
- Review trigger description when user intents change (e.g., review-only vs implementation scan).
- Add a short "common misses" checklist whenever omissions are observed in real usage.
- Avoid fragile external links (e.g., hardcoded non-default branches); keep branch-agnostic reference style.
