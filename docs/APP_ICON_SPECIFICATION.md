# App Launcher Icon Specification — Zynpath

## 1. Overview & Android Icon Standards

The Zynpath launcher icon is implemented in compliance with Android Adaptive Icon requirements (API 26+) and Material You Themed Icon specifications (API 33+):

- **Full Canvas Dimensions:** $108 \times 108\text{ dp}$
- **Adaptive Safe Zone (Mask Diameter):** $72\text{ dp}$ circle centered at $(54, 54)\text{ dp}$. Key artwork (path bends and numbered checkpoints) is strictly kept within coordinates $(21, 21)$ to $(87, 87)$ to guarantee zero clipping across round, squircle, square, and teardrop OEM mask shapes.
- **Resource Placement:**
  - Foreground: `res/drawable/ic_launcher_foreground.xml`
  - Background: `res/drawable/ic_launcher_background.xml`
  - Monochrome: `res/drawable/ic_launcher_monochrome.xml`
  - Adaptive Descriptors: `res/mipmap-anydpi-v26/ic_launcher.xml` and `res/mipmap-anydpi-v26/ic_launcher_round.xml`
  - Legacy Fallbacks: `res/drawable/ic_launcher.xml` and `res/drawable/ic_launcher_round.xml`

---

## 2. Icon Layers Architecture

```
Adaptive Icon Canvas (108 x 108 dp)
┌────────────────────────────────────────────────────────┐
│ Background Layer (ic_launcher_background.xml)          │
│   • Solid Midnight Navy (#0B132B)                      │
│   • Subtle 1px grid guides (#172244)                   │
├────────────────────────────────────────────────────────┤
│ Foreground Layer (ic_launcher_foreground.xml)          │
│   • Outer Glow: #3300F5D4 (12dp width)                 │
│   • Core Path Ribbon: #00F5D4 (6dp width)              │
│   • Checkpoint 1 (Start): Circle #52B788 + Number '1'  │
│   • Checkpoint 2 (Middle): Circle #40916C + Number '2' │
│   • Checkpoint 3 (Final): Circle #F59E0B + Number '3'  │
├────────────────────────────────────────────────────────┤
│ Monochrome Layer (ic_launcher_monochrome.xml)          │
│   • Pure White (#FFFFFF) paths and checkpoint rings    │
│   • Inverted black text numerals for contrast          │
│   • Dynamically tinted by Android 13+ Material You     │
└────────────────────────────────────────────────────────┘
```

---

## 3. High-Contrast & Accessibility Compliance

- **No Silhouette Confusion:** The distinctive orthogonal path structure with ascending numbered discs remains identifiable even when the icon is presented in low-contrast launcher modes or converted to greyscale.
- **High Checkpoint Contrast:** White checkpoint numerals maintain a $\ge 7:1$ contrast ratio against checkpoint fill circles and dark backgrounds.
- **Monochrome Dynamic Theming:** In Android 13+ launchers with themed icons enabled, the single-tone vector accurately reflects the user's wallpaper palette without loss of checkpoint numbering.
