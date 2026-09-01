# Changelog

All notable changes to this project are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this
project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) — see the
"Branching and Releases" section of `CLAUDE.md` for how a version graduates from `Unreleased`
into a tagged release.

## [Unreleased]

### Added

- Lazy List Pitfalls topic (`ui/topic/lazylistpitfalls/`) — an alphabetically-grouped contacts
  list with sticky letter headers built from several `stickyHeader()`/`items()` block pairs
  rather than a single `items(count = N)` call. Better precomputes each contact's row index
  across the whole flattened list rather than trusting the `index` parameter `items()` hands to
  each block, which reading `LazyLayoutIntervalContent.kt` confirms is local to that one call and
  resets to 0 at the start of every letter group — the exact mistake that would silently corrupt
  positional announcements in a naive fix attempt. Naive has no `CollectionInfo` override,
  `CollectionItemInfo`, or `heading()` on the section headers at all. This closes out requirements
  §3.2 (Collections).
- Input That Is Actually a Button topic (`ui/topic/inputasbutton/`) — an appointment date field
  styled like a text field but never meant to be typed into. Better replaces the field entirely
  with `OutlinedTextFieldDefaults.DecorationBox` — the visual shell with no editable-text core
  underneath — carrying `Role.Button` and a `contentDescription` composed as "label, value", and
  returns focus to the field once the date picker dialog closes using the same two-step fix
  established by the Focus After Navigation topic. Naive uses a real `OutlinedTextField(readOnly =
  true)`, which still exposes cursor and text-selection actions that do nothing; the developer
  note also covers `enabled = false`, the other common wrong attempt, in prose. This opens
  requirements §3.3 (Controls and Interaction).
- Composite Controls topic (`ui/topic/compositecontrols/`) — a Wi-Fi settings row with a label,
  a description, and a `Switch`. Better merges all three into one control with
  `Modifier.toggleable(role = Role.Switch)` plus `mergeDescendants = true` on the row, and makes
  the inner `Switch(onCheckedChange = null)` non-interactive so it isn't independently focusable
  — confirmed by an instrumented test reading the switch's own unmerged semantics node, which
  carries no click action once `onCheckedChange` is `null`. The developer note also traces
  `SemanticsNode.kt`'s `mergeConfig` to confirm merge order follows structural composition order
  (`zSortedChildren`), never `traversalIndex`. Naive leaves the row unmerged, so the switch — fully
  interactive on its own — carries no label connecting it back to "Wi-Fi".
- Selectable Icon Lists topic (`ui/topic/selectableiconlists/`) — a row of five colour swatches
  with no visible text, acting as a single-select accent colour picker. Better wraps the row in
  `Modifier.selectableGroup()` and gives each swatch `Modifier.selectable(role = Role.RadioButton)`
  plus a `contentDescription` naming its colour, so TalkBack gets both halves an icon-only control
  needs: what it is, and whether it's the current choice, with a group position like "2 of 5".
  Naive supplies only the `contentDescription` — the more obvious half — leaving every swatch
  without a selected state, role, or group, modelling the "implementations usually supply one"
  gap named in requirements §3.3.
- Minimum Touch Target topic (`ui/topic/minimumtouchtarget/`) — a delete action, Naive as a bare
  `Modifier.clickable` on a `Box` explicitly constrained to 24dp, Better as a plain `IconButton`
  with nothing else added. Building this surfaced a real requirements-doc-vs-observed-behaviour
  disagreement, reported rather than silently resolved: in this Compose version, `touchBoundsInRoot`
  auto-expands to 48dp for *any* clickable node (`SemanticsModifierNode.kt`'s `useMinimumTouchTarget
  = getOrNull(SemanticsActions.OnClick) != null`, combined with `ViewConfiguration
  .minimumTouchTargetSize`'s 48dp default), confirmed on the isolated Naive icon by instrumented
  test. `IconButton`'s `minimumInteractiveComponentSize()` instead expands real, reserved *layout*
  bounds (40dp measured) that a neighbour cannot encroach on — the genuine remaining difference.
  Developer notes and tests reflect this verified reality rather than the topic's original premise;
  a redesign to demonstrate the real risk (touch-region clipping in a row of packed icons) is
  logged in the topic backlog for later.
- Disabled Elements topic (`ui/topic/disabledelements/`) — a checkbox-gated Submit button. Better
  always attaches `Modifier.clickable(enabled = agreed, role = Role.Button)`, never conditionally
  omitting it; reading `Clickable.kt`'s `AbstractClickableNode.applySemantics()` shows this
  registers the click action unconditionally and only calls `disabled()` afterwards when not
  enabled, so a disabled button keeps its role and its (inert) action alongside the disabled
  marker. Naive attaches no `Modifier.clickable` at all until the checkbox is checked, so the
  button carries no role, click action, or disabled marker before then — confirmed absent, not
  present-and-false, by instrumented test. The checkbox itself reuses the Composite Controls
  topic's established toggleable pattern unchanged.

### Changed

- `docs/ActuA11y_Requirements.md` (0.3 → 0.4): WebView accessibility moves into scope (§9 →
  §3.7, Topic 37) now that EN 301 549 V4.1.1 clarifies a WebView embedded in native software is
  evaluated under Clause 11 only, removing the original reason for exclusion (needing to satisfy
  the much larger, partly divergent Clause 9 ruleset in parallel). Added Topics 38–45 (§3.8),
  sourced from the EN 301 549 V4.1.1 / WCAG 2.2 update: Dragging Movements, Focus Not Obscured
  (Minimum), Redundant Entry, Accessible Authentication (Minimum), Consistent Identification,
  Switch: Platform vs. Custom (the layered-conformance model, not tied to a single success
  criterion), and two void-clause note topics (Consistent Help, Parsing). Target Size (Minimum)
  and Bold Text were folded into existing Topics 13 and 31 rather than added separately; Screen
  Titled was considered and explicitly not added, since the app's own registry-driven app bar
  already makes a genuine naive counterpart impossible to build without breaking a structural
  invariant. Extended the `Topic` registry schema (§4.7) with optional `enClause`/`wcagVersion`/
  `bindingFrom` fields to support the new topics.

## [0.3.0] - 2026-08-21

### Added

- Pane Titles topic (`ui/topic/panetitles/`) — a Summary/Details view switcher whose content
  swaps in place, with no navigation event. Better gives the swapped content pane its own
  `paneTitle` that tracks the selected view; Naive relies only on the screen-level `paneTitle`
  set once on arrival, so TalkBack has no signal that the content changed.
- Focus After Navigation topic (`ui/topic/focusafternavigation/`) — a notification-frequency
  picker dialog. Better returns focus to the triggering button once the picker closes; Naive
  leaves where focus goes next undefined. This closes out requirements §3.1 (Structure and
  Traversal).
- PIN Show/Hide Toggle topic (`ui/topic/pinshowhide/`) — a six-digit PIN entry with a
  reveal/hide control, modelled on a real-world bug (six separate per-digit boxes with no
  password semantics, and a state-swapping toggle that made TalkBack narrate the PIN as a
  text-change diff). Better binds one `BasicSecureTextField`/`TextFieldState` decorated to draw
  the same six boxes, so TalkBack sees one password-marked field instead of six plain ones, and
  expresses the reveal/hide state as a `Role.Switch` with an explicit `stateDescription` instead
  of a button whose own label swaps. The developer note documents what remains open rather than
  claiming it fully solved — see the note for the `TextObfuscationMode` nuance.
- One-Dimensional Collections topic (`ui/topic/onedimensionalcollections/`) — a 24-item list,
  opening requirements §3.2 (Collections). Both versions use a plain `LazyColumn`, which
  automatically attaches its own `CollectionInfo` semantics regardless — but always reports
  `rowCount = -1` ("unknown"), even for a small, fully-known, static list. Better overrides that
  default with the real count and adds `CollectionItemInfo` to every item (never auto-supplied by
  any Lazy layout); Naive relies on the unmodified default, which is confidently wrong rather than
  simply absent. The override behaviour is confirmed by instrumented test on a real device, not
  just assumed from the API.
- Genuine Tables topic (`ui/topic/genuinetables/`) — a hand-built three-column inventory table
  (Compose has no table composable). Unlike `LazyColumn`, a plain `Column` supplies no
  `CollectionInfo` of its own, so unlike One-Dimensional Collections, Naive here is genuine
  silence rather than a present-but-wrong default. Better adds `CollectionInfo` to the table and
  `CollectionItemInfo` to all 18 cells, including the header row; the developer note is explicit
  that this API surface has no separate flag marking a cell as a header, rather than implying it
  does.

### Changed

- `CLAUDE.md`: corrected a stale reference to requirements §9 (Open Questions is §10; the
  requirements document was renumbered after that cross-reference was written).

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

[Unreleased]: https://github.com/frankrpeters/ActuA11y/compare/v0.3.0...dev
[0.3.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/frankrpeters/ActuA11y/releases/tag/v0.1.0
