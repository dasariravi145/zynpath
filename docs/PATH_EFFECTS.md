# Zynpath Path Effects Architecture & Rendering

## 1. Overview & Rendering Principles

Path effects in **Zynpath** determine the visual styling, glowing halos, and dynamic energy of the player's continuous trail on the `PuzzleBoard`.

### 1.1 Decoupling Engine from Rendering
- **Pure Geometry**: The underlying `PuzzlePath` contains purely mathematical grid coordinates (`GridPosition`).
- **Engine Isolation**: Path extension, backtrack retraction, cycle prevention, and win validation are completely independent of visual effects.
- **Visual Mapping**: `PuzzleBoard` canvas maps coordinates to center screen offsets, applying the equipped `pathEffectId`.

---

## 2. Path Effects Catalog

### 2.1 Free Path Effect
#### 1. Solid Glow (`path_solid_glow`) — Default
- **Design**: Dual-layer stroke.
- **Layers**:
  - Outer ambient aura (`palette.pathGlowColor`, width: `cellSize * 0.28f`, `StrokeCap.Round`).
  - Inner sharp core (`palette.pathCoreColor`, width: `cellSize * 0.16f`, `StrokeCap.Round`).
- **Characteristics**: Instant response, zero GPU overhead, battery efficient.

### 2.2 Premium Path Effects
#### 2. Cyan Energy Pulse (`path_cyan_pulse`)
- **Design**: Harmonic energy pulse traveling through the path segments.
- **Layers**:
  - Outer pulsing cyan halo (`palette.pathGlowColor.copy(alpha = glowAlpha)`, width: dynamic `cellSize * 0.32f * pulseMultiplier`).
  - Inner high-energy cyan core (`palette.pathCoreColor`, width: `cellSize * 0.17f`).
- **Requirement**: `PREMIUM_PATH_EFFECTS` entitlement.

#### 3. Golden Shimmer (`path_gold_shimmer`)
- **Design**: Triple-layer gold aura with a radiant starlight spine.
- **Layers**:
  - Outer golden aura (`#FFB703`, alpha: 0.40f, width: `cellSize * 0.30f`).
  - Radiant gold core (`#FFE082`, width: `cellSize * 0.16f`).
  - White starlight spine (`#FFFFFF`, alpha: 0.85f, width: `cellSize * 0.06f`).
- **Requirement**: `PREMIUM_PATH_EFFECTS` entitlement.

#### 4. Ember Trail (`path_ember_trail`)
- **Design**: Blazing warm gradient radiating heat from the path head.
- **Layers**:
  - Warm flame aura (`#FF5722`, alpha: 0.45f, width: `cellSize * 0.32f`).
  - Radiant ember core (`#FFD166`, width: `cellSize * 0.17f`).
- **Requirement**: `PREMIUM_PATH_EFFECTS` entitlement.

#### 5. Starlight Stream (`path_starlight`) — Coming Soon
- **Status**: Cataloged as `COMING_SOON`.

---

## 3. Reduced Motion & Performance Policy

Per Prompt 28 Sections 22, 23 & 24:
- **Respect User Settings**: When `isReducedMotion == true`, dynamic pulse multipliers freeze to static neutral constants (`pulseMultiplier = 1f`, `glowAlpha = 0.40f`).
- **Zero Allocation**: Drawing scopes reuse precalculated `Path` objects and coordinate mappers, preventing per-frame heap allocations.
- **Paused State**: Animations cease when games are paused or application is backgrounded.
