# Zynpath Achievements System & Architecture

## 1. Overview & Philosophy
The Zynpath achievement engine rewards player mastery, exploration, consistency, and puzzle-solving ingenuity. Achievements:
- Are verified exclusively against authoritative records (Room DB distinct level completions, UTC Daily Challenge records, and backend-validated competitive results).
- Unlock idempotently (an achievement unlocks exactly once; repeated triggers produce no duplicate records or celebration loops).
- Trigger lightweight, non-intrusive in-app celebrations with sound and haptic feedback honoring player preferences.
- Are organized into clear categories: `SOLO`, `WORLD`, `MASTERY`, `DAILY`, `COMPETITIVE`.

---

## 2. Complete Authoritative Registry (`AchievementRegistry`)

| ID | Title | Category | Target | Description | Verifiable Condition |
|---|---|---|---|---|---|
| `solo_first_step` | First Step | SOLO | 1 | Complete your first Solo level. | `completedDistinctLevels >= 1` |
| `solo_apprentice` | Pathfinder Apprentice | SOLO | 5 | Complete 5 distinct Solo levels. | `completedDistinctLevels >= 5` |
| `solo_journeyman` | Seasoned Traveler | SOLO | 15 | Complete 15 distinct Solo levels. | `completedDistinctLevels >= 15` |
| `solo_half_century` | Half Century | SOLO | 50 | Complete 50 distinct Solo levels. | `completedDistinctLevels >= 50` |
| `solo_century` | Century | SOLO | 100 | Complete 100 distinct Solo levels. | `completedDistinctLevels >= 100` |
| `solo_double_century` | Double Century | SOLO | 200 | Complete 200 distinct Solo levels. | `completedDistinctLevels >= 200` |
| `solo_campaign_master` | Master of Paths | SOLO | 300 | Complete all 300 distinct Solo levels. | `completedDistinctLevels >= 300` |
| `world_one_pioneer` | The Awakening Pioneer | WORLD | 20 | Complete all 20 levels in World 1: The Awakening. | Canonical levels 1..20 all completed |
| `world_two_explorer` | Binary Drift Explorer | WORLD | 30 | Complete all 30 levels in World 2: Binary Drift. | Canonical levels 21..50 all completed |
| `world_three_wall_breaker` | Wall Breaker | WORLD | 50 | Complete all 50 levels in World 3: Labyrinth of Walls. | Canonical levels 51..100 all completed |
| `world_four_navigator` | Warp Navigator | WORLD | 50 | Complete all 50 levels in World 4: Quantum Portal. | Canonical levels 101..150 all completed |
| `world_five_mastermind` | Temporal Mastermind | WORLD | 50 | Complete all 50 levels in World 5: Temporal Flux. | Canonical levels 151..200 all completed |
| `world_six_grandmaster` | Grandmaster of the Infinite | WORLD | 100 | Complete all 100 levels in World 6: The Singularity. | Canonical levels 201..300 all completed |
| `pure_intellect` | Pure Intellect | MASTERY | 1 | Solve any Solo level without using any hints. | Any level completed with `bestHintCount == 0` |
| `speed_demon` | Swift Footwork | MASTERY | 1 | Solve any Solo level in under 30 seconds. | Any level completed with `bestTimeMs in 1..30000` |
| `daily_first_dawn` | Day One | DAILY | 1 | Complete your first Daily Challenge. | `completedDailyChallenges >= 1` |
| `daily_three_streak` | Three-Day Devotion | DAILY | 3 | Maintain a 3-day Daily Challenge streak. | `dailyStreak >= 3` evaluated via UTC dates |
| `daily_seven_streak` | Weekly Dedication | DAILY | 7 | Maintain a 7-day Daily Challenge streak. | `dailyStreak >= 7` evaluated via UTC dates |
| `daily_server_validated` | Official Pathfinder | DAILY | 1 | Complete your first server-validated Daily Challenge. | Backend `verificationStatus == "VERIFIED"` |
| `daily_leaderboard_ranked` | On the Board | DAILY | 1 | Earn an official ranking on the Daily Leaderboard. | `isLeaderboardEligible == true` |
| `comp_quick_duel_complete` | Duelist Initiate | COMPETITIVE | 1 | Complete your first Quick Duel 1v1 match. | `quickDuelMatches >= 1` |
| `comp_quick_duel_win` | Quick Victor | COMPETITIVE | 1 | Win your first Quick Duel 1v1 match. | `quickDuelWins >= 1` |
| `comp_friend_duel_complete` | Friendly Rivalry | COMPETITIVE | 1 | Complete your first Friend Duel match. | `friendDuelMatches >= 1` |
| `comp_mini_league_participation` | League Contender | COMPETITIVE | 1 | Participate in your first Mini League room match. | `miniLeagueParticipations >= 1` |
| `comp_mini_league_win` | Crown of the Room | COMPETITIVE | 1 | Finish 1st place in a verified Mini League match. | `miniLeagueFirstPlaces >= 1` |

---

## 3. Evaluation & Idempotency Rules
1. **Distinct Level & Milestone Counting:** Replaying an already-completed level updates personal bests, but **never** increases the count of completed distinct levels.
2. **Canonical World Completion:** Evaluated strictly over the canonical boundaries (W1: 1–20, W2: 21–50, W3: 51–100, W4: 101–150, W5: 151–200, W6: 201–300).
3. **Daily Online Milestones:** Evaluated via authoritative Daily records. Untrusted offline or provisional completions never trigger `daily_server_validated`. Daily streaks are computed strictly using UTC calendar dates (`ZoneOffset.UTC`).
4. **Competitive Milestones:** Evaluated strictly via finalized server-validated statistics. Provisional in-match progress never unlocks competitive achievements.
5. **Transactional Unlock & Target Sync:**
   - Unlocks execute via SQL conditional update:
     ```sql
     UPDATE achievements 
     SET isUnlocked = 1, unlockedAt = :unlockedAt, currentProgress = targetProgress 
     WHERE achievementId = :id AND isUnlocked = 0
     ```
   - Target metrics sync dynamically on app initialization via `AchievementDao.updateTargetProgress`, preventing database discrepancies without destructive migrations.
6. **Gallery & Presentation:**
   - Supports interactive cards, TalkBack accessibility descriptions, category filters, and detail modals (`AchievementDetailDialog`).
