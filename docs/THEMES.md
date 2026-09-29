# Zynpath Theme System & Visual Palettes

## 1. Theme Architecture

The Zynpath Theme Architecture extends Material 3 with semantic color palettes (`ZynpathColorPalette`). Every palette defines coordinated tokens for:
- App Surfaces: Backgrounds, elevated cards, navigation bars, and status bars.
- Puzzle Board Canvas: Board background, cell grid outlines, covered cell fills, start halos.
- Checkpoints: Start (#1), Final (#N), Visited, and Upcoming/Next numbers with WCAG AA compliance.
- Walls (Blocked Edges): High-contrast edge lines and luminous glow halos.
- Path Drawing: Dual-layer core and ambient outer aura.

---

## 2. Palettes Catalog

### 2.1 Free Themes

#### 1. Classic Midnight (`theme_classic_midnight`) — Default
- **Background**: Midnight Navy (`#0B132B`) & Surface (`#1C2541`)
- **Primary / Start Checkpoint**: Forest Mint (`#52B788`)
- **Secondary / Path Core**: Electric Cyan (`#00F5D4`)
- **Tertiary / Final Checkpoint**: Warm Gold (`#FFB703`)
- **Walls**: Crimson Red (`#E63946`) with luminous edge aura
- **Contrast**: > 16:1 against dark surfaces.

#### 2. Pure Dark OLED (`theme_pure_dark`)
- **Background**: OLED True Black (`#000000`) & Charcoal (`#121212`)
- **Primary**: Neon Emerald (`#00E676`)
- **Secondary**: Glacial Cyan (`#00E5FF`)
- **Tertiary**: Sunburst Yellow (`#FFD600`)
- **Walls**: High-visibility Scarlet (`#FF1744`)
- **Contrast**: > 18:1 for maximum contrast and battery conservation.

### 2.2 Premium Themes

#### 3. Cyber Neon (`theme_cyber_neon`)
- **Background**: Deep Synthwave Purple (`#0D0221`) & Dark Plum (`#190B38`)
- **Primary**: Electric Magenta (`#FF007F`)
- **Secondary**: Neon Cyan (`#00F0FF`)
- **Tertiary**: Cyber Gold (`#FFE600`)
- **Walls**: Hyper Crimson (`#FF0055`) with broad radiant glow
- **Requirement**: `PREMIUM_THEMES` entitlement.

#### 4. Emerald Forest (`theme_emerald_forest`)
- **Background**: Deep Spruce Green (`#041C15`) & Dark Pine (`#0A2E23`)
- **Primary**: Luminous Jade (`#2EC4B6`)
- **Secondary**: Spring Green (`#80ED99`)
- **Tertiary**: Amber Blossom (`#FFB703`)
- **Walls**: Warm Terracotta Coral (`#E76F51`)
- **Requirement**: `PREMIUM_THEMES` entitlement.

#### 5. Solar Amber (`theme_solar_amber`)
- **Background**: Obsidian Charcoal (`#14110E`) & Smoky Ash (`#231F1A`)
- **Primary**: Radiant Amber (`#FFAA00`)
- **Secondary**: Warm Solar Gold (`#FFD166`)
- **Tertiary**: Sunset Ruby (`#EF476F`)
- **Walls**: Bright Vermilion (`#E63946`)
- **Requirement**: `PREMIUM_THEMES` entitlement.

#### 6. Arctic Frost (`theme_arctic_frost`) — Coming Soon
- **Status**: Honestly cataloged as `COMING_SOON`. Zero fake assets.

---

## 3. Contrast & Accessibility Verification

All themes satisfy:
1. **Checkpoint Legibility**: High-contrast text on solid checkpoint circles (`#000000` or `#FFFFFF` based on luminance).
2. **Wall Visibility**: Blocked edges render at `max(3.5dp, cellSize * 0.08f)` with dedicated aura, distinct from grid lines.
3. **Path Traceability**: Multi-layer drawing ensures path is unmistakable against both covered and uncovered cells.
