# AGENTS.md — working rules for coding agents

Rules for AI agents (and anyone acting as one) working in this repository.
Project-specific details live in the repo's own documentation — read them
before touching code. (PRODUCT.md / ARCHITECTURE.md are still to be created;
until then, `openspec/config.yaml` carries the project context.)

## Commit policy

- **Commit and push only on the user's explicit demand.** Finishing a
  task or passing tests is never consent to commit.
- **Sole exception — quality-check rounds**: when the user asks for a
  quality check (or when the quality-fix loop below is running), the agent
  is autonomous: it commits and pushes its fixes on its own so the fresh
  analyses (Sonar, CI) run, without asking each time.
- If the user asks to hold for local testing, report "done, ready to test"
  and stop — don't ask again; wait for an explicit go.
- When the working tree contains files the agent did not create, inspect
  them and say so before staging everything.

### Commit & branch conventions (this repository)

- Commit messages: short imperative or gerund summaries in English
  (e.g. "Adding CI configurations", "Update version to 1.3.0"), optional
  body explaining the why, no signature footers. Conventional-commit
  prefixes are **not** used in this repo.
- Feature work happens on `feature/*` branches opened as PRs — the CI
  triggers on `feature/*`, so other prefixes are invisible to it.
  Quality-fix iterations commit and push on the same branch/PR.
- Versions are managed by hand in the five POMs (parent + 4 modules'
  parent references); releases are cut by pushing a tag.

## Agent-local files are never committed

- **Never stage or commit agent-specific directories and files**
  (`.zcode/`, `.claude/`, `.agents/`, `.cursor/`, `.aider*`, and the like).
  They are machine-local configuration, not project content. The repo
  `.gitignore` covers them; if it doesn't yet, propose adding it rather
  than committing these paths.

## Quality gates

Before calling implementation work done, check what this repository has
available and fix what it reports:

1. **CI (GitHub Actions)** — the `feature/*`/PR workflow builds the full
   reactor on ubuntu (including the Linux jpackage bundle); the `main`
   workflow builds core only; the tag-triggered release workflow builds
   the three platform bundles on a matrix.
2. **Unit tests** — `core/src/test` (JUnit 6, Surefire); all must pass.
3. **PR comments** — bot and human review comments addressed.

There is no SonarCloud/CodeQL analysis wired into this repository (the
Sonar integration was removed); don't wait on those checks.

## Iteration policy

Fix what was found, push, wait for fresh CI runs, then re-check — repeat
until clean.

**During these fix/verify rounds the agent is autonomous**: it commits and
pushes each fix itself (this is the only case where committing without an
explicit user demand is allowed — see Commit policy), within the
quality-check scope only. It does not use that autonomy to commit anything
unrelated to the findings.

**Stop rule: 3 iterations maximum, autonomously.** After 3 fix/verify
iterations, stop and report remaining findings and what was tried; wait for
the user's decision.

## Testing stance

- Do not build heavy test suites unless asked. Verify by compiling,
  building, and exercising the real app (launching the built bundles,
  running a real time-lapse encode).
- When the user says to do fewer tests and move on, move on — don't stall
  on ceremony.

## Workflow

- Feature work goes through OpenSpec: `/opsx:propose` (planning only —
  never implement in the same turn), `/opsx:apply` (task-by-task, tick
  checkboxes), `/opsx:archive` when done. Spec scenarios are the
  acceptance criteria.
- **Documentation is part of the workflow**: planning artifacts, spec
  updates, and the PRODUCT.md / ARCHITECTURE.md changes implied by a
  change are maintained as the change progresses — not deferred to
  "later". `/opsx:archive` is only done once the documentation reflects
  the implemented behavior.

## Product & architecture documentation

**`PRODUCT.md` and `ARCHITECTURE.md` are mandatory in every repository and
must be created and kept up to date as part of the development process —
not as an afterthought.**

- Neither exists in this repository yet: create them as part of the first
  task that touches the repo:
  - `PRODUCT.md` — what the project is, who it's for, what it must and
    must not do (scope, key features, constraints).
  - `ARCHITECTURE.md` — how it is built: components, data flow, tech
    choices (with brief rationale), deployment, and quality/CI setup.
- **Maintain them continuously**: any feature, refactor, or infrastructure
  change that alters behavior, structure, or deployment includes the
  corresponding doc update in the same change — same PR, same commit
  series. Documentation drift is treated as incomplete work.
- When starting a task, read both files first; if the code and the docs
  disagree, surface the discrepancy to the user instead of silently
  trusting either one.

## Documentation edits need approval

`AGENTS.md`, `PRODUCT.md`, and `ARCHITECTURE.md` are never edited silently
beyond the upkeep duty above: substantive changes (new decisions, scope
changes, removed sections) are proposed to the user and applied after
approval. Routine sync of facts that the approved change already implies
(e.g. documenting the feature being merged) goes in directly.

## Repository quick reference

- **Stack**: Java 25 (JDK 25 required — e.g. `C:\Users\benoit\.jdks\jdk-25.0.4.1+1`
  locally, zulu 25 in CI), Maven multi-module (`core` + `windows`/`linux`/`macos`
  packaging modules), JavaFX 25 + AtlantaFX 3, Humble Video for encoding.
- **Build & test**: `mvn -B clean install` (full reactor + unit tests).
- **Self-contained bundles**: `mvn -pl <windows|linux|macos> -am package` —
  the jpackage profile only activates on the matching OS; outputs land in
  `<module>/target/app-image/` and `<module>/target/*-selfcontained.zip`.
- **Release**: push a tag → release workflow (platform matrix) attaches the
  three self-contained archives to a GitHub release named after the tag.
