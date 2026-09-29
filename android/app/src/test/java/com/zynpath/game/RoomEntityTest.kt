package com.zynpath.game

import com.zynpath.game.core.database.entity.DailyChallengeEntity
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.entity.PlayerStatsEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomEntityTest {

    @Test
    fun levelProgressEntity_instantiatesCorrectly() {
        val entity = LevelProgressEntity(
            levelId = 1,
            worldId = 1,
            stars = 3,
            bestTimeMs = 12450L,
            movesCount = 16,
            isCompleted = true,
            completedAt = 1758850000L,
            isUnlocked = true,
            bestHintCount = 0,
            completionCount = 1,
            firstCompletedAt = 1758850000L,
            lastCompletedAt = 1758850000L
        )

        assertEquals(1, entity.levelId)
        assertEquals(1, entity.worldId)
        assertEquals(3, entity.stars)
        assertEquals(12450L, entity.bestTimeMs)
        assertEquals(16, entity.movesCount)
        assertTrue(entity.isCompleted)
        assertTrue(entity.isUnlocked)
        assertEquals(0, entity.bestHintCount)
        assertEquals(1, entity.completionCount)
        assertEquals(1758850000L, entity.firstCompletedAt)
        assertEquals(1758850000L, entity.lastCompletedAt)
    }

    @Test
    fun gameSessionEntity_instantiatesCorrectly() {
        val session = GameSessionEntity(
            sessionId = "sess_test_123",
            levelId = 1,
            worldId = 1,
            puzzleSeed = 1001L,
            startedAt = 1000L,
            lastUpdatedAt = 2000L,
            elapsedActiveTimeMs = 15000L,
            status = "ACTIVE",
            pathSnapshot = "0,0;0,1;0,2",
            moveCount = 3,
            hintCount = 0,
            puzzleId = "puzzle_1_1",
            puzzleVersion = 1,
            catalogVersion = "1.0.0",
            revision = 5L,
            snapshotSchemaVersion = 1
        )

        assertEquals("sess_test_123", session.sessionId)
        assertEquals(1, session.levelId)
        assertEquals(1, session.worldId)
        assertEquals(1001L, session.puzzleSeed)
        assertEquals("ACTIVE", session.status)
        assertEquals(15000L, session.elapsedActiveTimeMs)
        assertEquals("0,0;0,1;0,2", session.pathSnapshot)
        assertEquals(3, session.moveCount)
        assertEquals(0, session.hintCount)
        assertEquals("puzzle_1_1", session.puzzleId)
        assertEquals(1, session.puzzleVersion)
        assertEquals("1.0.0", session.catalogVersion)
        assertEquals(5L, session.revision)
        assertEquals(1, session.snapshotSchemaVersion)
    }

    @Test
    fun playerStatsEntity_defaultsAreAccurate() {
        val stats = PlayerStatsEntity()

        assertEquals(1, stats.id)
        assertEquals(0, stats.totalLevelsCompleted)
        assertEquals(0, stats.totalStars)
        assertEquals(0, stats.currentStreakDays)
    }

    @Test
    fun dailyChallengeEntity_instantiatesCorrectly() {
        val daily = DailyChallengeEntity(
            dateKey = "2026-09-26",
            seed = 884129L,
            gridSize = 5,
            isCompleted = false
        )

        assertEquals("2026-09-26", daily.dateKey)
        assertEquals(884129L, daily.seed)
        assertEquals(5, daily.gridSize)
        assertFalse(daily.isCompleted)
    }
}
