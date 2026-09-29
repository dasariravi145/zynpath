# Achievement Presentation & Gallery Architecture — Zynpath

## 1. Overview

The Achievement system in Zynpath recognizes player mastery across all facets of the game: Solo progression, World completion, Puzzle mastery, Daily Challenge participation, and Competitive multiplayer.

Prompt 41 refines the achievement presentation by introducing:
- Distinct category filtering (`ALL`, `SOLO`, `WORLD`, `MASTERY`, `DAILY`, `COMPETITIVE`).
- Interactive achievement cards with accessibility semantics.
- Full modal detail dialog for each achievement (`AchievementDetailDialog`), presenting unlock criteria, progress fraction, earned date, and badge.
- Empty states with actionable next steps.

---

## 2. Categories & Schema

Achievements are structured under six standardized categories:

| Category | Description | Examples |
| :--- | :--- | :--- |
| **SOLO** | Solo campaign level counts | `solo_first_step`, `solo_apprentice`, `solo_journeyman`, `solo_half_century`, `solo_century`, `solo_double_century`, `solo_campaign_master` |
| **WORLD** | Canonical world completion (all levels cleared) | `world_one_pioneer`, `world_two_explorer`, `world_three_wall_breaker`, `world_four_navigator`, `world_five_mastermind`, `world_six_grandmaster` |
| **MASTERY** | Special puzzle execution milestones | `pure_intellect` (no hints), `speed_demon` (sub-30s solve) |
| **DAILY** | Daily Challenge participation, streaks, and server validation | `daily_first_dawn`, `daily_three_streak`, `daily_seven_streak`, `daily_server_validated`, `daily_leaderboard_ranked` |
| **COMPETITIVE** | Backend-authoritative match results | `comp_quick_duel_complete`, `comp_quick_duel_win`, `comp_friend_duel_complete`, `comp_mini_league_participation`, `comp_mini_league_win` |

---

## 3. Achievement Definitions Table

| ID | Title | Category | Target | Description | Authority / Source |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `solo_first_step` | First Step | SOLO | 1 | Complete your first Solo level. | Room DB: Completed unique levels |
| `solo_apprentice` | Apprentice | SOLO | 5 | Complete 5 unique Solo levels. | Room DB: Completed unique levels |
| `solo_journeyman` | Journeyman | SOLO | 15 | Complete 15 unique Solo levels. | Room DB: Completed unique levels |
| `solo_half_century` | Half Century | SOLO | 50 | Complete 50 unique Solo levels. | Room DB: Completed unique levels |
| `solo_century` | Century | SOLO | 100 | Complete 100 unique Solo levels. | Room DB: Completed unique levels |
| `solo_double_century` | Double Century | SOLO | 200 | Complete 200 unique Solo levels. | Room DB: Completed unique levels |
| `solo_campaign_master` | Master of Paths | SOLO | 300 | Complete all 300 unique Solo levels. | Room DB: Completed unique levels |
| `world_one_pioneer` | The Awakening Pioneer | WORLD | 20 | Complete all 20 levels in World 1: The Awakening. | Canonical range 1..20 |
| `world_two_explorer` | Binary Drift Explorer | WORLD | 30 | Complete all 30 levels in World 2: Binary Drift. | Canonical range 21..50 |
| `world_three_wall_breaker` | Wall Breaker | WORLD | 50 | Complete all 50 levels in World 3: Labyrinth of Walls. | Canonical range 51..100 |
| `world_four_navigator` | Warp Navigator | WORLD | 50 | Complete all 50 levels in World 4: Quantum Portal. | Canonical range 101..150 |
| `world_five_mastermind` | Temporal Mastermind | WORLD | 50 | Complete all 50 levels in World 5: Temporal Flux. | Canonical range 151..200 |
| `world_six_grandmaster` | Grandmaster of the Infinite | WORLD | 100 | Complete all 100 levels in World 6: The Singularity. | Canonical range 201..300 |
| `pure_intellect` | Pure Intellect | MASTERY | 1 | Complete a level without using hints. | Validated level solve without hint |
| `speed_demon` | Speed Demon | MASTERY | 1 | Complete a level in under 30 seconds. | Validated level solve time < 30000ms |
| `daily_first_dawn` | First Dawn | DAILY | 1 | Complete your first Daily Challenge. | Daily record (any verified/local) |
| `daily_three_streak` | Three-Day Devotion | DAILY | 3 | Complete Daily Challenge 3 consecutive days. | DailyChallengeStreakCalculator (UTC) |
| `daily_seven_streak` | Weekly Dedication | DAILY | 7 | Complete Daily Challenge 7 consecutive days. | DailyChallengeStreakCalculator (UTC) |
| `daily_server_validated` | Verified Genius | DAILY | 1 | Have a Daily Challenge solution validated by the server. | Backend verificationStatus == "VERIFIED" |
| `daily_leaderboard_ranked` | Top Contender | DAILY | 1 | Achieve a ranked position on the Daily Leaderboard. | Daily record isLeaderboardEligible == true |
| `comp_quick_duel_complete` | Quick Reflexes | COMPETITIVE | 1 | Complete a Quick Duel match. | Authoritative match record |
| `comp_quick_duel_win` | Duel Victor | COMPETITIVE | 1 | Win a Quick Duel match. | Authoritative match record |
| `comp_friend_duel_complete` | Friendly Rivalry | COMPETITIVE | 1 | Complete a Friend Duel match. | Authoritative match record |
| `comp_mini_league_participation`| League Contender | COMPETITIVE | 1 | Complete a Mini-League tournament. | Authoritative league record |
| `comp_mini_league_win` | League Champion | COMPETITIVE | 1 | Win first place in a Mini-League tournament. | Authoritative league record |

---

## 4. UI & Accessibility Enhancements

1. **Semantic Merging:** `AchievementCard` uses `Modifier.semantics(mergeDescendants = true)` with a descriptive `contentDescription` combining title, category, status (Unlocked on Date / Locked), and progress percentage.
2. **Accessible Modals:** Clicking any achievement opens `AchievementDetailDialog` with clear title, full unlock criteria, progress bar, and close action.
3. **Contrast & Theme Support:** Unlocked achievements show celebratory gold accents (`#F59E0B`), while locked achievements show muted, readable borders and progress bars compliant with WCAG AAA standards.
4. **Font Scaling:** All title and description texts use responsive Material 3 typography with `TextOverflow.Ellipsis` and flexible column arrangements.
