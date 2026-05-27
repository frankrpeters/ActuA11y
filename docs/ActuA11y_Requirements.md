# ActuA11y — Requirements Document

**Version:** 0.1 (initial)
**Platform:** Android
**Language:** Kotlin
**Package name:** `de.frpeters.actua11y`
**License:** Apache 2.0
**Repository:** GitHub — `ActuA11y`
**Min API:** 28 (Android 9.0)

---

## 1. Purpose and Goals

ActuA11y is an open source Android reference application with two distinct but compatible audiences:

**Primary audience — Android developers**
The source code is the deliverable. The app demonstrates correct (and, in debug builds, also incorrect) implementations of Android accessibility features, serving as a living, compilable reference. Each screen or component maps to a documented pattern that developers can read, copy, and adapt.

**Secondary audience — TalkBack learners**
The running app serves as a safe, well-structured environment for users learning to navigate Android with TalkBack, an external keyboard, or a switch device. All explanatory text is written in plain language without technical jargon. Users on release builds encounter only correct, polished implementations.

These two purposes are largely non-conflicting: developers consume source code; users consume the running UI. The one area of overlap — the comparison between good and bad implementations — is resolved via build flavors (see §6).

---

## 2. Non-Goals

- ActuA11y does not provide any real-world functionality (no data persistence beyond UI state, no network calls, no accounts).
- It is not a test suite or automated accessibility scanner.
- It is not a replacement for official Android accessibility documentation, but a companion to it.
- It does not target iOS or cross-platform frameworks.

---

## 3. Accessibility Coverage Areas

The following areas should be represented, each as one or more dedicated screens or components. This list is indicative, not exhaustive; it should grow over time.

### 3.1 TalkBack

| Topic | Notes |
|---|---|
| Content descriptions | Images, icon buttons, decorative vs. meaningful |
| Announcement ordering | Controlling the sequence TalkBack reads elements |
| Custom actions | Extra menu items accessible via the TalkBack local context menu |
| Live regions | `accessibilityLiveRegion` for dynamic content updates |
| Grouping and ungrouping | `importantForAccessibility`, `focusable`, view grouping |
| Traversal order | `accessibilityTraversalBefore/After` |
| Headings | `accessibilityHeading` for screen navigation |
| State descriptions | Custom state text (e.g., "loading", "3 of 7 selected") |
| Error announcements | Form validation errors surfaced to TalkBack |
| Pane titles | `accessibilityPaneTitle` for fragment/pane transitions |
| Double-tap target size | Minimum touch target dimensions |
| Role descriptions | `accessibilityRoleDescription` for custom views |

### 3.2 Keyboard Navigation

| Topic | Notes |
|---|---|
| Focus order | Logical tab sequence through all interactive elements |
| Focus visibility | Clearly visible focus indicator at all times |
| Keyboard-only operation | All functionality reachable without touch |
| Shortcut keys | Demonstrating `dispatchKeyEvent` / key listeners |
| Dialog and bottom sheet trapping | Focus must not escape modal surfaces |
| RecyclerList keyboard navigation | Arrow key support within lists |

### 3.3 Switch Access

| Topic | Notes |
|---|---|
| Scannable elements | All interactive elements reachable via scanning |
| Grouping for efficiency | Reducing scan steps with logical grouping |

### 3.4 Display and Visual

| Topic | Notes |
|---|---|
| Font scale support | UI survives up to 200% font scale without clipping or truncation |
| Display size support | UI survives large display sizes |
| Color contrast | WCAG AA minimum ratios for text and UI components |
| Color independence | No information conveyed by color alone |
| Dark mode | Full support; contrast maintained in both themes |
| Reduced motion | Respecting `preferReducedMotion` / `ANIMATOR_DURATION_SCALE` |

### 3.5 Forms and Input

| Topic | Notes |
|---|---|
| Input field labeling | `labelFor`, `hint`, `contentDescription` — when to use which |
| Error handling | Inline errors, announcements, focus management |
| Autofill hints | `autofillHints` for common field types |
| IME actions | Correct `imeOptions` and keyboard action button behavior |

### 3.6 Navigation and Structure

| Topic | Notes |
|---|---|
| Screen titles | `Activity` title and `Fragment` pane title propagation |
| Back navigation | Predictable and accessible back stack behavior |
| Focus on navigation | Where focus lands after a screen transition |
| Dialogs and overlays | Focus trapping, dismissibility, announcement on appear |
| Bottom navigation and drawer | Correct role and state announcements |
| Snackbars and toasts | Accessibility of transient messages |

### 3.7 Custom Views and Components

| Topic | Notes |
|---|---|
| Custom view from scratch | Implementing `AccessibilityNodeInfoCompat` correctly |
| Extending existing views | Adding missing accessibility semantics to a wrapped view |
| Canvas-based views | Accessibility for drawn content |

### 3.8 Advanced / Lesser-Known

| Topic | Notes |
|---|---|
| `AccessibilityDelegate` | Intercepting and augmenting default node info |
| `AccessibilityEvent` | Sending custom events |
| `performAccessibilityAction` | Custom action dispatch |
| Accessibility service interaction | What apps can and cannot assume about the active service |
| `ViewCompat.enableAccessibleClickableSpans` | Clickable spans in text |
| Spannable text | Accessibility of links and styled spans in `TextView` |

---

## 4. Application Structure

### 4.1 Navigation

- Single-activity architecture using Jetpack Navigation Component.
- A top-level home screen lists all categories.
- Each category leads to a list of individual topic screens.
- Each topic screen demonstrates one or more related patterns.

### 4.2 Topic Screen Layout

Each topic screen consists of:

1. **Title and plain-language description** — what this topic is and why it matters, written for a non-technical user.
2. **Interactive demonstration** — one or more live UI components demonstrating the correct implementation.
3. **Developer note (collapsible)** — brief pointer to the relevant API, attribute, or pattern. Written for developers; may use technical language. Links to relevant source file if feasible.
4. _(Debug only)_ **"Compare to bad version" button** — appears in the top app bar only in the debug flavor; navigates to the corresponding negative example screen (see §6).

### 4.3 Home Screen

- Organized by category (matching §3 above).
- Fully accessible: category items are headings, list items have correct roles.
- Serves as a self-demonstrating example of correct list accessibility.

---

## 5. Learner Experience

- All UI text is written in plain, jargon-free language.
- Each topic screen briefly explains what the user should try (e.g., "Turn on TalkBack and swipe through this screen — notice that the image is described aloud.").
- The home screen includes a short onboarding card explaining the app's dual purpose and how to enable TalkBack, with a link to Android's official TalkBack setup guide.
- No user account, login, or data collection of any kind.

---

## 6. Build Flavors

Two build variants:

| Variant | Audience | Bad-example screens | Developer notes |
|---|---|---|---|
| `release` | End users, TalkBack learners | Not present (not compiled in, or navigation entry points removed) | Collapsible, present |
| `debug` | Developers | Present, reachable via top-app-bar button on each topic screen | Collapsible, present |

**Implementation note:** Bad-example screens should be gated using a combination of:
- A `BuildConfig.DEBUG` check to conditionally show the navigation entry point.
- Optionally, a dedicated source set (`debug/`) for the bad-example fragments/composables, so they are entirely absent from the release artifact.

The flavor mechanism itself (the conditional button, the source set split) is documented in-code as a reference pattern for developer use.

---

## 7. Code Organization Principles

- Each accessibility pattern lives in a self-contained, clearly named file.
- Inline code comments explain *why*, not just *what* — the rationale for each accessibility decision.
- Bad examples (debug only) are clearly marked with a `// BAD:` comment prefix and an explanation of what is wrong.
- Good examples use a `// GOOD:` or `// WHY:` comment convention.
- UI implemented exclusively in Jetpack Compose. The traditional View system is not demonstrated; this reflects current Android development recommendations and keeps the scope coherent.
- No third-party UI libraries unless they are themselves accessibility-relevant and the accessibility behavior is the point being demonstrated.
- Minimum Android API level: 28 (Android 9.0). Features requiring higher API levels are noted with `@RequiresApi` annotations.

---

## 8. Out of Scope for v1

The following are acknowledged as valuable but deferred:

- Automated accessibility testing (Espresso + AccessibilityChecks integration) — strong candidate for v2.
- Localization / multi-language support.
- Tablet and large-screen layouts.
- Web content / WebView accessibility.
- Automotive or TV profiles.
- A companion website or documentation site.

---

## 9. Open Questions

| # | Question | Notes |
|---|---|---|
| 1 | Apache 2.0 header in each file | Standard practice; automate via IDE file template. |
