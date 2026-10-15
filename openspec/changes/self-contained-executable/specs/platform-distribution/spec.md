## Purpose

Defines how Time Lapse Maker reaches end users: per-platform self-contained bundles that embed a Java runtime, so the application launches on a machine without any Java installation and without network access.

## ADDED Requirements

### Requirement: Self-contained platform bundles

The project SHALL produce, for each supported platform (Windows, Linux, macOS), a single downloadable archive that contains the application and an embedded Java runtime, such that unzipping the archive and starting the bundled launcher runs the application.

#### Scenario: Launch on a machine without Java
- **WHEN** a user unzips the bundle for their platform on a machine with no Java installed and starts the bundled launcher
- **THEN** the Time Lapse Maker window opens and all functionality works

#### Scenario: Bundle is offline-complete
- **WHEN** the launcher is started with no network connection
- **THEN** the application starts and assembles time lapses without requiring any download

### Requirement: Platform-specific builds

Each bundle SHALL be built on the platform it targets and SHALL contain only that platform's native libraries (Humble Video and JavaFX), so a bundle never depends on another platform's binaries.

#### Scenario: Windows bundle contains only Windows natives
- **WHEN** the Windows bundle is inspected
- **THEN** it contains Windows native libraries only and no Linux/macOS native libraries from other platforms

### Requirement: Release publication

Pushing a version tag SHALL attach one archive per supported platform to the GitHub release for that tag.

#### Scenario: Tag produces platform archives
- **WHEN** a tag is pushed and the release workflow completes
- **THEN** the GitHub release contains a Windows, a Linux and a macOS self-contained archive
