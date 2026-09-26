# Zynpath Design System Specification

**Status:** Authoritative  
**Design Direction:** Midnight Navy, Forest Depth Accents, Luminescent Path Ribbon, and High-Contrast Light Board  
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
| `PathCyanSubtle` | `#48CAE4` | Outer glow ribbon for path illumination. |
| `BoardBackgroundLight`| `#F4F7F6` | High-readability puzzle grid board surface. |
| `BoardCellBorder` | `#E2E8F0` | Subtle cell division lines. |
| `CellCoveredTint` | `#3300F5D4` | Soft teal fill on covered cells. |
| `CellStartHalo` | `#5552B788` | Pulsing halo on start cell (checkpoint 1). |
| `CheckpointDark` | `#0B132B` | Circular numbered checkpoint background. |
| `CheckpointTextWhite` | `#FFFFFF` | Bold numeral text within checkpoints. |
| `WallCrimson` | `#E63946` | Impassable interior wall obstacles. |
| `AccentGold` | `#FFB703` | Stars, premium perks, and level mastery badges. |
| `AccentPurple` | `#7209B7` | Friend Duel and tournament accent. |
| `AccentBlue` | `#4361EE` | World 2 and informational secondary accent. |
| `SuccessGreen` | `#10B981` | Validation success and unlock indicator. |
| `WarningAmber` | `#F59E0B` | Cautionary notices and energy limits. |
| `ErrorRed` | `#EF4444` | Invalid move feedback and connection errors. |

---

## 3. Structural Design Tokens

### 3.1 Spacing & Touch Ergonomics (`Spacing.kt`)
- `xxs`: 2 dp, `xs`: 4 dp, `sm`: 8 dp, `md`: 12 dp, `lg`: 16 dp, `xl`: 24 dp, `xxl`: 32 dp, `xxxl`: 48 dp.
- `minTouchTarget`: 48 dp (WCAG AAA compliant touch target minimum).
- `comfortableTouchTarget`: 56 dp.
- `screenHorizontal`: 16 dp, `cardPadding`: 16 dp.

### 3.2 Corner Shapes (`ZynpathShapes.kt`)
- `small`: 8 dp (badges, chips)
- `medium`: 12 dp (small cards, reaction buttons)
- `large` / `buttonShape` / `cardShape`: 16 dp (cards, buttons, dialogs)
- `extraLarge` / `boardShape`: 20–24 dp (puzzle boards, hero banners)
- `pill`: 999 dp (status badges, player badges)

### 3.3 Elevation & Layering (`Elevation.kt`)
- `level0`: 0 dp (flat background)
- `level1` (`card`): 2 dp
- `level2` (`elevatedCard`): 4 dp
- `level4` (`modalDialog`): 12 dp
- `level5` (`bottomSheet`): 16 dp

### 3.4 Animation & Motion (`AnimationTokens.kt`)
- `durationFast`: 150 ms (button tap, toggle response)
- `durationNormal`: 300 ms (card expansion, tab switch)
- `durationSlow`: 450 ms (dialog entrance, screen transition)
- `durationTutorialStep`: 400 ms (tutorial step board transition)
- `durationPathSegment`: 80 ms (path trail step)
- Supports full respect for `isReducedMotion` user preference.

---

## 4. Reusable Component Catalog

| Component | File | Description & Properties |
|---|---|---|
| `ZynpathPrimaryButton` | `ZynpathButton.kt` | 56 dp primary action button in Forest Mint. |
| `ZynpathSecondaryButton` | `ZynpathButton.kt` | 52 dp outlined button with customizable border color. |
| `ZynpathScreenHeader` | `ScreenHeader.kt` | Screen header with back navigation button, title, subtitle, and action slot. |
| `ZynpathTopBar` | `ZynpathTopBar.kt` | Material 3 TopAppBar configured with dark theme tokens. |
| `ZynpathBottomNavigation` | `ZynpathBottomNavigation.kt` | Bottom navigation bar with Home, Worlds, Daily, and Settings destinations. |
| `ZynpathModeCard` | `ZynpathModeCard.kt` | Game mode selection card with icon, title, description, badge, and primary border. |
| `ZynpathLevelCard` | `ZynpathLevelCard.kt` | 72 dp square level card with level number, 0–3 stars, state (Locked, Unlocked, Completed), and current highlight. |
| `ZynpathWorldCard` | `ZynpathWorldCard.kt` | World selection card with world index, grid size, level range, completion progress bar, and lock state. |
| `ZynpathPlayerAvatar` | `PlayerAvatarBadge.kt` | Standalone avatar circle with customizable size and guest/online indicator. |
| `ZynpathStatusBadge` | `StatusBadge.kt` | Rounded pill badge with status indicator dot and label. |
| `ZynpathLoadingState` | `ZynpathFeedbackState.kt` | Centered circular progress with informative message. |
| `ZynpathErrorState` | `ZynpathFeedbackState.kt` | Centered error icon, message, and optional retry button. |
| `ZynpathEmptyState` | `ZynpathFeedbackState.kt` | Centered empty state icon, title, description, and optional action button. |
| `ZynpathConfirmationDialog`| `ZynpathConfirmationDialog.kt` | Modal alert dialog with title, message, and confirm/dismiss actions. |
| `ZynpathReactionPicker` | `ZynpathReactionPicker.kt` | Preset reaction grid (7 predefined phrases/emojis) for multiplayer sessions without permanent chat storage. |

---

## 5. Puzzle Board Rendering Model (`PuzzleBoard.kt`)

The puzzle board is a hardware-accelerated, pure Canvas-based Jetpack Compose component that consumes an immutable `PuzzleBoardState`:

1. **Board Canvas:** Rounded container in `BoardBackgroundLight` maintaining aspect ratio according to `rowCount : columnCount`.
2. **Grid Matrix:** Rounded cell borders drawn with `BoardCellBorder`.
3. **Covered Cells:** Soft teal overlay (`CellCoveredTint`) on visited grid cells.
4. **Start Halo:** Mint halo accent on start cell (`CellStartHalo`).
5. **Continuous Path:** Dual-stroke polyline connecting cell centers:
   - Outer glow stroke: width = $28\%$ cell size in `PathCyanSubtle`.
   - Inner core stroke: width = $16\%$ cell size in `PathCyanGlow` with `StrokeCap.Round` and `StrokeJoin.Round`.
6. **Numbered Checkpoints:** Circular dark discs (`CheckpointDark`) with crisp white numbers measured via `TextMeasurer`. Checkpoint 1 highlighted in `ForestMint`.
7. **Wall Obstacles:** Impassable thick barrier lines ($5\text{ dp}$, `WallCrimson`) drawn strictly along the shared border between adjacent cells.
8. **Path Head Indicator:** Bright cyan circular endpoint marker at current path head.
