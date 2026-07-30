# CLAUDE.md

Guidance for Claude Code when working in this repository.

`docs/ActuA11y_Requirements.md` is the authoritative specification of *what* to build. This
file describes *how this repository works*. Where the two disagree, the requirements document
wins — and the disagreement should be reported rather than silently resolved.

---

## Project Overview

**ActuA11y** is an open source Android accessibility reference application. It has no
real-world functionality. Its purpose is to demonstrate accessibility patterns in readable
source code, and to let users of assistive technology experience the difference between a
screen built with accessibility in mind and one built without.

Every topic exists in two implementations — **Naive** and **Better** — selected at runtime by
a toggle in the app bar. Both are compiled into every build variant.

- Package: `de.frpeters.actua11y`
- AGP 9.3.1 | compileSdk 36 | minSdk 28
- Kotlin, Jetpack Compose only
- License: Apache 2.0 — every source file carries the header

---

## Build Commands

```powershell
.\gradlew assembleDebug
.\gradlew assembleRelease
.\gradlew test
.\gradlew connectedAndroidTest          # requires device/emulator
.\gradlew test --tests "de.frpeters.actua11y.ExampleUnitTest"
.\gradlew clean
.\gradlew lint
```

---

## AGP Behaviour — Established By Trial

These were discovered by hitting them. Do not "correct" them back. Confirmed still true as of
AGP 9.3.1 / Kotlin 2.2.10 / Gradle 9.5.0.

- **Do NOT apply `org.jetbrains.kotlin.android`** in `app/build.gradle.kts`. AGP registers the
  `kotlin` extension internally; applying the plugin explicitly causes
  `Cannot add extension with name 'kotlin'`. Apply only `kotlin-compose`.
- **No `kotlinOptions` block.** `compileOptions { sourceCompatibility / targetCompatibility }`
  alone is sufficient; AGP 9.x syncs the Kotlin JVM target automatically.
- `compileSdk = 36` in plain integer form — not `release(36) { minorApiLevel = 1 }`, which is
  for SDK 36.1 extension APIs.
- XML base theme must be `android:Theme.Material.Light.NoActionBar` /
  `android:Theme.Material.NoActionBar`. `NoTitleBar` variants do not exist in the Material
  theme family and AAPT will error.
- `TopAppBar` is `@ExperimentalMaterial3Api` in Compose BOM 2025.05.00 — annotate callers
  `@OptIn(ExperimentalMaterial3Api::class)`.

---

## Project Structure

```
app/src/main/java/de/frpeters/actua11y/
  MainActivity.kt                     # sets theme, calls AppScaffold()
  ui/
    AppScaffold.kt                    # THE Scaffold + TopAppBar; hosts NavHost
    components/
      NaiveToggle.kt                  # the app-bar toggle
      DeveloperNote.kt                # collapsible note, shared by all topics
    theme/
    home/HomeScreen.kt
    category/CategoryScreen.kt
    topic/
      <topicname>/
        <Topic>Topic.kt               # dispatcher
        <Topic>Naive.kt
        <Topic>Better.kt
  navigation/
    TopicRegistry.kt                  # single source of truth for all topics
    ActuA11yNavHost.kt
gradle/libs.versions.toml             # add all dependencies here first
```

---

## The Two Structural Invariants

Everything else in this file is convention. These two are constraints.

### 1. The app bar is composed once, above the NavHost

`AppScaffold` owns the only `Scaffold` and the only `TopAppBar`. Screen composables receive
a `Modifier` and render content only — they must never introduce their own `Scaffold`,
`TopAppBar`, or full-screen overlay.

**Why:** a Naive screen is deliberately hard to navigate. It must never be able to obscure or
trap focus away from the toggle and back affordance, or a TalkBack user has no way out.
Requirements §4.6.

### 2. Naive and Better must be functionally equivalent

Same components, same layout, same visual result, same behaviour for a sighted touch user.
**The only difference is accessibility semantics.**

Naive does not mean "worse". It means the accessibility work was never done. Do not degrade
layout, remove features, or introduce bugs to make a Naive version look bad. If a topic
cannot be expressed this way, it belongs in the "no naive counterpart" category — set
`supportsNaive = false` and say so rather than forcing it.

---

## Adding a Topic

1. Create `ui/topic/<topicname>/` with the three files.
2. Append one entry to `TopicRegistry`.

That is all. Navigation, the home screen, category listings, the app-bar title, and the
toggle's enabled state are all derived from the registry. **Do not add routes to the nav
graph by hand, and do not maintain any parallel list of topics anywhere.**

### Canonical template

`ui/topic/contentdescriptions/` is the reference structure. Follow it exactly — file layout,
section order, comment style, preview set. Uniformity matters here more than usual: the
source code is the product, and thirty-six topics that each look slightly different are
harder to read than thirty-six that look identical.

### Topic screen content order

1. Plain-language description — non-technical, for a user rather than a developer.
2. "What to try" — a short instruction for someone exploring with TalkBack.
3. The interactive demonstration.
4. `DeveloperNote` — collapsible, technical. Present in **both** Naive and Better. In Naive
   it states what is missing and what it costs the user, factually and without scolding.

---

## Comment Conventions

Comments explain *why*, not *what*.

| Prefix | Use |
|---|---|
| `// BETTER:` | What was done in the accessible implementation. |
| `// NAIVE:` | What is missing here, and what it costs the user. |
| `// WHY:` | Rationale. The most important of the four. |
| `// VERIFIED:` | **See below.** |

### `// VERIFIED:` is reserved

It marks behaviour established by running the app on a real device with TalkBack, and must
record the Compose BOM version and API level tested.

**Claude Code must never write a `// VERIFIED:` comment.** You cannot run TalkBack, and
reasoning about what it will announce is not verification. If a claim needs device
confirmation, write `// TODO(verify):` and describe what should be checked. The project
author converts these after testing.

---

## Lint

Naive implementations deliberately violate accessibility lint rules — that is their purpose.

**Suppress per file, in the Naive files only.** Never add a lint baseline, and never disable a
check project-wide: that would also silence genuine mistakes in the Better implementations,
which is precisely the failure this project exists to warn against.

```kotlin
@file:Suppress("ContentDescription")   // NAIVE: intentional — see DeveloperNote in this file
```

Two useful consequences: the suppression names the exact rule being violated, which is itself
instructive to a reader; and its presence at the top of a file is a reliable signal that the
file is a Naive implementation.

If a Better file needs a suppression, something is wrong — investigate rather than suppress.

---

## Previews

Every topic screen carries the standard preview set, for both Naive and Better:

- Light theme, 100% font scale
- Dark theme, 100% font scale
- Light theme, 200% font scale (`fontScale = 2.0f`)

The 200% preview is not optional. Font scale and dark mode are themselves topics in this
project; shipping a screen that clips at 200% would be embarrassing in a way it would not be
elsewhere.

---

## Testing

Compose semantics assertions run on an emulator without TalkBack and test exactly the layer
this project is about. Each topic should have an instrumented test asserting that the Better
implementation carries the expected semantics and the Naive one does not — using
`onNodeWithContentDescription`, `assertHasClickAction`,
`SemanticsProperties.StateDescription`, merged-tree inspection, and so on.

These tests are reference material in their own right; most developers do not know the
semantics tree can be asserted against at all. Comment them accordingly.

This is **not** the same as the automated accessibility scanning deferred in requirements §8
(`AccessibilityChecks`), which remains out of scope.

---

## General Rules

- **No hardcoded UI strings.** Everything in `res/values/strings.xml`, including the
  developer-note text.
- **No new dependencies without asking.** The dependency list is deliberately minimal; a
  reference project that pulls in helper libraries teaches the libraries rather than the
  platform.
- **Compose only.** The single exception is the View-interop topic, where interop is the
  subject.
- Features above `minSdk` are annotated `@RequiresApi`, and the developer note states the
  requirement.
- Do not implement topics flagged with open questions in requirements §9 until the question
  is resolved.

---

## Branching and Releases

- **`main`** — release branch. Every commit on `main` is a merge from `dev` at release time;
  nothing is committed to it directly. It is also the repository's default branch, so a visitor
  lands on the current release's README, not in-progress work.
- **`dev`** — working branch. Day-to-day commits happen here. Once the project has enough
  concurrent work to justify it, feature/bugfix branches merge into `dev` instead of committing
  to it directly; `dev` itself still never commits straight to `main`.
- **Releasing** (`dev` → `main`):
  1. Bump `versionCode` and `versionName` in `app/build.gradle.kts` (Semantic Versioning —
     `0.y.z` while the topic catalogue in requirements §3 is incomplete; `1.0.0` once the
     author judges the reference set complete enough, which is a judgment call, not a
     mechanical trigger from "all 36 topics implemented").
  2. Move `CHANGELOG.md`'s `[Unreleased]` section into a new dated `[X.Y.Z] - YYYY-MM-DD`
     heading.
  3. Merge `dev` into `main`, tag the merge commit `vX.Y.Z`, push the tag.
  4. Cut a GitHub Release from the tag with the changelog entry as its notes.
- Claude Code should not merge to `main`, tag, or push tags without being explicitly asked to
  cut a release — a normal work session commits to `dev` (or a feature branch) only.

