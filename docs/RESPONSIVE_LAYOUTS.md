# Zynpath Responsive Layout Architecture

## 1. Overview
Zynpath is designed to provide an optimal, uncompromised puzzle-solving experience across the entire spectrum of Android form factors: compact smartphones, modern tall aspect-ratio devices, foldable screens, tablets, and resizable multi-window desktops (e.g., Samsung DeX, ChromeOS).

All layouts adaptively dynamically calculate available screen dimensions rather than hardcoding physical device profiles.

---

## 2. Window Size Classification & Breakpoints
Layout adaptations are governed by `ZynpathAdaptiveLayout.kt` and `rememberZynpathWindowInfo()`, utilizing standard Material 3 / Android window size classes:

```kotlin
enum class WindowWidthSize {
    COMPACT, // < 600dp (standard portrait phones)
    MEDIUM,  // 600dp - 839dp (foldables unfolded, small tablets, landscape phones)
    EXPANDED // >= 840dp (tablets, desktop/DeX windows)
}

enum class WindowHeightSize {
    COMPACT, // < 480dp (phones in landscape)
    MEDIUM,  // 480dp - 899dp (standard portrait phones)
    EXPANDED // >= 900dp (tall phones, tablets)
}
```

### Key Window Properties:
- `isLandscape`: `widthDp > heightDp`
- `isTablet`: `minDimension >= 600dp`
- `isCompactPhone`: `heightDp < 640dp && !isLandscape`
- `useSideBySideLayout`: `isLandscape || (isTablet && widthSize == EXPANDED)`
- `maxContentWidth`: Max width constraint for portrait columns to prevent horizontal control stretching (capped at 560dp).

---

## 3. Form Factor Adaptations

### 3.1 Compact Phones (< 640dp height, < 360dp width)
- **Vertical Spacing Compression:** Reduces spacers from 8–10dp to 4dp.
- **Header Optimization:** Compresses stat padding from 10dp to 6dp.
- **Priority Hierarchy:** The puzzle board and primary controls (Undo, Reset, Hint) retain strict >=48dp touch targets and never clip or push below the viewport.
- **Board Sizing:** Board scales to `min(availableWidth, availableHeight)` preserving square cell geometry.

### 3.2 Standard & Large Phones (640dp – 900dp height)
- **Thumb-Friendly Ergonomics:** Gameplay action controls sit comfortably in the lower quadrant.
- **Spacious Stats Bar:** Clear separation between timer, personal best, coverage counter, and checkpoint progress.

### 3.3 Tablets & Large Screens (>= 600dp min dimension)
- **Portrait Tablet Presentation:** Content is constrained using `Modifier.widthIn(max = 560.dp)` centered horizontally. This prevents buttons from stretching across 800–1200dp widths while maintaining optimal readability.
- **Landscape Tablet Layout:** Automatically triggers a 2-region side-by-side presentation.

### 3.4 Landscape Mode (Phones & Small Tablets)
- **Two-Region Side-by-Side Composition:**
  - **Left Pane (`weight(1.15f)`):** Board rendered with square aspect ratio, centered in available height (`fillMaxHeight(0.96f)`).
  - **Right Pane (`weight(0.85f)`):** Stats card, restoration indicator badge, move rejection feedback with `LiveRegionMode.Polite`, and controls arranged vertically with `verticalScroll(rememberScrollState())`.
- **Zero Control Clipping:** Essential buttons and info remain 100% visible and accessible without requiring orientation locking.

### 3.5 Foldables & Multi-Window (Window Resizing)
- **Dynamic Geometry Recalculation:** When folded/unfolded or resized, `BoxWithConstraints` recalculates `cellSize = availableSize / max(rowCount, colCount)`.
- **Path State Preservation:** Logical coordinates `(row, col)` remain completely decoupled from pixel geometry. Resizing never invalidates an existing valid path.
- **Cutout and Inset Safety:** All screens respect status bar, navigation bar, and display cutout safe insets.

---

## 4. Grid Size Scalability (4×4 to 8×8)
All canonical grid sizes dynamically scale within the square board container:
- **4×4 & 5×5:** Generous cell sizes with prominent checkpoint numbers and high-visibility path ribbons.
- **6×6, 7×7 & 8×8:** Typography automatically scales proportionally to `cellSize * 0.42f`. Font sizes never dip below minimum legible limits.

---

## 5. Verification Status (Prompt 48)
- **Adaptive Layout Architecture:** **PASSED** (Validated across `Compact`, `Medium`, and `Expanded` window size classes).
- **Landscape & Split-Screen Multi-Window:** **PASSED** (Two-column layout preserves 1:1 board aspect and full control access).
- **Tablet & Foldable Adaptations:** **PASSED** (Capped max-width columns and adaptive side panels verified).
- **Physical Device Matrix:** **BLOCKED** on host execution environment due to lack of connected physical devices. Automated JVM layout suites verified.
- **Detailed Reference:** See [docs/DEVICE_COMPATIBILITY_MATRIX.md](file:///d:/Zynpath/docs/DEVICE_COMPATIBILITY_MATRIX.md).
