# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ActuA11y** is an Android application focused on accessibility ("A11y" = accessibility). It targets Android 11+ (minSdk 30) and is written in Kotlin.

- Package: `de.frpeters.actua11y`
- AGP: 9.2.1 | compileSdk: 36 | minSdk: 30

## Build Commands

```powershell
# Build debug APK
.\gradlew assembleDebug

# Build release APK
.\gradlew assembleRelease

# Run local unit tests
.\gradlew test

# Run instrumented tests (requires connected device/emulator)
.\gradlew connectedAndroidTest

# Run a single unit test class
.\gradlew test --tests "de.frpeters.actua11y.ExampleUnitTest"

# Clean build
.\gradlew clean

# Lint
.\gradlew lint
```

## Project Structure

```
app/src/
  main/
    AndroidManifest.xml        # No activities declared yet
    java/de/frpeters/actua11y/ # Main source (empty — no activities/services yet)
    res/                       # Resources (themes, colors, launcher icons)
  test/                        # Local JVM unit tests (JUnit 4)
  androidTest/                 # Instrumented tests (Espresso)
gradle/
  libs.versions.toml           # Version catalog — add all dependencies here
```

## Dependencies & Tooling

Dependencies are managed via Gradle version catalog (`gradle/libs.versions.toml`). When adding a new dependency, add its version to `[versions]`, the library coordinates to `[libraries]`, and reference it in `app/build.gradle.kts` via `libs.<alias>`.

Current dependencies: `androidx-appcompat`, `androidx-core-ktx`, `material`.

## Key Notes

- The manifest declares no activities yet — the app is in early scaffolding state.
- Kotlin plugin is not explicitly listed in `libs.versions.toml`; it is pulled in transitively via AGP 9.x. If explicit Kotlin plugin configuration is needed, add `kotlin-android` to the version catalog and `app/build.gradle.kts`.
- `compileSdk` uses the AGP 9.x `release(36) { minorApiLevel = 1 }` syntax — do not change this to a plain integer.
- Proguard is disabled in release builds (`isMinifyEnabled = false`); enable when the app is closer to production.
