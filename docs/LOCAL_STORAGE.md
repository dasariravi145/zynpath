# Zynpath Local Storage Architecture

**Status:** Authoritative (Updated Prompt 13)  
**Database File:** `zynpath_local.db` (Room Database, Schema Version 3)  
**Preferences:** Jetpack DataStore Preferences (`zynpath_preferences`)  

---

## 1. Storage Boundaries & Offline-First Strategy

Local storage is divided into two distinct subsystems according to data access patterns and volatility:

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                 LOCAL STORAGE MATRIX                                   │
├──────────────────────────────┬──────────────────────────────┬──────────────────────────┤
│    DATASTORE PREFERENCES     │        ROOM DATABASE         │   SECURE TOKEN STORAGE   │
├──────────────────────────────┼──────────────────────────────┼──────────────────────────┤
│  - Key-Value typed settings  │  - Relational structured     │  - Android KeyStore AES  │
│  - Settings & Audio toggles  │  - Level progress & Stars    │  - AES/GCM/NoPadding     │
│  - Onboarding & Tutorial     │  - Lifetime statistics       │  - Encrypted credentials │
│  - Guest identity UUID       │  - Active/paused sessions    │  - Isolated private file │
│  - Selected language & theme │  - Daily challenge records   │  - Zero plain-text leaks │
│  - Last world & level        │  - Player profile & avatars  │  - Zero Logcat logging   │
│  - Reactive Flow observation │  - Achievements unlocked     │  - Session token & TTL   │
│  - Transactional writes      │  - Reactive Flow DAO queries │  - Server auth bearer    │
└──────────────────────────────┴──────────────────────────────┴──────────────────────────┘
```

Zero cloud network requests are initiated for Solo Play. No cloud database (e.g. Firestore) is used for offline gameplay.

---

## 2. Jetpack DataStore Preferences

All user settings are persisted asynchronously using AndroidX DataStore (`PreferencesDataStoreFactory`) with explicit default values and recoverable read error handling:

```kotlin
data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val isTutorialCompleted: Boolean = false,
    val guestUuid: String = "",
    val isSfxEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val themePreference: String = "FOREST_NAVY",
    val isReducedMotion: Boolean = false,
    val selectedLanguage: String = "en",
    val lastSelectedWorld: Int = 1,
    val lastSelectedLevel: Int = 1,
    val freeHintsRemaining: Int = 3,
    val isPremium: Boolean = false
)
```

### 2.1 Concurrency & Error Resilience
- Read operations catch `IOException` and emit `emptyPreferences()` to protect against read failures without wiping valid settings.
- Write operations execute in background coroutines on `Dispatchers.IO` via `dataStore.edit { ... }`.
- Singleton instance is provided via Hilt dependency injection (`@Singleton PreferencesRepository`).

---

## 3. Room Database Architecture (`zynpath_local.db`)

The database version is **2** and consists of four managed entity tables:

### 3.1 `level_progress` Table
Tracks progress across all 300 base levels (Worlds 1–6):
- `levelId` (`Int`, Primary Key): Discrete level identifier (1 to 300).
- `worldId` (`Int`, Indexed): World identifier (1 to 6).
- `stars` (`Int`): Highest stars earned (1, 2, or 3).
- `bestTimeMs` (`Long`): Fastest valid completion time in milliseconds.
- `movesCount` (`Int`): Step count required to solve on best run.
- `isCompleted` (`Boolean`): Completed status.
- `completedAt` (`Long`): Epoch timestamp of completion.
- `isUnlocked` (`Boolean`): Whether the level is unlocked for play (Level 1 unlocked by default).
- `bestHintCount` (`Int`): Lowest hint count used during a winning run.
- `completionCount` (`Int`): Total number of times this level was completed.
- `firstCompletedAt` (`Long`): Epoch timestamp of the very first completion.
- `lastCompletedAt` (`Long`): Epoch timestamp of the most recent completion.

### 3.2 `game_sessions` Table
Maintains active and paused session checkpoints without recording individual touch pointer events:
- `sessionId` (`String`, Primary Key): Unique session UUID.
- `puzzleId` (`String`): Identifier of the active puzzle.
- `levelId` (`Int`, Indexed): Target level.
- `puzzleVersion` (`Int`): Puzzle layout/format version.
- `startedAt` (`Long`): Session start timestamp.
- `lastUpdatedAt` (`Long`): Last checkpoint timestamp.
- `sessionStatus` (`String`): `ACTIVE`, `PAUSED`, or `ABANDONED`.
- `elapsedActiveTimeMs` (`Long`): Cumulative active solve time.
- `compactPathSnapshot` (`String?`): Serialized coordinates list of currently active path.

> [!IMPORTANT]
> An interrupted or abandoned session is **never** marked completed. Only authoritative validation by the puzzle engine can record a completion.

### 3.3 `player_stats` Table
Maintains lifetime player metrics:
- `id` (`Int`, Primary Key = 1): Singleton row.
- `totalLevelsCompleted` (`Int`)
- `totalStars` (`Int`)
- `currentStreakDays` (`Int`)
- `bestStreakDays` (`Int`)
- `totalSolveTimeMs` (`Long`)
- `lastPlayedTimestamp` (`Long`)

### 3.4 `daily_challenge` Table
Tracks daily challenge outcomes, attempts, and streaks (Prompt 16):
- `dateKey` (`String`, Primary Key): Canonical format `YYYY-MM-DD`.
- `challengeId` (`String`): Format `daily-YYYY-MM-DD-v1`.
- `challengeVersion` (`Int`): Challenge schema revision.
- `puzzleId` (`String`): Assigned puzzle ID.
- `puzzleVersion` (`Int`): Assigned puzzle version.
- `puzzleFingerprint` (`String`): SHA-256 canonical hash.
- `scheduleVersion` (`String`): Offline schedule mapping version.
- `seed` (`Long`): Puzzle seed.
- `gridSize` (`Int`): Grid dimension.
- `isCompleted` (`Boolean`): Completed flag.
- `solveTimeMs` (`Long`): Elapsed solve time.
- `completedAt` (`Long`): Timestamp.
- `attemptCount` (`Int`): Total initiated attempts.
- `bestTimeMs` (`Long`): Best validated completion time.
- `firstAttemptAt` (`Long`): First attempt timestamp.
- `lastAttemptAt` (`Long`): Latest attempt timestamp.
- `movesCount` (`Int`): Move count in completed solution.

### 3.5 `player_profile` Table
Tracks local player profile and guest identity (Prompt 17):
- `playerId` (`String`, Primary Key): Stable UUIDv4 generated once.
- `displayName` (`String`): User callsign (default: "Pathfinder").
- `avatarId` (`String`): Built-in avatar identifier.
- `createdAt` (`Long`): Profile creation timestamp.
- `lastActiveAt` (`Long`): Last active interaction timestamp.
- `accountType` (`String`): `GUEST`, `LINKING`, `LINKED`, or `LINK_FAILED`.
- `publicZynpathId` (`String?`): Backend-issued public tag (null for offline guest).

### 3.6 `achievements` Table
Tracks local milestone progress and idempotent unlocks (Prompt 17):
- `achievementId` (`String`, Primary Key): Authoritative achievement identifier.
- `unlockedAt` (`Long?`): Unlock timestamp (null if locked).
- `currentProgress` (`Int`): Current progress toward milestone.
- `targetProgress` (`Int`): Target value required for completion.
- `isUnlocked` (`Boolean`): Unlock boolean flag.

---

## 4. Migration Strategy (`MIGRATION_1_2` through `MIGRATION_4_5`)

Destructive migration fallback (`fallbackToDestructiveMigration`) is strictly prohibited. Schema evolution from version 1 through 5 is handled via explicit migration objects:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE level_progress ADD COLUMN isUnlocked INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE level_progress ADD COLUMN bestHintCount INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE level_progress ADD COLUMN completionCount INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE level_progress ADD COLUMN firstCompletedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE level_progress ADD COLUMN lastCompletedAt INTEGER NOT NULL DEFAULT 0")
        
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS game_sessions (
                sessionId TEXT NOT NULL PRIMARY KEY,
                puzzleId TEXT NOT NULL,
                levelId INTEGER NOT NULL,
                puzzleVersion INTEGER NOT NULL,
                startedAt INTEGER NOT NULL,
                lastUpdatedAt INTEGER NOT NULL,
                sessionStatus TEXT NOT NULL,
                elapsedActiveTimeMs INTEGER NOT NULL,
                compactPathSnapshot TEXT
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_game_sessions_levelId ON game_sessions(levelId)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE game_sessions ADD COLUMN puzzleId TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE game_sessions ADD COLUMN puzzleVersion INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE game_sessions ADD COLUMN catalogVersion TEXT NOT NULL DEFAULT '1.0.0'")
        db.execSQL("ALTER TABLE game_sessions ADD COLUMN revision INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE game_sessions ADD COLUMN snapshotSchemaVersion INTEGER NOT NULL DEFAULT 1")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN challengeId TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN challengeVersion INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN puzzleId TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN puzzleVersion INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN puzzleFingerprint TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN scheduleVersion TEXT NOT NULL DEFAULT '1.0.0'")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN attemptCount INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN bestTimeMs INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN firstAttemptAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN lastAttemptAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE daily_challenge ADD COLUMN movesCount INTEGER NOT NULL DEFAULT 0")
    }
}
```

---

## 5. Repository Contract & Business Rules

### 5.1 `ProgressRepository` Interface
Feature ViewModels interact exclusively with `ProgressRepository`, never directly with Room DAOs:
- `observeLevelProgress(levelId: Int): Flow<LevelProgressEntity>`
- `observeWorldProgress(worldId: Int): Flow<List<LevelProgressEntity>>`
- `observeAllProgress(): Flow<List<LevelProgressEntity>>`
- `observeCompletedLevelCount(): Flow<Int>`
- `observeTotalStarsEarned(): Flow<Int>`
- `getNextPlayableLevel(): Int`
- `recordValidatedCompletion(result: ValidatedCompletionResult)`
- `saveSessionSnapshot(...)`
- `getActiveSession(levelId: Int): GameSessionEntity?`
- `abandonActiveSession(sessionId: String)`
- `reconcileCloudProgress(cloudRecords: List<LevelProgressEntity>)`

### 5.2 Validated Completion Contract
```kotlin
data class ValidatedCompletionResult(
    val puzzleId: String,
    val levelId: Int,
    val worldId: Int,
    val isValidated: Boolean,
    val elapsedTimeMs: Long,
    val movesCount: Int,
    val hintsUsed: Int,
    val completedAtTimestamp: Long = System.currentTimeMillis()
)
```
- `isValidated` MUST be `true` (asserted with `require(result.isValidated)`).
- `elapsedTimeMs`, `movesCount`, and `hintsUsed` must be non-negative.
- The persistence layer does NOT decide whether a puzzle is solved; it only records pre-validated results.

### 5.3 Personal Best Preservation Rules
When a player completes a level repeatedly:
1. `completionCount` increments by 1.
2. `firstCompletedAt` is preserved permanently from the original solve.
3. `lastCompletedAt` updates to the timestamp of the new solve.
4. `bestTimeMs` retains `minOf(existingBest, newTime)` (preserving the faster time).
5. `movesCount` retains `minOf(existingBest, newMoves)`.
6. `bestHintCount` retains `minOf(existingBest, newHints)`.
7. `stars` retains `maxOf(existingBest, newStars)` (never degrades).

### 5.4 Star Rating Policy (`StarRatingPolicy`)
- 0 Hints Used: **3 Stars**
- 1 Hint Used: **2 Stars**
- 2+ Hints Used: **1 Star**

### 5.5 Hint Usage Persistence (`HintUsageRepository`)
- Solo free hint balance (`freeHintsRemaining`) is stored in DataStore Preferences (`zynpath_preferences`).
- Premium entitlement status (`isPremium`) is read from verified local state.
- Deductions occur atomically only after a useful, verified `NEXT_MOVE` or `RECOVERY_REQUIRED` hint is successfully presented.
- Balances survive app restarts and session updates.
- Inconclusive searches, cancellations, and invalid states do not consume allowance.

---

## 6. What is Explicitly Excluded from Storage

1. **NO Temporary Reactions**: Predefined in-match emojis/phrases are strictly ephemeral in memory. No SQLite tables, DAOs, or DataStore keys exist for reactions.
2. **NO Pointer / Drag Coordinates**: Individual touch offsets and drag events are not persisted in Room.
3. **NO Production Cloud Databases**: Ordinary offline play uses zero network bandwidth and incurs ₹0 / $0 cloud costs.
4. **NO Pending Solver Tasks**: Background solver jobs are not persisted across app restarts or serialized to disk.
