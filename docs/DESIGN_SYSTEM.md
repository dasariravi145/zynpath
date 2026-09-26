# Zynpath Design System Specification

**Status:** Authoritative  
**Design Direction:** Midnight Navy, Forest Depth Accents, and Luminescent Path Ribbon  
**UI Framework:** Jetpack Compose + Material 3  

---

## 1. Visual Philosophy & Identity

Zynpath combines cognitive calm with vibrant focus. High-contrast puzzle elements ensure that the grid board and continuous path remain instantly readable under any lighting conditions.

```
+-------------------------------------------------------------+
| BACKGROUND: Midnight Navy (#0B132B / #1C2541)               |
|                                                             |
|   +-----------------------------------------------------+   |
|   | PUZZLE BOARD: Off-White Slate (#F4F7F6)             |   |
|   |                                                     |   |
|   |   ( 1 ) ═══════════ ( 2 )                           |   |
|   |     ║                 ║                             |   |
|   |     ║   [WALL]        ║     PATH: Cyan (#00F5D4)    |   |
|   |     ║                 ║     CHECKPOINTS: (#0B132B)  |   |
|   |   ( 4 ) ═══════════ ( 3 )   WALL: Crimson (#E63946) |   |
|   +-----------------------------------------------------+   |
|                                                             |
| ACCENTS: Forest Mint (#52B788) & Gold Stars (#FFB703)       |
+-------------------------------------------------------------+
```

---

## 2. Color Palette & Token Definitions

| Token Name | Hex Code | Role / Usage |
|---|---|---|
| `BackgroundDark` | `#0B132B` | Root application background and system bar scrim. |
| `BackgroundSurface` | `#1C2541` | Elevated container surfaces and navigation bars. |
| `BackgroundCard` | `#223055` | Secondary cards, buttons, and inactive chips. |
| `BackgroundElevated` | `#2A3B66` | Primary hero cards and active board frames. |
| `ForestPrimary` | `#2E5D4B` | Brand identity anchor and deep green containers. |
| `ForestAccent` | `#40916C` | Medium forest borders and secondary badges. |
| `ForestMint` | `#52B788` | Primary interactive color (buttons, active toggles). |
| `PathCyanGlow` | `#00F5D4` | The continuous glowing puzzle polyline. |
| `BoardBackgroundLight` | `#F4F7F6` | High-readability puzzle grid board surface. |
| `BoardCellBorder` | `#E2E8F0` | Subtle cell division lines. |
| `CheckpointDark` | `#0B132B` | Circular numbered checkpoint background. |
| `CheckpointTextWhite` | `#FFFFFF` | Bold numeral text within checkpoints. |
| `WallCrimson` | `#E63946` | Impassable interior wall obstacles. |
| `AccentGold` | `#FFB703` | Stars, premium perks, and level mastery badges. |

---

## 3. Reusable UI Components

### 3.1 Buttons
- **`ZynpathPrimaryButton`**: $56\text{ dp}$ height, full-width or weighted, filled with `ForestMint` background, bold dark text, $16\text{ dp}$ rounded corners. Supports optional leading icon.
- **`ZynpathSecondaryButton`**: $52\text{ dp}$ height, transparent background with $1.5\text{ dp}$ forest border, white text.

### 3.2 Navigation & Headers
- **`ScreenHeader`**: Back navigation circular button ($42\text{ dp}$), bold title ($20\text{ sp}$), optional subtitle, and trailing action slot.
- **`PlayerAvatarBadge`**: Pill badge containing avatar icon, player display name, and player tag (`ZYN-XXXX`).

### 3.3 Cards & Badges
- **`FeatureCard`**: Elevated card for game modes with category icon, title, description, accent border, and phase badges.
- **`StatusBadge`**: Pill badge with pulsing dot and status text ("Offline Solo Ready").

---

## 4. Accessibility & Touch Ergonomics
- All interactive touch targets are strictly $\ge 48\text{ dp} \times 48\text{ dp}$.
- Contrast ratios between text and background exceed WCAG AAA standards ($> 7:1$).
- Full support for `isReducedMotion` toggle, muting particle animations for motion-sensitive players.
