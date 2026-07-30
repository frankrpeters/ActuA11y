<div align="center">

# ActuA11y

**An Android accessibility reference implementation — in code you can read, and an app you can feel the difference in.**

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84.svg)](https://developer.android.com)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Language](https://img.shields.io/badge/language-Kotlin-7F52FF.svg)](https://kotlinlang.org)

</div>

> ⚠️ **Work in progress.** This README describes the intended shape of the project. Most topic screens are not implemented yet. See [Releases](../../releases) for what actually exists today.

---

## What this is

ActuA11y is an Android app that does nothing useful on purpose.

It exists to demonstrate accessibility patterns — the ones developers get wrong, the ones that are easy to miss, and the ones most people don't know exist. Every pattern appears twice: once implemented with care, and once implemented the way it usually gets implemented when nobody thought about it.

There are two things you can take from this project, depending on who you are.

**If you build Android apps**, the source code is the point. Each topic is a pair of files that are identical except for their accessibility semantics, so the diff between them *is* the lesson. Comments explain why each decision was made rather than restating what the code does.

**If you use a screen reader, a keyboard, or a switch** — or you're learning to — the running app is the point. It's a safe place to explore, and a place to see for yourself what the difference actually amounts to. Not as a description. As the same screen, twice.

---

## The toggle

Every topic screen has a switch in the app bar: **Accessible version** or **Naive version**.

The two versions are functionally identical. Same components, same layout, same behaviour for someone using touch and looking at the screen. The only difference is whether the accessibility work was done.

Flipping that switch is the whole project in one gesture.

A note on the word *naive*: it isn't there to be diplomatic. It's accurate. Inaccessible software is very rarely built by people trying to exclude anyone — it's built by people who didn't know there was anything to do. That's the failure this project is about, and calling it what it is seemed more useful than calling it *bad*.

The other version is called *better*, not *good*, for a similarly literal reason: it might not be. If you find something in here that's wrong, [that's an issue worth filing](../../issues).

Some topics have no honest naive counterpart — where the naive version would simply be *the feature missing*, which teaches nothing by comparison. On those screens the toggle is disabled and explains why.

---

## Coverage

Thirty-six topics across seven areas. Not all are implemented yet.

<details>
<summary><strong>Structure and traversal</strong> — how a screen reader moves through a screen</summary>

Traversal groups · traversal index · headings · pane titles · where focus lands after navigation

</details>

<details>
<summary><strong>Collections</strong> — lists, grids, and the difference between them</summary>

One-dimensional collections · grids that aren't tables · genuine tables · lazy list pitfalls with sticky headers and mixed item types

Includes a rule that Android's own defaults get wrong: a visual grid whose columns carry no meaning should be announced as a *list*, not a grid. "Item 8 of 24" is useful. "Row 3, column 2" is noise.

</details>

<details>
<summary><strong>Controls and interaction</strong> — when a thing is not the thing it looks like</summary>

Inputs that are actually buttons (date pickers, and why `readOnly` and `enabled = false` both fail) · composite controls that should be one element instead of three · selectable icon lists · minimum touch targets · disabled elements · custom actions · progress and sliders

</details>

<details>
<summary><strong>Text and announcement</strong> — what gets said, and when</summary>

Where `contentDescription` belongs and where it silently breaks things · state versus content description · live regions · verbatim strings for phone numbers and postcodes · selectable text · error semantics

</details>

<details>
<summary><strong>Forms and input</strong></summary>

Field labelling · password fields and the show/hide button · validation, announcement, and focus · autofill hints · IME actions

</details>

<details>
<summary><strong>Visual and motor</strong></summary>

Keyboard focus indicators · keyboard-only operation · font scale to 200% · colour contrast and colour independence · dark mode · reduced motion · modal surfaces

</details>

<details>
<summary><strong>Interop</strong></summary>

Repairing accessibility on a legacy `View` wrapped in `AndroidView` — because most real codebases are hybrid, and the pure-Compose answer isn't always available.

</details>

Each screen carries a plain-language description, a short "what to try", the live demonstration, and a collapsible developer note with the APIs and the reasoning. The developer note is present on both versions — on the naive one, it explains what's missing and what it costs.

---

## Structure

```
app/src/main/java/de/frpeters/actua11y/
├── ui/
│   ├── AppScaffold.kt              # the single app bar, above the NavHost
│   ├── components/
│   │   ├── NaiveToggle.kt
│   │   └── DeveloperNote.kt
│   ├── home/
│   ├── category/
│   └── topic/
│       └── <topic>/
│           ├── <Topic>Topic.kt     # dispatcher
│           ├── <Topic>Naive.kt
│           └── <Topic>Better.kt
└── navigation/
    ├── TopicRegistry.kt            # single source of truth for all topics
    └── ActuA11yNavHost.kt
```

Two design decisions worth knowing about if you're reading the source:

**The app bar lives above the navigation host, not inside each screen.** A deliberately inaccessible screen must never be able to trap someone. The escape route is structurally outside anything a screen can affect.

**Adding a topic means creating one package and appending one registry entry.** Navigation, titles, category listings, and toggle state all derive from that single list.

---

## Building

```bash
git clone https://github.com/frankrpeters/ActuA11y.git
./gradlew assembleDebug
```

No configuration, keys, or accounts required. Both versions of every screen are in every build — the toggle is a runtime switch, not a build variant.

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Min SDK | 28 |
| Target SDK | 36 |
| License | Apache 2.0 |

---

## Testing

Instrumented tests assert against the Compose semantics tree — that the accessible version exposes what it should, and the naive one doesn't. These run on an emulator without a screen reader, and they're written to be read: the fact that you *can* assert on semantics is one of the more useful things in here.

They are not a substitute for running the app with TalkBack on a real device. Announcement wording, announcement order, and where focus goes after content changes cannot be verified any other way. Findings confirmed on-device are marked `// VERIFIED:` in the source, together with the Compose version and API level they were checked against.

---

## Contributing

Contributions are welcome, particularly:

- Additional topics, especially ones that bit you in production
- Corrections — if a "better" implementation isn't, please say so
- Device verification of anything currently marked `// TODO(verify):`

Please open an issue before starting a new topic, so effort isn't duplicated.

If you contribute a topic, two rules matter more than the rest:

1. **The two versions must be functionally equivalent.** Same components, same layout, same behaviour for a sighted touch user. Don't make the naive version worse — make it unconsidered. Anything else muddies the comparison.
2. **Comments explain *why*.** `// WHY:` is the one that earns its place. What the code does is visible; why it's shaped that way usually isn't.

Naive files carry file-level lint suppressions, since they deliberately violate the checks. Never a baseline, never project-wide — that would also silence real mistakes, which is the exact failure this project is about.

---

## Disclaimer

### No warranty

This software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, and non-infringement. The author makes no guarantee that the implementations shown are exhaustive, current with the latest platform releases, or suitable for any particular production use. **You use this software entirely at your own risk.** In no event shall the author be liable for any claim, damages, or other liability arising from the use of this software.

### Accessibility conformance

ActuA11y demonstrates patterns intended to improve accessibility. It does not guarantee that any implementation shown will pass a formal accessibility audit, satisfy any specific legal or regulatory requirement, or meet the needs of all users with disabilities. Requirements vary by jurisdiction, platform version, and assistive technology, and they change. Always test with real users and real assistive technologies.

### Personal project

ActuA11y is a personal, independent project developed in the author's own time. It is not affiliated with, endorsed by, or connected to the author's employer in any way. Any views, design decisions, implementation choices, or content expressed in this project are solely those of the author and do not reflect the views or practices of any employer, past or present.

---

<div align="center">

Licensed under the [Apache License 2.0](LICENSE).

</div>
