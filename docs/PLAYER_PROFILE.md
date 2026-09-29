# Zynpath Player Profile Specification

## 1. Overview
The Player Profile is the central hub for local player personalization, progress overview, offline statistics, and account linking. It is accessible directly from the Home screen top bar and provides full offline editing capabilities.

---

## 2. Profile Domain Model
The `PlayerProfile` data structure contains:

| Field | Type | Description |
|---|---|---|
| `playerId` | `String` | Stable internal UUID generated once on first startup. |
| `displayName` | `String` | Editable player name (default: "Pathfinder"). |
| `avatarId` | `String` | Built-in avatar identifier (e.g. `avatar_compass`). |
| `createdAt` | `Long` | UTC timestamp of profile initialization. |
| `lastActiveAt` | `Long` | UTC timestamp of last game interaction. |
| `accountType` | `AccountType` | `GUEST`, `LINKING`, `LINKED`, or `LINK_FAILED`. |
| `publicZynpathId` | `String?` | Backend-issued public tag (e.g. `ZYN-7749-ECHO`). |

---

## 3. Account Linking Integration (Prompt 18)
From the Profile Screen, guests can link their progress to an online identity:
- **Account Linking Card:** Displays current linking state ("Guest Mode" or "Linked with Google/Facebook") and Public Zynpath ID.
- **Account Linking Dialog:**
  - Allows linking with Google or Facebook.
  - Shows honest provider configuration badges (`Setup Pending` if credentials missing).
  - Explicit sign-out option that revokes the remote session and restores offline guest operation without erasing local progress.
- **Conflict Handling:** Notifies the player if an external account is already bound to another Zynpath player (`ACCOUNT_LINK_CONFLICT`).

---

## 4. Display Name Validation Rules
Display names must adhere to strict input validation (`DisplayNameValidator`):
- **Length:** Minimum 2 characters, maximum 20 characters.
- **Allowed Characters:** Alphanumeric, spaces, hyphens (`-`), underscores (`_`). Regex: `^[a-zA-Z0-9 _-]+$`.
- **Normalization:** Leading and trailing whitespace is trimmed; consecutive internal spaces are collapsed into a single space.
- **Error States:** `Empty`, `TooShort`, `TooLong`, `InvalidCharacters`.

---

## 5. Built-in Local Avatars
Zynpath provides a curated catalog of built-in vector avatars (`AvatarCatalog`) requiring zero remote downloads:

1. **Pathfinder (`avatar_compass`):** Guiding the path through every number (Forest Mint, Explore icon).
2. **Grid Master (`avatar_grid`):** Spatial reasoning and board control (Path Cyan, Grid icon).
3. **Zenith (`avatar_zenith`):** Calm, focused, and steady solutions (Forest Mint, Self-Improvement icon).
4. **Champion (`avatar_trophy`):** Pure mastery across all worlds (Accent Gold, Trophy icon).
5. **Speedster (`avatar_bolt`):** Swift moves and record solve times (Accent Purple, Bolt icon).
6. **Geometric (`avatar_cube`):** Mathematical logic and geometric paths (Path Cyan, Category icon).

---

## 6. Player Statistics Derivation
Statistics displayed on the profile screen are **derived directly from verified local records**, avoiding duplicated mutable state:
- **Solo Progression:** Derived from `LevelProgressDao.getCompletedLevelCount()` vs `LevelCatalogRepository.getWorlds().sumOf { it.levels.size }`.
- **Stars Earned:** Sum of stars earned across all distinct completed levels (`LevelProgressDao.getTotalStarsEarned()`).
- **Daily Challenges:** Total completed challenges (`DailyChallengeDao.getCompletedCount()`) and consecutive day streaks from `DailyChallengeRepository`.
- **Achievements:** Verified milestones unlocked (`AchievementDao.observeUnlockedCount()`).

---

## 7. Competitive Profile Integration (Prompt 24)
For authenticated/linked players, the Profile screen surfaces server-authoritative competitive records:
- **Competitive Progression Card (`CompetitiveProgressionCard`):**
  - **Quick Duel Overview:** Shows total matches played, wins, losses, ties, and win rate percentage.
  - **Friend Duel Overview:** Shows total private matches played, wins, losses, ties, and win rate percentage.
  - **Mini League Overview:** Shows total multiplayer room participations, first-place finishes, top 3 finishes, and average finish rank.
  - **Overall Completions:** Displays total verified puzzle completions across all online competitive modes.
  - **Navigation Shortcuts:** Quick actions to launch the full paginated **Match History** screen (`Screen.MatchHistory`) and global **Leaderboard** screen (`Screen.Leaderboard`).
- **Guest Player State:**
  - For unlinked offline guest players, competitive statistics display an educational empty/guest banner explaining that competitive match recording and leaderboards unlock upon signing in with Google or Facebook.
  - Offline Solo progress, Daily Challenge streaks, and local profile customizations remain completely intact and functional in guest mode.
- **Data Integrity:**
  - Competitive metrics are retrieved directly from `GET /api/v1/multiplayer/stats` backed by authoritative server-finalized match logs. Android never locally fabricates or mutates competitive win counts or ranks.

---

## 8. Daily Challenge & Verification Integration (Prompt 25)
- **Daily Challenge Verification States**:
  - The profile reflects official server-validated Daily Challenge achievements (`daily_server_validated`, `daily_leaderboard_ranked`).
  - Distinguishes local/provisional participation streaks from server-verified competitive standings.
- **Guest History Preservation on Account Linking**:
  - When a guest links their account to Google or Facebook, their existing Room `daily_challenge` records, active streak, and best solve times remain 100% intact.
  - Prior offline solves remain honestly tagged as `LOCAL_COMPLETION` or `PROVISIONAL` and are not falsely promoted to competitive leaderboard standings.

---

## 9. Premium Entitlement & Customization Integration (Prompt 26)
- **Membership Status Badge**: Profile displays authoritative subscription status (`Active Premium`, `Free Tier`, `Grace Period`).
- **Premium Cosmetic Boundaries**:
  - **Avatar Frames:** Premium unlocks prestige Gold and Animated border options.
  - **Themes & Path Effects:** Midnight Glass, Neon Cyber, and particle path trail shaders are unlocked via `FeatureAccessPolicy`.
- **Advanced Personal Statistics**: Premium members unlock deep telemetry (solve speed distributions, backtracking frequency, perfect coverage efficiency) without paywalling standard solve counts or streaks.
- **Fair Play Commitment**: Premium badges and cosmetic frames are strictly visual and convey zero gameplay advantages in competitive matchmaking or leaderboard rankings.

---

## 10. Premium Solo Puzzle Pack Progression & Statistics (Prompt 27)
- **Isolated Pack Tracking**: Pack progression (`PremiumPackProgressEntity`) tracks completed levels, best solve times, and pack completion percentages separately from the 6 free worlds.
- **Progress Preservation Across Expirations**: When a subscription expires, earned completion records, personal best times, and achievements remain permanently stored in local Room storage. Resubscription instantly restores access to previously earned pack states.
- **Competitive Isolation**: Premium pack completions and best times are personal Solo achievements and are strictly isolated from competitive leaderboard metrics, ratings, and duel statistics.

---

## 11. Cosmetic Customization & Equipped Avatar Frame (Prompt 28)
- **Equipped Avatar Frame Display**: The Profile header renders the player's current avatar framed by their equipped frame (`AvatarWithFrame.kt`), supporting `frame_default_slate`, `frame_silver_outline`, `frame_gold_accent`, and `frame_neon_ring`.
- **Customize Appearance Action**: An explicit "Customize Appearance" button navigates to `Screen.Cosmetics`, where players can preview and equip themes, path effects, and avatar frames.
- **Public Privacy Safeguard**: When public profiles are retrieved by other players (e.g. in Friends list, Duels, or Mini Leagues), only the cosmetic identifier string is returned. Billing tokens, subscription dates, or payment details are strictly never exposed.

---

## 12. Personal Statistics & Progression Insights Integration (Prompt 30)
- **Dedicated Dashboard Entry**: The Profile screen includes a dedicated "Detailed Analytics & Insights" card that navigates directly to `Screen.Statistics`.
- **Comprehensive Progression**: Direct access to the 5-tab dashboard covering Overview, Solo Worlds 1–6 progression charts, Daily Challenge calendar history, Competitive 1v1 and tournament breakdowns with sample sizes ($N$), and Premium Insights.
---

## 13. Notification Center & Badge State (Prompt 31)
- **Unread Notification Badge**: The Home screen and top navigation display an unread notification counter badge bound reactively to `PlayerProfileRepository` and `NotificationRepository`.
- **Account Isolation**: When the active profile changes (sign-in, account switch, or sign-out), local notification state is partitioned by `recipientPlayerId`, clearing cached alerts on logout and re-synchronizing notifications for the authenticated profile.
- **Push Token Security**: Push tokens are registered exclusively against the authenticated internal Account ID and are never exposed on public profile APIs or friend searches.

---

## 14. Profile Visibility & Privacy Settings (Prompt 32)
- **Visibility Tiers**: Profiles enforce `PUBLIC`, `FRIENDS_ONLY`, or `PRIVATE` tiers via backend `player_privacy_settings`.
- **Zynpath ID Search Toggle**: Players can hide their profile from exact-match public ID searches.
- **Friend Request Controls**: Incoming friend requests can be disabled independently of profile visibility.
- **Account Deletion & Data Export**: Self-service deletion and Schema v1 JSON data export can be initiated from Settings, ensuring full control over personal profile data.

---

## 15. Offline Synchronization & Status Indication (Prompt 35)
- **Real-Time Sync Badge**: Profile and settings surfaces render `SyncStatusBadge` displaying the authoritative synchronization state:
  - `SAVED_LOCALLY`: Guest mode or offline operation; changes safely committed to Room on-device.
  - `WAITING_TO_SYNC`: Pending operations queued in Room waiting for network or battery constraints.
  - `SYNCING`: Active batched transmission to backend in progress.
  - `SYNCED`: Authoritatively acknowledged by backend without remaining queue.
  - `ACTION_REQUIRED`: Authentication expired or non-recoverable conflict requiring player action.
- **User-Initiated Foreground Sync**: Players can trigger manual synchronization ("Sync Now") on demand via `SyncCoordinator.syncNow()`, reusing the same idempotent reconciliation pipeline as background workers.
- **Zero Fabrication**: Local statistics continue to derive exclusively from verified Room records, eliminating optimistic score inflations or unverified remote claims.

---

## 16. Achievement Presentation & Retention Experience (Prompt 41)
- **Refined Achievement Entry Point**: Direct access to the full `AchievementsScreen` gallery with organized category tabs (`ALL`, `SOLO`, `WORLD`, `MASTERY`, `DAILY`, `COMPETITIVE`).
- **Interactive Detail Modal**: Tapping any achievement card opens `AchievementDetailDialog`, displaying exact unlock requirements, current progress fraction, and unlock timestamps.
- **Privacy-Aware Statistics**: Guest statistics and achievements remain stored locally on device. Public profiles respect privacy tiers (`PUBLIC`, `FRIENDS_ONLY`, `PRIVATE`) without exposing raw gameplay coordinates, payment details, or private solve attempts.
- **Account Isolation & Linking**: Guest achievements earned offline are seamlessly preserved and migrated upon linking with Google or Facebook without record loss or duplicate grants.






