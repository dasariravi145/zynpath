# Home Screen Architecture & UX

## Overview

The Zynpath Home Screen serves as the central hub of the player's journey, unifying progression, daily routines, competitive multiplayer, and account management into a cohesive, uncluttered presentation.

## Core Design Principles

1. **Clarity Over Clutter:** The player should immediately know where they left off and how to continue playing.
2. **Guest-First Accessibility:** Solo gameplay and daily puzzles are playable offline without requiring account creation or login walls.
3. **Session Continuity Priority:** If an active unfinished puzzle attempt exists, the primary CTA is dynamically promoted to **"Resume Level"** with moves count and elapsed time, preventing accidental session loss.
4. **Adaptive Presentation:** Dynamically reflows across phone portrait, phone landscape, foldable devices, and tablets using `ZynpathAdaptiveLayout` (`rememberZynpathWindowInfo()`).

---

## Screen Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│ Top Bar: [Avatar / Guest Tag]    [Sync Badge] [🔔] [⚙️]     │
├─────────────────────────────────────────────────────────────┤
│ Brand Identity: Zynpath • "One path. Every number."         │
│ Status: Offline Solo Ready / Online                         │
├─────────────────────────────────────────────────────────────┤
│ Primary Play Action Hero:                                   │
│   - Active Session: Resume Level X (Moves + Time)           │
│   - New Guest: Play Level 1 + Interactive Tutorial          │
│   - Returning Player: Continue Level X (World Progress Bar) │
│   - Campaign Master: Explore Worlds & Replay                │
├─────────────────────────────────────────────────────────────┤
│ Next Objective & Milestones Card (Prompt 41):               │
│   - Next Goal Discovery: Highest progress uncompleted goal │
│   - Progress bar with percentage readout                    │
│   - Unlocked achievements count badge ("🏆 X / Y Unlocked") │
│   - Direct navigation to Achievement Gallery                │
├─────────────────────────────────────────────────────────────┤
│ Daily Challenge Hero Card:                                  │
│   - Handcrafted 5×5 / 6×6 / 7×7 UTC daily puzzle            │
│   - Streak counter ("🔥 Xd Streak")                         │
│   - Status: Ready / In Progress / Completed                 │
│   - UTC midnight reset countdown                            │
├─────────────────────────────────────────────────────────────┤
│ Game Mode Discovery:                                        │
│   - Solo Worlds (300 levels • Offline ready)                │
│   - Quick Duel (1v1 matchmaking • Online required)          │
│   - Friend Duel (Private room code • Online required)       │
│   - Mini League (2–5 players party room • Online required)  │
├─────────────────────────────────────────────────────────────┤
│ Quick Access Grid:                                          │
│   [Tutorial]  [Worlds]  [Friends]  [Trophies]  [Premium]    │
├─────────────────────────────────────────────────────────────┤
│ Premium Discovery (Free Tier Only):                         │
│   - Curated Solo Packs, Dark Palettes, Path Effects         │
│   - Official Google Play store pricing link                 │
└─────────────────────────────────────────────────────────────┘
```

---

## State Management (`HomeViewModel`)

The home screen UI state is powered by `HomeUiState` assembled via reactive Flows:

- `displayName` & `guestTag`: Derived from `PlayerProfileRepository` and `PreferencesRepository`.
- `completedLevelsCount` & `totalStars`: Derived authoritatively from Room `ProgressRepository.observeAllProgress()`.
- `nextPlayableWorldId` & `nextPlayableLevelId`: Derived via `WorldConfiguration.getNextPlayableLevel()`.
- `resumableSession`: Derived from Room `GameplaySessionRepository.getLatestResumableSession()`.
- `isOnline`: Observed live from Android `NetworkConnectivityMonitor`.
- `syncStatus`: Observed live from `SyncCoordinator.syncStatus`.
- `userNoticeMessage`: Controlled non-intrusive feedback when player taps an online-required mode while offline.

---

## Responsive Layout Behavior

- **Compact Phones (< 360dp width or < 640dp height):** Tightened padding (14dp), reduced vertical spacers (10–12dp), compact badges to prevent text clipping with large font scaling.
- **Standard Phones:** Single-column vertical scroll with balanced 20dp padding and 48dp minimum touch targets.
- **Tablets in Portrait:** Centered layout constrained to `maxContentWidth` (560dp) preventing over-stretching.
- **Landscape & Wide Tablets (Expanded):** Two-pane layout:
  - Left Pane: Header, Player Avatar, Primary Action Hero, Daily Challenge card, Quick Navigation.
  - Right Pane: Game Mode Discovery cards, Premium preview banner.

---

## Accessibility (TalkBack & Visual)

- High contrast text conforming to WCAG AAA standards on `BackgroundDark` (#0B132B).
- Non-color-only indicators: Badges combine icons, borders, and textual status ("Online Ready" vs "Requires Internet", "🔥 Streak").
- Touch targets for all interactive cards and icon buttons meet or exceed Android 48dp guidelines.

---

## Brand Identity Integration & Splash Handoff (Prompt 42)

- **Official App Identity**: "Zynpath: Number Path Puzzle" with tagline "One path. Every number."
- **Splash Transition**: Cold launches transition instantaneously from the native Android 12+ API 31 `Theme.Zynpath.Splash` (Midnight Dark `#0B132B` background with centered `@drawable/ic_splash_logo`) into `MainActivity` with zero simulated delays.
- **Visual Continuity**: The Midnight Navy palette, Forest Depth accents, and glowing Cyan path markers maintain exact visual parity from launcher icon and splash screen directly into the Home header and primary play cards.

---

## QA Verification Status (Prompt 48)
- **Home UI State & Guest Identity:** **PASSED** (Verified in `HomeScreenAndWorldMapComprehensiveTest.testHomeUiStateDisplaysGuestIdentityAndInitialOfflineState()`).
- **Resumable Session Banner:** **PASSED** (Verified in `HomeScreenAndWorldMapComprehensiveTest.testActiveSessionTriggersResumableState()`).
- **Mode Discovery Routing:** **PASSED** (Direct routing to Solo, Daily, Quick Duel, Friend Duel, Mini League verified).
- **Offline Solo Ready Indicator:** **PASSED** (Verified offline readiness flag when network disconnected).
