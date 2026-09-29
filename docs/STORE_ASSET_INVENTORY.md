# Google Play Store Asset Inventory — Zynpath

## 1. Store Asset Inventory

| Asset Name | Target Destination | Dimensions / Format | Status | Description |
| :--- | :--- | :--- | :--- | :--- |
| **App Icon (Hi-Res)** | Google Play Console | $512 \times 512\text{ px}$, 32-bit PNG | **IMPLEMENTED** | High-res store icon with continuous cyan path & checkpoints #1..#3 on Midnight Navy |
| **Adaptive Launcher Icon** | Android APK / Bundle | $108 \times 108\text{ dp}$, Vector XML | **IMPLEMENTED** | Background, foreground, and monochrome layers with 72dp safe zone |
| **Splash Logo** | Android App Splash | $160 \times 160\text{ dp}$, Vector XML | **IMPLEMENTED** | High-res vector logo for native Android 12+ and pre-31 splash themes |
| **Feature Graphic (Vector)**| `assets/store/feature-graphic/` | $1024 \times 500\text{ px}$, SVG | **IMPLEMENTED** | Editable vector source of promotional banner with 4x4 board, walls, and badges |
| **Feature Graphic (Promo)** | Store Listing Banner | $1024 \times 500\text{ px}$ (16:9), PNG/JPG | **IMPLEMENTED** | High-res generated promotional feature graphic with glowing path aesthetic |
| **Phone Screenshots (1–8)** | Google Play Listing | $1080 \times 2400\text{ px}$, PNG | **SPECIFIED / PLANNED** | 8-screen sequence covering all core features and game modes |
| **Tablet Screenshots** | Google Play 7" & 10" | $1200 \times 1920\text{ px}$ / $1600 \times 2560\text{ px}$ | **SPECIFIED / PLANNED** | Tablet 2-column landscape layout captures |

---

## 2. Directory Layout

```
Zynpath/
├── assets/
│   ├── branding/
│   │   ├── brand_tokens.json             # Canonical colors, typography, and safe zones
│   │   ├── logo_mark.svg                 # Standalone vector mark (512x512)
│   │   └── wordmark.svg                  # Title & tagline vector (600x160)
│   └── store/
│       ├── metadata_en_US.json           # Title, short & full descriptions, tags
│       ├── feature-graphic/
│       │   └── feature_graphic_1024x500.svg # Vector source banner
│       └── screenshots/
│           └── screenshot_manifest.json  # 8-screenshot metadata & capture plan
```
