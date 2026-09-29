# Zynpath Gameplay Accessibility Architecture

## Overview
This document specifies the accessibility architecture and implementation details for Zynpath: Number Path Puzzle (Phase 3, Prompt 15). The goal is to provide a responsive, barrier-free puzzle solving experience for players of diverse visual, auditory, cognitive, and motor abilities.

---

## 1. Core Principles

1. **Non-Color-Only Communication (WCAG 1.4.1)**:
   - Checkpoint states, path endpoints, walls, and validation errors are never communicated through color alone.
   - Distinct geometric outlines, concentric rings, directional pips, and star/diamond badges accompany all color variations.

2. **Contrast Ratios (WCAG 1.4.3 & 1.4.11)**:
   - All text content maintains a minimum contrast ratio of **4.5:1** against backgrounds.
   - UI components and graphical elements (borders, walls, paths) maintain a minimum contrast ratio of **3:1** against adjacent backgrounds.

3. **Touch Target Sizing (WCAG 2.5.5 & Material 3 Guidelines)**:
   - All interactive controls (`Undo`, `Hint`, `Reset`, `Pause`, `Rules`, `Resume`, `Next Level`, `Replay`) provide a minimum touch target height and width of **48dp**.
   - Generous spacing between controls prevents accidental activations.

4. **Scalable Typography (WCAG 1.4.4)**:
   - Text elements use scalable `.sp` units, adapting dynamically to system font size preferences and Android display scaling.
   - Puzzle board checkpoint numbers scale dynamically with cell size and respect density scaling.

5. **Reduced Motion Support**:
   - Breathing glows, pulse animations, and celebratory particles respect the system animator duration scale and the in-app `isReducedMotion` setting.
   - When reduced motion is enabled, all animations transition immediately or use subtle static opacity without motion.

---

## 2. Checkpoint Visual States (Non-Color Identifiers)

| State | Visual Treatment | Non-Color Indicator | Accessibility Semantic Description |
|---|---|---|---|
| **Start (#1)** | Forest Mint fill, mint border | Solid mint border + distinct Start halo | "Checkpoint 1: Start (Visited or Not reached)" |
| **Next Required** | Dark navy fill, Cyan text | Double concentric ring + 4 cardinal directional pips | "Checkpoint N: Next required checkpoint" |
| **Visited** | Dark navy fill, Path Cyan text | Connected through by path + inner concentric ring | "Checkpoint N: Already visited on path" |
| **Final Checkpoint** | Dark navy fill, Gold text | Double gold border + top diamond crown emblem | "Checkpoint N: Final checkpoint" |
| **Upcoming / Unreached** | Dark navy fill, White text | Single white border, standard circle | "Checkpoint N: Upcoming checkpoint" |

---

## 3. Alternative Tap-to-Move Input Mode

To support players with motor impairments or those who find continuous dragging difficult, Zynpath provides a dedicated **Tap Mode** alongside **Continuous Drag Mode**:

- **Activation**: Toggleable via the top bar input mode badge (`MODE: TAP` vs `MODE: DRAG`).
- **Persistence**: Persisted locally via `PreferencesRepository` in Jetpack DataStore (`tap_input_mode`).
- **Zero State Loss**: Switching between Drag and Tap modes does not alter engine state or reset the puzzle path.
- **Interaction Rules**:
  1. *Starting Path*: Tap Checkpoint #1 to initiate path.
  2. *Extending Path*: Tap an orthogonally adjacent unvisited cell to move forward.
  3. *Backtracking 1 Step*: Tap the cell immediately preceding the current endpoint to retract by 1 step.
  4. *Backtracking to Branch*: Tap any earlier cell on the active path to retract to that position.
  5. *Invalid Tap*: Tapping an illegal or non-adjacent cell triggers gentle error feedback without altering the accepted path.
- **Engine Authority**: Tap interactions route to the exact same [PuzzleEngine] actions (`StartPath`, `ExtendPath`, `BacktrackOne`, `BacktrackTo`), ensuring no separate rules engine or divergence.

---

## 4. Screen Reader Semantics

The Compose Canvas and controls expose rich accessibility semantics via `Modifier.semantics`:

- **Board Overview**:
  `"Puzzle grid, R by C. Coverage: X of Y cells. Next required checkpoint: N of M."`
- **Undo Action**:
  `"Undo last move"` when available, or `"Undo unavailable, board at initial state"`.
- **Hint Action**:
  `"Request hint, Hint (N)"` or `"Analyzing puzzle for hint"`.
- **Reset Action**:
  `"Reset puzzle path back to Checkpoint #1"`.
- **Mode Toggle**:
  `"Tap mode active. Tap to switch to continuous drag."` / `"Drag mode active. Tap to switch to tap to move."`

*Note on frequency*: Touch moves during dragging do not spam speech synthesis. Screen readers announce state updates upon discrete actions, pause, and victory.

---

## 5. Audio and Haptic Feedback

All sensory feedback is lightweight, completely offline, and respects device settings:
- **Audio Feedback**: Powered by Android's native `ToneGenerator` (zero network, zero external assets).
  - *Checkpoint Reached*: Crisp high beep (`TONE_PROP_BEEP`, 50ms).
  - *Invalid Move*: Gentle low blip (`TONE_PROP_NACK`, 70ms).
  - *Victory*: Celebratory confirmation chime (`TONE_PROP_ACK`, 250ms).
- **Haptic Feedback**:
  - *Checkpoint Reached*: Subtle haptic click.
  - *Invalid Move*: Text handle feedback.
  - *Victory*: Long press confirmation.
- **User Control**: Haptics and sound can be toggled on/off independently via DataStore user preferences.

---

## 6. Deferred Testing Requirements

In accordance with the Prompt 15 Development and Testing Policy:
- Full TalkBack screen reader verification is deferred to the final testing phase.
- Switch Access and Voice Access automated testing is deferred to final testing.
- Physical device haptic amplitude verification is deferred to final testing.
