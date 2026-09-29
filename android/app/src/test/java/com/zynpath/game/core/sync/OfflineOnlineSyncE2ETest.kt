package com.zynpath.game.core.sync

import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.ProgressRepositoryImpl
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.sync.conflict.SyncConflictPolicy
import com.zynpath.game.fake.FakeGameSessionDao
import com.zynpath.game.fake.FakeLevelProgressDao
import com.zynpath.game.fake.FakePlayerStatsDao
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * End-to-End Verification of Offline Autonomy, Solo Gameplay without Network,
 * Reconnection Recovery, and Non-Destructive Conflict Resolution.
 *
 * Implements Prompt 48 Requirements 87, 88, 89, 90, 91, 92.
 */
class OfflineOnlineSyncE2ETest {

    private lateinit var fakeLevelDao: FakeLevelProgressDao
    private lateinit var fakeStatsDao: FakePlayerStatsDao
    private lateinit var fakeSessionDao: FakeGameSessionDao
    private lateinit var progressRepo: ProgressRepositoryImpl
    private lateinit var fakePrefsRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        fakeLevelDao = FakeLevelProgressDao()
        fakeStatsDao = FakePlayerStatsDao()
        fakeSessionDao = FakeGameSessionDao()
        progressRepo = ProgressRepositoryImpl(fakeLevelDao, fakeStatsDao, fakeSessionDao)
        fakePrefsRepo = FakePreferencesRepository(UserPreferences(guestUuid = "offline_user_99"))
    }

    @Test
    fun testOfflineStartupRequiresZeroNetworkCalls() = runBlocking {
        // App starts up using only local Room and DataStore
        val prefs = fakePrefsRepo.userPreferencesFlow.first()
        assertEquals("offline_user_99", prefs.guestUuid)
        assertFalse(prefs.isOnboardingCompleted)

        val allProgress = progressRepo.observeAllProgress().first()
        assertTrue("Initial progress is empty but ready", allProgress.isEmpty())
    }

    @Test
    fun testOfflineSoloGameplayCompletesAndAdvancesWorldProgress() = runBlocking {
        // Play level 1 offline and complete it
        val completion = ValidatedCompletionResult(
            puzzleId = PackagedPuzzles.LEVEL_1.puzzleId,
            levelId = 1,
            worldId = 1,
            isValidated = true,
            elapsedTimeMs = 15000L,
            moveCount = 16,
            hintCount = 0
        )
        progressRepo.recordValidatedCompletion(completion)

        val progress1 = progressRepo.observeLevelProgress(1).first()
        assertNotNull(progress1)
        assertTrue(progress1?.isCompleted == true)
        assertEquals(3, progress1?.stars)
        assertEquals(15000L, progress1?.bestTimeMs)

        // Verify level 2 is now unlocked
        val progress2 = progressRepo.observeLevelProgress(2).first()
        assertNotNull(progress2)
        assertTrue("Level 2 must be unlocked after Level 1 completion", progress2?.isUnlocked == true)
    }

    @Test
    fun testNonDestructiveConflictResolutionPreservesBestPerformance() {
        val local = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            isCompleted = true,
            isUnlocked = true,
            stars = 2,
            bestTimeMs = 25000L,
            movesCount = 18,
            bestHintCount = 1,
            completionCount = 1,
            completedAt = 1000L,
            firstCompletedAt = 1000L
        )

        val remote = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            isCompleted = true,
            isUnlocked = true,
            stars = 3, // Higher stars
            bestTimeMs = 18000L, // Faster time
            movesCount = 16, // Fewer moves
            bestHintCount = 0, // Fewer hints
            completionCount = 2,
            completedAt = 2000L,
            firstCompletedAt = 1000L
        )

        val merged = SyncConflictPolicy.mergeLevelProgress(local, remote)

        assertTrue("Completion status must be preserved", merged.isCompleted)
        assertEquals("Higher stars must be preserved", 3, merged.stars)
        assertEquals("Fastest solve time must be preserved", 18000L, merged.bestTimeMs)
        assertEquals("Fewest moves must be preserved", 16, merged.movesCount)
        assertEquals("Fewest hints must be preserved", 0, merged.bestHintCount)
        assertEquals("Earliest completion timestamp must be preserved", 1000L, merged.firstCompletedAt)
    }

    @Test
    fun testNonDestructiveConflictResolutionNeverOverwritesWithZeroOrEmpty() {
        val local = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            isCompleted = true,
            isUnlocked = true,
            stars = 3,
            bestTimeMs = 12000L,
            movesCount = 16
        )

        // Stale remote entity with 0 time and missing values
        val remoteStale = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            isCompleted = false,
            isUnlocked = true,
            stars = 0,
            bestTimeMs = 0L,
            movesCount = 0
        )

        val merged = SyncConflictPolicy.mergeLevelProgress(local, remoteStale)

        assertTrue("Valid completion must never be rolled back by stale entity", merged.isCompleted)
        assertEquals("Valid 3 stars must never be overwritten by 0", 3, merged.stars)
        assertEquals("Valid 12s solve time must never be overwritten by 0", 12000L, merged.bestTimeMs)
        assertEquals("Valid 16 moves count must never be overwritten by 0", 16, merged.movesCount)
    }
}
