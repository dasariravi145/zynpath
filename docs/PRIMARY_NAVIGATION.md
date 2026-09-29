# Primary Navigation Architecture

## Overview

The Zynpath navigation graph is built on Android Jetpack Navigation Compose, operating as a strictly unified, predictable, single back-stack architecture without duplicate navigation graphs or contradictory progress repositories.

---

## Route Definitions & Navigation Graph

The authoritative routes are declared in `Screen`:

```kotlin
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Tutorial : Screen("tutorial")
    object Home : Screen("home")
    object WorldSelection : Screen("world_selection")
    object LevelSelection : Screen("level_selection/{worldId}")
    object Gameplay : Screen("gameplay/{worldId}/{levelId}")
    object DailyChallenge : Screen("daily_challenge")
    object DailyLeaderboard : Screen("daily_leaderboard")
    object QuickDuel : Screen("quick_duel")
    object FriendDuel : Screen("friend_duel")
    object MiniLeague : Screen("mini_league")
    object Friends : Screen("friends")
    object Profile : Screen("profile")
    object Achievements : Screen("achievements")
    object MatchHistory : Screen("match_history")
    object MatchDetails : Screen("match_details/{matchId}")
    object Leaderboard : Screen("leaderboard")
    object Premium : Screen("premium")
    object PremiumPacks : Screen("premium_packs")
    object PackLevels : Screen("pack_levels/{packId}")
    object PackGameplay : Screen("pack_gameplay/{packId}/{levelIndex}")
    object Cosmetics : Screen("cosmetics")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object Privacy : Screen("privacy")
    object Notifications : Screen("notifications")
    object SignIn : Screen("sign_in")
}
```

---

## Primary Navigation Flows

### 1. Primary Play & Continue
- **Active Session:** `Home` -> `Gameplay/{worldId}/{levelId}` (resumes active game attempt directly without overwriting progress).
- **First-Time Guest:** `Home` -> `Gameplay/1/1` or `Tutorial`.
- **Continuing Player:** `Home` -> `Gameplay/{nextWorldId}/{nextLevelId}` or `WorldSelection`.
- **World Selection Entry:** `Home` -> `WorldSelection` -> `LevelSelection/{worldId}` -> `Gameplay/{worldId}/{levelId}`.

### 2. Deep Linking Fallback & Safety
All deep links (`zynpath://daily`, `zynpath://friend_duel`, `zynpath://minileague?code=...`, `zynpath://match/{matchId}`, `zynpath://invite?id=...`) are registered on the single `NavGraph`. In the event of network failure or missing session credentials, deep links gracefully fallback to safe local destinations (`Home` or `SignIn`) without crashing or stranding the user.

### 3. Back Navigation Predictability
- Back button behavior pops back predictably to `Home`.
- Progress is saved atomically before any navigation pop occurs.
