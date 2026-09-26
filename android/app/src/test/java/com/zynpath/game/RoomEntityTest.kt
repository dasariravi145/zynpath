package com.zynpath.game

import com.zynpath.game.core.database.entity.DailyChallengeEntity
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
            completedAt = 1758850000L
        )

        assertEquals(1, entity.levelId)
        assertEquals(1, entity.worldId)
        assertEquals(3, entity.stars)
        assertEquals(12450L, entity.bestTimeMs)
        assertEquals(16, entity.movesCount)
        assertTrue(entity.isCompleted)
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
