# Claude Code — ActuA11y Architecture Restructure

## Context

The initial scaffold is built and compiling: Compose is configured, `MainActivity` hosts a
`NavHost`, and there is a home screen plus one topic screen (Content Descriptions).

The design has since changed in one significant way, and the scaffold must be restructured
before further topic screens are added. Read `docs/ActuA11y_Requirements.md` (version 0.3)
and `CLAUDE.md` first — §4.2, §4.4, §4.5, §4.6, §4.7 and §6 of the requirements are new.

**The change:** deliberately inaccessible screens are no longer hidden behind
`BuildConfig.DEBUG`. They are a first-class, user-facing feature, selected at runtime by a
persistent toggle in the app bar. Every topic exists as a *Naive* and a *Better*
implementation, both compiled into every build variant.

Do this restructure now, while there is exactly one topic screen to migrate.

**Scope discipline:** this session is architecture only. Do not add new topic screens. The
pattern established here will be replicated thirty-five more times, so it is worth settling
first.

---

## Task 1 — Remove the debug gating

- Delete the `BuildConfig.DEBUG` conditional and the "Bad version" button from the topic
  screen's `TopAppBar`.
- Delete the associated `Toast` placeholder and its string resource.
- If a `src/debug/` source set was created, remove it.
- Leave `buildConfig = true` in `build.gradle.kts`; harmless and possibly wanted later.

No product flavours are to be introduced. The project uses the two default build types.

Also update `CLAUDE.md`: its "Accessibility Conventions" section still describes the old
per-screen `Scaffold`, the debug-gated button, and the `// GOOD:` comment prefix. A revised
`CLAUDE.md` accompanies this document — use it as the replacement rather than editing the old
one piecemeal.

---

## Task 2 — Topic registry

Create `navigation/TopicRegistry.kt` as the single source of truth. Requirements §4.7.

```kotlin
enum class TopicCategory(@StringRes val titleRes: Int) {
    STRUCTURE(R.string.category_structure),
    COLLECTIONS(R.string.category_collections),
    CONTROLS(R.string.category_controls),
    TEXT(R.string.category_text),
    FORMS(R.string.category_forms),
    VISUAL(R.string.category_visual),
    INTEROP(R.string.category_interop),
}

data class Topic(
    val id: String,
    val category: TopicCategory,
    @StringRes val titleRes: Int,
    val supportsNaive: Boolean,
    val content: @Composable (showNaive: Boolean, modifier: Modifier) -> Unit,
) {
    val route: String get() = "topic/$id"
}

object TopicRegistry {
    val all: List<Topic> = listOf(
        Topic(
            id = "content_descriptions",
            category = TopicCategory.TEXT,
            titleRes = R.string.topic_content_descriptions,
            supportsNaive = true,
            content = { showNaive, modifier ->
                ContentDescriptionsTopic(showNaive, modifier)
            },
        ),
    )

    fun byRoute(route: String?): Topic? = all.firstOrNull { it.route == route }
    fun byCategory(category: TopicCategory): List<Topic> = all.filter { it.category == category }
}
```

Declare all seven categories now even though only one has a topic — the home screen should show
the full structure from the start, with empty categories indicated as such.

The `NavHost` builds its topic destinations by iterating `TopicRegistry.all`. The existing
`Screen` sealed class keeps only the non-topic routes (Home, Category); its per-topic entries
are removed.

---

## Task 3 — Hoist the app bar above the NavHost

Currently each screen supplies its own `Scaffold` and `TopAppBar`. Restructure so there is a
**single** `Scaffold` and `TopAppBar`, composed once, with the `NavHost` in its content slot.

Hard structural requirement, not tidiness — requirements §4.6. No naive screen may be able to
obscure the app bar or trap focus away from it.

### `ui/AppScaffold.kt` (new)

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    var showNaive by rememberSaveable { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTopic = TopicRegistry.byRoute(currentRoute)

    Scaffold(
        topBar = {
            ActuA11yTopBar(
                title = /* topic title, category title, or app name */,
                canNavigateBack = navController.previousBackStackEntry != null,
                onBack = { navController.popBackStack() },
                showNaive = showNaive,
                onToggleNaive = { showNaive = it },
                toggleEnabled = currentTopic?.supportsNaive == true,
            )
        }
    ) { padding ->
        ActuA11yNavHost(
            navController = navController,
            showNaive = showNaive,
            modifier = Modifier.padding(padding),
        )
    }
}
```

`MainActivity` calls `AppScaffold()` inside `ActuA11yTheme`.

Notes:

- `showNaive` is hoisted above the `NavHost` so it persists across navigation (§4.4).
- `rememberSaveable` so it survives configuration change — but **do not** persist it to
  DataStore or preferences. It must reset to Better on cold start (§4.4).
- `toggleEnabled` is false on Home and Category screens and on topics with
  `supportsNaive = false`.

### Screen composables

Remove `Scaffold`, `TopAppBar`, and the `onBack` parameter from `HomeScreen` and the topic
screen. They receive a `Modifier` and render content only.

⚠️ `paneTitle` semantics currently sit on each screen's `Scaffold` root. Move them to the
content root of each screen so the announcement still fires on navigation.

---

## Task 4 — The Naive/Better toggle

### `ui/components/NaiveToggle.kt` (new)

This control is itself one of the more important reference implementations in the project — it
is on every screen and is the first thing any user encounters. Comment it fully using the
`// BETTER:` and `// WHY:` conventions.

Requirements, from §4.4:

- Label and switch form **one** control: `Modifier.toggleable` on the containing `Row` with
  `mergeDescendants`, and `onCheckedChange = null` on the `Switch` so it is not independently
  focusable. This is the composite-control pattern topic 11 will later demonstrate; getting it
  right here is not optional.
- `stateDescription` reads "Accessible version" / "Naive version" — not the default on/off,
  which is meaningless here.
- Touch target ≥48dp.
- Visible keyboard focus indicator, ≥3:1 contrast in both light and dark themes.
- When `toggleEnabled` is false: `semantics { disabled() }` **in addition to** the disabled
  parameter, so TalkBack announces it as disabled rather than skipping it silently (§4.5).

### Focus behaviour on toggle

When the toggle flips, the content below is swapped wholesale and any focus inside it is
destroyed. Attach a `FocusRequester` and ensure focus remains on the toggle across the swap.

Do not add a separate `announceForAccessibility` call — the `stateDescription` change is the
announcement.

This cannot be verified without a device. Mark it `// TODO(verify):` describing what to check;
do not write `// VERIFIED:`.

---

## Task 5 — Migrate the existing topic

Restructure `ui/topic/contentdescriptions/` to:

```
ContentDescriptionsTopic.kt     // dispatcher
ContentDescriptionsNaive.kt
ContentDescriptionsBetter.kt
```

```kotlin
@Composable
fun ContentDescriptionsTopic(showNaive: Boolean, modifier: Modifier = Modifier) {
    if (showNaive) ContentDescriptionsNaive(modifier)
    else ContentDescriptionsBetter(modifier)
}
```

### Functional equivalence — the central constraint

The two must be **identical for a sighted touch user**: same components, same layout, same
visual result, same behaviour. The *only* difference is accessibility semantics.

If you find yourself changing layout or removing features to make the naive version "worse",
stop. Naive means the accessibility work was simply never done.

For this topic:

- **Better:** meaningful `contentDescription` on the informative image; `contentDescription` on
  the icon-only `IconButton`; `contentDescription = null` on the decorative image.
- **Naive:** no `contentDescription` anywhere. Same three elements, same appearance.

### Lint suppression

`ContentDescriptionsNaive.kt` gets a file-level suppression:

```kotlin
@file:Suppress("ContentDescription")   // NAIVE: intentional — see the developer note in this file
```

Never a lint baseline, never a project-wide disable. A global suppression would also silence
genuine mistakes in Better implementations, which is precisely the failure this project exists
to warn about. Better files should need no suppressions at all — if one does, investigate
rather than suppress.

### Developer note in both

Extract the collapsible note into `ui/components/DeveloperNote.kt` so all topics share it.
Present in both implementations: in Better it explains what was done and why; in Naive, what is
missing and what it costs the user. Keep the Naive wording factual rather than scolding.

---

## Task 6 — Previews

Standard preview set for **both** implementations of every topic:

- Light, 100% font scale
- Dark, 100% font scale
- Light, 200% font scale (`fontScale = 2.0f`)

Apply to the migrated topic and to `NaiveToggle`. The 200% preview is not optional — font scale
is itself a topic in this project.

---

## Task 7 — Semantics tests

Add test dependencies to the version catalog (`androidx.compose.ui:ui-test-junit4`,
`ui-test-manifest`) and write an instrumented test for the migrated topic:

- Better exposes the expected content descriptions; the decorative image exposes none.
- Naive exposes none of them.
- The toggle is present, has a click action, and carries the expected `stateDescription` in each
  state.

Use `onNodeWithContentDescription`, `assertHasClickAction`, and
`SemanticsProperties.StateDescription`. Comment the tests — the fact that the semantics tree can
be asserted against is itself reference material for developers reading this project.

This is the per-topic test template; every future topic gets its equivalent.

---

## Task 8 — Home screen

Add the onboarding card (requirements §5):

- What the app is, in plain language.
- What the toggle does, and that naive versions are **deliberately** flawed — so a user who
  encounters one understands it is intentional, not a bug in the app.
- A link to Android's official TalkBack documentation.

The card must be genuinely accessible: heading semantics on its title, link announced as a link
with its destination described.

The home screen lists all seven categories from `TopicRegistry`, with empty ones shown as empty
rather than hidden.

---

## Verify

1. Compiles; no new warnings.
2. Toggle present, keyboard-reachable, correct announcement in both states.
3. Toggling swaps content and leaves focus on the toggle.
4. Toggle disabled — and announced as disabled — on Home and Category screens.
5. `showNaive` survives rotation, resets to Better after cold start.
6. Back navigation works from every screen in both toggle states.
7. No `BuildConfig.DEBUG` conditionals remain in the UI layer.
8. `.\gradlew lint` passes with only the intended file-level suppression.
9. Semantics tests pass.
10. All previews render, including 200% font scale, without clipping.

Do not proceed past a compilation error.

---

## Do not

- Add topic screens beyond the migrated one.
- Write `// VERIFIED:` comments — you cannot run TalkBack. Use `// TODO(verify):`.
- Add dependencies beyond the Compose test artifacts named in Task 7.
- Start topics 7 or 35 — both carry unresolved open questions (requirements §10).
- Change `minSdk` — there is a recorded discrepancy between the build script and the
  requirements document (§10, question 4). Leave both as they are and flag it if it becomes
  relevant.
