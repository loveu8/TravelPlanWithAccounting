# GitHub Java Backend Pattern Notes

Use these references to calibrate scan findings against common Java backend repo layouts.

## 1) Layered monolith baseline
- Reference: `spring-petclinic/spring-petclinic-rest`
- URL: https://github.com/spring-petclinic/spring-petclinic-rest
- Useful paths:
  - Repo root: https://github.com/spring-petclinic/spring-petclinic-rest
  - Then inspect default branch folders: `src/main/java`, `src/test/java`
- What to borrow for scans:
  - Check `src/main/java` + `src/test/java` separation.
  - Verify whether controllers/services/repositories are organized by technical layer or by domain package.
  - Verify API docs/testing and build wrapper (`mvnw`) existence.

## 2) Microservices repo split baseline
- Reference: `spring-petclinic/spring-petclinic-microservices`
- URL: https://github.com/spring-petclinic/spring-petclinic-microservices
- Useful paths:
  - Repo root: https://github.com/spring-petclinic/spring-petclinic-microservices
  - Then inspect default branch folders: `spring-petclinic-api-gateway`, `spring-petclinic-config-server`
- What to borrow for scans:
  - Detect if repository is single-service or multi-service.
  - Check for infra modules (gateway/config/discovery/observability).
  - Check whether each service keeps independent ownership boundaries.

## 3) Domain-modular monolith baseline
- Reference: `spring-projects/spring-modulith`
- URL: https://github.com/spring-projects/spring-modulith
- Useful paths:
  - https://github.com/spring-projects/spring-modulith/tree/main/spring-modulith-examples
- What to borrow for scans:
  - Verify root package and direct sub-packages as module boundaries.
  - Verify module-level tests and architecture verification patterns.
  - Distinguish module boundaries from pure technical-layer boundaries.

## How to use during scan
1. Do not force-fit the project to any pattern.
2. Pick closest baseline and explain why with concrete file evidence.
3. If mixed pattern exists, mark it as hybrid and list implications only.
4. Treat references as heuristics; repository evidence remains source of truth.
5. Check repository default branch before opening branch-specific paths.
