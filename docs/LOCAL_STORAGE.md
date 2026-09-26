# Zynpath Local Storage Architecture

**Status:** Authoritative  
**Engines:** Jetpack DataStore Preferences & Room Database (SQLite)  

---

## 1. Storage Boundaries

Local storage is divided into two distinct subsystems according to data access patterns and volatility:

```
┌─────────────────────────────────────────────────────────────┐
│                    LOCAL STORAGE MATRIX                     │
├──────────────────────────────┬──────────────────────────────┤
│    DATASTORE PREFERENCES     │        ROOM DATABASE         │
│  - Key-Value unstructured    │  - Relational structured     │
│  - Settings & Toggles        │  - Level progress & Stars    │
│  - Onboarding state          │  - Lifetime statistics       │
│  - Guest identity UUID       │  - Daily challenge records   │
│  - Reactive Flow updates     │  - Reactive Flow DAO queries │
└──────────────────────────────┴──────────────────────────────┘
```

---

## 2. Jetpack DataStore Preferences

All user settings are persisted asynchronously using AndroidX DataStore (`PreferencesDataStoreFactory`):

```kotlin
data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val guestUuid: String = "",
    val isSfxEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val themePreference: String = "FOREST_NAVY",
    val isReducedMotion: Boolean = false,
    val freeHintsRemaining: Int = 3,
    val isPremium: Boolean = false
)
```

### 2.1 Concurrency & Error Resilience
- Read operations utilize `catch { exception -> if (exception is IOException) emit(emptyPreferences()) else throw exception }`.
- Write operations execute in background coroutines on `Dispatchers.IO` via `dataStore.edit { ... }`.

---

## 3. Room Database Schema (`zynpath_database.db`)

The database consists of three core tables:

### 3.1 `level_progress` Table
Tracks progress across all 300 base levels:
- `levelId` (`Int`, Primary Key): Discrete level identifier (1 to 300).
- `worldId` (`Int`): World identifier (1 to 6).
- `stars` (`Int`): Stars earned (1, 2, or 3).
- `bestTimeMs` (`Long`): Fastest successful completion time in milliseconds.
- `movesCount` (`Int`): Step count required to solve.
- `isCompleted` (`Boolean`): Completion status.
- `completedAt` (`Long`): Epoch timestamp of completion.

### 3.2 `player_stats` Table
Maintains lifetime player metrics:
- `id` (`Int`, Primary Key = 1): Singleton row.
- `totalLevelsCompleted` (`Int`)
- `totalStars` (`Int`)
- `currentStreakDays` (`Int`)
- `bestStreakDays` (`Int`)
- `totalSolveTimeMs` (`Long`)
- `lastPlayedTimestamp` (`Long`)

### 3.3 `daily_challenge` Table
Tracks daily challenge outcomes:
- `dateKey` (`String`, Primary Key): Format `YYYY-MM-DD`.
- `seed` (`Long`): Deterministic puzzle seed.
- `gridSize` (`Int`): Grid dimension.
- `isCompleted` (`Boolean`): Completed flag.
- `solveTimeMs` (`Long`): Elapsed solve time.
- `completedAt` (`Long`): Timestamp.

---

## 4. What is Explicitly Excluded from Storage

1. **NO Temporary Reactions**: In-match reactions are strictly ephemeral in server RAM and client memory. No SQLite tables or SharedPreferences keys are allocated for reactions.
2. **NO Raw Touch / Path Coordinates**: Only the final completion time and star score are recorded. Individual finger drag offsets are discarded immediately after puzzle validation.
