# Zynpath Premium Feature Policy & Access Gating

**Application:** Zynpath: Number Path Puzzle  
**Phase:** 6 — Monetization and Premium (Prompt 26/50)

---

## 1. Feature Access Policy Specification

All feature access decisions are centralized in [FeatureAccessPolicy](file:///d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/premium/FeatureAccessPolicy.kt) to prevent scattered, inconsistent Boolean checks across the codebase.

```kotlin
object FeatureAccessPolicy {
    fun isFeatureUnlocked(
        featureKey: PremiumFeatureKey,
        gameMode: GameMode? = null,
        entitlement: PremiumEntitlement
    ): Boolean
}
```

---

## 2. Feature Key Definitions

### 1. `AD_FREE`
- **Scope:** Global.
- **Behavior:** Suppresses banner and interstitial ad units throughout the app.
- **Free User Experience:** Free players experience minimal, non-intrusive ads between world chapters. Core gameplay is never blocked by compulsory ads.

### 2. `UNLIMITED_SOLO_HINTS`
- **Scope:** Solo Campaign (`GameMode.SOLO`) and Practice (`GameMode.PRACTICE`).
- **Behavior:** Allows unlimited solver-backed step hints without consuming daily quota.
- **Fairness Boundary:** **STRICTLY PROHIBITED** in `QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`, and competitive `DAILY_CHALLENGE`. Any hint request in these modes returns `false` unconditionally, regardless of subscription tier.

### 3. `PREMIUM_SOLO_PACKS`
- **Scope:** Solo Level Catalog (`GameMode.SOLO`).
- **Behavior:** Unlocks access to premium puzzle collections and Master World packs.
- **Free Guarantee:** Shipped standard Worlds (1–10) are unconditionally free. No progression paywalls exist for core levels.

### 4. `PREMIUM_THEMES`
- **Scope:** Appearance / Board Visuals.
- **Behavior:** Grants access to Midnight Glass, Neon Cyber, and Emerald Forest themes.
- **Cosmetic Integrity:** Themes alter only color palettes and board textures; number contrast and grid visibility are maintained for all themes.

### 5. `PREMIUM_PATH_EFFECTS`
- **Scope:** Gameplay Path Rendering.
- **Behavior:** Unlocks custom path trail shaders (Cyan Pulse, Gold Shimmer, Ember Trail).
- **Multiplayer Visibility:** Opponent sees standard path representation in multiplayer duels to avoid visual distraction or rendering lag.

### 6. `PREMIUM_AVATAR_FRAMES`
- **Scope:** Player Profile & Match Lobby Badges.
- **Behavior:** Unlocks prestige avatar borders and badges.

### 7. `ADVANCED_PERSONAL_STATS`
- **Scope:** Player Profile & Analytics.
- **Behavior:** Displays in-depth personal statistics (solve time distributions, backtracking frequency, perfect path efficiency).
- **Core History Guarantee:** Free players retain lifetime win counts, world completion counts, star counts, and daily streaks without restrictions.

---

## 3. Rewarded Ad & Free Player Policy (Prompt 29)

- **Optional Rewarded Ads:** Free players retain the option to watch short rewarded videos to replenish Solo hints (+1 hint per completed video) after their standard daily free allowance is exhausted.
- **Allowance Separation:** Standard free hints and earned rewarded credits are tracked independently in `UserPreferences`. Earning a reward does not overwrite standard allowance.
- **Zero Forced Advertising:** Active puzzle drawing, competitive matches, and daily challenges are never interrupted by ads.
- **Strict Dismissal Rule:** Ad dismissal alone never grants a reward; only official SDK completion callbacks trigger credit increments.
- **Zero Competitive Exploits:** Rewarded ads can **never** be used in competitive duels or daily challenges to gain hints, time extensions, or ranking boosts. Hints are strictly disabled at the engine layer for competitive modes.
- **Daily & Storage Abuse Limits:** Free players may earn a maximum of 5 rewarded ads per calendar day, with a maximum wallet cap of 10 stored bonus credits.
- **Premium Suppression:** Active Premium subscribers (`AD_FREE`) have all ad preloading, ad presentation, and reward prompts completely suppressed.

---

## 4. Cosmetic Customization Policy & Competitive Non-Interference (Prompt 28)

- **Pure Visual Personalization**: Cosmetics alter only color palettes, path ribbons, and avatar borders.
- **Strict Competitive Fairness**:
  - Cosmetics must never modify puzzle rules, grid dimensions, valid moves, checkpoint orders, or wall collisions.
  - Cosmetics must never reveal hints, upcoming checkpoint directions, or hidden solutions.
  - Cosmetics must never alter competitive timers, scoring formulas, or matchmaking priority.
- **Reduced Motion**: If reduced-motion is requested by the player, path animations and particles are suppressed while preserving full puzzle playability.

---

## 5. Personal Analytics Policy & Fair Presentation (Prompt 30)

- **Zero-Fabrication Mandate**: Analytics must never invent, simulate, or extrapolate unrecorded gameplay. Missing historical data (e.g. solve times on legacy builds) must be marked as unavailable or excluded from averages rather than replaced with zeros.
- **Fair Free Tier Baseline**: Free players unconditionally retain access to:
  - Total Solo levels completed and active world status.
  - Overall canonical completion percentage (out of 300).
  - Personal best solve times per completed puzzle.
  - Full Daily Challenge participation history and streaks.
  - Basic competitive match records (Matches, Wins, Losses, Ties, and win rates).
- **Premium Unlocked Analytics (`ADVANCED_PERSONAL_STATS`)**:
  - Longitudinal completion trends (first-time solves vs replays).
  - Same-puzzle time improvement analysis (strictly identical puzzle versions).
  - Per-world solve speed velocity.
  - Detailed personal best chronology and replay logs.
- **Transparent Previews**: Free players viewing the Premium Insights tab see a clean, informative preview of available metrics. Previews never display fake personal numbers or simulated improvements.
- **Record Preservation on Subscription Lapses**: All recorded gameplay events remain stored in Room persistence. If a subscription lapses, raw data is preserved; resubscribing immediately restores advanced views without replaying levels.


