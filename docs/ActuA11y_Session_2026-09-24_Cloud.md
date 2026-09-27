# Session record — 2026-09-24, first Claude Code cloud session

A replacement memory for a future Claude Code session: what was done, why, what went wrong on
the way, and what is still open. `CLAUDE.md` and `docs/ActuA11y_Requirements.md` remain the
authorities; this file records one session's history and does not override either.

- **Branch:** `claude/eloquent-wright-nhngxd` (cut from `main` at `9edcbf9`, content identical to
  `dev` at the time). Not yet merged into `dev`. No pull request was opened.
- **Outcome:** all 46 catalogued topics implemented (36 existed, 10 added). Every commit
  was built (`assembleDebug`, `assembleRelease`, `assembleDebugAndroidTest`, `lint`, `test`).
  **No instrumented test has been run** — there is no device or emulator in the cloud container.

---

## 1. Cloud environment setup

The user normally works in Claude Code inside a JetBrains IDE; this was the first cloud session.

### What the container had and lacked

| Needed | State at start |
|---|---|
| JDK 17+ | OpenJDK 21 preinstalled |
| Gradle wrapper download (`services.gradle.org`) | Reachable |
| `maven.google.com`, Maven Central, Gradle plugin portal | Reachable |
| Android SDK | Not installed; `dl.google.com` blocked (HTTP 403 from the proxy) |
| Emulator | Impossible — no `/dev/kvm` |

### Settings the user changed (cloud environment → Edit)

1. **Network access:** added `dl.google.com` to allowed domains.
2. **Environment variable:** `ANDROID_HOME=/opt/android-sdk`.
3. **Setup script** (final, working version):

```bash
#!/bin/bash
set -euo pipefail
export ANDROID_HOME=/opt/android-sdk
if [ ! -x "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
  mkdir -p "$ANDROID_HOME/cmdline-tools"
  curl -fsSL -o /tmp/clt.zip \
    https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip
  unzip -q /tmp/clt.zip -d /tmp/clt
  mv /tmp/clt/cmdline-tools "$ANDROID_HOME/cmdline-tools/latest"
  rm -rf /tmp/clt /tmp/clt.zip
fi
(yes || true) | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses >/dev/null
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platforms;android-36" "platform-tools"
```

The network and environment-variable changes took effect in the running session; the setup
script only runs when a new container starts. The fixed script was run by hand in this container,
so it has not yet been proven by a fresh container on its own.

### Snares hit during setup

- **Setup script failed with exit code 141.** Cause: `yes | sdkmanager --licenses` under
  `set -o pipefail`. `sdkmanager` exits once it has its answers, `yes` then dies of SIGPIPE
  (141), and `pipefail` turns that into a script failure. Fix: `(yes || true) | …`. This was the
  assistant's own mistake in the first version of the script.
- **Maven Central returned HTTP 429 (Too Many Requests)** to Gradle, consistently, even at
  `--max-workers=1`, while single `curl` requests to the same file returned 200. Probably
  rate-limiting of burst traffic from shared cloud egress (a guess). **Workaround, container-local
  only, not in the repo:** `~/.gradle/init.d/central-mirror.gradle.kts` redirects Maven Central to
  Google's mirror. A new container will not have it; recreate it if the 429s return:

  ```kotlin
  // Container-local only (not in the repo): Maven Central rate-limits this environment (HTTP 429),
  // so route its traffic to Google's Maven Central mirror.
  val mirror = "https://maven-central.storage-download.googleapis.com/maven2/"
  fun RepositoryHandler.redirect() = all {
      if (this is MavenArtifactRepository && url.toString().contains("repo.maven.apache.org")) setUrl(mirror)
  }
  settingsEvaluated {
      pluginManagement.repositories.redirect()
      dependencyResolutionManagement.repositories.redirect()
  }
  allprojects {
      buildscript.repositories.redirect()
      repositories.redirect()
  }
  ```

  Adding this to the setup script (writing the file into `~/.gradle/init.d/`) would make it
  permanent for cloud sessions. Not done yet.
- **A one-off plugin-resolution failure** (`foojay-resolver-convention`) on the first build
  resolved itself on retry — transient, not a configuration problem.
- **`./gradlew`: Permission denied.** The wrapper was committed with mode `100644` (the repo comes
  from Windows). Fixed in commit `36e2ccd` with `git update-index --chmod=+x gradlew`.
- Line endings are fine: all tracked text files are LF; there is no `.gitattributes`.

---

## 2. Commits on the branch

| Commit | Contents |
|---|---|
| `36e2ccd` | `gradlew` executable bit |
| `1a5a513` | `Topic` gains `enClause`, `wcagVersion`, `bindingFrom` (nullable, default `null`) |
| `1a6d4e3` | Topics 42, 44, 45, 46 |
| `c606fa5` | Topics 40, 41; `androidx.biometric` 1.1.0; `MainActivity` → `FragmentActivity` |
| `2f7fbce` | Topics 38, 39 |
| `cfbcb15` | Topics 36, 37 |
| `19e17a5` | README Coverage (46 implemented, Topic 46 listed), CLAUDE.md counts, new CLAUDE.md section "Focus Visibility and Interop — Established By Reading Source" |

Each batch was committed and pushed separately, as agreed with the user, so that a reclaimed
container could never lose more than one batch. `CHANGELOG.md` `[Unreleased]` has an entry for
everything above.

---

## 3. Decisions

### Made by the user

- **Add `androidx.biometric`** for Topic 41 (CLAUDE.md requires asking before any new
  dependency). Version 1.1.0, the latest stable; 1.4.0 is alpha only.
- **Work on a feature branch; nothing reaches `dev` until the user has built and tested locally.**
  The assigned session branch served as the feature branch.
- **Push after every batch.**
- **Testing out of scope for this session** — tests are written and compiled, not run.

### Made by the assistant, flagged for review

- **Parity exceptions (conflict between requirements §3.8 and CLAUDE.md invariant 2, reported, not
  silently resolved):**
  - Topic 41: the biometric sign-in button exists only in Better.
  - Topic 38: the visible arrow buttons exist only in Better.

  In both cases the requirement is an *alternative* input method, which cannot exist without an
  element to offer it through. Each is documented in a `// WHY:` comment and the developer note.
- **Topic 41 has no arithmetic CAPTCHA**, although requirements §3.8 names one as the example.
  It would have been a second Naive-only element. Naive fails SC 3.3.8 through blocked
  paste/autofill instead; the developer note describes the CAPTCHA.
- **Categories for §3.8/§3.9 topics:** 38 → Controls, 39 → Visual, 40/41 → Forms, 42/46 → Text,
  44/45 → Structure (the weakest fit; a dedicated "Standards notes" category was the alternative).
- **Topic 37 loads a bundled asset** (`app/src/main/assets/webview_scope/faq.html`), not a live
  URL, so no `INTERNET` permission; the load failure is simulated by a switch present in both
  versions.
- **Topic 40 pre-fills** billing from delivery rather than offering a "same as delivery" checkbox,
  to keep Naive and Better layouts identical.
- **Registry metadata:** 38 `11.2.5.7`, 39 `11.2.4.11`, 40 `11.3.3.7`, 41 `11.3.3.8`,
  42 `11.3.2.4` — all `bindingFrom = "EN 301 549 V4.1.1"`. `wcagVersion` is `"2.2"` except
  Topic 42 (`"2.1"`: SC 3.2.4 predates 2.2; V3.2.1 had marked 11.3.2.4 void for software, V4.1.1
  applies it). 44 and 45 set only `wcagVersion = "2.2"` (no binding clause to cite). Clause numbers
  were cross-checked against `docs/WCAG2.2_addenda.md` and a web search. Topic 43 remains all-null
  (deliberately not tied to one SC).
- **The requirements document was not edited.** Corrections are reported instead (section 6).

---

## 4. The ten new topics

All follow the `contentdescriptions/` template: three files, the same section order, the three
previews, `paneTitle`, headings, and a developer note in both versions. Shared declarations live in
the `<Topic>Topic.kt` dispatcher as `internal`, following the `ColourContrastTopic.kt` precedent.

| # | Package | Naive | Better |
|---|---|---|---|
| 36 | `wrappedview` | Legacy custom-drawn `LegacyStarRatingView` (in dispatcher) in `AndroidView`, nothing else | Same unmodified class, repaired from outside: `contentDescription`, `ViewCompat.setStateDescription` (re-applied on change, which notifies services), `AccessibilityDelegateCompat` reporting `SeekBar` class name, `RangeInfoCompat` 0–5, scroll forward/backward actions; `isFocusable` + D-pad key listener |
| 37 | `webviewscope` | Error overlay: plain text + `Text.clickable` "Try again" (no role); covered WebView stays in a11y tree | Polite live region, `TextButton`, WebView `IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS` while covered |
| 38 | `draggingmovements` | Long-press drag reorder only (shared `Modifier.dragToReorder` in dispatcher) | Same drag + row `customActions` (only possible moves) + visible arrow `IconButton`s |
| 39 | `focusnotobscured` | Pay bar overlaid in a `Box` with bottom padding (touch-only fix); no IME handling | Bar laid out below the form (`weight(1f)`); root `windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.systemBars))` |
| 40 | `redundantentry` | Two-step checkout, step 2 empty | Step 2 pre-filled from step 1 while untouched |
| 41 | `accessibleauthentication` | Password field accepts ≤1 new char per change (blocks paste *and* autofill); no `ContentType` | Accepts any change; `ContentType.Username`/`Password`; `BiometricPrompt` (`BIOMETRIC_WEAK`), status in polite live region |
| 42 | `consistentidentification` | Same heart icon labelled "Save" and "Add to wishlist" | Both from one string resource |
| 44 | `voidconsistenthelp` | — (`supportsNaive = false`) | Content-only note, three headed sections |
| 45 | `voidparsing` | — (`supportsNaive = false`) | Content-only note, three headed sections |
| 46 | `concatenateddescriptions` | Card description segments joined with spaces | Joined with `\n` |

Test approaches worth knowing (all compiled, none run):
- 42 compares two nodes' names to each other (each is individually valid).
- 46 counts `\n` in the single `ContentDescription`.
- 40 drives Continue, then reads `EditableText` of each billing field.
- 41 uses `performTextInput` as a stand-in for a one-step paste/fill (it commits the whole string
  at once); compares `EditableText` **length** because the masked text is bullets.
- 38 invokes a `CustomAccessibilityAction` directly, as TalkBack would.
- 39 is pure geometry: `getUnclippedBoundsInRoot()` of the focused field vs the bar, after
  `performSemanticsAction(SemanticsActions.RequestFocus)`.
- 36 and 37 use `createAndroidComposeRule<ComponentActivity>()` and find the View in
  `activity.window.decorView` (helper `findFirst<T>()` in `WrappedViewTopicTest.kt`), because the
  Compose semantics tree cannot see inside `AndroidView`.
- 44/45 assert three headings and `supportsNaive == false` in the registry.

---

## 5. Snares hit while implementing

- **Kotlin: two `private` top-level classes with the same name in one package clash**
  ("Redeclaration") — they compile to the same JVM class name, even though each is file-private.
  Private top-level *functions* are fine (they go into per-file `…Kt` facade classes). Fix:
  one `internal` declaration in the dispatcher.
- **Kotlin: a `reified` inline function cannot be recursive.** Split into a non-inline worker
  taking `Class<T>` plus a thin reified wrapper.
- **`strings.xml`: `\n` inside a string resource renders as a real line break.** To display the
  two characters `\n` in a developer note, write `\\n` in the XML.
- **Lint `TypographyDashes` flags an order number like `7734-2291`** as a number range (en dash
  suggestion). Rather than suppress in a Better-used string, the new topic uses `5829174`. (The
  existing Selectable Text topic still has one such pre-existing warning.)
- **Lint `PluralsCandidate`** fires for `%d` followed by words — "3 items" and "3 of 5 stars" became
  `<plurals>`.
- **Fake suppression avoided:** a first draft put `@file:Suppress("unused")` on a Naive file
  where no lint rule actually fires. Removed — the project convention (checked: only 1 of the 33
  pre-existing Naive files carries one) is no suppression unless a rule really fires, and the suppression must name that
  rule.
- **Hardcoded UI strings:** sample form values (Topic 40) were first written as Kotlin constants;
  moved to `strings.xml` per CLAUDE.md. (Pre-existing: `TextFieldLabellingBetter.kt` still
  initialises its field with a hardcoded `"Alex"`.)
- **Three-file template:** a draft added a fourth file to a topic package; no precedent existed,
  so shared code went into the dispatcher instead.
- Missing imports (`assertCountEquals`) and similar were caught by the compiler — the reason
  building before every commit mattered.

**Lint baseline:** 32 warnings before this session (GradleDependency 8, UnusedResources 8,
TypographyEllipsis 6, NewerVersionAvailable 2, Typos 2, PluralsCandidate 2, OldTargetApi 1,
RedundantLabel 1, AndroidGradlePluginVersion 1, TypographyDashes 1). Still 32 after — no new
warnings from any added topic.

---

## 6. Findings (from reading Compose Foundation 1.8.1 sources; not device-confirmed)

Recorded in CLAUDE.md, section "Focus Visibility and Interop — Established By Reading Source".

- **No `BringIntoViewRequester` needed for focus.** `FocusableNode.onFocusStateChange`
  (`Focusable.kt`) calls `bringIntoView()` on focus gain; `ContentInViewNode.onRemeasured`
  re-reveals a focused child clipped by a viewport shrink (the keyboard opening). The failure is
  the viewport's geometry. **Correction to requirements §3.8**, which lists
  `BringIntoViewRequester` for Topic 39.
- **App-wide: nothing outside Topic 39 handles IME insets.** The app uses `enableEdgeToEdge()`;
  `adjustResize` no longer resizes the window under edge-to-edge, and `Scaffold`'s default content
  insets are the system bars only. Any topic with a text field low on screen can have it covered by
  the keyboard. Whether to fix this in `AppScaffold` is an **open decision for the author**.
- **Compose semantics modifiers do not reach a wrapped View's own `AccessibilityNodeInfo`** —
  fixes and tests must use View APIs.

---

## 7. Open items

1. **Run `connectedAndroidTest`** on the Pixel 9 Pro. First time any of the ten new tests run.
2. **`TODO(verify)` comments added this session:**
   - 46: TalkBack pauses at each `\n`, and whether audibly longer than a comma.
   - 41: live-region status is read after the system biometric prompt dismisses.
   - 38: where TalkBack focus lands after a move (custom action or arrow button).
   - 39: a field near the bottom is covered by the keyboard in Naive and stays visible in Better.
   - 36: the role TalkBack announces for the `SeekBar`-classed View; its adjust gestures trigger
     the scroll actions.
   - 37: TalkBack/Tab traversal order around the WebView; covered page reachable in Naive only.
3. **Requirements §10 open question #3** (Topic 35 escape route) is still listed as open, though
   Topic 35 was built and commit `b636589` says questions #1–#3 were resolved; only #1 and #2 are
   marked resolved. Left untouched pending the author's confirmation that the shipped design is
   the approved one. Asked twice, not yet answered.
4. **Requirements corrections to consider** (not applied): §3.8 Topic 39 APIs
   (`BringIntoViewRequester`); §3.8 Topic 41 CAPTCHA and the two parity exceptions (38, 41);
   `docs/WCAG2.2_addenda.md` marked the registry fields as done before the code had them (now
   true).
5. **App-wide IME inset handling** (section 6).
6. **Merge into `dev`** after local verification. The branch was cut from `main`, so merging it
   also brings `main`'s merge commit `9edcbf9` into `dev` — harmless.
7. Optional: add the Maven mirror init script to the cloud setup script.
8. Backlog items noted but out of scope: Topic 13 redesign (touch-region clipping in packed icon
   rows); a UI badge for `bindingFrom`.

---

## 8. Working notes for the next session

- Adding strings: a small Python helper was used to append blocks to `strings.xml`, escaping
  `'`, `"` and `&`; watch the `\n` rule above.
- Registry imports were re-sorted alphabetically when adding entries (this moved the previously
  out-of-order `CompositeControlsTopic` import once).
- The cloud container is ephemeral: push after each unit of work; the SDK, Gradle caches, the
  Foundation source jar and the Maven mirror script all vanish with it.
- Never write `// VERIFIED:`; use `// TODO(verify):`.
