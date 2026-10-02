## 1. Toolchain and dependencies

- [x] 1.1 Install a local JDK 25 and point `JAVA_HOME` at it; confirm `mvn -v` runs on Java 25 (verification: `mvn -v` output shows Java 25)
- [x] 1.2 Parent POM: set `java.version` to 25, remove the four `org.apache.pivot` dependencies, add `org.openjfx:javafx-controls` and `org.openjfx:javafx-fxml` 25.0.4 (classifier-less, compile scope) and `io.github.mkpaz:atlantafx-base` 3.0.0 (verification: `mvn -pl core -am clean install` compiles on JDK 25)
- [x] 1.3 Update enum list helpers to plain Java lists: `EnumFrameRate.getValues()` and `EnumOutputFormat.getValues()` return `java.util.List<String>`; delete Pivot collection usage (verification: updated unit tests pass)
- [x] 1.4 Update existing enum unit tests from the Pivot collection API to `java.util.List` assertions (verification: `mvn -pl core test` green, 24+ tests)

## 2. JavaFX UI rewrite (core)

- [x] 2.1 Create `TimeLapseApplication extends javafx.application.Application` (loads `main.fxml`, sets the scene, applies `PrimerLight` via `Application.setUserAgentStylesheet`) and turn `TimeLapseMakerApplication` into the launcher-pattern main that calls `Application.launch(TimeLapseApplication.class)` (verification: app starts from IDE with the themed window)
- [x] 2.2 Create `main.fxml` mirroring the current `main.bxml` layout: source directory + Choose, output file + Choose, output format selector (MPEG4/AVI), frame rate selector with "images / second" hint, photo list, status label + progress bar + "Make Time Lapse"/"Exit" (verification: file exists under core/src/main/resources/views and is loaded at startup)
- [x] 2.3 Rewrite `TimeLapseController` as the FXML controller: populate selectors with defaults (MPEG4, 24), wire Choose handlers to `DirectoryChooser`/`FileChooser`, photo list refresh on directory pick, output extension append/switch per selected format (per specs/time-lapse-gui), validation alerts on empty fields, Exit handler (verification: manual pass through each spec scenario on Windows)
- [x] 2.4 Convert `TimeLapseTask` to `javafx.concurrent.Task<Void>` reporting progress via `updateProgress` and remove `TimeLapseTaskListener`; handle success/failure with `setOnSucceeded`/`setOnFailed` in the controller (status Done/Error, input re-enable) (verification: manual assembly run — progress bar advances, status transitions Assembling→Done, controls lock and re-enable)
- [x] 2.5 Rewrite `GuiUtils` to marshal with `Platform.runLater` (progress bar, status label, text input, component enable/disable helpers) (verification: no direct UI mutation from the task thread; manual assembly shows live progress)
- [x] 2.6 Delete `events/` classes (`ChooseSourceEvent`, `ChooseTargetEvent`, `SelectOutputFormatEvent`) and `main.bxml`; drop the Pivot `beans/Image` usage from the photo list (use file paths/`File` properties) (verification: `grep -ri pivot core/src` returns no source hits)

## 3. Packaging (windows / linux / macos)

- [x] 3.1 Windows module: add `javafx-graphics` 25.0.4 classifier `win`, add the `linux` and `mac` JavaFX native jars to the repackage excludes, set launch4j `jre.minVersion` to 25 (verification: full build produces `Time Lapse Maker.exe`; jar manifest Main-Class intact)
- [x] 3.2 Linux module: add `javafx-graphics` classifier `linux`, exclude `win`/`mac` natives, keep launcher script working (verification: full build produces `Time_Lapse_Maker-linux.tar.gz` with the linux native jar inside)
- [x] 3.3 macOS module: add `javafx-graphics` classifier `mac`, exclude `win`/`linux` natives; keep thin-layout repackage and bundler config (verification: full build produces the `.app`; its `Contents/Java/classpath` contains the `mac` classified JavaFX jars)

## 4. CI and final verification

- [x] 4.1 Update both GitHub workflows (`dev-build.yml`, `release-build.yml`) to JDK 25 (zulu) (verification: workflow YAML parses; run on the feature branch is green)
- [x] 4.2 Full local build `mvn -B clean install` on JDK 25 with all modules, tests green, all three artifacts produced (verification: BUILD SUCCESS, 24+ tests, exe/tar.gz/app present)
- [x] 4.3 Smoke-test the Windows artifact: launch `Time Lapse Maker.exe`, run a real small time-lapse encode end-to-end on JDK 25 (verifies Humble Video on the new JVM + JavaFX classpath launch) (verification: output video file created and playable; progress and Done status observed)
- [x] 4.4 Linux/macOS artifacts built and their contents verified in CI (natives present, launcher entry correct); macOS `.app` launch left as manual follow-up per design (verification: CI build green, archive listings checked)

## 5. Wrap-up

- [x] 5.1 PR `feature/javafx-migration` → `main` green on CI; after merge, bump version to 1.3.0 and tag `v1.3.0` to produce the release artifacts (verification: release workflow attaches the three archives to the GitHub release)
