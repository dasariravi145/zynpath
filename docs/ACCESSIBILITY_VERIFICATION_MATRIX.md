# Zynpath Accessibility Verification Matrix

## 1. Compliance Standards
Zynpath targets **WCAG 2.1 Level AA** and Android Accessibility Guidelines. In compliance with the project implementation policy, all accessibility and device features are implemented in code and marked **NOT VERIFIED** until comprehensive testing in the final prompts.

---

## 2. Accessibility Criteria Matrix

| Criterion | Requirement | Implementation Details | Status |
| :--- | :--- | :--- | :--- |
| **WCAG 1.1.1 Non-text Content** | All non-text content has text alternatives | Virtual accessibility overlay on `PuzzleBoard` (`buildCellAccessibilityDescription`), all `Icon`s have `contentDescription`. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 1.3.1 Info & Relationships** | Structure & relationships programmatically determined | Semantic groups, `isTraversalGroup = true`, headings on `ScreenHeader`, `StatColumn` descriptions. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 1.4.1 Use of Color** | Color is not sole visual means of conveying info | Checkpoint numerals, path boundary lines, error text banners, sound/haptic cues. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 1.4.3 Contrast (Minimum)** | Contrast ratio >= 4.5:1 for text, 3:1 for UI components | High-contrast palette (`ForestMint` #10B981, `PathCyanGlow` #00E5FF, `BackgroundDark` #0B132B). | IMPLEMENTED, NOT VERIFIED |
| **WCAG 1.4.4 Resize Text** | Text scales up to 200% without loss of content | Scalable `sp` units, flexible column containers, wrapping headers. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.1.1 Keyboard Navigation** | All functionality operable via keyboard interface | Arrow keys (Up/Down/Left/Right), Spacebar/Enter to select, Backspace to undo in `PuzzleBoard`. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.1.2 No Keyboard Trap** | Focus can be moved away from components | Standard focus cycle maintained; dialogs allow Esc / Back dismiss. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.2.1 Timing Adjustable** | Users given enough time to read and use content | Solo mode has no forced time-out; timer is descriptive without tick interruptions. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.3.1 Three Flashes** | No component flashes >3 times per second | Rejection shake uses gentle 4-cycle horizontal translation; no strobe effects. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.4.7 Focus Visible** | Keyboard focus indicator is clearly visible | Focusable virtual cells and Material 3 action buttons display active focus states. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.5.1 Pointer Gestures** | Multipoint or path-based gestures have single-pointer alternative | Tap-to-move mode and TalkBack cell click provide alternatives to drag. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 2.5.5 Target Size** | Touch target size >= 48dp for interactive elements | All action buttons (`GameplayActionButton`, pause, header) have min 48dp bounds. | IMPLEMENTED, NOT VERIFIED |
| **WCAG 4.1.3 Status Messages** | Status messages presented via live regions | `LiveRegionMode.Polite` attached to move rejection pills and hint notifications. | IMPLEMENTED, NOT VERIFIED |

---

## 3. Assistive Technology Verification Matrix

| Technology | Intended Behavior | Implementation Hook | Verification Status |
| :--- | :--- | :--- | :--- |
| **Google TalkBack** | Reads cell position, checkpoint number, path status, and wall edges; announces rejections | Virtual cell grid overlay + `liveRegion` on feedback pills | IMPLEMENTED, NOT VERIFIED |
| **Switch Access** | Traverses grid sequentially, triggers moves via single switch click | Virtual cell `Box` elements with `focusable()` and `onClick()` | IMPLEMENTED, NOT VERIFIED |
| **Physical Keyboard / D-Pad** | Navigates cells with arrows, confirms moves with Space/Enter | `Modifier.onKeyEvent` in `PuzzleBoard` | IMPLEMENTED, NOT VERIFIED |
| **System Font Scaling (200%)** | Text scales gracefully without clipping button bounds | Scalable `sp` typography throughout | IMPLEMENTED, NOT VERIFIED |
| **Reduced Motion** | Disables shake, ripple, and complex ease animations | `isReducedMotion` setting + `rejectionShake` bypass | IMPLEMENTED, NOT VERIFIED |
| **Muted Audio / Silent Mode** | Gameplay remains 100% understandable visually | Visual feedback banners, checkpoint numeral updates | IMPLEMENTED, NOT VERIFIED |
| **Disabled Haptics** | Gameplay remains 100% understandable without vibration | Visual + audio dual-channel status | IMPLEMENTED, NOT VERIFIED |

---

## 4. Deferred Testing Plan
All automated accessibility scans (Accessibility Scanner, Espresso AccessibilityChecks, Compose UI semantic tree assertions) and manual TalkBack testing on physical hardware are deferred to the final testing prompts (Prompts 47–50).
