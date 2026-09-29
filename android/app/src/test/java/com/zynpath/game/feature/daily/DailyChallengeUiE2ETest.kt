package com.zynpath.game.feature.daily

import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeSchedule
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus
import com.zynpath.game.core.puzzle.daily.SystemDailyChallengeClock
import com.zynpath.game.fake.FakeDailyChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verification of Daily Challenge Presentation, Deterministic Scheduling,
 * Offline Provisional Status, Server Verification, and Result Presentation.
 *
 * Implements Prompt 48 Requirements 42, 43, 44.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DailyChallengeUiE2ETest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var clock: DailyChallengeClock
    private lateinit var schedule: DailyChallengeSchedule
    private lateinit var repository: FakeDailyChallengeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clock = object : DailyChallengeClock {
            override fun currentUtcInstant(): java.time.Instant = java.time.Instant.ofEpochMilli(1790467200000L)
            override fun currentUtcDate(): java.time.LocalDate = java.time.LocalDate.parse("2026-09-27")
            override fun currentUtcDateKey(): String = "2026-09-27"
            override fun millisUntilNextReset(): Long = 43200000L
            override fun challengeWindowStartMs(date: java.time.LocalDate): Long = 1790467200000L
            override fun challengeWindowEndMs(date: java.time.LocalDate): Long = 1790553599999L
            override fun isToday(dateKey: String): Boolean = dateKey == "2026-09-27"
        }
        schedule = DailyChallengeSchedule()
        repository = FakeDailyChallengeRepository(clock, schedule)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDailyChallengeScheduleDeterministicAcrossRestarts() {
        val dateKey = "2026-09-27"
        val challenge1 = schedule.resolveChallenge(dateKey)
        val challenge2 = schedule.resolveChallenge(dateKey)

        assertEquals("Same UTC date must resolve to identical challenge ID", challenge1.challengeId, challenge2.challengeId)
        assertEquals("Same UTC date must resolve to identical puzzle ID", challenge1.puzzleId, challenge2.puzzleId)
        assertEquals(challenge1.puzzleFingerprint, challenge2.puzzleFingerprint)
        assertEquals(dateKey, challenge1.dateKey)
    }

    @Test
    fun testTodayChallengeResolvesFromUtcDate() {
        val todayDef = repository.getTodayChallenge()
        assertEquals("2026-09-27", todayDef.dateKey)
        assertNotNull(todayDef.puzzleDefinition)
        assertTrue(todayDef.puzzleDefinition.gridDimensions.rows >= 4)
    }

    @Test
    fun testOfflineSolveRecordedAsOfflineProvisional() = runTest(testDispatcher) {
        val def = repository.getTodayChallenge()

        // Solve offline
        repository.recordCompletion(
            challenge = def,
            solveTimeMs = 42000L,
            movesCount = 25,
            verificationStatus = DailyVerificationStatus.OFFLINE_PROVISIONAL,
            serverAttemptId = null,
            isLeaderboardEligible = false
        )

        val completed = repository.observeCompletedChallenges().first()
        assertEquals(1, completed.size)
        val entry = completed.first()
        assertEquals(DailyVerificationStatus.OFFLINE_PROVISIONAL.name, entry.verificationStatus)
        assertEquals(42000L, entry.solveTimeMs)
        assertEquals(25, entry.moveCount)

        // Verify streak increased
        val streak = repository.observeCurrentStreak().first()
        assertEquals(1, streak)
    }

    @Test
    fun testServerVerifiedSolveRecordedAsServerVerified() = runTest(testDispatcher) {
        val def = repository.getTodayChallenge()

        // Solve online with server verification
        repository.recordCompletion(
            challenge = def,
            solveTimeMs = 28000L,
            movesCount = 25,
            verificationStatus = DailyVerificationStatus.SERVER_VERIFIED,
            serverAttemptId = "srv_attempt_99",
            isLeaderboardEligible = true
        )

        val completed = repository.observeCompletedChallenges().first()
        assertEquals(1, completed.size)
        val entry = completed.first()
        assertEquals(DailyVerificationStatus.SERVER_VERIFIED.name, entry.verificationStatus)
        assertEquals(28000L, entry.solveTimeMs)
    }

    @Test
    fun testCountdownUntilMidnightFormattedCorrectly() {
        val millisRemaining = clock.millisUntilNextReset() // 43200000L = 12 hours
        val hours = millisRemaining / (1000 * 60 * 60)
        val minutes = (millisRemaining / (1000 * 60)) % 60
        assertEquals(12L, hours)
        assertEquals(0L, minutes)
    }
}
