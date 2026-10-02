## Context

The UI is Apache Pivot (retired to the Attic): a BXML view (`main.bxml`) bound to `TimeLapseController implements Bindable`, five event/listener classes, `GuiUtils` marshalling UI updates through `ApplicationContext.queueCallback`, and `TimeLapseTask extends org.apache.pivot.util.concurrent.Task`. The app is built as Spring Boot repackaged fat jars per platform (launch4j `.exe`, Linux tar.gz, macOS thin-layout `.app` via macosappbundler) on JDK 17. See proposal.md — Why for the motivation, specs/time-lapse-gui/spec.md for the behavior contract.

The user decided the target baseline: **JDK 25 + JavaFX 25.0.4 + AtlantaFX 3.0.0** (AtlantaFX 3.0.0 requires JDK 25; JDK 17 left extended support in September 2026).

## Goals / Non-Goals

**Goals:**
- Same observable GUI behavior (specs/time-lapse-gui/spec.md), restyled with an AtlantaFX theme.
- All three platform packages keep building and launching from the existing packaging pipeline.
- One supported, current toolchain: JDK 25, JavaFX 25 (LTS), AtlantaFX 3.0.0.

**Non-Goals:**
- No changes to encoding logic (`TimeLapseTask` Humble Video flow), photo filtering/ordering, formats or frame rates.
- No jlink/jpackage self-contained runtimes; the jar-based packaging (fat jar + launcher) stays.
- No new GUI features beyond the spec; no custom CSS beyond applying the theme; no GUI automation tests (manual smoke verification per platform).

## Decisions

1. **Non-modular JavaFX (classpath), no `module-info.java`.** The whole packaging chain (spring-boot repackage fat jar, thin-layout macOS bundle that copies dependency jars to `Contents/Java/classpath`, launch4j) is classpath-based; modules would break it. *Alternative rejected:* modular/jlink build. Consequence: JavaFX on the classpath refuses to start an `Application` subclass as main class ("JavaFX runtime components are missing"), so the entry point uses the launcher pattern (decision 2).

2. **Launcher-pattern entry point.** `TimeLapseMakerApplication` keeps its name and `main(String[])` but no longer extends the toolkit application class; it calls `Application.launch(TimeLapseApplication.class)` where `TimeLapseApplication extends javafx.application.Application` (loads FXML, applies AtlantaFX). This keeps every existing `mainClass` reference (3 packaging POMs, plist) untouched. *Alternative rejected:* renaming/re-pointing main classes in packaging configs.

3. **FXML view + `@FXML` controller.** `main.fxml` mirrors the current `main.bxml` structure (labels, two text fields with Choose buttons, two selectors, photo list, status label + progress bar + two buttons), wired to a rewritten `TimeLapseController` annotated `@FXML`. *Alternative rejected:* programmatic UI construction — loses the declarative layout the project already uses.

4. **AtlantaFX Primer theme (light).** Applied once at startup via `Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet())`. Switching variant (e.g. `PrimerDark`) is a one-line change later.

5. **Threading via `javafx.concurrent.Task` + `Platform.runLater`.** `TimeLapseTask` becomes a `javafx.concurrent.Task<Void>`: the encoding loop reports progress through `updateProgress(...)` (thread-safe by contract) bound to the progress bar; completion/failure are handled with `setOnSucceeded`/`setOnFailed` in the controller, replacing `TimeLapseTaskListener`. `GuiUtils` keeps its funnel role but marshals with `Platform.runLater`. *Alternative rejected:* keeping Pivot's Task/TaskListener shapes with adapter glue — extra indirection for no benefit.

6. **JavaFX platform natives per packaging module, like the Humble arch jars.** `core` gets classifier-less `javafx-controls`/`javafx-fxml` for compilation; each packaging module adds `javafx-graphics` with its platform classifier (`win`, `linux`, `mac`) and the spring-boot `repackage` excludes the two foreign-platform JavaFX native jars exactly as it excludes foreign Humble arch jars today. *Alternative rejected:* letting OpenJFX resolve natives from the build machine (`javafx.platform` default) — non-deterministic across CI/local builds.

7. **Enum list helpers return plain Java lists.** `EnumFrameRate.getValues()` / `EnumOutputFormat.getValues()` switch from `org.apache.pivot.collections.List` to `java.util.List<String>`; the controller wraps them in `FXCollections.observableArrayList` for the ComboBoxes. Keeps enums UI-toolkit-free; the existing enum unit tests are updated to the new API. *Alternative rejected:* returning `ObservableList` directly — couples domain enums to the UI toolkit again.

8. **JDK 25 rollout points** (from the user's baseline decision): parent `java.version` → 25; both CI workflows `java-version: '25'` (zulu); launch4j `jre.minVersion` → 25; macOS bundler plist `JVMVersion` → 25 if the plugin config exposes it. A local JDK 25 is required to build.

## Risks / Trade-offs

- [JavaFX classes must never be loaded outside the FX application thread] → all UI mutation stays inside `GuiUtils` helpers (single choke point) plus controller callbacks; encoding task touches only thread-safe `Task` APIs.
- [Fat-jar classpath launch of JavaFX fails with an `Application` main class] → launcher-pattern entry (decision 2) + smoke launch of each platform artifact before release.
- [macOS `.app` bundle misses the `mac` JavaFX natives after thin-layout repackaging] → verify bundle contents (`Contents/Java/classpath`) in the packaging task; the bundler copies resolved runtime deps, which include the declared classifier jar.
- [Humble Video native binding unverified on JDK 25] → smoke-test a small real time-lapse encode on JDK 25 during implementation.
- [CI builds Linux artifacts while the development machine is Windows] → platform behavior can only be smoke-checked on Windows locally and Linux in CI; macOS launch verification stays manual (as today).

## Migration Plan

1. Feature branch off `main` (convention: `feature/javafx-migration`), single PR.
2. POM/toolchain updates first (JDK 25, JavaFX, AtlantaFX, remove Pivot) with the rewritten UI and tests — the build must be green before any packaging work; then per-platform packaging adjustments; CI workflow updates last.
3. Version stays `1.3.0-SNAPSHOT` throughout; the version bump + `v1.3.0` tag remain the release step after the PR merges.
4. Rollback: revert the merge commit; the Pivot implementation remains intact in history and no data/format changes occur.

## Open Questions

None — the JDK baseline was decided with the user; remaining choices above are implementation-local and reversible.
