# Changelog

All notable changes to this project are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this
project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) — see the
"Branching and Releases" section of `CLAUDE.md` for how a version graduates from `Unreleased`
into a tagged release.

## [Unreleased]

## [0.1.0] - 2026-07-30

### Added

- Naive/Better runtime toggle in a persistent app bar (`ui/components/NaiveToggle.kt`),
  replacing the earlier `BuildConfig.DEBUG`-gated "bad version" mechanism.
- `navigation/TopicRegistry.kt` as the single source of truth for topics, categories,
  navigation, and the toggle's enabled state.
- `ui/AppScaffold.kt` hoisting the single `Scaffold`/`TopAppBar` above the `NavHost`.
- Shared `ui/components/DeveloperNote.kt` component.
- Content Descriptions topic, split into `ContentDescriptionsNaive`/`ContentDescriptionsBetter`
  implementations, with instrumented Compose semantics tests.
- Home screen onboarding card and category browsing (`ui/category/CategoryScreen.kt`).
- Standard light/dark/200%-font preview set across all main composables.
- `LICENSE` (Apache 2.0).
- `CHANGELOG.md` (this file).

### Changed

- Build tooling: AGP 9.3.1, Kotlin 2.2.10, Gradle 9.5.0.
- `minSdk` documentation corrected to 28 throughout (the build script already used 28; only
  `CLAUDE.md`'s text was stale).
- Default branch renamed `master` → `main`.

[Unreleased]: https://github.com/frankrpeters/ActuA11y/compare/v0.1.0...dev
[0.1.0]: https://github.com/frankrpeters/ActuA11y/releases/tag/v0.1.0
