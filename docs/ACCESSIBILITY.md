# Zynpath Accessibility Specification

**Status:** Authoritative  
**Domain:** System-Wide Accessibility, High-Contrast Cues, and Reduced-Motion Support  

---

## 1. Overview
Zynpath is designed to be fully accessible to puzzle enthusiasts of all ages, visual sensitivities, and motor capabilities. Visual styling and cosmetic enhancements must strictly preserve readability and non-color cues.

---

## 2. Reduced-Motion Support (`isReducedMotion`)
Zynpath provides explicit support for reduced-motion settings, respecting both system-wide Android accessibility preferences and in-app settings (`UserPreferences.isReducedMotion`).

### 2.1 Cosmetic Path Effects
- **Standard Behavior**: Animated path effects such as `path_gentle_pulse` and `path_particle_accent` perform gentle sine-wave opacity breathing and subtle particle accents.
- **Reduced-Motion Enforcement**:
  - Pulsing opacity and scale oscillations are disabled.
  - Particle emitters and dynamic trail shifts are suppressed.
  - The path renders as a static, solid high-contrast dual-stroke ribbon.
  - Core puzzle validation, move responsiveness, and backtracking remain identical.

### 2.2 Board and Screen Transitions
- Screen transitions use simple fade effects instead of large sliding transforms.
- Celebration confetti or victory bursts respect reduced-motion constraints.

---

## 3. High-Contrast Appearance & Color Contrast (WCAG 2.1 AA)
Every visual theme in the cosmetic catalog (`ZynpathColorPalette`) is engineered to exceed WCAG 2.1 AA contrast requirements for both normal and large elements:
1. **Checkpoint Numbers**: Rendered in pure white (`#FFFFFF`) or high-contrast dark discs, maintaining $\ge 4.5:1$ contrast against checkpoint circles and board surfaces.
2. **Wall Barriers**: Rendered in vivid, distinctive barrier tones (e.g. Crimson `#E63946`, Ruby `#D90429`, Coral `#FF5370`) with thick $5\text{ dp}$ strokes along cell edges. Walls are never rendered as blocked cells and are never occluded by path glows or particles.
3. **Active Path**: High-contrast glow and core ribbon with clear path-head circular indicator.
4. **Touch Targets**: All interactive buttons, chips, and catalog cards maintain a minimum touch target size of $48 \times 48\text{ dp}$.

---

## 4. Non-Color-Only Indicators
Game state is never communicated solely through color:
- **Checkpoints**: Numbered digits $1, 2, 3, \dots, N$ communicate exact sequence requirements.
- **Start Cell**: Highlighted with an explicit numeric label `1` and halo boundary.
- **Locked Items**: Feature a lock icon and textual "Premium" / "Locked" badge in addition to color dimming.
- **Equipped State**: Indicated by an explicit checkmark icon and "Equipped" label.
- **Error States**: Accompanied by haptic vibrations (configurable), shape outlines, and clear text messages.

---

## 5. Screen Readers & TalkBack Support
- All cosmetic catalog cards expose content descriptions detailing the item name, category, free/premium tier, and equipped state.
- Live preview canvas components expose descriptive semantics (`"Appearance preview showing sample 3 by 3 board"`).

---

## 6. Player Settings Integration (Prompt 32)
- **Reduced Motion Toggle**: Exposed in Settings → Appearance & Accessibility.
- **High-Contrast Mode Toggle**: Directly enables thick, high-contrast borders across all board sizes and themes.
- **Touch Sensitivity Adjustment**: Configurable slider ($0.5\times$ to $2.0\times$) in Settings → Gameplay to assist players with varying fine-motor control.
- **Tap Input Mode**: Allows single-finger tapping on consecutive cells as an accessible alternative to continuous dragging.

---

## 7. Tutorial & Onboarding Accessibility (Prompt 33)
- **Alternative Text Guide Mode**: The interactive tutorial features an accessible, readable text guide (`TutorialTextGuide`) toggled with a single tap. Players can read complete explanations of every mechanic without requiring gesture manipulation.
- **Granular Rejection Feedback**: When illegal movements occur (diagonal moves, wall crossings, premature final checkpoints), supportive text feedback is surfaced immediately and announced via TalkBack.
- **Large Touch Targets**: Undo and Reset recovery buttons exceed the standard 48dp target requirement and are clearly labeled with icons and text.
- **Focus Order & Semantic Descriptions**: The stage progress bar, instruction card, active board, and action buttons maintain a logical, linear screen-reader traversal order.

---

## 8. Audio, Haptic & Interaction Accessibility (Prompt 34)
- **Zero Auditory/Tactile Dependency**: All gameplay states, checkpoint numbers, wall barriers, rejection banners, and puzzle victories are communicated clearly through visual UI elements. Sound and vibration are purely supplementary presentation feedback.
- **Micro-Interaction Reduced-Motion Bypassing**:
  - `rejectionShake`: When `isReducedMotion == true`, horizontal board vibration/translation is completely eliminated; rejection is presented via static visual banner and subtle haptic double-click.
  - `zynpathClickable`: Button press spring scale compression is suppressed (fixed at 1.0f) to avoid motion sickness.
- **Independent Audio & Haptic Controls**:
  - Master SFX toggle, ambient music toggle, and tactile vibration toggle can be enabled or disabled independently in Player Settings.
  - Haptics fail gracefully and silently on devices without vibration hardware.

---

## 9. Virtual Accessibility Cell Grid & TalkBack Navigation (Prompt 39)
- **Accessible Board Overlay**: `PuzzleBoard` overlays a transparent virtual cell grid on top of the custom canvas, enabling individual cell exploration with TalkBack and Switch Access without intercepting continuous drag gestures.
- **Rich Semantic Descriptions (`buildCellAccessibilityDescription`)**:
  - Grid Coordinates: 1-indexed for intuitive understanding (e.g. `"Row 2, column 3"`).
  - Checkpoint Identification: `"Checkpoint 1, starting cell"`, `"Checkpoint 3"`, `"Final checkpoint 5"`.
  - Path Relationship: `"Current path head (endpoint)"`, `"Visited on path"`, `"Unvisited cell"`.
  - Blocked Wall Edges: Explicitly describes blocked directions: `"Blocked by wall edge above, to the right"`.
  - Action Prompts: `"Double tap to start path"`, `"Double tap to move path here"`, `"Double tap to backtrack"`.

---

## 10. Hardware Keyboard & D-Pad Control (Prompt 39)
- Operable via physical Bluetooth/USB keyboards and gamepad D-pads:
  - **Arrow Keys (Up/Down/Left/Right)**: Move the focused cell position orthogonally across the grid.
  - **Spacebar / Enter**: Activate the focused cell and extend/retract path.
  - **Backspace**: Undo last move step.

---

## 11. LiveRegion Move Rejection & Status Announcements (Prompt 39)
- Move rejection banners and hint notifications attach `liveRegion = LiveRegionMode.Polite` with contextual content descriptions, allowing TalkBack to announce feedback politely without interrupting ongoing screen traversal.

---

## 12. Scalable Typography & Display Density (Prompt 39)
- All in-game text uses scalable `sp` units and fluid layouts, supporting up to 200% system font scaling without control clipping.
- Touch targets on gameplay controls (Undo, Reset, Hint, Pause) maintain strict $\ge 48\text{ dp}$ dimensions.

---

## 13. Responsive Layouts & Orientation (Prompt 39)
- **Landscape Mode**: Renders a 2-column side-by-side layout (Board left, controls & stats right) preventing board squishing and control clipping.
- **Tablets & Foldables**: Portrait content is width-constrained (`560dp`) to maintain thumb ergonomics. Windows dynamically adapt to resizing in multi-window / desktop mode without path corruption.

---

## 14. Achievement Presentation & Celebration Accessibility (Prompt 41)
- **TalkBack Semantic Merging (`AchievementCard`)**:
  - `AchievementCard` integrates `Modifier.semantics(mergeDescendants = true)` with a comprehensive content description including title, category, status (Unlocked on Date / Locked), and progress percentage (e.g. `"First Step, Solo achievement, Unlocked on September 27, 2026, 100% complete"`).
- **Accessible Detail Modals (`AchievementDetailDialog`)**:
  - Tapping any achievement opens a high-contrast modal dialog that displays exact unlock criteria, progress fraction, status badge, and an accessible dismiss button with $\ge 48\text{ dp}$ touch target.
- **Celebration Reduced Motion Compatibility**:
  - Celebration dialogs (`WorldCompletionCelebrationDialog`) respect `isReducedMotion == true`. Confetti particle emitters and spring scale bounces are gracefully bypassed, presenting clear static congratulatory cards with immediate TalkBack announcements.

---

---

## 16. Comprehensive QA Accessibility Audit Results (Prompt 48)
- **WCAG 2.1 Level AA Verification**:
  - All 5 shipped color palettes (`ClassicMidnight`, `PureDark`, `CyberNeon`, `EmeraldForest`, `SolarAmber`) were mathematically tested and confirmed to achieve $\ge 4.5:1$ text contrast, $\ge 3.0:1$ wall border contrast, and $\ge 4.5:1$ checkpoint text contrast.
  - Non-color-only indicators verified: Checkpoints enforce explicit digits $1 \dots N$; walls render as distinct cell border barriers.
  - Touch targets: All action buttons, back navigation, undo, reset, and hint buttons maintain minimum $48 \times 48\text{ dp}$ hit bounds.
  - Detailed audit documentation: [docs/ACCESSIBILITY_AUDIT.md](file:///d:/Zynpath/docs/ACCESSIBILITY_AUDIT.md).




