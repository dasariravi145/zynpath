# Zynpath Haptic Feedback Architecture

## 1. Overview & Objectives

The Zynpath Haptic Feedback System provides subtle, tactile confirmations for puzzle actions, checkpoint arrivals, rejections, and milestone completions.

Core Design Philosophy:
- **Subtlety**: Haptics are short, refined impulse taps, never buzzing continuous motor hums.
- **Independence**: All puzzle gameplay remains 100% playable and understandable without vibration.
- **Safety & Fallback**: Automatically checks device capability; fails silently without crashes or performance hits on devices lacking modern vibrators.

---

## 2. Haptic Event Taxonomy

Defined in `com.zynpath.game.core.haptics.model.ZynpathHapticEvent`:

| Event | Waveform / Primitive | Duration | Intensity | Purpose |
|---|---|---|---|---|
| `PATH_START` | `VibrationEffect.createOneShot(15, 120)` | 15 ms | Medium-Low | Gentle confirmation when beginning path at Checkpoint 1 |
| `CHECKPOINT_REACHED` | `VibrationEffect.createOneShot(25, 180)` | 25 ms | Medium | Solid tactile click when validly hitting the next sequential checkpoint |
| `INVALID_MOVE` | Double pulse: 20ms on, 30ms off, 20ms on | 70 ms | High/Sharp | Non-punitive tactile rejection on blocked edges, revisits, or wrong checkpoints |
| `UNDO` | `VibrationEffect.createOneShot(10, 80)` | 10 ms | Light | Very subtle click when stepping back one cell |
| `RESET` | Descending double pulse: 15ms @ 150, 15ms @ 70 | 50 ms | Medium-Low | Tactile sensation of clearing the active path |
| `COMPLETION` | 3-pulse celebration: 20ms @ 120, 25ms @ 180, 40ms @ 255 | 180 ms | Ascending | Triumphant milestone pulse when 100% grid coverage is confirmed |
| `BUTTON_CONFIRM` | `VibrationEffect.createOneShot(15, 140)` | 15 ms | Medium | UI confirmation on critical toggles and modal buttons |
| `SELECTION_CHANGE` | `VibrationEffect.createOneShot(8, 60)` | 8 ms | Very Light | Micro-tick on cosmetic selector or tab switches |

---

## 3. High-Frequency Throttling & Filtering

During finger dragging across the puzzle board:
- Move haptics are throttled to a minimum interval of **50 milliseconds**.
- Invalid move rejections are throttled to a minimum interval of **200 milliseconds**.
- If a player holds their finger on a blocked edge or invalid cell, haptics trigger **once** and do not loop or buzz continuously.

---

## 4. Hardware Compatibility & Version Management

`ZynpathHapticManagerImpl` utilizes tiered Android vibration APIs:
- **Android 12+ (API 31+)**: Uses `VibratorManager.defaultVibrator`.
- **Android 10+ (API 29+)**: Uses `VibrationEffect.createPredefined(EFFECT_CLICK)` where available.
- **Android 8.0+ (API 26+)**: Uses amplitude-controlled `VibrationEffect.createOneShot()` and `createWaveform()`.
- **Pre-Oreo Fallback**: Deprecated `vibrator.vibrate(milliseconds)` with safe duration clamping.
- **No Hardware / Disabled**: If `vibrator.hasVibrator()` is false or `isHapticsEnabled` is false in preferences, execution exits immediately without allocating objects or issuing system calls.

---

## 5. Reduced-Motion & Accessibility

- When `isReducedMotion == true`, haptic feedback remains functional as a non-visual tactile cue unless the user explicitly disables `isHapticsEnabled`.
- Players with motor or sensory sensitivities can toggle haptics off entirely via **Settings > Audio & Haptics > Haptic Feedback**.

---

## 6. Non-Tactile Alternatives & Fallbacks (Prompt 39)
- **Zero Tactile Dependency**: Every haptic event is mirrored visually and through TalkBack semantics. Devices without vibration motors provide the identical gameplay clarity.
- **Graceful Fallback**: If vibration hardware is missing or throws a security/runtime exception, `ZynpathHapticManagerImpl` catches the error and degrades silently without crashing.

