# Rendering Performance & Jetpack Compose Optimization

## 1. Board Canvas Pipeline
The `PuzzleBoard` composable is the primary visual component in Zynpath, rendering all canonical grid sizes (4x4, 5x5, 6x6, 7x7, 8x8) at 60Hz and 120Hz display refresh rates.

### Optimization Mechanics:
1. **Draw-Phase Animation Deferral**:
   - `pulseAlpha` and `headPulseScale` infinite animations are retained as `State<Float>`.
   - Their values are read exclusively inside the `Canvas { ... }` block (`drawScope`), preventing the `PuzzleBoard` composable body from recomposing on every frame.
2. **Path Allocation Elimination**:
   - Instead of allocating `Path()` on every draw pass, `PuzzleBoard` maintains `sharedPath` and `sharedDiamondPath`.
   - Paths are reset (`rewind()` / `reset()`) and repopulated, eliminating heap churn and GC pauses during dragging.
3. **Text Measurement Cache**:
   - `CheckpointTextCache` caches pre-measured `TextLayoutResult` objects keyed by `(checkpointNumber, fontSize, textColor)`.
   - Static checkpoint numbers (e.g. 1 through 10) are measured once rather than on every frame.
4. **Intermediate Drag Resolution**:
   - `GridCoordinateMapper.resolveIntermediatePath` breaks down multi-axis / fast diagonal swipes into bounded Manhattan orthogonal steps (max 6 units, max 8 steps).
   - This ensures rapid gestures never drop valid intermediate cells or create invalid diagonal board moves.
5. **Reduced Motion Support**:
   - Respects user accessibility preferences (`isReducedMotion = true`) by disabling continuous pulsing effects while preserving clear visual checkpoints.
