package com.zynpath.game.core.sync

import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus
import com.zynpath.game.core.sync.conflict.SyncConflictPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying conflict resolution policies and PB preservation.
 *
 * Implements Prompt 35 Sections 20, 21, 22, 28, 29, 30 & 51.
 */
class SyncConflictPolicyTest {

    @Test
    fun `mergeLevelProgress with null local returns remote record intact`() {
        val remote = LevelProgressEntity(
            levelId = 5,
            worldId = 1,
            stars = 3,
            bestTimeMs = 4500L,
            movesCount = 18,
            isCompleted = true,
            bestHintCount = 0,
            completedAt = 1000L
        )

        val merged = SyncConflictPolicy.mergeLevelProgress(null, remote)
        assertEquals(remote, merged)
    }

    @Test
    fun `mergeLevelProgress preserves completed state even if remote is incomplete`() {
        val local = LevelProgressEntity(
            levelId = 10,
            worldId = 1,
            stars = 2,
            bestTimeMs = 6000L,
            movesCount = 20,
            isCompleted = true,
            bestHintCount = 1,
            completedAt = 2000L
        )
        val remote = LevelProgressEntity(
            levelId = 10,
            worldId = 1,
            stars = 0,
            bestTimeMs = 0L,
            movesCount = 0,
            isCompleted = false,
            bestHintCount = 0,
            completedAt = 0L
        )

        val merged = SyncConflictPolicy.mergeLevelProgress(local, remote)
        assertTrue("Completion must never be rolled back", merged.isCompleted)
        assertEquals(2, merged.stars)
        assertEquals(6000L, merged.bestTimeMs)
        assertEquals(20, merged.movesCount)
    }

    @Test
    fun `mergeLevelProgress preserves faster valid positive solve time`() {
        val localFaster = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            stars = 3,
            bestTimeMs = 3200L,
            movesCount = 15,
            isCompleted = true
        )
        val remoteSlower = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            stars = 3,
            bestTimeMs = 4800L,
            movesCount = 17,
            isCompleted = true
        )

        val merged1 = SyncConflictPolicy.mergeLevelProgress(localFaster, remoteSlower)
        assertEquals(3200L, merged1.bestTimeMs)
        assertEquals(15, merged1.movesCount)

        val remoteFaster = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            stars = 3,
            bestTimeMs = 2900L,
            movesCount = 14,
            isCompleted = true
        )
        val merged2 = SyncConflictPolicy.mergeLevelProgress(localFaster, remoteFaster)
        assertEquals(2900L, merged2.bestTimeMs)
        assertEquals(14, merged2.movesCount)
    }

    @Test
    fun `mergeLevelProgress rejects zero or negative times overwriting valid times`() {
        val local = LevelProgressEntity(
            levelId = 2,
            worldId = 1,
            stars = 3,
            bestTimeMs = 5000L,
            movesCount = 16,
            isCompleted = true
        )
        val remoteZeroTime = LevelProgressEntity(
            levelId = 2,
            worldId = 1,
            stars = 3,
            bestTimeMs = 0L,
            movesCount = 0,
            isCompleted = true
        )

        val merged = SyncConflictPolicy.mergeLevelProgress(local, remoteZeroTime)
        assertEquals("Valid time must not be overwritten by 0", 5000L, merged.bestTimeMs)
        assertEquals("Valid moves must not be overwritten by 0", 16, merged.movesCount)
    }

    @Test
    fun `mergeLevelProgress maxes stars and preserves earliest completion timestamp`() {
        val local = LevelProgressEntity(
            levelId = 3,
            worldId = 1,
            stars = 1,
            bestTimeMs = 7000L,
            movesCount = 25,
            isCompleted = true,
            firstCompletedAt = 1000L,
            completedAt = 1000L
        )
        val remote = LevelProgressEntity(
            levelId = 3,
            worldId = 1,
            stars = 3,
            bestTimeMs = 5200L,
            movesCount = 20,
            isCompleted = true,
            firstCompletedAt = 1500L,
            completedAt = 1500L
        )

        val merged = SyncConflictPolicy.mergeLevelProgress(local, remote)
        assertEquals(3, merged.stars)
        assertEquals(1000L, merged.firstCompletedAt)
    }

    @Test
    fun `daily verification reconciliation preserves local completion`() {
        val reconciled = SyncConflictPolicy.reconcileDailyVerification(
            currentLocalStatus = DailyVerificationStatus.LOCAL_COMPLETION,
            serverReportedStatus = DailyVerificationStatus.LEADERBOARD_ELIGIBLE
        )
        assertEquals(DailyVerificationStatus.LEADERBOARD_ELIGIBLE, reconciled)

        val rejected = SyncConflictPolicy.reconcileDailyVerification(
            currentLocalStatus = DailyVerificationStatus.LOCAL_COMPLETION,
            serverReportedStatus = DailyVerificationStatus.NOT_ELIGIBLE
        )
        assertEquals(DailyVerificationStatus.NOT_ELIGIBLE, rejected)
    }

    @Test
    fun `isBetterSolveTime logic correctly evaluates times`() {
        assertTrue(SyncConflictPolicy.isBetterSolveTime(existingTimeMs = 5000L, newTimeMs = 4000L))
        assertFalse(SyncConflictPolicy.isBetterSolveTime(existingTimeMs = 5000L, newTimeMs = 6000L))
        assertFalse(SyncConflictPolicy.isBetterSolveTime(existingTimeMs = 5000L, newTimeMs = 0L))
        assertFalse(SyncConflictPolicy.isBetterSolveTime(existingTimeMs = 5000L, newTimeMs = -100L))
        assertTrue(SyncConflictPolicy.isBetterSolveTime(existingTimeMs = 0L, newTimeMs = 4000L))
    }
}
