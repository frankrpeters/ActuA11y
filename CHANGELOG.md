# Changelog

All notable changes to this project are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this
project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) — see the
"Branching and Releases" section of `CLAUDE.md` for how a version graduates from `Unreleased`
into a tagged release.

## [Unreleased]

### Changed

- `Topic` (`navigation/TopicRegistry.kt`) gains the three optional source-metadata fields
  requirements §4.7 already specified but the code never had: `enClause`, `wcagVersion`, and
  `bindingFrom`, all defaulting to `null`. `docs/WCAG2.2_addenda.md` had this item ticked off as
  done; only the requirements text had been updated, not the class itself. No existing entry sets
  them — Topic 43 (Switch: platform vs. custom) is deliberately not tied to a single success
  criterion, so `null` is its correct value rather than an omission.
- `gradlew` is now committed as executable (mode `100755`); it was `100644`, so `./gradlew` failed
  with "Permission denied" on Linux, including cloud build environments.

## [0.5.0] - 2026-09-13

### Added

- Reduced Motion topic (`ui/topic/reducedmotion/`, Topic 34) — a "Show shipping details"
  expand/collapse disclosure. Naive: the expand/collapse animation always runs at a fixed 400ms
  duration, never reading `Settings.Global.ANIMATOR_DURATION_SCALE`. Better: reads that setting
  once via `remember { Settings.Global.getFloat(...) }` and scales the animation's duration by it
  — 0 collapses to instant, 1 leaves it unchanged, 2 doubles it — the same multiplier semantics
  the platform's own View animators already use, applied by hand since Compose animations do not
  read this setting automatically. The read is deliberately one-time (a `remember`, not a live
  `ContentObserver`), documented as a scoped tradeoff rather than an oversight. Confirmed by an
  instrumented test of the pure `scaledAnimationDurationMillis` function itself (scale 0 → 0,
  scale 1 → unchanged, scale 2 → doubled) plus a toggle-reveals-content check for both versions —
  animation duration, like Dark Mode's colour, has no representation in the semantics tree, so
  the scaling logic is tested as a plain function rather than through a rendered node.

- Dark Mode topic (`ui/topic/darkmode/`, Topic 33) — a promotional banner card. Naive: hardcodes
  `Color.White`/`Color.Black` directly instead of reading `MaterialTheme.colorScheme`, so the
  banner renders identically regardless of which theme is active — correct-looking in light mode,
  but an unchanged bright white box once dark mode is on. Better: reads
  `MaterialTheme.colorScheme.primaryContainer`/`onPrimaryContainer`, so it automatically follows
  whichever palette is active, the same mechanism every other screen in the app already relies on.
  This is a deliberate, documented exception to this project's own mandatory dark-theme preview —
  the mismatch is the point of the demonstration. Confirmed by an instrumented test that renders
  a light-forced and a dark-forced copy of each version side by side (each wrapped in its own
  `lightColorScheme()`/`darkColorScheme()` `MaterialTheme`) and samples each card's centre pixel
  with `captureToImage()`: the Naive card's luminance is identical in both, the Better card's
  differs. This is the project's first topic to assert a rendered colour rather than a semantics
  property, since colour has no representation in the semantics tree at all.

- Colour Contrast and Colour Independence topic (`ui/topic/colourcontrast/`, Topic 32) — a
  three-row recent-orders list (Delivered / Pending / Cancelled), each row a coloured dot next to
  an order id. Naive: the dot's colour is the *only* signal of status — no text anywhere says
  "Delivered", "Pending", or "Cancelled" — and the colours themselves (calculated with the WCAG
  relative-luminance formula against a fixed `#F5F5F5` card background) measure roughly 1.0–2.0:1,
  well under the 3:1 WCAG 1.4.11 minimum for non-text UI components. Better: each row pairs its
  colour with a Material icon (`CheckCircle` / `Info` / `Close`) and a visible text label, and
  uses more saturated colours that measure at least 4.5:1 against the same background. Adds
  `androidx.compose.material:material-icons-core` as a new dependency (this project's first icon
  usage) to supply those icons. Confirmed by an instrumented test reading each row's own merged
  semantics text back: the Naive row's text contains the order id but never the status word, the
  Better row's contains both. Contrast-ratio compliance itself is explicitly documented as a
  Level 2 (Accessibility Test Framework) concern per `TESTING.md`, not something a Level 1
  semantics-tree instrumented test can verify — the test here only confirms the non-colour cue is
  present, not the actual contrast ratio.

- Font Scale topic (`ui/topic/fontscale/`, Topic 31) — a delivery-notice card. Naive: the card is
  wrapped in `Modifier.heightIn(max = 64.dp)` with `clipToBounds()`, and its text hardcodes
  `fontSize = 14.sp` and `fontWeight = FontWeight.Normal` instead of using
  `MaterialTheme.typography`. The height cap is in dp and does not scale with the system font
  size setting, so at a large enough scale the sp-sized text needs more room than the cap allows
  and `clipToBounds()` makes the overflow actually disappear; hardcoding `FontWeight.Normal` also
  leaves nothing for the system's Bold Text setting (`Configuration.fontWeightAdjustment`,
  requirements §3.6.1) to adjust. Better: no height cap and no hardcoded weight, so the card grows
  with the text and the Bold Text setting is free to apply. This is a deliberate, documented
  exception to this project's own "no screen may clip at 200%" rule — the clipping is the point of
  the demonstration. Confirmed by an instrumented test that renders both versions side by side at
  3x font scale (via a `LocalDensity` override) and reads each card's measured height back from
  the semantics tree: the Naive card never exceeds its fixed cap while the Better card grows past
  it with the identical string at the identical width.

- Keyboard-Only Operation topic (`ui/topic/keyboardonlyoperation/`, Topic 30) — a three-action
  article toolbar (Share, Bookmark, More). Naive: Share and More use `Modifier.clickable`;
  Bookmark is built with a bare `Modifier.pointerInput(Unit) { detectTapGestures { ... } }`
  instead — the same "draw your own gesture handling" failure mode already established in
  Switch: Platform vs. Custom, here costing keyboard/switch-access reachability specifically
  rather than a missing role. Confirmed by an instrumented test that Bookmark carries neither
  `SemanticsActions.OnClick` nor `SemanticsActions.RequestFocus` at all — structurally
  unreachable by keyboard Tab order, switch-access scanning, and TalkBack's own swipe traversal
  simultaneously, since `pointerInput` never touches the semantics tree on its own. Better uses
  `Modifier.clickable` for all three.

- Keyboard Focus Indicator topic (`ui/topic/keyboardfocusindicator/`, Topic 29) — opens
  requirements §3.6 (Visual and Motor). A row of three action chips. Naive: bare
  `Modifier.clickable`, focusable but drawing no visible indicator when focus moves to it via an
  external keyboard. Better: reuses the exact pattern already shipped in `NaiveToggle.kt` —
  `Modifier.onFocusChanged` observing `clickable`'s own internal focus target, a border drawn
  conditionally while focused, ≥3:1 contrast in both themes. Developer note states plainly what
  an instrumented test can't confirm here: whether a ring is actually drawn is a pixel-level,
  sighted fact outside the semantics tree — the test only confirms both versions' chips are
  genuinely focusable, which is honestly true of both; the real difference needs eyes on a real
  external keyboard. A real threading bug surfaced while writing that test — invoking the
  `RequestFocus` semantics action directly from the test thread throws
  `CalledFromWrongThreadException`; fixed by wrapping the call in `composeTestRule.runOnIdle {}`.

- IME Actions topic (`ui/topic/imeactions/`, Topic 28) — closes requirements §3.5 (Forms and
  Input). A two-field contact form. Naive: default `KeyboardOptions` on both fields, the
  keyboard's own action button left generic. Better: `ImeAction.Next`/`KeyboardActions.onNext`
  advances focus, `ImeAction.Done`/`onDone` hides the keyboard and marks the form ready. A first
  pass wrongly concluded `KeyboardOptions.imeAction` has no semantics wiring at all, based on
  grepping the wrong file (`BasicTextField.kt`); an on-device test failure
  (`performImeAction()` refuses to run on a `Default` action) surfaced the real wiring in
  `CoreTextFieldSemanticsModifier.kt`, corrected before shipping. Both the `ImeAction` semantics
  value and the actual `performImeAction()` behaviour are confirmed by instrumented test.

- Autofill Hints topic (`ui/topic/autofillhints/`, Topic 27) — a sign-up form (username, new
  password), enriching requirements §3.5.2 into a built topic. Naive: no `ContentType` declared
  on either field, so a password manager cannot detect or fill them, forcing manual entry — a
  disproportionate cost for a TalkBack user compared to a sighted one. Better:
  `Modifier.semantics { contentType = ContentType.Username }` / `ContentType.NewPassword`,
  confirmed present in the resolved `ui-android` sources, confirmed by an instrumented test
  reading each field's `SemanticsProperties.ContentType` back. Developer note carries the
  correction already logged in the topic backlog: this is opt-in work on any field type, not
  something a plain `TextField` gets for free either.

- Validation and Error Focus topic (`ui/topic/validationanderrorfocus/`, Topic 26) — a two-field
  sign-in form. Both versions mark invalid fields using the Error Semantics topic's established
  `error()` pattern unchanged; the only difference is that Better moves focus to the first invalid
  field on submit via `FocusRequester.requestFocus()`. Verified before assuming the Focus After
  Navigation topic's two-step fix would be needed again: `BasicTextField`'s focus target uses
  `Modifier.focusable()`'s default `Focusability.Always`, not the `Focusability.SystemDefined`
  that gated `Button`'s `requestFocus()` on touch/keyboard input mode, and there's no dialog or
  window transition in this flow either — confirmed by reading `Focusable.kt`/`BasicTextField.kt`
  directly, and empirically by instrumented test, that a direct call is enough here.

- Text Field Labelling topic (`ui/topic/textfieldlabelling/`, Topic 24) — opens requirements §3.5
  (Forms and Input). A "Name" field, pre-filled with "Alex" in both versions so the failure state
  doesn't require simulated typing. Naive: `placeholder`, used as a label. Better: `label`.
  Verified via `TextFieldImpl.kt` before writing either developer note, not assumed: the
  placeholder composable is only created `if (placeholder != null && transformedText.isEmpty() &&
  showPlaceholder)`, so it leaves the composition entirely — not merely fades visually — the
  moment any text is entered, while `label`'s composable is created unconditionally whenever
  `label != null` and only animates size/position. Confirmed by an instrumented test reading each
  field's merged semantics text back with "Alex" already present: Better's still contains "Name",
  Naive's does not.

- Error Semantics topic (`ui/topic/errorsemantics/`, Topic 23) — a static, already-invalid email
  field. Building this corrected the topic's own premise: `OutlinedTextField(isError = true)` is
  not semantically silent — reading `TextFieldImpl.kt` shows it applies its own
  `semantics { error(...) }` internally via `defaultErrorSemantics`, with a generic,
  locale-dependent default message ("Invalid input", confirmed on-device via a German-locale
  reading of "Ungültige Eingabe"). The real Naive/Better contrast is therefore generic-vs-specific,
  not absent-vs-present: Naive relies on that automatic generic message; Better overrides it with
  the actual reason (`Modifier.semantics { error("Enter a valid email address") }`), confirmed by
  an instrumented test distinguishing the two rather than checking for mere presence. Added a new
  `CLAUDE.md` "Established By Trial" section for this finding, relevant to the still-unbuilt Text
  Field Labelling and Validation and Error Focus topics.

- Selectable and Copyable Text topic (`ui/topic/selectablecopyabletext/`, Topic 22) — no naive
  counterpart, and corrects a doc inconsistency: the catalogue row said "Weak — §4.5" while §4.5's
  own list already treated this as fully disabled, now both agree ("No — §4.5"). Demo: a
  `SelectionContainer`-wrapped paragraph (real text selection/copy) plus an order-reference `Card`
  that isn't real selectable text but still needs a copy affordance via
  `Modifier.semantics { copyText(label) { ... } }`, confirmed by an instrumented test invoking the
  action directly and checking the resulting status update. Also migrated off the deprecated
  `ClipboardManager`/`LocalClipboardManager` to the suspend-based `Clipboard`/`LocalClipboard` —
  a Better file needing a suppression would have meant something was wrong, so this got fixed
  instead of suppressed, per `CLAUDE.md`'s own rule.

- Verbatim Strings topic (`ui/topic/verbatimstrings/`, Topic 21). An order-confirmation reference
  code ("Reference: AB1234"). Naive: a plain string, no TTS annotation of any kind. Better: builds
  an `AnnotatedString` and marks just the code segment with
  `withAnnotation(VerbatimTtsAnnotation(code)) { append(code) }`, confirmed present in this
  Compose version by reading `ui-text`'s `TtsAnnotation.kt`/`AnnotatedString.kt` directly, and read
  back in the instrumented test via `AnnotatedString.getTtsAnnotations()`. The developer note
  states the platform gap honestly rather than implying it's closed: the View system's `TtsSpan`
  additionally has `TYPE_TELEPHONE`/`TYPE_DIGITS`/`TYPE_MONEY`/`TYPE_ORDINAL`, each a different
  reading strategy; Compose exposes only one "read this literally" marker, so phone numbers in
  particular still have no clean Compose answer.

- announceForAccessibility topic (`ui/topic/announceforaccessibility/`, Topic 20) — no naive
  counterpart (§4.5), same single-implementation pattern as Custom Actions. Contrasts
  `View.announceForAccessibility` (reached via `LocalView.current`, no `AndroidView` needed)
  against a live region on the same kind of status update. Confirmed the method is not merely
  discouraged by convention but `@Deprecated` in the Android SDK itself (the compiler's own
  warning at the call site, suppressed deliberately since using it is the point). Instrumented
  test confirms the structural asymmetry the topic is about: the live-region side has a real
  `LiveRegion` semantics property to assert on, the announce side has none at all, since it
  dispatches a raw `AccessibilityEvent` rather than setting anything on the semantics tree.

- Grids That Are Not Tables topic (`ui/topic/gridsthatarenottables/`, Topic 7) — closes
  requirements §3.2 (Collections) and resolves open question #2. A 12-tile photo grid via
  `LazyVerticalGrid`. Building this corrected a real assumption in requirements §3.2.1: the grid's
  own default `CollectionInfo` turned out to be `(rowCount = -1, columnCount = -1)` — both
  dimensions unknown, not the confident 2D report the doc expected — confirmed by reading
  `LazySemantics.kt` and by instrumented test. Better overrides it to report as one-dimensional
  per §3.2.1's normative rule (`CollectionInfo(rowCount = 12, columnCount = 1)` plus per-tile
  `CollectionItemInfo`), confirmed by instrumented test to cleanly win over the grid's own
  internal default — the same mechanism already established for `LazyColumn` in One-Dimensional
  Collections. Naive relies on the unmodified, uninformative-on-both-axes default.

- Modal Surfaces topic (`ui/topic/modalsurfaces/`, Topic 35) — resolves open question #3. A
  "More options" `ModalBottomSheet` triggered from a button, deliberately not another
  `AlertDialog` picker since Focus After Navigation already covers that shape. Both versions use
  `ModalBottomSheetProperties()` at its defaults and never disable them — confirmed by reading
  `ModalBottomSheet.android.kt` that `shouldDismissOnBackPress` defaults to `true` and the sheet's
  scrim wires `onDismissRequest` unconditionally, not even configurable — the explicit §4.6
  exception this topic is built around. Better requests focus into the sheet's first action on
  open and returns it to the trigger button on close (the latter reusing Focus After Navigation's
  two-step fix verbatim), and gives the sheet content a `paneTitle` announcing its appearance.
  Building the open-on-appear focus request surfaced a real race, caught by instrumented test
  failure rather than assumed away: requesting focus from the outer composable, keyed on the
  sheet's visibility, can run before the sheet's own content has composed and crash with
  "FocusRequester is not initialized" — fixed by moving that `LaunchedEffect` inside the sheet's
  own content scope, where it is guaranteed to run only after that content commits.

- Custom Actions topic (`ui/topic/customactions/`, Topic 15) — the first topic in the catalogue
  to actually exercise requirements §4.5 (no naive counterpart). A swipeable inbox message row
  (`SwipeToDismissBox`) with Archive and Delete reachable only by swipe, no persistent button for
  either. `Modifier.semantics { customActions = listOf(CustomAccessibilityAction(...)) }` gives a
  keyboard or TalkBack user a local-context-menu equivalent for both actions, confirmed by an
  instrumented test reading `SemanticsActions.CustomActions` back with the exact expected labels.
  The app bar's toggle-disabling infrastructure (`AppScaffold.kt`/`NaiveToggle.kt`) needed no
  changes at all — confirmed both by reading the code and by inspecting the running app's
  accessibility tree directly, where the toggle correctly reports `enabled="false"` on this topic.

### Changed

- Added `androidx.compose.material:material-icons-core` as a dependency (`gradle/libs.versions.toml`,
  `app/build.gradle.kts`) for the Colour Contrast and Colour Independence topic's status icons —
  this project's first use of Material icons.
- `docs/ActuA11y_Requirements.md`: added a new `§3.9 Findings from real-world use` section
  (Topic 46, "Concatenated content descriptions" — a `contentDescription` joining several pieces
  of information with no pauses, and the `\n`-segmentation fix, cross-referenced against Topic
  21's verbatim-digit problem rather than merged into it) — sourced from topic-backlog item 1,
  kept in its own trailing section since it comes from lived bugs rather than the EN 301 549
  update §3.8 is scoped to. Enriched Topic 27 (Autofill hints, §3.5.2) with topic-backlog item 3:
  the accessibility-first framing for why autofill matters disproportionately for TalkBack users,
  and the correction that `ContentType` declarations are opt-in work on any field, not free even
  on a plain `TextField`. Both topics are catalogued only — not yet built, matching the precedent
  set by the EN 301 549 batch, where cataloguing and building were deliberately separate sessions.
- `CLAUDE.md`'s "Compose Collection Semantics" section updated with the `LazyVerticalGrid` default
  finding above, and `§10`'s open questions #1 and #2 marked resolved (see `docs/ActuA11y_Requirements.md`
  for both).
- `README.md`'s Coverage section updated for the expanded catalogue: topic count corrected from
  "forty-five" to "forty-six" (the real total, including Topic 46 from `§3.9`), the EN 301 549
  count corrected from nine to eight (Topic 46 comes from `§3.9`, not the `§3.8` EN 301 549 batch),
  and the "most topic screens are not implemented yet" banner updated to reflect that thirty-six
  of forty-six now are.
- `CLAUDE.md`'s Project Overview and "AGP Behaviour" section corrected from a stale `AGP 9.3.1` to
  the actual `9.3.2` already in use since the `b5b3d81` bump.

### Fixed

- `app/src/test/java/de/frpeters/actua11y/ExampleUnitTest.kt` was missing the Apache 2.0 header —
  overlooked by the 2026-07-31 project-wide retrofit because it lives in `src/test`, a source set
  that retrofit's search didn't cover.

## [0.4.0] - 2026-09-01

### Added

- Switch: Platform vs. Custom topic (`ui/topic/switchplatformvscustom/`, catalogue Topic 43) — a
  Do Not Disturb toggle drawn from scratch with `Canvas`, identical pixels in both versions.
  Naive attaches a bare `Modifier.clickable`, which registers a real click action (confirmed via
  `Clickable.kt`'s `AbstractClickableNode.applySemantics()`) but supplies no `Role.Switch`,
  `ToggleableState`, or label — a control TalkBack can activate but say nothing about. Better
  replaces it with `Modifier.toggleable(role = Role.Switch)` plus an explicit
  `contentDescription`, restoring by hand exactly what Topic 11 (Composite Controls) gets for
  free from a real, unmodified `Switch()`. This is the layered-conformance model the rest of the
  catalogue's platform controls quietly rely on, made explicit for the first time.
- State vs. Content Description topic (`ui/topic/statevscontentdescription/`, Topic 18) — a "Show
  details" disclosure. Naive rebuilds `contentDescription` as `"$label, $state"` on every toggle,
  folding a control's stable name and its changing state into one string; Better keeps
  `contentDescription` constant and carries the changing half in `stateDescription` instead,
  confirmed by an instrumented test reading both properties across a real click rather than one
  snapshot. Opens requirements §3.4 (Text and Announcement).
- Live Regions topic (`ui/topic/liveregions/`, Topic 19) — a flight status display. Better marks
  the status `Text` `liveRegion = LiveRegionMode.Polite`, so a status change announces itself
  without requiring focus or a full navigation event; Naive carries no live region at all, so the
  same change is visually obvious and audibly silent. The developer note also covers two nuances
  not built into the demo itself: a node leaving composition announces nothing, and identical
  text does not re-announce, since the underlying change detection is a text diff.
- Progress and Sliders topic (`ui/topic/progressandsliders/`, Topic 16) — a hand-built upload
  progress bar (two coloured `Box`es, no platform composable). Naive carries no semantics at all,
  so the bar is not even a stop during a TalkBack swipe; Better adds
  `progressBarRangeInfo = ProgressBarRangeInfo(current, range)` plus a `contentDescription`,
  confirmed by an instrumented test asserting `current` tracks the same value the bar's visual
  width is drawn from after a real state change.
- `TESTING.md` — a reader-facing explanation of what each level of accessibility testing actually
  catches and requires: static analysis (Lint), Compose semantics tree assertions, the
  Accessibility Test Framework (`AccessibilityChecks`/Accessibility Scanner), and real assistive
  technology. Written for anyone building their own app, not only contributors to this one.
  Cross-linked from `README.md`, `CLAUDE.md`, and requirements §8.
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
- `README.md`'s Coverage section updated for the expanded catalogue — topic count and per-category
  lists now include the nine topics added above.
- AGP 9.3.1 → 9.3.2.

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

[Unreleased]: https://github.com/frankrpeters/ActuA11y/compare/v0.5.0...dev
[0.5.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/frankrpeters/ActuA11y/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/frankrpeters/ActuA11y/releases/tag/v0.1.0
