# Zynpath Device Compatibility Specification

## 1. Overview
Zynpath is engineered to run reliably across a broad range of Android devices, OS versions, display densities, and peripheral configurations. Core gameplay is never gated behind high-end hardware or optional hardware capabilities.

---

## 2. Platform & OS Targets

| Parameter | Specification | Notes |
| :--- | :--- | :--- |
| **Minimum SDK** | API 26 (Android 8.0 Oreo) | Covers >95% of active Android devices globally |
| **Target SDK** | API 34 (Android 14) | Up-to-date with modern platform standards |
| **Compile SDK** | API 34 / 35 | Material 3 & Jetpack Compose compiler compatibility |
| **Architecture** | 64-bit and 32-bit (arm64-v8a, armeabi-v7a, x86_64) | Tested on physical ARM and emulator x86 |

---

## 3. Form Factor Matrix

| Device Profile | Screen Class | Layout Strategy | Verification Status |
| :--- | :--- | :--- | :--- |
| **Compact Phone** | < 360dp width, < 640dp height | Compressed vertical spacing (4dp), unclipped >=48dp controls | IMPLEMENTED, NOT VERIFIED (physical) |
| **Standard Phone** | 360–420dp width, 640–840dp height | Standard vertical layout, centered puzzle board, bottom controls | IMPLEMENTED, NOT VERIFIED (physical) |
| **Large / Tall Phone** | > 840dp height (20:9, 21:9) | Expanded stats section, comfortable thumb-reach controls | IMPLEMENTED, NOT VERIFIED (physical) |
| **Foldable (Folded)** | Narrow width (< 340dp) | Auto-downscaled typography, square grid geometry maintained | IMPLEMENTED, NOT VERIFIED (physical) |
| **Foldable (Unfolded)** | Square-ish (600–800dp width/height) | Responsive cell scaling, avoids hinge occlusion | IMPLEMENTED, NOT VERIFIED (physical) |
| **Tablet (Portrait)** | >= 600dp width | Max-width constraint (560dp) preventing control distortion | IMPLEMENTED, NOT VERIFIED (physical) |
| **Tablet (Landscape)** | >= 840dp width | Side-by-side two-region layout (Board left, controls right) | IMPLEMENTED, NOT VERIFIED (physical) |
| **Multi-Window / DeX** | Arbitrary resizable window | Dynamic bounds recalculation, zero path-state corruption | IMPLEMENTED, NOT VERIFIED (physical) |

---

## 4. Display & Accessibility Scaling

### 4.1 Font Scaling (100% to 200%)
- All typography uses scalable text units (`sp`).
- Controls, stats, and dialogs avoid fixed-height text clipping containers.
- Subtitle and label lines allow wrapping where appropriate; numeric badges auto-scale with cell dimensions.

### 4.2 Display Density & Scaling
- In-game dimensions are defined using device-independent pixels (`dp`).
- Custom Canvas rendering recalculates physical pixel sizes on layout passes (`onSizeChanged`), decoupling touch coordinates from fixed pixel densities.

---

## 5. System Navigation Compatibility

### 5.1 Gesture Navigation
- Essential puzzle cells and buttons maintain safe margin padding away from screen edges (>= 16dp horizontal padding).
- Dragging near screen borders does not trigger accidental system back gestures.

### 5.2 3-Button & 2-Button Navigation
- Window insets (`WindowInsets.safeDrawing`, `WindowInsets.navigationBars`) prevent navigation bar overlays from occluding gameplay controls.

### 5.3 System Back Button & Predictive Back
- Android back gesture triggers standard game confirmation dialogs or clean exit to parent level selector.
- Back handling preserves active uncommitted moves safely in Room database.

---

## 6. Device Capability Fallbacks

### 6.1 Haptic Feedback
- Devices lacking vibrator motors or advanced amplitude control gracefully degrade to no-op feedback without throwing exceptions.
- Settings toggle allows manual disabling.

### 6.2 Audio System
- Centralized `SoundFeedbackManager` safely checks MediaPlayer / AudioTrack state. If audio output fails or is disabled by user, gameplay continues uninterrupted.

### 6.3 Notification Permissions (`POST_NOTIFICATIONS`)
- If notification permission is denied on Android 13+, app suppresses background reminder triggers without blocking any gameplay feature.

### 6.4 Reduced Motion
- When the system setting `Settings.Global.TRANSITION_ANIMATION_SCALE == 0` or in-game "Reduced Motion" is enabled:
  - Error shake animations are disabled.
  - Path ribbon animations snap instantly without easing.
  - Pulse animations are replaced with static highlights.

---

## 7. Deferred Testing Notice
Physical multi-device testing (e.g., Pixel 8, Galaxy S23, Galaxy Z Fold 5, Galaxy Tab S9, Android Go devices) is formally deferred to the final implementation testing prompts. All items above are marked **IMPLEMENTED, NOT VERIFIED** in accordance with Section 1 and Section 76.
