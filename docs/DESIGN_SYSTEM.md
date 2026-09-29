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

---

## 6. Dynamic Themes, Path Effects & Avatar Frames (Prompt 28)

### 6.1 Theme Palettes (`ZynpathColorPalette`)
The design system dynamically supports 4 cohesive visual themes providing high contrast and comfortable puzzle solving:
- **Classic Midnight (Free)**: Midnight Navy background (`#0B132B`), Off-White Slate board (`#F4F7F6`), Cyan path glow (`#00F5D4`), Crimson walls (`#E63946`).
- **Dark Minimal (Free)**: Pitch Dark background (`#121212`), Graphite board (`#1E1E1E`), Bright Lime path (`#A6E22E`), Coral walls (`#FF5370`).
- **Solar Sunset (Premium)**: Deep Plum background (`#1A0B2E`), Warm Sandstone board (`#FFF3E0`), Amber Gold path (`#FFB703`), Ruby walls (`#D90429`).
- **Cyber Neon (Premium)**: Obsidian Black background (`#05050A`), Electric Slate board (`#0F172A`), Hot Magenta path (`#FF007F`), Vivid Amber walls (`#FFB800`).

### 6.2 Path Visual Effects
- **Solid Glow (`path_solid_glow` - Free)**: Dual-layer cyan ribbon with smooth anti-aliased geometry.
- **Gentle Pulse (`path_gentle_pulse` - Premium)**: Subtle sine-wave breathing opacity on the outer glow stroke.
- **Gradient Trail (`path_gradient_trail` - Premium)**: Dynamic gradient shifting from start cell to path head.
- **Particle Accents (`path_particle_accent` - Premium)**: Subtle ambient accent particles along the active path segments.
- *Reduced-Motion Enforcement*: If `isReducedMotion` is true, all pulsing and particle generation are suppressed, instantly falling back to a clean, high-contrast static path ribbon.

### 6.3 Avatar Frames (`AvatarWithFrame.kt`)
Enriches player profile avatars across all screens without altering identity:
- **Default Slate (`frame_default_slate` - Free)**: Clean 2 dp slate border.
- **Silver Outline (`frame_silver_outline` - Premium)**: Polished metallic silver ring.
- **Gold Accent (`frame_gold_accent` - Premium)**: Radiant dual-layer golden champion halo.
- **Neon Ring (`frame_neon_ring` - Premium)**: Vibrantly glowing cyan/magenta cyber ring.

---

## 7. Brand Identity, Production Icons & Store Visual Tokens (Prompt 42)

### 7.1 Production Launcher & Adaptive Icon System
- **Layer Architecture (`res/mipmap-anydpi-v26/ic_launcher.xml`)**:
  - `ic_launcher_background.xml`: Midnight Navy `#0B132B` field with subtle grid lines (`#1C2541`).
  - `ic_launcher_foreground.xml`: Continuous glowing cyan ribbon (`#00F5D4`) connecting numbered circular checkpoints `#1`, `#2`, and `#3`.
  - `ic_launcher_monochrome.xml`: Material You themed icon using high-contrast white vector geometry and inverted black digits for dynamic Android 13+ wallpaper tinting.
- **Adaptive Safe Zone Guarantee**: All visual marks, path bends, and numeric checkpoint discs are strictly bounded inside the central $72\text{ dp}$ circular safe zone on the $108\text{ dp}$ adaptive canvas, preventing clipping across squircle, circle, or rounded rectangle OEM masks.
- **Legacy Fallbacks**: Dedicated vector `ic_launcher.xml` and `ic_launcher_round.xml` layer-lists provide crisp rendering on pre-Oreo Android versions without oversized raster bitmaps.

### 7.2 Native Splash Screen Theme Alignment
- **Theme (`Theme.Zynpath.Splash`)**:
  - API 31+ Native Splash: `windowSplashScreenBackground` set to `@color/bg_midnight_dark` (`#0B132B`) and `windowSplashScreenAnimatedIcon` set to `@drawable/ic_splash_logo`.
  - Pre-API 31 Fallback: Centered vector layer-list (`splash_background.xml`) on `android:windowBackground`.
  - **Zero Startup Delay**: Seamlessly shifts to `@style/Theme_Zynpath` inside `MainActivity.onCreate()` prior to `super.onCreate()` with zero artificial sleep delays.

### 7.3 Canonical Vector Assets
- Standalone vector master assets preserved under `assets/branding/` (`logo_mark.svg`, `wordmark.svg`, `brand_tokens.json`).
- High-fidelity Google Play Store feature graphic source at `assets/store/feature-graphic/feature_graphic_1024x500.svg` with $15\%$ edge safety margins and solver-valid path geometry.

