# ActuA11y

**A reference implementation of Android accessibility best practices, built with Jetpack Compose.**

> ⚠️ **Work in progress.** The scope and structure documented below reflect the intended final state of the project. Not all topic screens are implemented yet. See [Releases](../../releases) for what is currently available.

ActuA11y is an open source Android application with no real-world functionality. Its purpose is to demonstrate correct — and, in debug builds, deliberately incorrect — implementations of accessibility patterns that Android developers commonly encounter, get wrong, or may not know exist at all.

The source code is the primary deliverable. The running app is a secondary artifact: a safe, fully accessible environment that TalkBack learners can explore to practice navigation with TalkBack, an external keyboard, or a switch device.

---

## What it covers

- TalkBack: content descriptions, live regions, custom actions, traversal order, headings, state descriptions, pane titles, role descriptions, clickable spans, and more
- Keyboard navigation: focus order, focus visibility, dialog focus trapping, full keyboard operability
- Switch Access: scannable elements, grouping for scan efficiency
- Display and visual: font scale, display size, color contrast, color independence, dark mode, reduced motion
- Forms and input: field labeling, error announcement, autofill hints, IME actions
- Navigation and structure: screen titles, back stack behavior, focus on transition, transient messages
- Custom views and components: `AccessibilityDelegate`, `AccessibilityNodeInfoCompat`, canvas-based views

Each topic is demonstrated on its own screen with a plain-language explanation for non-technical users and a collapsible developer note pointing to the relevant APIs and patterns.

## Debug builds

In debug builds, each topic screen includes a "Bad version" button in the top app bar that navigates to a deliberately inaccessible implementation of the same screen. These screens exist for side-by-side comparison and are completely absent from release builds — a TalkBack learner installing the app from a distribution channel will never encounter them. The flavor-gating mechanism itself is documented in-code as a reference pattern.

## For TalkBack learners

ActuA11y is fully accessible by design. Every screen can be navigated with TalkBack, an external keyboard, or switch access. Each topic screen briefly explains what to try and what to notice. No account, login, or data of any kind is required or collected.

---

## Technical details

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Min SDK | 28 (Android 9.0) |
| Target SDK | 36 |
| License | Apache 2.0 |

---

## Building

Clone the repository and open in Android Studio. No additional configuration is required. To build the debug variant with bad-example screens:

```
./gradlew assembleDebug
```

To build the release variant:

```
./gradlew assembleRelease
```

---

## Contributing

Contributions are welcome — additional topic screens, corrections, or improvements to existing implementations. Please open an issue before submitting a pull request for a new topic, to avoid duplication.

When contributing a topic screen, follow the structure of existing screens: plain-language description, interactive demonstration, collapsible developer note. If a meaningful bad-example counterpart exists, include it in the debug source set.

---

## Disclaimer

### No Warranty

This software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, and non-infringement. The author makes no guarantee that the implementations shown are exhaustive, current with the latest platform releases, or suitable for any particular production use. **You use this software entirely at your own risk.** In no event shall the author be liable for any claim, damages, or other liability arising from the use of this software.

### Accessibility Conformance

ActuA11y demonstrates patterns intended to improve accessibility. It does not guarantee that any implementation shown will pass a formal accessibility audit, satisfy any specific legal or regulatory requirement, or meet the needs of all users with disabilities. Accessibility requirements vary by jurisdiction, platform version, and assistive technology. Always test with real users and real assistive technologies.

### Personal Project Disclaimer

ActuA11y is a personal, independent project developed in the author's own time. It is not affiliated with, endorsed by, or connected to the author's employer in any way. Any views, design decisions, implementation choices, or content expressed in this project are solely those of the author and do not reflect the views or practices of any employer, past or present.
