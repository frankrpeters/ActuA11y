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
    AndroidManifest.xml
    java/de/frpeters/actua11y/
      MainActivity.kt
      navigation/          # NavRoutes.kt (sealed Screen), ActuA11yNavHost.kt
      ui/
        theme/Theme.kt     # Material3 + dynamic color (API 31+)
        home/HomeScreen.kt
        topic/contentdescriptions/ContentDescriptionsScreen.kt
    res/
      drawable/            # ic_arrow_back, ic_info, ic_demo_smartphone, ic_demo_wave
      values/strings.xml   # All UI strings — no hardcoded strings in Kotlin
  test/                    # JUnit 4 boilerplate (no test deps in current build)
  androidTest/             # Espresso boilerplate (no test deps in current build)
gradle/
  libs.versions.toml       # Version catalog — add all new deps here first
```

## Dependencies & Tooling

Dependencies are managed via Gradle version catalog (`gradle/libs.versions.toml`). When adding a dependency: add version to `[versions]`, coordinates to `[libraries]`, then reference in `app/build.gradle.kts` via `libs.<alias>`.

Current stack: Compose BOM 2025.05.00, Material3, Navigation Compose, activity-compose, core-ktx, lifecycle-runtime-ktx.

## Key Notes — AGP 9.2.1 Behaviour

- **Do NOT apply `org.jetbrains.kotlin.android`** in `app/build.gradle.kts`. AGP 9.2.1 registers the `kotlin` extension internally; applying the plugin explicitly causes `Cannot add extension with name 'kotlin'`. Only apply `kotlin-compose` for Compose compiler support.
- **No `kotlinOptions` block** — `compileOptions { sourceCompatibility/targetCompatibility }` alone is sufficient; AGP 9.x syncs the Kotlin JVM target automatically.
- `compileSdk = 36` uses the plain integer form (not the `release(36) { minorApiLevel = 1 }` form from the original scaffold — that form is for SDK 36.1 extension APIs).
- XML base theme must use `android:Theme.Material.Light.NoActionBar` / `android:Theme.Material.NoActionBar` — `NoTitleBar` variants do not exist in the Material theme family and AAPT will error.
- `TopAppBar` from Material3 is `@ExperimentalMaterial3Api` in BOM 2025.05.00; annotate callers with `@OptIn(ExperimentalMaterial3Api::class)`.

## Accessibility Conventions

This project uses inline `// WHY:` and `// GOOD:` comments to explain every accessibility decision (per the requirements doc). Future topic screens follow the same template as `ContentDescriptionsScreen`:

1. `Scaffold` with `Modifier.semantics { paneTitle = "..." }` on the root
2. `TopAppBar` with `IconButton` back nav (contentDescription = `navigate_back` string)
3. Debug-only "Bad version" `TextButton` in TopAppBar actions, gated by `BuildConfig.DEBUG`
4. Scrollable `Column` content: intro → demo sections (each starting with a heading-semantics `Text`) → collapsible developer note `Card`

Each new route: add `object` to `Screen` sealed class, add `composable {}` entry in `ActuA11yNavHost`.
