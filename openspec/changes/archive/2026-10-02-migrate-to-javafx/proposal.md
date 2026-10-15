## Why

Apache Pivot has been retired to the Apache Attic and receives no fixes or releases, freezing the UI on an unmaintained toolkit. Migrating the GUI to JavaFX — the actively maintained Java desktop toolkit — unlocks current theming (AtlantaFX) and a supported JDK baseline (25), which the project needs now that JDK 17 has left extended support (September 2026).

## What Changes

- Replace Apache Pivot (WTK/Terra/BXML) with JavaFX 25.0.4 (LTS) as the UI toolkit; `main.bxml` is replaced by a JavaFX view (FXML) with equivalent layout and controls.
- Apply AtlantaFX 3.0.0 theming to the whole application window.
- **BREAKING**: project baseline moves from JDK 17 to JDK 25 (compiler target, CI runners, launch4j `minVersion`, macOS bundler JVM version, local build toolchain).
- Rewrite the view/threading layer around JavaFX idioms: FXML controller replaces the Pivot `Bindable` controller, `javafx.concurrent.Task` replaces Pivot `Task`, `Platform.runLater` replaces `ApplicationContext.queueCallback`, JavaFX `Alert`/`FileChooser`/`DirectoryChooser`/`ProgressBar`/`ComboBox` replace their Pivot counterparts.
- Change enum list helpers (`EnumFrameRate.getValues()`, `EnumOutputFormat.getValues()`) from Pivot collections to plain Java lists; corresponding unit tests updated.
- Update all three packaging modules to carry the JavaFX platform-specific natives (win/linux/mac classifiers) alongside the existing Humble Video natives.
- Remove all `org.apache.pivot` dependencies from the build.

## Capabilities

### New Capabilities
- `time-lapse-gui`: observable behavior of the desktop window — source directory and output file selection (including automatic output-extension handling per format), output format and frame rate selection with defaults, the photo list display, progress and status feedback during assembly, input locking while assembling, error alerts, and exit behavior.

### Modified Capabilities
<!-- none: the project has no existing specs; this change introduces the first capability spec -->

## Impact

- **Code**: `TimeLapseMakerApplication`, `TimeLapseMaker`, `TimeLapseController` (rewritten as FXML controller), `events/*` (folded into controller handlers or thin classes), `GuiUtils` (thread marshalling), `services/TimeLapseTask` + `TimeLapseTaskListener` (JavaFX task API), `EnumFrameRate`/`EnumOutputFormat` list helpers, existing enum unit tests.
- **Dependencies**: remove `org.apache.pivot:*`; add `org.openjfx:javafx-controls`/`javafx-fxml`:25.0.4 with platform classifiers and `io.github.mkpaz:atlantafx-base:3.0.0`.
- **Build/packaging**: `java.version` 17 → 25 in the parent POM; CI workflows to a JDK 25 distribution; `launch4j` `minVersion` 25; macOS bundler JVM version 25; per-module JavaFX native handling (Windows `win`, Linux `linux`, macOS `mac` classifiers) in the spring-boot repackage excludes/includes.
- **Unaffected**: Humble Video encoding logic, photo filtering/ordering, output formats and frame rate values, logback configuration.
