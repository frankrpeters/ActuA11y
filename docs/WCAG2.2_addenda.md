## Geplante Topics — EN 301 549 V4.1.1 / WCAG 2.2

Status: alle Punkte gegen `docs/ActuA11y_Requirements.md` (v0.4) geprüft und übernommen bzw.
begründet zurückgestellt — siehe §3.8 dort für den vollen Text. Diese Datei bleibt als
Ursprungsnotiz stehen.

- [x] **Dragging Movements** (11.2.5.7, AA) → Topic 38 (§3.8)
  Naive: LazyColumn mit detectDragGesturesAfterLongPress zum Umsortieren.
  Better: zusätzlich CustomAccessibilityAction "nach oben/unten verschieben"
  plus sichtbare Pfeil-Buttons.
- [x] **Target Size (Minimum)** (11.2.5.8, AA) → in bestehendes Topic 13 eingepflegt, kein
  eigenes Topic (§3.8, "Folded into existing topics")
  Naive: Icon in Box(24.dp).clickable — Material-Mindestgröße greift hier nicht.
  Better: IconButton bzw. minimumInteractiveComponentSize().
  Merksatz-Kandidat: 24 dp ist die Norm, 48 dp ist die Plattformvorgabe.
- [x] **Focus Not Obscured (Minimum)** (11.2.4.11, AA) → Topic 39 (§3.8)
  Naive: Formular unter fixer BottomBar, fokussiertes Feld verschwindet hinter IME.
  Better: imePadding + BringIntoViewRequester.
  Test explizit mit physischer Tastatur, nicht nur mit TalkBack.
- [x] **Redundant Entry** (11.3.3.7, A) → Topic 40 (§3.8)
  Naive: zweistufiges Formular, das E-Mail und Name erneut abfragt.
  Better: vorbefüllt bzw. auswählbar.
- [x] **Accessible Authentication (Minimum)** (11.3.3.8, AA) → Topic 41 (§3.8)
  Naive: OTP-Feld mit deaktiviertem Paste und Rechen-Captcha.
  Better: ContentType.NewPassword/Username, Paste erlaubt, Biometrie-Alternative.
- [ ] **Screen Titled** (11.2.4.2, A — in V3.2.1 noch void) → geprüft, bewusst NICHT übernommen
  (§3.8, "Considered and not added"): jeder Screen bekommt den Titel bereits strukturell über
  die App-Bar/Registry; ein naiver Gegenpart wäre nicht echt reproduzierbar ohne die beiden
  Struktur-Invarianten aus CLAUDE.md zu brechen.
- [x] **Consistent Identification** (11.3.2.4, AA — in V3.2.1 noch void) → Topic 42 (§3.8)
  Naive: dieselbe Aktion heißt auf zwei Screens unterschiedlich.
  Better: zentrale String-Ressourcen, konsistente contentDescription.
- [x] **User Preferences** (11.7) → größtenteils bereits durch Topics 31/32/33/34 abgedeckt;
  Bold Text zusätzlich in Topic 31 (§3.6.1) eingepflegt, kein eigenes Topic.
  fontScale, Dark Mode, Bold Text, Reduce Motion, Farbfilter.
  Naive: hartkodierte sp-Größen und eigenes Theme, das Systemwerte ignoriert.
  Better: durchgereichte Systemwerte.
- [x] **WebView-Screen** (Klausel 11 statt 9) → Topic 37 (§3.7/§3.7.1), aus §9 (Out of Scope)
  entfernt.
  Demonstriert, dass eingebettete WebViews nach Klausel 11 geprüft werden
  und Klausel 9 nicht parallel gilt.

### Weiterer Punkt, aus einer anderen Session eingebracht (nicht ursprünglich in dieser Datei)
- [x] **Switch: Plattform vs. custom-gezeichnet** (Schichtenmodell, keine einzelne SC) → Topic 43
  (§3.8). Zeigt, dass ein unverändertes `Switch()` Role/Toggle-State/Klick-Aktion kostenlos von
  der Plattform bekommt, ein custom-gezeichneter Switch dagegen nichts davon — bis es von Hand
  nachgebaut wird.

### Querschnitt (kein eigenes Topic)
- [x] TopicRegistry um Felder erweitern: enClause (z.B. "11.2.5.8"),
  wcagVersion ("2.1" | "2.2"), status ("aktuell" | "ab V4.1.1") → als `enClause`/`wcagVersion`/
  `bindingFrom` in §4.7 ergänzt (optional, `null` standardmäßig).
- [ ] Filter/Badge in der UI: "erst ab EN 301 549 V4.1.1 verbindlich" — offen, als mögliche
  spätere UI-Erweiterung im Topic-Backlog vermerkt, nicht Teil dieser Schema-Änderung.
- [x] Hinweis-Topic: 3.2.6 Consistent Help ist für Nicht-Web-Software void —
  typischer Fehler beim Übertragen von Web-Checklisten auf Apps. → Topic 44 (§3.8)
- [x] Hinweis-Topic: 4.1.1 Parsing entfällt. → Topic 45 (§3.8)
