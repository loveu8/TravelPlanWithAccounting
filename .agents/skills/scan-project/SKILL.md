---
name: scan-project
description: Use when entering an unfamiliar repo or before generating/updating AGENTS.md and skills; map real structure, commands, and constraints without guessing.
---

# scan-project

## Purpose
Produce a factual project scan to support planning or rule updates.

## Trigger Conditions
- First task in an unfamiliar repository.
- Before creating/updating `AGENTS.md` or `SKILL.md` files.

## Inputs
- User request
- Existing repository files

## Steps
1. Identify top-level directories and key docs.
2. Detect existing `AGENTS.md` files and scope hierarchy.
3. Identify runnable commands from real scripts/manifests.
4. Separate confirmed facts from assumptions.

## Outputs
- Directory and module summary
- Confirmed build/test/run commands
- Known constraints and risks
- `REQUIRES CONFIRMATION` list

## Boundaries
- Never fabricate commands or architecture details.
- Keep scan concise and actionable.

## Verification
- Re-check cited file paths exist.
- Ensure each command/output claim is evidence-based.
