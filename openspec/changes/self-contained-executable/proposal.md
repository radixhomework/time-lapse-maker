## Why

The current packages require a Java runtime on the user's machine — launch4j shows a "This application requires a Java Runtime Environment 25" dialog when none is present, and the macOS bundle even downloads its dependencies over Maven at first launch. Users should get a single archive that contains the application together with everything needed to run it, with no Java installation and no network access required.

## What Changes

- **BREAKING**: replace the jar-based packaging — launch4j `.exe` (Windows), tar.gz + shell script (Linux) and the macosappbundler thin-layout `.app` (macOS) are removed.
- Package the application with `jpackage --type app-image` per platform: a native launcher plus an **embedded Java runtime**, published as a zip archive per platform.
- The release workflow moves to a platform matrix (windows/ubuntu/macos runners) because jpackage can only build images for the platform it runs on; each runner builds, packages and uploads its own bundle to the GitHub release.
- Spring Boot fat jar remains the application artifact fed to jpackage; no changes to application code are expected.

## Capabilities

### New Capabilities
- `platform-distribution`: how the application is distributed to end users — per-platform self-contained bundles with an embedded runtime, launchable without any installed Java, published as release artifacts.

### Modified Capabilities
<!-- none -->

## Impact

- **Packaging modules** (windows/linux/macos): launch4j, macosappbundler, thin-layout and the shell-script/desktop resources are replaced by a jpackage invocation and a zip assembly.
- **CI**: `release-build.yml` becomes a three-runner matrix; `dev-build.yml` keeps building `core` only.
- **Dependencies**: spring-boot-thin-layout removed from the macos module; `phase.repackage`/launcher scripts/desktop files dropped.
- **Unaffected**: application code, the `time-lapse-gui` capability, Humble Video encoding, versions of JavaFX/AtlantaFX.
