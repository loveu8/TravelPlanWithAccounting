# Industry & GitHub Convention Checks for Java Backend Scan

Use this checklist to validate whether backend structure follows common conventions.

## A) Maven standard directory layout (official)
- Source: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
- Expected:
  - `src/main/java`
  - `src/main/resources`
  - `src/test/java`
- Scan action:
  1. Mark `Pass` if all standard paths exist.
  2. Mark `Partial` if custom layout exists but can be justified by docs.
  3. Mark `Fail` if layout is unclear and undocumented.

## B) Spring package structure + component scanning baseline
- Source: https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html
- Expected:
  - Main application class in a root package.
  - Components in sub-packages so default component scanning works predictably.
- Scan action:
  1. Verify main application class package and key component package positions.
  2. Flag unusual cross-package scanning as risk when undocumented.

## C) GitHub repository discoverability baseline
- Source: https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-readmes
- Source: https://docs.github.com/en/repositories/creating-and-managing-repositories/best-practices-for-repositories
- Expected:
  - Clear `README` entry points.
  - Contribution/structure hints discoverable (docs or folder-level guidance).
- Scan action:
  1. Verify backend-relevant commands can be found from docs or wrappers.
  2. Record gaps as onboarding risk, not implementation bug.

## D) Branch/path robustness when referencing GitHub examples
- Convention:
  - Do not hardcode `master` branch paths in reusable skill references.
  - Prefer repo URL + short note "check default branch" or use branch-agnostic guidance.
- Scan action:
  1. If example links are branch-specific, add fallback repo root URL.
  2. Mark links needing manual branch confirmation as `REQUIRES CONFIRMATION`.

## E) Path consistency for reproducible scans
- Convention:
  - A scan should not mix incompatible path bases (`backend/...` and `./...`) without explanation.
- Scan action:
  1. Declare execution mode (`repo-root` or `backend-dir`) before listing evidence.
  2. Keep evidence paths consistent with the selected mode.
