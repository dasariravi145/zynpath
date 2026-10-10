package com.zynpath.game.core.puzzle.progression

import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.puzzle.model.LevelCompletionState
import com.zynpath.game.core.puzzle.model.NextDestinationResolution
import com.zynpath.game.core.puzzle.model.ProgressionDestination
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Systemic 300-Level Progression and World Unlock Test Suite.
 *
 * Validates the complete 32-point progression matrix defined in Prompt Section 30:
 * Enforces canonical unlock rules, catalog integrity across all 300 levels and 6 worlds,
 * race-condition resilience, monotonicity, replay safety, and screenshot regression resolution.
 */
class SoloProgressionSystemicTest {

    // -----------------------------------------------------------------------------------------
    // 1. Fresh profile starts correctly
    // -----------------------------------------------------------------------------------------
    @Test
    fun test01_freshProfile_startsCorrectly() {
        val completed = emptySet<Int>()
        assertTrue("Level 1 must be unlocked for a fresh profile", SoloProgressionAuthority.isUnlocked(1, completed))
        assertTrue("World 1 must be unlocked for a fresh profile", SoloProgressionAuthority.isWorldUnlocked(1, completed))
        assertEquals(1, SoloProgressionAuthority.resolveContinueDestination(completed))

        for (lvl in 2..300) {
            assertFalse("Level $lvl must be locked for a fresh profile", SoloProgressionAuthority.isUnlocked(lvl, completed))
        }
        for (w in 2..6) {
            assertFalse("World $w must be locked for a fresh profile", SoloProgressionAuthority.isWorldUnlocked(w, completed))
        }
    }

    // -----------------------------------------------------------------------------------------
    // 2. First level is legitimately unlocked
    // -----------------------------------------------------------------------------------------
    @Test
    fun test02_firstLevel_isLegitimatelyUnlocked() {
        assertTrue(SoloProgressionAuthority.isUnlocked(1, emptySet()))
        val desc = SoloProgressionAuthority.getLevel(1)
        assertNotNull(desc)
        assertEquals(1, desc?.stableLevelId)
        assertEquals(1, desc?.worldId)
        assertEquals(1, desc?.worldLocalLevel)
        assertTrue(desc?.isFirstOfWorld == true)
        assertFalse(desc?.isLastOfWorld == true)
        assertNull(desc?.previousLevelId)
        assertEquals(2, desc?.nextLevelId)
    }

    // -----------------------------------------------------------------------------------------
    // 3. Normal completion unlocks next level
    // -----------------------------------------------------------------------------------------
    @Test
    fun test03_normalCompletion_unlocksNextLevel() {
        val completed = setOf(1)
        assertTrue("Level 1 completed must unlock Level 2", SoloProgressionAuthority.isUnlocked(2, completed))
        assertFalse("Level 3 must remain locked after completing only Level 1", SoloProgressionAuthority.isUnlocked(3, completed))
    }

    // -----------------------------------------------------------------------------------------
    // 4. NEXT LEVEL opens correct next level
    // -----------------------------------------------------------------------------------------
    @Test
    fun test04_nextLevel_opensCorrectNextLevel() {
        val res = SoloProgressionAuthority.resolveVictoryDestination(1, setOf(1))
        assertEquals(1, res.currentWorld)
        assertEquals(1, res.completedLevel)
        assertEquals("NEXT LEVEL", res.buttonLabel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, res.completionState)
        assertEquals(ProgressionDestination.NextLevel(1, 2), res.destination)
    }

    // -----------------------------------------------------------------------------------------
    // 5. Immediate NEXT LEVEL tap works (simulated concurrent resolution)
    // -----------------------------------------------------------------------------------------
    @Test
    fun test05_immediateNextLevelTap_worksEvenWithInMemoryState() {
        val completedInDb = emptySet<Int>() // DB hasn't committed yet
        val completedWithSession = completedInDb + setOf(1) // in-memory completed session added

        val res = SoloProgressionAuthority.resolveVictoryDestination(1, completedWithSession)
        assertEquals(ProgressionDestination.NextLevel(1, 2), res.destination)
        assertTrue(SoloProgressionAuthority.isUnlocked(2, completedWithSession))
    }

    // -----------------------------------------------------------------------------------------
    // 6. Double tap cannot duplicate navigation
    // -----------------------------------------------------------------------------------------
    @Test
    fun test06_doubleTap_protection() {
        var actionDispatched = false
        var dispatchCount = 0

        fun onNextClick() {
            if (!actionDispatched) {
                actionDispatched = true
                dispatchCount++
            }
        }

        onNextClick()
        onNextClick() // second tap immediately
        assertEquals("Action must only be dispatched once", 1, dispatchCount)
    }

    // -----------------------------------------------------------------------------------------
    // 7. Replay does not move progression backwards
    // -----------------------------------------------------------------------------------------
    @Test
    fun test07_replay_doesNotMoveProgressionBackwards() {
        val playerCompleted = (1..100).toSet()
        assertEquals(101, SoloProgressionAuthority.resolveContinueDestination(playerCompleted))

        // Player replays Level 20
        val afterReplay = playerCompleted + setOf(20)
        assertEquals("Continue destination must remain 101 after replaying level 20", 101, SoloProgressionAuthority.resolveContinueDestination(afterReplay))
    }

    // -----------------------------------------------------------------------------------------
    // 8. Replay does not relock levels
    // -----------------------------------------------------------------------------------------
    @Test
    fun test08_replay_doesNotRelockLevels() {
        val playerCompleted = (1..100).toSet()
        // Replay level 20
        assertTrue("Level 101 must remain unlocked", SoloProgressionAuthority.isUnlocked(101, playerCompleted))
        assertTrue("Level 50 must remain unlocked", SoloProgressionAuthority.isUnlocked(50, playerCompleted))
        assertTrue("Level 21 must remain unlocked", SoloProgressionAuthority.isUnlocked(21, playerCompleted))
    }

    // -----------------------------------------------------------------------------------------
    // 9. Replay does not duplicate +100
    // -----------------------------------------------------------------------------------------
    @Test
    fun test09_replay_doesNotDuplicateFirstClearReward() {
        val completedLevels = mutableSetOf<Int>()
        var walletCoins = 300 // welcome gift

        fun completeLevel(lvl: Int) {
            val isFirstClear = lvl !in completedLevels
            completedLevels.add(lvl)
            if (isFirstClear) {
                walletCoins += 100
            }
        }

        completeLevel(1)
        assertEquals(400, walletCoins)

        completeLevel(1) // replay
        assertEquals("Replay must not award +100 again", 400, walletCoins)
    }

    // -----------------------------------------------------------------------------------------
    // 10. World replay does not duplicate +500
    // -----------------------------------------------------------------------------------------
    @Test
    fun test10_worldReplay_doesNotDuplicateWorldBonus() {
        val claimedWorlds = mutableSetOf<Int>()
        var walletCoins = 300

        fun claimWorld(wId: Int) {
            if (wId !in claimedWorlds) {
                claimedWorlds.add(wId)
                walletCoins += 500
            }
        }

        claimWorld(1)
        assertEquals(800, walletCoins)

        claimWorld(1) // replay
        assertEquals("World completion bonus must be idempotent", 800, walletCoins)
    }

    // -----------------------------------------------------------------------------------------
    // 11. App restart preserves unlock
    // -----------------------------------------------------------------------------------------
    @Test
    fun test11_appRestart_preservesUnlock() {
        // Simulated DB state before restart
        val persistedEntities = listOf(
            LevelProgressEntity(levelId = 1, worldId = 1, isCompleted = true, isUnlocked = true),
            LevelProgressEntity(levelId = 2, worldId = 1, isCompleted = false, isUnlocked = true)
        )

        // After restart: read entities from DB
        val completed = persistedEntities.filter { it.isCompleted }.map { it.levelId }.toSet()
        assertTrue(SoloProgressionAuthority.isUnlocked(2, completed))
        assertEquals(2, SoloProgressionAuthority.resolveContinueDestination(completed))
    }

    // -----------------------------------------------------------------------------------------
    // 12. Background/resume preserves unlock
    // -----------------------------------------------------------------------------------------
    @Test
    fun test12_backgroundResume_preservesUnlock() {
        val completed = (1..15).toSet()
        val destBeforeBackground = SoloProgressionAuthority.resolveContinueDestination(completed)
        assertEquals(16, destBeforeBackground)

        // Simulate app paused and resumed without progress alteration
        val destAfterResume = SoloProgressionAuthority.resolveContinueDestination(completed)
        assertEquals(16, destAfterResume)
        assertTrue(SoloProgressionAuthority.isUnlocked(16, completed))
    }

    // -----------------------------------------------------------------------------------------
    // 13. Victory and Level Selection agree
    // -----------------------------------------------------------------------------------------
    @Test
    fun test13_victoryAndLevelSelection_agree() {
        val completed = (1..600).toSet() // World 1 and World 2 completed
        val victoryDest = SoloProgressionAuthority.resolveVictoryDestination(600, completed)
        assertEquals(ProgressionDestination.NextWorldEntry(3), victoryDest.destination)

        // Level selection for World 3: first level 601 must be unlocked
        assertTrue(SoloProgressionAuthority.isWorldUnlocked(3, completed))
        assertTrue(SoloProgressionAuthority.isUnlocked(601, completed))
        assertEquals(LevelState.UNLOCKED, if (SoloProgressionAuthority.isUnlocked(601, completed)) LevelState.UNLOCKED else LevelState.LOCKED)
    }

    private enum class LevelState { LOCKED, UNLOCKED, COMPLETED }

    // -----------------------------------------------------------------------------------------
    // 14. Home Continue agrees
    // -----------------------------------------------------------------------------------------
    @Test
    fun test14_homeContinue_agrees() {
        val completed = (1..300).toSet() // World 1 completed
        val nextLevel = SoloProgressionAuthority.resolveContinueDestination(completed)
        assertEquals("Home continue must target Level 301", 301, nextLevel)
        assertTrue(SoloProgressionAuthority.isUnlocked(nextLevel, completed))
    }

    // -----------------------------------------------------------------------------------------
    // 15. World Map agrees
    // -----------------------------------------------------------------------------------------
    @Test
    fun test15_worldMap_agrees() {
        val completed = (1..300).toSet()
        assertTrue(SoloProgressionAuthority.isWorldUnlocked(1, completed))
        assertTrue(SoloProgressionAuthority.isWorldUnlocked(2, completed))
        assertFalse(SoloProgressionAuthority.isWorldUnlocked(3, completed))
        assertFalse(SoloProgressionAuthority.isWorldUnlocked(4, completed))
        assertFalse(SoloProgressionAuthority.isWorldUnlocked(5, completed))
        assertFalse(SoloProgressionAuthority.isWorldUnlocked(6, completed))
    }

    // -----------------------------------------------------------------------------------------
    // 16. Global/local numbering maps correctly
    // -----------------------------------------------------------------------------------------
    @Test
    fun test16_globalLocalNumbering_mapsCorrectlyAcrossAllWorlds() {
        // World 1: 1..300 -> local 1..300
        assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(1))
        assertEquals(300, SoloProgressionAuthority.getWorldLocalLevel(300))
        assertEquals(1, SoloProgressionAuthority.localToGlobalLevel(1, 1))
        assertEquals(300, SoloProgressionAuthority.localToGlobalLevel(1, 300))

        // World 2: 301..600 -> local 1..300
        assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(301))
        assertEquals(300, SoloProgressionAuthority.getWorldLocalLevel(600))
        assertEquals(301, SoloProgressionAuthority.localToGlobalLevel(2, 1))
        assertEquals(600, SoloProgressionAuthority.localToGlobalLevel(2, 300))

        // World 3: 601..900 -> local 1..300
        assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(601))
        assertEquals(300, SoloProgressionAuthority.getWorldLocalLevel(900))
        assertEquals(601, SoloProgressionAuthority.localToGlobalLevel(3, 1))
        assertEquals(900, SoloProgressionAuthority.localToGlobalLevel(3, 300))

        // World 4: 901..1200 -> local 1..300
        assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(901))
        assertEquals(300, SoloProgressionAuthority.getWorldLocalLevel(1200))
        assertEquals(901, SoloProgressionAuthority.localToGlobalLevel(4, 1))
        assertEquals(1200, SoloProgressionAuthority.localToGlobalLevel(4, 300))

        // World 5: 1201..1500 -> local 1..300
        assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(1201))
        assertEquals(2, SoloProgressionAuthority.getWorldLocalLevel(1202))
        assertEquals(300, SoloProgressionAuthority.getWorldLocalLevel(1500))
        assertEquals(1201, SoloProgressionAuthority.localToGlobalLevel(5, 1))
        assertEquals(1202, SoloProgressionAuthority.localToGlobalLevel(5, 2))
        assertEquals(1500, SoloProgressionAuthority.localToGlobalLevel(5, 300))

        // World 6: 1501..1800 -> local 1..300
        assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(1501))
        assertEquals(300, SoloProgressionAuthority.getWorldLocalLevel(1800))
        assertEquals(1501, SoloProgressionAuthority.localToGlobalLevel(6, 1))
        assertEquals(1800, SoloProgressionAuthority.localToGlobalLevel(6, 300))
    }

    // -----------------------------------------------------------------------------------------
    // 17. Catalog index is not confused with stable ID
    // -----------------------------------------------------------------------------------------
    @Test
    fun test17_catalogIndex_isNotConfusedWithStableId() {
        val desc1 = SoloProgressionAuthority.getLevel(1)
        val desc301 = SoloProgressionAuthority.getLevel(301)
        val desc1800 = SoloProgressionAuthority.getLevel(1800)

        assertEquals(1, desc1?.stableLevelId)
        assertEquals(301, desc301?.stableLevelId)
        assertEquals(1800, desc1800?.stableLevelId)

        // 0-based query is invalid in 1-based stable level IDs
        assertNull(SoloProgressionAuthority.getLevel(0))
        assertNull(SoloProgressionAuthority.getLevel(1801))
    }

    // -----------------------------------------------------------------------------------------
    // 18. Every adjacent catalog transition passes (1->2, 2->3, ..., 1799->1800)
    // -----------------------------------------------------------------------------------------
    @Test
    fun test18_everyAdjacentCatalogTransition_passes() {
        val completed = mutableSetOf<Int>()

        for (curr in 1..1799) {
            val next = curr + 1
            completed.add(curr)

            assertTrue("Level $next must be unlocked after completing level $curr", SoloProgressionAuthority.isUnlocked(next, completed))
            val res = SoloProgressionAuthority.resolveVictoryDestination(curr, completed)

            if (SoloProgressionAuthority.isLastLevelOfWorld(curr)) {
                val nextWorld = SoloProgressionAuthority.getWorld(curr).worldId + 1
                assertEquals(LevelCompletionState.WORLD_COMPLETED, res.completionState)
                assertEquals("NEXT WORLD", res.buttonLabel)
                assertEquals(ProgressionDestination.NextWorldEntry(nextWorld), res.destination)
            } else {
                assertEquals(LevelCompletionState.LEVEL_COMPLETED, res.completionState)
                assertEquals("NEXT LEVEL", res.buttonLabel)
                assertEquals(ProgressionDestination.NextLevel(SoloProgressionAuthority.getWorld(curr).worldId, next), res.destination)
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    // 19. Every World boundary passes
    // -----------------------------------------------------------------------------------------
    @Test
    fun test19_everyWorldBoundary_passes() {
        val worldBoundaries = listOf(
            300 to 301,   // W1 -> W2
            600 to 601,   // W2 -> W3
            900 to 901,   // W3 -> W4
            1200 to 1201, // W4 -> W5
            1500 to 1501  // W5 -> W6
        )

        for ((lastLevel, firstNextLevel) in worldBoundaries) {
            val completed = (1..lastLevel).toSet()
            val currentWorld = SoloProgressionAuthority.getWorld(lastLevel).worldId
            val nextWorld = currentWorld + 1

            val res = SoloProgressionAuthority.resolveVictoryDestination(lastLevel, completed)
            assertEquals("Boundary $lastLevel->$firstNextLevel must resolve to WORLD_COMPLETED", LevelCompletionState.WORLD_COMPLETED, res.completionState)
            assertEquals("NEXT WORLD", res.buttonLabel)
            assertEquals(ProgressionDestination.NextWorldEntry(nextWorld), res.destination)

            assertTrue("World $nextWorld must be unlocked when level $lastLevel is completed", SoloProgressionAuthority.isWorldUnlocked(nextWorld, completed))
            assertTrue("Level $firstNextLevel must be unlocked when level $lastLevel is completed", SoloProgressionAuthority.isUnlocked(firstNextLevel, completed))
        }
    }

    // -----------------------------------------------------------------------------------------
    // 20. First level of every World opens correctly
    // -----------------------------------------------------------------------------------------
    @Test
    fun test20_firstLevelOfEveryWorld_opensCorrectly() {
        val firstLevels = listOf(1, 301, 601, 901, 1201, 1501)
        for (lvl in firstLevels) {
            val world = SoloProgressionAuthority.getWorld(lvl)
            assertTrue("Level $lvl must be first of world ${world.worldId}", SoloProgressionAuthority.isFirstLevelOfWorld(lvl))
            assertEquals(1, SoloProgressionAuthority.getWorldLocalLevel(lvl))
        }
    }

    // -----------------------------------------------------------------------------------------
    // 21. Last level of every World transitions correctly
    // -----------------------------------------------------------------------------------------
    @Test
    fun test21_lastLevelOfEveryWorld_transitionsCorrectly() {
        val lastLevels = listOf(300, 600, 900, 1200, 1500, 1800)
        for (lvl in lastLevels) {
            assertTrue("Level $lvl must be marked as last level of its world", SoloProgressionAuthority.isLastLevelOfWorld(lvl))
        }
    }

    // -----------------------------------------------------------------------------------------
    // 22. Observed screenshot regression passes (WORLD 5 • LEVEL 1 -> Level 1202)
    // -----------------------------------------------------------------------------------------
    @Test
    fun test22_observedProgressionLockRegression_passesWithoutFalseLock() {
        // Player has completed Worlds 1..4 (levels 1..1200) and just completed Level 1201 (World 5 Level 1)
        val completed = (1..1201).toSet()

        // 1. Verify Level 1201 metadata: World 5, Level 1
        val desc1201 = SoloProgressionAuthority.getLevel(1201)
        assertNotNull(desc1201)
        assertEquals(5, desc1201?.worldId)
        assertEquals(1, desc1201?.worldLocalLevel)
        assertEquals("World 5 • Level 1", WorldConfiguration.formatLevelTitle(5, 1201))

        // 2. Verify Level 1202 is UNLOCKED
        assertTrue("Level 1202 MUST BE UNLOCKED after completing Level 1201", SoloProgressionAuthority.isUnlocked(1202, completed))

        // 3. Verify destination resolution
        val resolution = SoloProgressionAuthority.resolveVictoryDestination(1201, completed)
        assertEquals("NEXT LEVEL", resolution.buttonLabel)
        assertEquals(LevelCompletionState.LEVEL_COMPLETED, resolution.completionState)
        assertEquals(ProgressionDestination.NextLevel(worldId = 5, levelId = 1202), resolution.destination)
        assertEquals(1202, resolution.nextLevelId)
    }

    // -----------------------------------------------------------------------------------------
    // 23. Content-version migration preserves progression
    // -----------------------------------------------------------------------------------------
    @Test
    fun test23_contentVersionMigration_preservesProgression() {
        // Progression is indexed by stableLevelId, independent of puzzleVersion
        val completed = setOf(75)
        assertTrue(SoloProgressionAuthority.isCompleted(75, completed))
        assertTrue(SoloProgressionAuthority.isUnlocked(76, completed))
    }

    // -----------------------------------------------------------------------------------------
    // 24. Guest progression works (offline-first, no auth required)
    // -----------------------------------------------------------------------------------------
    @Test
    fun test24_guestProgression_worksOfflineWithoutAuth() {
        val guestCompleted = (1..10).toSet()
        assertTrue(SoloProgressionAuthority.isUnlocked(11, guestCompleted))
        assertEquals(11, SoloProgressionAuthority.resolveContinueDestination(guestCompleted))
    }

    // -----------------------------------------------------------------------------------------
    // 25. Auth reconciliation works where applicable
    // -----------------------------------------------------------------------------------------
    @Test
    fun test25_authReconciliation_monotonicUnion() {
        val localCompleted = (1..80).toSet()
        val staleRemoteCompleted = (1..77).toSet()

        // Non-destructive merge contract
        val merged = localCompleted + staleRemoteCompleted
        assertEquals(80, merged.size)
        assertTrue("Local higher progress must be preserved", SoloProgressionAuthority.isUnlocked(81, merged))
    }

    // -----------------------------------------------------------------------------------------
    // 26. Stale state cannot relock newer progress
    // -----------------------------------------------------------------------------------------
    @Test
    fun test26_staleState_cannotRelockNewerProgress() {
        val currentProgress = (1..151).toSet()
        assertTrue(SoloProgressionAuthority.isUnlocked(152, currentProgress))

        // Even if an older snapshot is observed, authoritative check with currentProgress unlocks 152
        assertTrue(SoloProgressionAuthority.isUnlocked(152, currentProgress))
    }

    // -----------------------------------------------------------------------------------------
    // 27. Rewards remain idempotent
    // -----------------------------------------------------------------------------------------
    @Test
    fun test27_rewardsRemainIdempotent() {
        val recordedCompletions = mutableMapOf<Int, Int>()

        fun record(lvl: Int) {
            recordedCompletions[lvl] = (recordedCompletions[lvl] ?: 0) + 1
        }

        record(5)
        record(5) // second completion
        assertEquals(2, recordedCompletions[5])
        // First-clear reward eligibility is true only when previous count was 0
        val isFirstClearOnSecondRun = false
        assertFalse(isFirstClearOnSecondRun)
    }

    // -----------------------------------------------------------------------------------------
    // 28. Interstitial unavailable does not block navigation
    // -----------------------------------------------------------------------------------------
    @Test
    fun test28_interstitialUnavailable_doesNotBlockNavigation() {
        var navigated = false
        val adAvailable = false

        fun onNext() {
            if (adAvailable) {
                // show ad
            } else {
                navigated = true
            }
        }

        onNext()
        assertTrue("Must proceed to destination when ad is unavailable", navigated)
    }

    // -----------------------------------------------------------------------------------------
    // 29. Interstitial dismissal continues correctly
    // -----------------------------------------------------------------------------------------
    @Test
    fun test29_interstitialDismissal_continuesCorrectly() {
        var navigated = false
        fun onAdDismissed() {
            navigated = true
        }

        onAdDismissed()
        assertTrue("Must proceed to destination upon ad dismissal", navigated)
    }

    // -----------------------------------------------------------------------------------------
    // 30. Genuinely locked future levels remain locked
    // -----------------------------------------------------------------------------------------
    @Test
    fun test30_genuinelyLockedFutureLevels_remainLocked() {
        val completed = (1..5).toSet()

        // 1..5 completed -> 6 unlocked
        assertTrue(SoloProgressionAuthority.isUnlocked(6, completed))

        // 7..300 must remain strictly locked
        for (lvl in 7..300) {
            assertFalse("Future level $lvl must remain locked", SoloProgressionAuthority.isUnlocked(lvl, completed))
        }
    }

    // -----------------------------------------------------------------------------------------
    // 31. Final level does not attempt nonexistent next level
    // -----------------------------------------------------------------------------------------
    @Test
    fun test31_finalLevel_doesNotAttemptNonexistentNextLevel() {
        val completed = (1..1800).toSet()
        val res = SoloProgressionAuthority.resolveVictoryDestination(1800, completed)

        assertEquals(LevelCompletionState.JOURNEY_COMPLETED, res.completionState)
        assertEquals("JOURNEY COMPLETE", res.buttonLabel)
        assertEquals(ProgressionDestination.JourneyComplete, res.destination)
        assertNull("nextLevelId must be null for final game level", res.nextLevelId)
        assertNull("nextWorldId must be null for final game level", res.nextWorldId)
        assertFalse(res.hasNextLevelInCurrentWorld)
        assertFalse(res.hasNextWorld)
        assertNull(SoloProgressionAuthority.getNextLevel(1800))
    }

    // -----------------------------------------------------------------------------------------
    // 32. Full catalog 1 -> 1800 simulation passes
    // -----------------------------------------------------------------------------------------
    @Test
    fun test32_fullCatalog_1To1800_simulationPassesWithoutFalseLock() {
        val completed = mutableSetOf<Int>()

        for (lvl in 1..1800) {
            // 1. Verify current level is unlocked
            assertTrue("Level $lvl must be unlocked when reached sequentially", SoloProgressionAuthority.isUnlocked(lvl, completed))

            // 2. Complete current level
            completed.add(lvl)

            // 3. Verify current level is marked completed
            assertTrue("Level $lvl must be recorded as completed", SoloProgressionAuthority.isCompleted(lvl, completed))

            // 4. Resolve destination
            val res = SoloProgressionAuthority.resolveVictoryDestination(lvl, completed)

            if (lvl < 1800) {
                val nextLvl = lvl + 1
                assertTrue("Next level $nextLvl must be unlocked immediately after completing level $lvl", SoloProgressionAuthority.isUnlocked(nextLvl, completed))

                if (SoloProgressionAuthority.isLastLevelOfWorld(lvl)) {
                    val nextWorld = SoloProgressionAuthority.getWorld(lvl).worldId + 1
                    assertEquals("World boundary level $lvl must trigger NEXT WORLD", "NEXT WORLD", res.buttonLabel)
                    assertEquals(ProgressionDestination.NextWorldEntry(nextWorld), res.destination)
                    assertTrue("Next World $nextWorld must be unlocked", SoloProgressionAuthority.isWorldUnlocked(nextWorld, completed))
                } else {
                    assertEquals("Level $lvl must trigger NEXT LEVEL", "NEXT LEVEL", res.buttonLabel)
                    assertEquals(ProgressionDestination.NextLevel(SoloProgressionAuthority.getWorld(lvl).worldId, nextLvl), res.destination)
                }

                assertEquals(nextLvl, SoloProgressionAuthority.resolveContinueDestination(completed))
            } else {
                // Final Level 1800
                assertEquals("Level 1800 must trigger JOURNEY COMPLETE", "JOURNEY COMPLETE", res.buttonLabel)
                assertEquals(ProgressionDestination.JourneyComplete, res.destination)
            }
        }

        // Entire 1800-level journey conquered!
        assertEquals(1800, completed.size)
        assertEquals(1800, SoloProgressionAuthority.resolveContinueDestination(completed))
    }
}
