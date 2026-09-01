# Testing Android accessibility: what each level actually covers

This document is for anyone building an Android app, not just contributors to ActuA11y. It
answers a question that doesn't have an obvious answer: *how much of "is this accessible" can a
machine check for you, and how much genuinely needs a person?*

The honest answer is that there's a hard ceiling, and it's worth knowing exactly where it is
before you plan a testing strategy around it. What follows are four levels, each strictly more
expensive than the last, each catching things the level below it structurally cannot.

| Level | Needs a device/emulator? | Needs TalkBack running? | Needs a human? |
|---|---|---|---|
| 0 — Static analysis | No | No | No |
| 1 — Semantics tree assertions | Yes | No | No |
| 2 — Accessibility Test Framework | Yes | No | No |
| 3 — Real assistive technology | Yes (physical device strongly preferred) | Yes | **Yes** |

---

## Level 0 — Static analysis (Android Lint)

**What it is.** Android's built-in Lint accessibility rules run at build time, against your
source and compiled resources, with nothing executing. `ContentDescription`,
`ClickableViewAccessibility`, and similar checks catch a narrow but real set of mistakes: an
`ImageView`/`Icon` with no `contentDescription`, a clickable element with no accessible label, a
touch target defined too small.

**What it catches.** Syntactic/structural omissions the compiler itself can see — the pattern
"this kind of element usually needs this attribute, and it's missing here."

**What it cannot catch.** Anything semantic. Lint cannot tell whether a `contentDescription` you
did supply is *correct*, whether it duplicates visible text badly, or whether a screen's overall
structure makes sense. It only checks for absence of expected patterns, not for quality.

**In this repo.** Every Naive implementation deliberately trips one or more of these rules — that
is the whole point of a Naive file. Suppressions are applied per file, never as a baseline, never
project-wide (see `CLAUDE.md`'s Lint section for why that distinction matters). Run it with:

```
./gradlew lintDebug
```

---

## Level 1 — Semantics tree assertions

**What it is.** `androidx.compose.ui.test` lets you write instrumented tests that read a
composable's actual semantics tree back — the same structured data (`Role`, `ContentDescription`,
`StateDescription`, `ToggleableState`, `CollectionInfo`, custom actions, and so on) that any
accessibility service, including TalkBack, consumes. You can assert a property is present, assert
its value, simulate a click, and re-assert afterward.

**What it catches.** Whether the *developer attached the right semantics* — the code-level half
of the problem. This is the layer ActuA11y's own test suite lives in: every topic has an
instrumented test confirming the Better implementation carries what it should, and the Naive one
doesn't, often by asserting a property's key is entirely *absent* rather than merely different.

**What it cannot catch.** Anything about the actual assistive-technology experience. A node
carrying the right `Role` and `ContentDescription` tells you nothing about what TalkBack will
literally say, in what order, or when.

**Setup.** These are instrumented tests (the `androidTest` source set) — they require a real
Android runtime to execute, so a connected device or a running emulator, same as any
`androidTest`. They do **not** require TalkBack (or any accessibility service) to be turned on;
the semantics tree exists independently of whether anything is reading it. Run with:

```
./gradlew connectedDebugAndroidTest
```

---

## Level 2 — Accessibility Test Framework (ATF)

**What it is.** Google's [Accessibility Test Framework](https://github.com/google/Accessibility-Test-Framework-for-Android)
inspects the real `AccessibilityNodeInfo` tree — the same tree Level 1 checks, but reached the way
an actual accessibility service reaches it, and run through a library of heuristic rules rather
than assertions you write by hand. It's surfaced two ways: wired into an Espresso test via
`AccessibilityChecks.enable()`, or run ad hoc against a running app with the standalone
[Accessibility Scanner](https://play.google.com/store/apps/details?id=com.google.android.apps.accessibility.auditor)
app — no test code required for the latter, just install it and scan a screen.

**What it catches**, automatically, across a whole app rather than one hand-picked node: missing
labels on actionable or image elements, touch targets under the platform minimum, low text
contrast, duplicate or redundant speakable text, editable fields with no associated label. This is
broader coverage than Level 1 for less effort, because you didn't have to think of each check —
but it's still a fixed rule set applied heuristically, not a simulation of a real listener.

**What it cannot catch.** The same ceiling as Level 1, for the same reason: no spoken output, no
timing, no real focus behaviour. It also cannot catch anything outside its rule set — a
technically well-labelled control that is nonetheless *confusing* passes cleanly.

**In this repo.** Deliberately **not** integrated yet — requirements §8 names this explicitly as
"out of scope for v1," a v2 candidate. If you're building your own app, this is usually the best
ratio of coverage to effort: wiring `AccessibilityChecks` into an existing Espresso suite, or
periodically scanning key screens with the standalone app, costs little and catches a real,
recurring class of mistakes before they ship.

---

## Level 3 — Real assistive technology

**What it is.** An actual accessibility service — TalkBack, Switch Access, Voice Access — running
on a real device, with a person (ideally someone fluent in that tool, not just aware of it)
listening, navigating, or operating the screen.

**What it catches — and why nothing else can.** TalkBack composes what it actually speaks from
its own internal templates, which are locale-dependent, version-dependent, and in places
vendor-dependent. There is no public API that returns "the string TalkBack would say for this
node." Concretely, this level is the only one that can confirm:

- **Exact wording and phrasing** — including cases like verbatim digit reading for phone numbers,
  where the semantics can be perfectly correct and the spoken result still wrong.
- **Announcement timing and interruption behaviour** — whether a `LiveRegionMode.Polite` update
  genuinely queues instead of interrupting, whether two rapid changes both get spoken or the
  second is dropped.
- **The accessibility-focus cursor** — a concept distinct from Compose's own internal `Focused`
  semantics property. Level 1 can confirm a `FocusRequester.requestFocus()` call succeeded inside
  Compose; only a real accessibility service can confirm *its own* focus indicator actually moved.
- **Vendor divergence.** Samsung's One UI TalkBack and stock/Google TalkBack have genuinely
  diverged for the same semantics in this project's own experience (see the PIN Show/Hide topic).
  A finding confirmed on one is not automatically true on the other.

**Setup.** A physical device is strongly preferred over an emulator — TalkBack's gesture
recognition and its interaction with touch input are unreliable to nonexistent in most emulator
configurations. Enable TalkBack (Settings → Accessibility → TalkBack), and budget real time: this
is manual, one screen and one scenario at a time, and it does not parallelize.

**In this repo.** This is what `// VERIFIED:` means in the source — a claim confirmed this way,
recorded together with the Compose BOM version and the API level tested, per `CLAUDE.md`'s
comment conventions. A claim not yet confirmed this way is marked `// TODO(verify):` instead.
**An AI assistant must never write `// VERIFIED:`** — reasoning about what TalkBack will probably
announce, however well-grounded in the semantics API, is not the same thing as hearing it.

---

## Put together: a minimum viable strategy

If you're building your own app and have to prioritize:

1. **Level 0 is free — leave Lint's accessibility rules on.** They already run on every build.
2. **Level 1 for anything custom.** If you build a control that isn't a stock platform composable
   (see the Switch: Platform vs. Custom topic for exactly why that matters), write a semantics
   assertion for it. Cheap, fast, regression-proof, and it documents your intent.
3. **Level 2 costs little more and catches a different class of bug.** Wire `AccessibilityChecks`
   into an existing Espresso suite, or scan new screens with Accessibility Scanner before you
   consider a feature done.
4. **Level 3 cannot be skipped, only budgeted.** Nothing above substitutes for it. Plan real
   device time with TalkBack — and ideally a second pass with Switch Access or an external
   keyboard for anything with custom focus or gesture handling — before anything genuinely new or
   changed ships, not as an afterthought if time allows.

None of these levels replaces another; they catch different, non-overlapping classes of mistake.
A screen that passes Levels 0–2 cleanly can still be a bad experience in practice — that gap is
exactly why Level 3 exists, and exactly why it's the one level this document can't make cheaper.
