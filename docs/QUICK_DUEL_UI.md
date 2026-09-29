# Zynpath Quick Duel 1v1 — UI & UX Specification

## 1. Overview
The Quick Duel UI is designed for high-stakes, real-time 1v1 puzzle racing. The interface combines responsive touch-driven path drawing with live competitive HUD indicators, authoritative match timing, floating reaction badges, and clear result verification.

---

## 2. Screen State Machine

```
               ┌───────────────┐
               │  ENTRY POINT  │ ── [Sign-In Required Gate if Guest]
               └───────┬───────┘
                       │ (Find Opponent Tapped)
                       ▼
               ┌───────────────┐
               │   SEARCHING   │ ── [Cancel Matchmaking] ──> [ENTRY POINT]
               └───────┬───────┘
                       │ (Match Found)
                       ▼
               ┌───────────────┐
               │  MATCH LOBBY  │ ── [Leave Match] ──> [ENTRY POINT]
               └───────┬───────┘
                       │ (Both Players Ready)
                       ▼
               ┌───────────────┐
               │   COUNTDOWN   │ (3-Second Overlay, Input Disabled)
               └───────┬───────┘
                       │ (Server ACTIVE Transition)
                       ▼
               ┌───────────────┐
               │ ACTIVE RACING │ ◄── [Forfeit Dialog] ──> [RESULT SCREEN]
               └───────┬───────┘
                       │ (Full Path Completed)
                       ▼
               ┌───────────────┐
               │  VALIDATING   │ (Modal Overlay Awaiting Server Verification)
               └───────┬───────┘
                       │ (Server Validation Result Received)
                       ▼
               ┌───────────────┐
               │ RESULT SCREEN │ ── [Play Again] ──> [SEARCHING]
               └───────────────┘ ── [Return to Hub] ──> [MULTIPLAYER HUB]
```

---

## 3. Component Breakdown

### 3.1 Entry Point (`QuickDuelEntryPointContent`)
- **Title Banner:** "Quick Duel 1v1 — Automatic Real Player Matchmaking".
- **Rules Card:** Explains start at 1, ascending checkpoints, 100% cell coverage, hints disabled.
- **Authentication Card:** Explains that online play requires an authenticated account to protect match integrity. Includes "Sign In to Play Online" button.
- **Find Opponent Button:** Full-width Neon Cyan action button. Disabled when unauthenticated or offline.

### 3.2 Searching View (`QuickDuelSearchingContent`)
- **Pulsing Radar:** Infinite animation pulsing between 0.92x and 1.08x scale with cyan circular progress indicator.
- **Queue Status:** Shows active ticket ID snippet (first 8 characters) and reassurance that both players receive identical puzzles.
- **Cancel Button:** Outlined button in crimson warning tint.

### 3.3 Match Lobby (`QuickDuelLobbyContent`)
- **Versus Comparison Card:**
  - Left: Local player avatar, display name, public ID, ready badge.
  - Center: Glowing "VS" divider badge.
  - Right: Opponent player avatar, display name, public ID, ready status.
- **Puzzle Assignment Preview:** Grid dimensions (e.g. 6x6), checkpoint count, and SHA-256 fingerprint preview.
- **Ready Action:** "I'm Ready!" button. Changes to a green spinner indicating "Waiting for opponent confirmation" once confirmed.
- **Timeout Policy:** 20-second bounded ready window.

### 3.4 Countdown Overlay (`QuickDuelCountdownContent`)
- Semi-transparent backdrop (`DeepIndigo` at 95% opacity).
- 110sp bold countdown numerals (3... 2... 1...).
- Subtitle: "Trace every cell in checkpoint order!"

### 3.5 Live Gameplay Board (`QuickDuelActiveGameplayContent`)
- **Dual Progress HUD:**
  - Local player progress bar (Cyan) showing covered cells and last checkpoint.
  - Opponent progress bar (Gold) showing opponent covered cells and last checkpoint.
  - Center elapsed timer: Format `MM:SS.S` calculated from server start time.
- **Board Canvas:** Embedded `PuzzleBoard` component. Receives touch gestures and delegates to `PuzzleEngine` for immediate path rendering.
- **Controls Bar:**
  - "Undo" button: Reverts the last path step (disabled at checkpoint 1).
  - "Reset" button: Clears the entire current path.
  - "Forfeit" button: Opens confirmation dialog.
- **Preset Reactions Bar:**
  - 4 quick-tap reaction buttons: 👍 (Well Played), ⚡ (Speed Run), 🔥 (On Fire), 🤯 (Incredible).
  - Ephemeral floating badge appears above the board for 3 seconds.

### 3.6 Validation Modal Overlay (`QuickDuelValidationOverlay`)
- Centered card with indeterminate cyan progress indicator.
- Message: "Validating Solution... Server is authoritatively verifying 8-point puzzle compliance, checkpoint sequence, and finish timestamp."

### 3.7 Result Screen (`QuickDuelResultContent`)
- **Outcome Banner:**
  - `VICTORY!`: Emerald Green border and glow, "You solved the continuous route first!".
  - `DEFEAT`: Crimson border, "Opponent completed the verified path ahead.".
  - `TIE`: Gold border, "Both players submitted simultaneous validated solutions!".
- **Outcome Breakdown Table:** Displays player vs opponent side-by-side comparison with server-authoritative solve times and verification status.
- **Integrity Footer:** Authoritative SHA-256 puzzle fingerprint.
- **Primary Actions:**
  - "Play Again": Starts a fresh matchmaking queue request.
  - "Return to Multiplayer Hub": Exits match and navigates back.

---

## 4. Design System Compliance
- **Color Palette:**
  - Background: `DeepIndigo` (`#0F172A`)
  - Elevated Cards: `CharcoalNavy` (`#1E293B`) and `GunmetalCard` (`#334155`)
  - Primary Accent: `PathNeonCyan` (`#06B6D4`)
  - Success / Victory: `EmeraldGreen` (`#10B981`)
  - Accent / Opponent: `AccentGold` (`#FFB703`)
  - Warning / Forfeit: `ErrorRed` (`#EF4444`)
- **Typography:** Material 3 Typography system with bold headline hierarchy and readable data labels.
- **Touch Target Minimums:** All buttons and interactive cells adhere to $\ge 48\times 48\text{ dp}$ touch target guidelines.
