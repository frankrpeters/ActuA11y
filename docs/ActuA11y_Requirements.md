# ActuA11y — Requirements Document

**Version:** 0.3
**Platform:** Android
**Language:** Kotlin
**UI framework:** Jetpack Compose (exclusively; see §7)
**Package name:** `de.frpeters.actua11y`
**License:** Apache 2.0
**Repository:** GitHub — `ActuA11y`
**Min API:** 28 (Android 9.0) — ⚠️ see §9, question 4

> **Changes from 0.1:** the debug-gated "bad screen" mechanism is replaced by a user-facing
> Naive/Better toggle (§4, §6); §3 rewritten in Compose terms; build-variant terminology
> corrected.
> **Changes from 0.2:** topic registry (§4.7); lint suppression convention (§7); semantics
> testing brought into scope (§8); `minSdk` discrepancy recorded (§9).

---

## 1. Purpose and Goals

ActuA11y is an open source Android reference application with two audiences.

**Android developers.** The source code is the deliverable. Each topic demonstrates a correct
implementation of an accessibility pattern alongside a functionally equivalent implementation
that neglects accessibility, so the two can be read and run side by side.

**Users of assistive technology, and people learning to use it.** The running app is a safe
environment for exploring TalkBack, external keyboard navigation, or switch access. It also
lets a user experience directly what a neglected implementation feels like — and, by
toggling, what the same screen feels like when done properly.

The second audience is not merely tolerated. The Naive/Better toggle (§4.4) exists because the
people best placed to appreciate the contrast are the people who encounter inaccessible
software daily.

---

## 2. Non-Goals

- No real-world functionality: no persistence beyond UI state, no network, no accounts.
- Not an automated accessibility scanner.
- Not a replacement for official Android accessibility documentation — a companion to it.
- No iOS or cross-platform targets.
- Not a conformance certification of any kind (see README disclaimer).

---

## 3. Accessibility Coverage

Each entry below becomes one topic screen. Every topic screen exists in two implementations —
**Naive** and **Better** — unless marked otherwise (see §4.5).

The naming is deliberate. "Naive" describes what was actually done: built without considering
accessibility, which is the real-world failure mode, not malice. "Better" is comparative
rather than absolute, because the reference implementation may itself be improvable.

### 3.1 Structure and Traversal

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 1 | Traversal groups | `isTraversalGroup` | Yes | Groups make swipe navigation coherent over headers, footers, FABs. Formerly `isContainer` — renamed; older sources mislead. |
| 2 | Traversal index | `traversalIndex` | Yes | Only sorts *within* an enclosing traversal group. Applied without one, it silently does nothing. |
| 3 | Headings | `semantics { heading() }` | Yes | Enables TalkBack heading-by-heading navigation. Absent headings force linear swiping through the whole screen. |
| 4 | Pane titles | `semantics { paneTitle = … }` | Yes | Announced on content change without full navigation. |
| 5 | Focus after navigation | `FocusRequester`, `LaunchedEffect` | Yes | Where focus lands after a screen transition or after a picker dismisses. Invisible to sighted testing. |

### 3.2 Collections

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 6 | One-dimensional collections | `collectionInfo`, `collectionItemInfo` | Yes | "Item 8 of 24" requires correct counts. Unknown totals (paging) must be `-1`, not `0`. |
| 7 | Grids that are not tables | `collectionInfo(columnCount = 1)` | Yes | **See §3.2.1.** |
| 8 | Genuine tables | `collectionInfo`, `rowIndex`/`columnIndex` | Yes | Compose has no table composable; row/column semantics are constructed by hand. |
| 9 | Lazy list pitfalls | `LazyColumn`, `LazyRow`, sticky headers | Yes | Sticky headers and mixed item types corrupt auto-supplied indices, so positional announcements lie. |

#### 3.2.1 Grid dimensionality — normative rule

A two-dimensional *visual* layout should be reported as a **one-dimensional collection** when
the column position carries no semantic meaning — photo grids, icon pickers, product tiles.
Announcing "row 3, column 2" for such a layout is noise, and it displaces the information the
user actually needs ("item 8 of 24").

Two-dimensional collection semantics are reserved for layouts where a column is a distinct
field with its own meaning — genuine data tables.

> **Source:** this rule was given to the project author by the assessor who certified a
> professional Android application under BITV. It is stricter than what Android's own defaults
> produce: `LazyVerticalGrid` supplies grid semantics reflecting the visual layout regardless
> of whether the columns mean anything.

> ⚠️ **Requires empirical verification before implementation.** It is not established whether
> applying `semantics { collectionInfo = CollectionInfo(rowCount = n, columnCount = 1) }` to a
> `LazyVerticalGrid` cleanly overrides the internally supplied value, or whether the lazy
> layout's own semantics node takes precedence. Verify with a real TalkBack test and record
> the result **together with the Compose BOM version tested**. If the override does not work,
> document the workaround (likely: a `LazyColumn` of `Row`s with manual `collectionItemInfo`)
> as the reference implementation, and document the failed approach as well — a documented
> dead end is worth as much as a working pattern here.

### 3.3 Controls and Interaction

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 10 | Input that is actually a button | `Role.Button`, `OutlinedTextFieldDefaults.DecorationBox` | Yes | **See §3.3.1.** |
| 11 | Composite controls | `toggleable`, `selectable`, `mergeDescendants` | Yes | **See §3.3.2.** |
| 12 | Selectable icon lists | `selectable`, `selectableGroup`, `Role.RadioButton` | Yes | Without `selectableGroup()` the "2 of 5" positional context is lost. Icon-only items need both a description and a selected state; implementations usually supply one. |
| 13 | Minimum touch target | `minimumInteractiveComponentSize()` | Yes | `IconButton` enforces 48dp. A bare `Modifier.clickable` on a 24dp `Icon` does not. Invisible in the layout inspector; obvious to a user with a tremor. |
| 14 | Disabled elements | `semantics { disabled() }` | Yes | A merely non-clickable element is skipped silently. One marked `disabled()` announces as disabled. Very different experience. |
| 15 | Custom actions | `customActions`, `CustomAccessibilityAction` | **No** — §4.5 | Swipe-only actions (dismiss, archive) with no equivalent in the TalkBack local context menu are unreachable. |
| 16 | Progress and sliders | `progressBarRangeInfo` | Yes | Custom progress indicators and sliders that announce nothing. |

#### 3.3.1 Input that looks editable but is a button

Very common — date, time, colour, and location pickers — and almost always wrong.

Both naive approaches fail, differently:

- `TextField(readOnly = true)` still announces as an edit field. TalkBack offers text-editing
  actions and cursor movement that do nothing. The user is told they may type; they may not.
- `TextField(enabled = false)` announces as disabled, which is worse: it signals *not
  actionable* for what is the primary control on the screen.

The Better implementation is a non-`TextField` composable styled to look like one
(`OutlinedTextFieldDefaults.DecorationBox` supplies the visual shell without editing
semantics), carrying `Role.Button`, with the announcement composed as label + current value +
role: *"Appointment date, 14 March 2026, button."* The current value must be in the
announcement, not only visible.

Focus return after the picker dismisses belongs to this topic. The naive failure is
sighted-invisible: focus lands at the top of the screen and the user must swipe back to where
they were.

#### 3.3.2 Composite controls

A row containing a label, a description, and a `Switch` is one control, not three.
`Modifier.toggleable` on the row plus `mergeDescendants = true` is the obvious half.

The half that is routinely missed: the inner control must be made non-interactive —
`Switch(onCheckedChange = null)`, `RadioButton(onClick = null)`,
`Checkbox(onCheckedChange = null)` — or it stays independently focusable and the user meets
the same control twice, once as the row and once as the switch. This API convention is not
guessable; it has to be known.

Also in scope: `Role` belongs on the container, not the child; `ToggleableState` for tri-state;
and merged announcement order follows composition order, which `traversalIndex` cannot reorder
inside a merged node.

### 3.4 Text and Announcement

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 17 | Where contentDescription goes — and doesn't | `contentDescription`, `clearAndSetSemantics` | Yes | On a `Text`, it overrides the text itself, breaking selection and translation. "Save button" on a `Button` yields "Save button, button". |
| 18 | State vs. content description | `stateDescription` | Yes | Expanded/collapsed, selected, loading. Developers cram state into `contentDescription`, where it is read at the wrong moment. |
| 19 | Live regions | `liveRegion`, `LiveRegionMode` | Yes | Identical text does not re-announce. `Assertive` interrupts mid-utterance and is nearly always wrong. A node leaving composition announces nothing. |
| 20 | announceForAccessibility | `View.announceForAccessibility` | **No** — §4.5 | Discouraged by Google; announcements are dropped during screen transitions. The demonstration is live region *versus* announcement. |
| 21 | Verbatim strings | `VerbatimTtsAnnotation` | Yes | **See §3.4.1.** |
| 22 | Selectable and copyable text | `SelectionContainer`, `semantics { copyText { } }` | Weak — §4.5 | `SelectionContainer` interferes with clickable children. |
| 23 | Error semantics | `semantics { error(…) }` | Yes | Validation announced as an error rather than as ordinary text. Pairs with §3.5. |

#### 3.4.1 Verbatim strings and the TTS gap

Phone numbers, postal codes, reference numbers, and IBANs are read as quantities rather than
digit sequences unless marked.

This topic documents a genuine platform gap rather than a tidy pattern. The View system
provides `TtsSpan` with `TYPE_TELEPHONE`, `TYPE_DIGITS`, `TYPE_MONEY`, and `TYPE_ORDINAL`.
Compose exposes only `VerbatimTtsAnnotation`. Phone numbers in particular have no clean Compose
answer.

The Better implementation shows what *is* achievable with `VerbatimTtsAnnotation`; the
developer note states plainly what is not, and names the interop escape hatch (§3.7) for cases
where full `TtsSpan` control is required. An honestly documented limitation is more useful than
a pattern that pretends the gap is closed.

### 3.5 Forms and Input

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 24 | Text field labelling | `label`, `placeholder`, `contentDescription` | Yes | When each is appropriate; placeholder-as-label is the classic failure. |
| 25 | Password fields | `PasswordVisualTransformation`, `stateDescription` | Yes | **See §3.5.1.** |
| 26 | Validation and error focus | `error()`, `FocusRequester` | Yes | Announcing the error, and moving focus to the offending field. |
| 27 | Autofill hints | `ContentType` semantics | Yes | Correct hints for common field types. |
| 28 | IME actions | `KeyboardOptions`, `KeyboardActions` | Yes | Correct action button and its behaviour. |

#### 3.5.1 Password fields and the reveal button

The reveal button's description must track state — "Show password" ↔ "Hide password" — rather
than being a static label.

Worth surfacing in the developer note: TalkBack speaks password characters as "dot" unless
headphones are connected. This is a deliberate security behaviour and is routinely misdiagnosed
as a bug. Documenting it here saves someone a wasted investigation.

### 3.6 Visual and Motor

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 29 | Keyboard focus indicator | `focusable`, `indication`, `focusProperties` | Yes | `Modifier.clickable` is focusable but draws no visible focus by default. The indicator needs ≥3:1 contrast against adjacent colours in both themes (WCAG 2.2 SC 2.4.11, 2.4.13). |
| 30 | Keyboard-only operation | `onKeyEvent`, focus order | Yes | Every function reachable without touch; logical tab sequence. |
| 31 | Font scale | `sp`, layout constraints | Yes | **See §3.6.1.** |
| 32 | Colour contrast and colour independence | theme, `Icon` + text pairing | Yes | WCAG AA ratios; no information by colour alone. |
| 33 | Dark mode | `isSystemInDarkTheme` | Yes | Contrast maintained in both themes; focus indicator visible in both. |
| 34 | Reduced motion | `Settings.Global.ANIMATOR_DURATION_SCALE` | Yes | Respecting the system animation-scale setting. |
| 35 | Modal surfaces | `Dialog`, `ModalBottomSheet` | Yes — with constraint | Focus containment, dismissibility, announcement on appear, focus destination on dismiss. **Subject to §4.6.** |

#### 3.6.1 Non-linear font scaling

From Android 14 (API 34), `sp` no longer scales linearly above 100%. A layout verified at 200%
on API 33 behaves differently on API 34 and later. This topic demonstrates a behavioural
difference across the supported API range, and the developer note should state which API levels
were used for verification.

### 3.7 Interop

| # | Topic | Primary APIs | Naive counterpart | Central teaching point |
|---|---|---|---|---|
| 36 | Fixing accessibility on a wrapped View | `AndroidView`, `ViewCompat.setAccessibilityDelegate` | Yes | See below. |

Most production Android codebases are hybrid rather than pure Compose. A screen showing how to
repair the accessibility of a legacy `View` hosted inside `AndroidView` — supplying semantics
the original omits via `AccessibilityDelegateCompat`, and understanding how View semantics
surface through the Compose semantics tree — is likely among the most practically valuable
screens in the project.

This is the sole and deliberate exception to the Compose-only rule in §7. The exception exists
because interop *is* the topic, not because Views are being demonstrated as an alternative.

---

## 4. Application Structure

### 4.1 Navigation

- Single activity, Jetpack Navigation Compose.
- Home screen lists categories (§3.1–§3.7).
- Each category lists its topics; each topic is one screen.
- Routes, titles, categories and naive-support flags are all derived from the topic registry
  (§4.7). No route is declared by hand.

### 4.2 Persistent app bar

**The top app bar is composed once, above the `NavHost` — not inside individual screens.**

This is a structural requirement, not a stylistic preference. It guarantees that the
Naive/Better toggle and the back affordance cannot be obscured, overridden, or made unreachable
by the content of any screen, including a deliberately naive one. See §4.6.

The app bar contains the back affordance (when not on Home), the current screen title, and the
Naive/Better toggle.

### 4.3 Topic screen layout

Content area, below the persistent app bar:

1. **Plain-language description** — what this topic is, why it matters, for a non-technical
   reader.
2. **What to try** — a short instruction, e.g. "Turn on TalkBack and swipe through the list
   below. Note what is announced for each item, then switch to the other version and compare."
3. **Interactive demonstration.**
4. **Developer note (collapsible)** — APIs, rationale, known limitations. Technical language is
   appropriate here. Present in both Naive and Better versions; in the Naive version it explains
   what is wrong and why, factually rather than reproachfully.

### 4.4 The Naive/Better toggle

A control in the app bar's actions area, present in **all build variants**, determining which
implementation of the current topic is displayed.

Behaviour:

- **Global state**, hoisted above the `NavHost` — set once, applies to every topic screen until
  changed.
- **Defaults to Better on every cold start.** May survive configuration change; must not be
  persisted to storage. A user opening the app must never land in a naive implementation
  without having chosen it.
- **Home and category screens are always accessible implementations.** The toggle affects topic
  content only, and is disabled elsewhere.

Its own accessibility is a reference implementation in its own right — it is the most-used
control in the app and the first thing anyone encounters:

- It is a composite control: label and switch form one focusable element, implemented per
  §3.3.2 including the `onCheckedChange = null` convention.
- `stateDescription` reads as "Accessible version" / "Naive version", not the default on/off.
- Reachable by external keyboard, with a visible focus indicator, in every state and both
  themes.
- Touch target ≥48dp.
- On toggle, focus remains on the control. The content swap destroys focus in the content area;
  focus must not jump there or to the top of the screen.
- The state change is announced via `stateDescription`, not a separate announcement.

### 4.5 Topics without a meaningful naive counterpart

Some topics have no functionally equivalent naive implementation. A naive version of "custom
actions" is simply a screen with no custom actions, which is a real failure but not an
equivalent screen — it breaks the parity that makes the comparison informative.

For these topics the toggle is **rendered disabled, with a short explanation in the content
area** stating why there is no counterpart. It is not hidden: a control that appears and
disappears between screens is its own navigation annoyance, and the explanation is itself
informative. `semantics { disabled() }` is required in addition to the disabled parameter, so
the control is announced rather than skipped (topic 14).

Currently identified: topics 15, 20, and 22.

### 4.6 Escape route — hard constraint

**No naive implementation may prevent the user from reaching the app bar.**

A naive screen may be unnavigable *within its content area* — that is the entire point. It may
not compromise the global escape route.

Consequently:

- The app bar is outside the `NavHost` (§4.2) and is not affected by screen content.
- The hardware/gesture back action must function on every screen in every state.
- Any topic whose naive version would involve a genuine focus trap or a full-screen modal —
  topic 35 in particular — requires an explicitly documented exception describing how the user
  escapes. Design this before implementing the screen, not after.

This is the one failure mode in the project capable of harming a user rather than merely
teaching them something. It is worth over-engineering.

### 4.7 Topic registry

A single declarative list is the source of truth for every topic. Navigation, the home screen,
category listings, app-bar titles, and the toggle's enabled state are all derived from it.

```kotlin
enum class TopicCategory(@StringRes val titleRes: Int) { … }

data class Topic(
    val id: String,
    val category: TopicCategory,
    @StringRes val titleRes: Int,
    val supportsNaive: Boolean,
    val content: @Composable (showNaive: Boolean, modifier: Modifier) -> Unit
) {
    val route: String get() = "topic/$id"
}

object TopicRegistry {
    val all: List<Topic> = listOf( … )
    fun byRoute(route: String?): Topic? = all.firstOrNull { it.route == route }
}
```

Adding a topic is therefore: create the package, append one registry entry. Nothing else.

**Rationale:** thirty-six topics maintained across a sealed route class, a nav graph, a title
lookup, a toggle-support lookup and a category list is five opportunities for drift per topic.
Drift in a project whose purpose is to be read is worse than drift in one that is merely run.

---

## 5. Learner Experience

- All user-facing text is plain and jargon-free. Technical language lives in the developer note,
  which is collapsed by default.
- The home screen carries a short onboarding card explaining the app's dual purpose, the
  Naive/Better toggle, and how to enable TalkBack, with a link to Android's official TalkBack
  documentation.
- The onboarding card states that naive versions are deliberately flawed, so a user encountering
  one understands it is intentional rather than a defect in the app.
- No account, login, or data collection of any kind.

---

## 6. Build Variants

Terminology, since it matters for implementation: a **build variant** is the combination of one
**build type** (debug, release, and any custom types) with one **product flavour** from each
declared **flavour dimension**.

ActuA11y declares **no product flavours** and uses the two default build types.

The Naive/Better distinction is a **runtime toggle** (§4.4), not a build-time concern. Both
implementations are compiled into every variant, live in `src/main`, and are reachable by all
users.

> The 0.1 requirement to gate naive screens behind `BuildConfig.DEBUG` and a `src/debug/` source
> set is **withdrawn**. It served an audience model that no longer applies, and it carried a
> structural cost: code in `src/main` cannot reference code in `src/debug`, so the navigation
> graph would have needed a registry indirection to compile in release.

---

## 7. Code Organisation

- One topic per package: `ui/topic/<topicname>/`, containing `<Topic>Naive.kt`,
  `<Topic>Better.kt`, and a thin dispatcher.
- Naive and Better implementations must be **functionally equivalent**: same components, same
  layout, same behaviour for a sighted touch user. The only difference is accessibility
  semantics. If a topic cannot satisfy this, it belongs under §4.5.
- Comment conventions:
  - `// NAIVE:` — what is missing here, and what it costs the user.
  - `// BETTER:` — what was done.
  - `// WHY:` — the rationale. Comments explain *why*, not *what*.
  - `// VERIFIED:` — behaviour established by device testing with TalkBack, recording the
    Compose BOM version and API level tested. **Written only by a human who ran the test.**
    Where verification is pending, `// TODO(verify):` states what must be checked.
- **Lint:** Naive implementations deliberately violate accessibility lint rules. Suppress at
  file level in Naive files only, never via a baseline and never project-wide — a global
  suppression would also silence genuine errors in Better implementations, which is exactly the
  mistake this project warns against. The suppression names the rule being violated and is
  therefore itself instructive.
- Every source file carries the Apache 2.0 header.
- Compose exclusively. The single exception is topic 36 (§3.7), where View interop is the
  subject.
- No third-party UI libraries unless the library's accessibility behaviour is itself the point.
- Features above `minSdk` are annotated `@RequiresApi`; the developer note states the
  requirement.
- No hardcoded UI strings — everything in `res/values/strings.xml`.
- **Previews:** every topic carries light/100%, dark/100%, and light/200% font scale previews
  for both implementations. Topics 31 and 33 are font scale and dark mode; a screen that clips
  at 200% is a defect in this project specifically.

---

## 8. Testing

**In scope for v1:** Compose semantics assertions (`androidx.compose.ui.test`). These run on an
emulator without TalkBack and exercise precisely the layer this project concerns. Each topic
carries an instrumented test asserting that the Better implementation has the expected semantics
and the Naive one does not.

These tests are reference material in their own right — the fact that the semantics tree can be
asserted against at all is not widely known — and should be commented as such.

**Out of scope for v1:** automated accessibility scanning via Espresso `AccessibilityChecks`,
which is a different mechanism and remains a v2 candidate.

⚠️ No automated test substitutes for running the app with TalkBack on a real device. Announcement
wording, announcement order, and focus behaviour after content swaps cannot be verified any other
way.

---

## 9. Out of Scope for v1

- `AccessibilityChecks` scanning (see §8).
- Localisation. String resources are used throughout regardless, so adding German later is
  additive rather than a rewrite.
- Tablet and large-screen layouts.
- WebView accessibility.
- TV and Automotive profiles.
- A companion documentation website.

---

## 10. Open Questions

| # | Question | Blocks | Notes |
|---|---|---|---|
| 1 | Apache 2.0 header automation | Nothing | IDE file template. |
| 2 | §3.2.1 grid override behaviour | Topic 7 | Requires device verification. |
| 3 | Topic 35 escape-route design | Topic 35 | Required by §4.6 before implementation. |
| 4 | `minSdk` — 28 or 30? | Nothing yet | This document specifies 28; the build script and `CLAUDE.md` say 30. Unresolved. If 30 was chosen deliberately, amend this document; if it was incidental, amend the build. Relevant to topic 31, where API-level differences in font scaling are the subject. |
