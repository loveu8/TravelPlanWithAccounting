# AGENTS.md

## Scope

This file applies to the `backend/` subtree only.

- Backend work should follow both `project-root/AGENTS.md` and this file.
- If a root rule and a backend rule overlap, the backend rule wins for files under `backend/` because it is more specific.
- This file is for **durable backend rules** only. Multi-step workflows, reusable procedures, and task-specific playbooks should live in repo skills under `.agents/skills/`.

---

## Purpose

Use this file to keep backend work consistent, safe, and aligned with the existing codebase.

When handling backend tasks, prioritize these goals:

1. Preserve existing architecture and conventions.
2. Reuse current utilities and patterns before creating new ones.
3. Keep Controller thin and business logic in Service.
4. Avoid silent breaking changes to API, database, security, or dependency setup.
5. Validate changes with real commands before final handoff.

---

## Source of Truth

When this file conflicts with the real repository state, follow the repository and explicitly note the mismatch.

Use these as the source of truth, in order:

1. `backend/pom.xml`
2. `backend/mvnw` and real Maven configuration
3. Actual package structure under `src/main/java`
4. Existing tests and current implementation patterns
5. `backend/docs/` and related migration notes

Do not invent package paths, commands, profiles, or dependency versions.
If something cannot be confirmed from the repo, mark it clearly as `REQUIRES CONFIRMATION` instead of guessing.

---

## Technology Baseline

Known backend baseline:

```text
Java: 25
Spring Boot: 4.0.4
OpenAPI: springdoc-openapi 3.0.1
JWT: jjwt 0.13.0
Database: PostgreSQL + Spring Data JPA
Apache HttpClient: 4.5.14
Other: HikariCP, Jackson Hibernate6, Gson, Spring Security, Spring Mail,
       spring-boot-starter-aspectj
```

Important notes:

- For Spring Boot 4, AOP should use `spring-boot-starter-aspectj`, not `spring-boot-starter-aop`.
- If version-related behavior matters, verify against `pom.xml` before changing code or docs.
- Do not change Spring Boot parent version, plugin versions, or dependency versions unless the task explicitly requires it and approval has been given.

Reference materials:

- `docs/spring-boot-4-upgrade-plan.md`
- `.cursor/rules/spring-boot-4-aop-migration.mdc`

---

## Required Working Style

### Before editing code

For any of the following cases, do **not** jump straight into implementation:

- new feature development
- ambiguous product requirement
- unfamiliar module or flow
- refactor request
- security-sensitive change
- API contract change
- database/schema change

First do the minimum needed discovery:

1. inspect the affected packages and existing examples
2. identify the entry points, services, repositories, DTOs, and config involved
3. summarize the impacted files and constraints
4. only then implement

Use backend skills for reusable workflows instead of duplicating multi-step SOP in this file.

---

## Backend Skill Routing Policy

Use this section to decide **which backend skill to call first**.  
Keep detailed execution steps inside each skill.

### Skill entry conditions

- `scan-project-backend`
  - Use first when backend module boundaries, command map, or architecture context are unclear.
  - Typical trigger: unfamiliar repo/module, outdated architecture snapshot, or cross-module task.
- `create-prd`
  - Use when requirement intent, acceptance criteria, or scope/non-goals are ambiguous.
  - Typical trigger: new feature with unclear behavior, unclear API/data/auth expectation.
- `plan-work`
  - Use after PRD/bug context is known but execution order, risk checkpoints, and validation sequence are not finalized.
  - Typical trigger: “plan before coding”, “define sequence/risk/validation”.
- `implement-backend-change`
  - Use only when scope + acceptance criteria are explicit and implementation is approved.
  - Typical trigger: concrete backend coding task with bounded targets.
- `refactor-backend`
  - Use for refactor-only work where external behavior must remain unchanged.
  - Typical trigger: technical debt cleanup, deduplication, structure/readability/testability improvement.
- `review-change`
  - Use when a patch/diff is ready for correctness/security/maintainability/testing-risk assessment.
  - Typical trigger: pre-merge review, severity-based findings request, delivery gate decision.
- `backend-test-verification`
  - Use when verification evidence is needed for PR/CI/merge readiness.
  - Typical trigger: run/summarize Maven checks, classify blocking vs non-blocking verification results.

### Routing guardrails

1. If requirement is unclear, do **not** start implementation skill first; route to `create-prd`.
2. If module context is unclear, do **not** force planning/implementation; route to `scan-project-backend`.
3. For tiny, obvious, localized bug fixes, direct implementation can be acceptable, but still follow escalation and validation rules.
4. For medium/high-risk tasks, include both `review-change` and `backend-test-verification` before delivery recommendation.

---

## Recommended Default Sequences

These are default paths, not rigid SOP. Keep detailed sub-steps in skills.

1. **New feature + ambiguous requirement**
   - `scan-project-backend` (if context unclear) → `create-prd` → `plan-work` → `implement-backend-change` → `review-change` → `backend-test-verification`
2. **New feature + clear PRD**
   - `scan-project-backend` (only if module context unclear) → `plan-work` → `implement-backend-change` → `review-change` → `backend-test-verification`
3. **Bug fix in familiar module**
   - `plan-work` (lightweight) → `implement-backend-change` → `backend-test-verification`
4. **Bug fix in unfamiliar module**
   - `scan-project-backend` → `plan-work` → `implement-backend-change` → `review-change` → `backend-test-verification`
5. **Refactor-only work**
   - `scan-project-backend` (if context unclear) → `plan-work` → `refactor-backend` → `review-change` → `backend-test-verification`
6. **High-risk change (rollback/escalation likely)**
   - `scan-project-backend` → `create-prd` (if requirement/impact ambiguity exists) → `plan-work` (must include escalation checkpoints) → `implement-backend-change` or `refactor-backend` → `review-change` → `backend-test-verification`
   - If any checkpoint triggers risk gates in this file, stop and mark `REQUIRES CONFIRMATION` before continuing.

---

## Skill Handoff Contract

When handing off from one skill to another, preserve these minimum outputs.

- `scan-project-backend` → next skill
  - module map
  - command map
  - architecture snapshot
  - unknowns marked `REQUIRES CONFIRMATION`
- `create-prd` → next skill
  - problem statement
  - scope / non-goals
  - acceptance criteria
  - risks / dependencies / open questions
  - explicit `REQUIRES CONFIRMATION` items
- `plan-work` → next skill
  - task type and risk level
  - ordered execution sequence
  - validation plan (commands + pass criteria)
  - escalation checkpoints
- `implement-backend-change` → next skill
  - changed files
  - impact summary (contract unchanged/changed)
  - commands run + outcomes
  - remaining risks / unverified areas
- `refactor-backend` → next skill
  - preserved invariants checklist
  - files changed by slice
  - validation summary
  - remaining risks / `BLOCKED` / `RISKY` items
- `review-change` → next skill / final handoff
  - findings with severity
  - evidence locations
  - delivery recommendation
  - known unknowns / missing evidence
- `backend-test-verification` → final handoff
  - verified commands
  - pass/fail/warn/flaky/skipped/missing-evidence classification
  - merge recommendation from verification perspective
  - missing evidence and follow-ups

### During implementation

- Prefer the smallest safe change that fits the current architecture.
- Reuse existing components before adding new abstractions.
- Match the naming, packaging, and code style already used in nearby files.
- Keep changes localized unless the task explicitly requires broader refactoring.

### Before final handoff

Always report:

1. what changed
2. which files were touched
3. which commands were run
4. whether tests/build passed or failed
5. any remaining risks, assumptions, or unverified areas

---

## Architecture and Layering

### Core layering

Use Controller → Service → Repository layering.

- `controller/`: request/response handling only
- `service/`: business logic and orchestration
- `repository/`: persistence through Spring Data JPA
- `mapper/`: DTO ↔ Entity conversion
- `dto/`: request/response models
- `validator/`: validation helpers and custom validators
- `config/`: application-wide configuration
- `aspect/`: cross-cutting logic such as auth and locale handling

### Layering rules

- Do not place business logic in Controllers.
- Do not let Controllers directly orchestrate persistence-heavy flows.
- Do not bypass Service and write complex logic inside Repository.
- Keep DTO mapping centralized in `mapper/` or the established local pattern.
- Prefer extending the existing architecture over introducing a parallel pattern.

---

## Authentication and Authorization

- Endpoints that require login should use `@AccessTokenRequired`.
- Obtain the current user through injected `AuthContext` and the established `memberId` flow.
- JWT verification should go through the existing `AccessTokenAspect` + `JwtUtil` flow.
- Token-related operations should reuse `service/util/TokenUtil` or the repository’s established token utilities.

Do not:

- manually parse the `Authorization` header when the standard flow already exists
- bypass existing aspect-based auth checks
- introduce a separate token validation path unless the task explicitly requires it

Any change that affects authentication, authorization, token issuance, token validation, or security defaults requires confirmation before implementation.

---

## Locale and i18n

- Locale should be derived from `Accept-Language` through the existing locale mechanism.
- Use `LocaleContextHolder.getLocale()` to read the current locale.
- Use `MessageSourceHolder.getMessage(...)` for localized messages.
- Default locale is `Locale.TAIWAN` (`zh-TW`), with `en-US` also supported.

Do not:

- parse locale manually from headers if the aspect/config already handles it
- hardcode user-facing multilingual text in application code
- create a separate locale resolution path without an explicit reason

When adding or changing message keys:

1. update `MessageCode` or the equivalent code registry
2. update both `messages_zh_TW.properties` and `messages_en_US.properties`
3. verify the new message path is actually used

---

## Response and Exception Handling

- Use the established `RestResponse` response model.
- Prefer `RestResponseUtils.success(...)` and `RestResponseUtils.error(...)`.
- Let `ResponseBodyWrapperAdvice` and related response advice handle standard wrapping.
- Use `ApiException` + `MessageCode` for business/API error signaling.
- Let `GlobalExceptionHandler` handle exception translation consistently.

Do not:

- return ad hoc response shapes
- bypass the standard response wrapper without a strong reason
- catch and swallow exceptions in Controllers just to shape output manually
- use `ResponseEntity` as a parallel response style when the standard flow already covers the case

If a route truly must bypass the wrapper, document the reason in code comments and the final handoff.

---

## Validation, DTOs, and API Docs

- Use `jakarta.validation` annotations for request validation.
- Keep DTOs simple and consistent with the existing project style.
- Use `record` or Lombok patterns only where they match the codebase’s established conventions.
- Maintain Swagger / OpenAPI annotations such as `@Operation`, `@Tag`, and `@Parameter` when modifying public API behavior.

Whenever an API request/response contract changes:

1. update DTOs and validation rules
2. update API annotations as needed
3. update related docs in `backend/docs/` if applicable
4. call out contract changes explicitly in the handoff

---

## Reuse Existing Utilities First

Before adding a new util/helper, search the existing codebase.

Prefer existing utilities under `service/util`, `service/validator`, `service/dto`, and nearby packages.

Known reusable examples include:

- `TokenUtil`
- `UuidGeneratorUtils`
- `RestResponseUtils`
- `JsonHelper`
- `EmailValidatorUtil`
- `PoiTypeMapper`
- `LangTypeMapper`
- `LocationHelper`

Also check existing config/services for patterns such as:

- `GoogleRequestFactory`
- `MailConfig`
- `CacheConfig`
- `CacheConstants`
- `PoiLanguageEnrichmentPublisher`
- `CacheCleanupService`

Do not add a new utility class just because it feels cleaner.
Only introduce one when there is no suitable reusable implementation and the new abstraction is clearly justified.

---

## External Integrations and Configuration

When touching external integrations:

- follow existing factories/config classes first
- keep credentials and environment-specific values out of code
- preserve current configuration style unless the task explicitly changes it

Be cautious with changes involving:

- Google API integration
- mail sending
- caching behavior
- scheduled/background jobs
- HTTP client configuration
- security configuration

These often have runtime side effects beyond the edited file.

---

## Dependencies

Dependency management rules:

- Prefer existing dependencies and current Spring Boot ecosystem defaults.
- New dependencies must have an explicit version when that is the repository convention or when the BOM does not manage them.
- Any new dependency must be checked for compatibility with the current Spring Boot baseline.
- Avoid large, overlapping, or redundant libraries.

Requires confirmation before:

- adding a new dependency
- changing dependency versions
- changing Maven plugins or parent version
- replacing a foundational library already used across the project

---

## Commands

Run commands from `backend/` unless the task clearly requires another working directory.

Preferred order:

```bash
# Full build and test
./mvnw clean install

# If Maven Wrapper is unavailable in the environment
mvn clean install

# Tests only
./mvnw test
mvn test
```

Guidelines:

- After backend code changes, run the most relevant validation commands you can actually execute.
- Prefer `./mvnw` when available to reduce environment drift.
- If full build is too expensive for a tiny docs-only change, say so explicitly.
- Never claim a command passed unless you actually ran it and saw the result.

---

## Validation Expectations

### Minimum expectation

For any backend code change, run at least one real validation step unless the environment blocks execution.

### Strong expectation

For meaningful code changes, aim to run:

1. targeted tests if available
2. `./mvnw test` or `mvn test`
3. `./mvnw clean install` or `mvn clean install` before final handoff when feasible

### If validation is blocked

State clearly:

- which command could not be run
- why it could not be run
- what remains unverified

Do not hide validation gaps.

---

## Project Structure Reference

```text
backend/src/main/java/com/travelPlanWithAccounting/service/
├── controller/          # REST API entry points
├── service/             # business logic layer
├── repository/          # Spring Data JPA persistence
├── dto/                 # request/response models
├── mapper/              # DTO / Entity mapping
├── util/                # shared utility classes
├── validator/           # validators
├── aspect/              # AOP, including auth and locale aspects
├── security/            # JWT and auth-related helpers
├── config/              # global configuration
│   ├── advice/          # response wrapper, exception handling
│   └── other config classes such as i18n/cache/mail
├── constant/            # constants
├── exception/           # custom exceptions
├── message/             # message codes and related handling
└── entity/              # JPA entities

backend/src/main/resources/
├── i18n/                # localized message bundles
└── application-*.yml    # environment configs

backend/docs/            # API and process documentation
```

If the actual package layout differs, follow the real repo and note the discrepancy.

---

## Risk Escalation

Stop and ask for confirmation before making any of these changes:

1. database schema or migration changes
2. public API contract changes that may break clients
3. auth / permission / token flow changes
4. dependency additions or version changes
5. major refactoring across modules
6. cache semantics changes
7. mail, external API, or background job behavior changes
8. deleting or moving critical configuration files
9. changing Spring Boot parent version or core framework setup

### Escalation routing rules

- If risk is discovered during requirement shaping, return to `create-prd` and add `REQUIRES CONFIRMATION`.
- If risk is discovered during planning, `plan-work` must add an explicit checkpoint and pause before implementation.
- If risk is discovered during implementation/refactor:
  1. stop further edits that depend on the risky decision,
  2. document impacted files/contracts,
  3. mark `REQUIRES CONFIRMATION`,
  4. continue only after confirmation.
- If review/verification finds blocking evidence, recommendation must be non-ship/block-merge until resolved or explicitly accepted.

Small internal refactors or bug fixes may proceed without confirmation only when:

- the scope is localized
- the behavior is well understood
- the contract does not change
- the validation path is clear

---

## Documentation Sync

Update docs when the change affects any of the following:

- API behavior or request/response shape
- configuration or setup steps
- Spring Boot 4 migration constraints
- message keys / user-visible error behavior
- major architectural decisions

At minimum, review whether related files in `backend/docs/` should change.
If docs are intentionally left unchanged, say why.

---

## Submission Checklist

Before finalizing a backend change, check these items:

1. `mvnw` / Maven command results are reported honestly.
2. API annotations are updated when API behavior changed.
3. Message codes and i18n files are updated when new errors/messages were introduced.
4. Response shape still follows the established `RestResponse` pattern.
5. Reused existing utilities and patterns where possible.
6. New dependencies or version changes were approved if needed.
7. Related docs were reviewed and updated when necessary.
8. File/class naming is consistent with nearby code.
9. Remaining risks, assumptions, and unverified areas are stated.

---

## Safety and Permissions

### Allowed without extra confirmation

- read repository files
- edit backend code within task scope
- run safe build/test commands
- update backend documentation tied to the change

### Requires confirmation first

- dependency changes
- major architecture changes
- schema or migration changes
- security/auth flow changes
- destructive file deletion
- broad refactors across modules
- changes with unclear production/runtime impact

---

## When Stuck

1. Search for an existing implementation before creating a new pattern.
2. Check nearby Controller / Service / Repository examples.
3. Inspect `service/util`, `config`, `validator`, and `message` first.
4. Read relevant docs under `backend/docs/`.
5. Verify real dependency/config state in `pom.xml`.
6. If multiple valid designs exist, present the trade-offs briefly and ask for direction.
7. If the repo and this file disagree, follow the repo and report the mismatch.

---

## What This File Should Not Contain

This file should not become a giant workflow manual.

Do not move these into `backend/AGENTS.md` unless there is a strong reason:

- full PRD writing procedures
- detailed scan/report templates
- long review scoring rubrics
- multi-step orchestration logic
- model/provider/sandbox/approval configuration
- optional subagent definitions

Those belong in skills, task-specific docs, or `.codex/config.toml`.
