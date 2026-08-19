# Changelog

All notable changes to this project are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this
project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) — see the
"Branching and Releases" section of `CLAUDE.md` for how a version graduates from `Unreleased`
into a tagged release.

## [Unreleased]

### Added

- Pane Titles topic (`ui/topic/panetitles/`) — a Summary/Details view switcher whose content
  swaps in place, with no navigation event. Better gives the swapped content pane its own
  `paneTitle` that tracks the selected view; Naive relies only on the screen-level `paneTitle`
  set once on arrival, so TalkBack has no signal that the content changed.
- Focus After Navigation topic (`ui/topic/focusafternavigation/`) — a notification-frequency
  picker dialog. Better returns focus to the triggering button once the picker closes; Naive
  leaves where focus goes next undefined. This closes out requirements §3.1 (Structure and
  Traversal).

## [0.2.0] - 2026-07-31

### Added

- Traversal Groups topic (`ui/topic/traversalgroups/`) — a list with an overlapping floating
  action button; Better marks the list `isTraversalGroup = true` so TalkBack's geometric
  traversal sort can't interleave the overlap into the list's swipe order.
- Traversal Index topic (`ui/topic/traversalindex/`) — a card header (dismiss button + title)
  where both versions set identical `traversalIndex` values, but only Better also sets
  `isTraversalGroup` on the enclosing Row, demonstrating that the index is silently inert
  without it.
- Headings topic (`ui/topic/headings/`) — three sections where Better marks each title
  `semantics { heading() }` for TalkBack's Headings navigation; Naive renders identical styling
  with no heading semantics at all.
- Instrumented semantics tests and the standard preview set for all three new topics.

### Fixed

- `app/build.gradle.kts` never declared `testImplementation(libs.junit)`; unit test compilation
  had never actually succeeded since the initial scaffold.
- `connectedAndroidTest` failed on all tests on newer Android versions (confirmed on a real
  device at API 37) because the transitively-resolved `espresso-core` (3.5.0) calls a reflective
  `InputManager.getInstance()` API that newer platform versions have hardened away. Pinned
  `androidTestImplementation(libs.androidx.espresso.core)` explicitly to resolve 3.7.0 instead.

### Changed

- Retrofitted the corrected Apache 2.0 license header onto every existing source file. Also
  corrected Android Studio's default "Apache 2.0" copyright profile, which had been inserting
  the Apache Software Foundation's own contributor boilerplate rather than an
  independently-licensed header.
- `docs/ActuA11y_Requirements.md`: removed the stale `minSdk` open question — already resolved
  in practice, but the requirements document itself hadn't been updated to say so.

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

[Unreleased]: https://github.com/frankrpeters/ActuA11y/compare/v0.2.0...dev
[0.2.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/frankrpeters/ActuA11y/releases/tag/v0.1.0
