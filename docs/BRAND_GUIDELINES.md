# Zynpath Brand Guidelines & Visual Identity

## 1. Brand Identity Overview

- **Brand Name:** Zynpath
- **Full Application Title:** Zynpath: Number Path Puzzle
- **Official Tagline:** One path. Every number.
- **Core Genre:** Pure orthogonal number-path logic puzzle.
- **Brand Personality:**
  - **Clarity & Logic:** Clean, distraction-free geometry where every cell matters.
  - **Modern Simplicity:** Sleek dark navy canvases paired with luminous path ribbons.
  - **Rewarding Mastery:** Joyful, elegant milestone feedback without artificial friction or predatory monetization.
  - **Zero Confusion:** Distinct from match-three, tile-clearing, or number-merging games.

---

## 2. Brand Color Palette

| Name | Hex Code | Android Resource | Usage |
| :--- | :--- | :--- | :--- |
| **Midnight Dark** | `#0B132B` | `@color/bg_midnight_dark` | Primary app background, splash background, icon background |
| **Midnight Surface** | `#1C2541` | `@color/bg_midnight_surface` | Card surfaces, board backgrounds, dialog surfaces |
| **Midnight Card** | `#223055` | `@color/bg_midnight_card` | Elevated card surfaces, borders, dividers |
| **Path Cyan Glow** | `#00F5D4` | `@color/path_glow_cyan` | Primary brand accent, continuous path core, active focus |
| **Forest Mint** | `#52B788` | `@color/forest_mint` | Checkpoint #1 (Start), unlocked state, success indicators |
| **Forest Accent** | `#40916C` | `@color/forest_accent` | Checkpoint #2 (Intermediate), progress bars |
| **Forest Primary** | `#2E5D4B` | `@color/forest_primary` | Checkpoint #3, deep emerald accents |
| **Accent Gold** | `#F59E0B` | `AccentGold` token | Final checkpoint (#N), 3-star rating, achievement medals |
| **Wall Crimson** | `#E63946` | `@color/wall_crimson` | Blocked corridor edge barriers |
| **Pure White** | `#FFFFFF` | `@color/text_primary` | High-contrast checkpoint numbers, primary headings |
| **Text Secondary** | `#94A3B8` | `@color/text_secondary` | Descriptions, subtitles, metric labels |
| **Text Muted** | `#64748B` | `@color/text_muted` | Inactive icons, subtle grid lines |

---

## 3. Logo & Wordmark Usage

### The Zynpath Mark
The Zynpath mark depicts an authentic continuous orthogonal path traversing numbered checkpoints #1, #2, and #3 on a subtle Midnight Navy grid.
- **Safe Zone:** The mark must always be surrounded by clear padding equal to at least 25% of its width.
- **Minimum Size:** 24dp for digital screens; never render smaller than 16dp to preserve checkpoint number legibility.
- **Prohibited Alterations:**
  - Do not add drop shadows that obscure orthogonal corridors.
  - Do not rotate the mark diagonally (the game is strictly orthogonal).
  - Do not change checkpoint numbers into arbitrary symbols or letters.

### The Wordmark
The wordmark pairs geometric sans-serif lettering (`Inter` / `Roboto`) with a dual-color emphasis:
- **ZYN**: Crisp pure white (`#FFFFFF`).
- **PATH**: Luminous Path Cyan (`#00F5D4`).
- **Tagline Placement:** Placed below the wordmark in tracking-expanded Forest Mint (`#52B788`): `ONE PATH. EVERY NUMBER.`

---

## 4. Typography Guidelines

- **Primary Typeface:** `Inter` (or system `Roboto` on Android fallback).
- **Scale Hierarchy:**
  - Display Title: 24sp Bold (`FontWeight.Bold`).
  - Screen Headings: 18–20sp Bold.
  - Section Headers: 14–16sp SemiBold (`FontWeight.SemiBold`).
  - Body Text: 13–14sp Normal (`FontWeight.Normal`).
  - Badges & Checkpoints: 11–13sp Bold.
- **Scalability:** All typography strictly uses scalable `sp` units supporting system font scaling up to 200% without clipping.
