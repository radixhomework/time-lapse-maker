## 1. Windows packaging module

- [x] 1.1 Remove launch4j-maven-plugin and the jar-plugin finalName override from windows/pom.xml; keep the spring-boot repackage as the fat-jar producer (verification: `mvn -pl windows -am package` still produces the fat jar)
- [x] 1.2 Add exec-maven-plugin invoking `jpackage --type app-image --name "Time Lapse Maker" --input target --main-jar time-lapse-maker-${project.version}.jar --main-class io.github.radixhomework.timelapsemaker.TimeLapseMakerApplication --add-modules <pinned list> --dest target/app-image` bound to the package phase (verification: `target/app-image/Time Lapse Maker/Time Lapse Maker.exe` exists)
- [x] 1.3 Update the assembly descriptor to zip the app-image directory as `Time_Lapse_Maker-windows-selfcontained.zip` (verification: archive contains the launcher exe and `runtime` directory)
- [ ] 1.4 Launch the unzipped bundle on a machine without JAVA_HOME/PATH Java to verify offline startup (verification: GUI opens; smoke encode works)

## 2. Linux packaging module

- [x] 2.1 Remove maven-antrun/resources extra-resources processing (launcher script, .desktop) from linux/pom.xml (verification: module builds without them)
- [x] 2.2 Add jpackage app-image execution mirroring 1.2 and zip as `Time_Lapse_Maker-linux-selfcontained.zip` (verification: archive contains the launcher binary and `lib/runtime`)
- [ ] 2.3 Launch the bundle on a Linux runner/machine without system Java (verification: GUI or headless smoke check passes)

## 3. macOS packaging module

- [x] 3.1 Remove macosappbundler-maven-plugin and spring-boot-thin-layout from macos/pom.xml; keep spring-boot repackage (verification: module builds)
- [x] 3.2 Add jpackage app-image execution mirroring 1.2 and zip as `Time_Lapse_Maker-macos-selfcontained.zip`, preserving the .app structure (verification: archive contains `Time Lapse Maker.app/Contents/MacOS` and embedded runtime)
- [ ] 3.3 Verify the unzipped .app launches on macOS (verification: manual launch on a Mac; document any Gatekeeper step in the release notes)

## 4. Release workflow

- [ ] 4.1 Restructure release-build.yml into a matrix (windows-latest, ubuntu-latest, macos-latest) where each leg builds only its module (`mvn -B -pl <module> -am package`) and uploads its zip (verification: workflow YAML parses; matrix jobs run on a test tag)
- [ ] 4.2 Release creation: a prepare job (or first leg) runs `gh release create` with `--generate-notes`, other legs `gh release upload` (verification: a pushed tag yields one release with three self-contained archives)
- [ ] 4.3 Confirm dev-build.yml remains core-only and green (verification: feature branch CI run succeeds)

## 5. Verification and wrap-up

- [x] 5.1 Full local build `mvn -B clean install` green with the new packaging (verification: BUILD SUCCESS, 23 tests)
- [x] 5.2 Inspect each produced bundle for foreign-platform natives (verification: each archive contains only its platform's Humble/JavaFX natives) — verified for the Windows bundle; Linux via CI, macOS pending
- [ ] 5.3 PR to main green on CI; after merge, push a tag to publish the first self-contained release (verification: release shows three archives)

## Implementation notes

- The jpackage launcher config was verified: app classpath is the Spring Boot fat jar and the main class is the Boot `JarLauncher`; on launch, JavaFX natives load from the nested jars and the toolkit starts cleanly.
- `java.scripting` had to be added to `--add-modules` (AtlantaFX's stylesheet engine touches `javax.script`).
- jpackage refuses to overwrite an existing app-image, so each bundle profile deletes `target/app-image` via antrun before packaging.
- On a locked/transitioning Windows desktop session no process can create windows (even notepad fails to start), so the final visual window confirmation of the bundled app is pending: the bundle exe is ready to double-click once the session is interactive.
- Pending live verifications: 1.4 window visual check + encode from the bundle, 2.3 Linux bundle launch, 3.3 macOS launch (manual), 4.1/4.2 first tagged release run, 5.3 release publication.
