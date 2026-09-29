# Performance Baseline & Targets

## 1. Observed Bottlenecks (Lightweight Inspection)
During code inspection for Prompt 37, the following observable bottlenecks were identified:
1. **Gameplay UI Ticker**: `GameplayViewModel` emitted state copies every 100ms exclusively to update elapsed time, triggering 10 full-screen recompositions per second.
2. **Board Animation Recomposition**: Animation state values (`pulseAlpha`) were read in the composable body, causing continuous frame-rate recompositions.
3. **Canvas Path Allocations**: Path objects were repeatedly instantiated inside `onDraw` blocks during pointer drag events.
4. **Repeated Asset Validation**: Catalog availability checks re-read JSON assets and parsed definitions on every progression Flow emission.
5. **Backend Leaderboard Scans**: Server leaderboard queries scanned all finalized matches and resolved player accounts repeatedly per HTTP request.

## 2. Engineering Targets (For Final Verification Phase)
The following targets are established for verification during final benchmark and hardware test phases (Prompts 48–50):
- **First Frame Interactive**: < 1.2s cold start on mid-tier Android devices.
- **Board Render Frame Rate**: Consistent 60 FPS on standard screens, 120 FPS on high-refresh displays.
- **Touch Responsiveness**: Touch-to-path reaction within 16ms (1 frame at 60Hz).
- **Timer CPU Overhead**: < 1% CPU utilization during active gameplay timer polling.
- **Backend Leaderboard Response**: < 20ms p95 latency for paginated queries under in-memory caching.
- **Memory Footprint**: Steady heap under 90MB during extended puzzle sessions.

*Note: All numerical metrics are engineering targets and remain `NOT VERIFIED` until final device benchmarks.*
