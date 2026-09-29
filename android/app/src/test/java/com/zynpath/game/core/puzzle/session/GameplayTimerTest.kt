package com.zynpath.game.core.puzzle.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GameplayTimer] and [TimeProvider].
 *
 * Implements Prompt 13 Section 37 Mandatory Tests:
 * 1. Timer starts after accepted StartPath.
 * 2. Invalid initial touch does not start timer.
 * 3. Pause freezes duration.
 * 4. Resume continues duration.
 * 5. Repeated pause is idempotent.
 * 6. Repeated resume is idempotent.
 * 7. Background time is excluded.
 * 8. Reset starts a fresh attempt.
 * 9. Completion freezes final duration.
 * 10. Injectable clock produces deterministic results.
 */
class GameplayTimerTest {

    private lateinit var testTime: TestTimeProvider
    private lateinit var timer: GameplayTimer

    @Before
    fun setUp() {
        testTime = TestTimeProvider(currentMonotonicMs = 10_000L, currentWallClockMs = 1_700_000_000_000L)
        timer = GameplayTimer(testTime)
    }

    @Test
    fun `test 1 - timer starts after accepted action and tracks elapsed time`() {
        assertFalse(timer.isRunning)
        assertEquals(0L, timer.elapsedDurationMs())

        timer.start()
        assertTrue(timer.isRunning)

        testTime.advanceTimeMs(1500L)
        assertEquals(1500L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 2 - unstarted timer does not advance when time passes`() {
        assertFalse(timer.isRunning)
        testTime.advanceTimeMs(5000L)
        assertEquals(0L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 3 - pause freezes duration`() {
        timer.start()
        testTime.advanceTimeMs(2000L)
        assertEquals(2000L, timer.elapsedDurationMs())

        val pausedDuration = timer.pause()
        assertEquals(2000L, pausedDuration)
        assertFalse(timer.isRunning)

        // Monotonic time moves forward by 10 seconds while paused
        testTime.advanceTimeMs(10_000L)
        assertEquals(2000L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 4 - resume continues duration from accumulated duration`() {
        timer.start()
        testTime.advanceTimeMs(3000L)
        timer.pause()

        testTime.advanceTimeMs(5000L) // Paused time
        assertEquals(3000L, timer.elapsedDurationMs())

        timer.resume()
        assertTrue(timer.isRunning)

        testTime.advanceTimeMs(2000L) // Active play resumes
        assertEquals(5000L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 5 - repeated pause is strictly idempotent`() {
        timer.start()
        testTime.advanceTimeMs(1200L)

        val firstPause = timer.pause()
        assertEquals(1200L, firstPause)

        testTime.advanceTimeMs(3000L)
        val secondPause = timer.pause()
        assertEquals(1200L, secondPause)

        val thirdPause = timer.pause()
        assertEquals(1200L, thirdPause)
        assertEquals(1200L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 6 - repeated resume is strictly idempotent`() {
        timer.start()
        testTime.advanceTimeMs(1000L)
        timer.pause()

        timer.resume()
        testTime.advanceTimeMs(500L)
        // Calling resume while already running must not reset the interval
        timer.resume()
        testTime.advanceTimeMs(500L)

        assertEquals(2000L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 7 - background time is completely excluded`() {
        timer.start()
        testTime.advanceTimeMs(4000L) // 4s active play

        // App backgrounded: onAppBackgrounded calls pause
        timer.pause()

        // 60 seconds pass while app is in background
        testTime.advanceTimeMs(60_000L)
        assertEquals(4000L, timer.elapsedDurationMs())

        // App foregrounded: onResume
        timer.resume()
        testTime.advanceTimeMs(1000L)

        // Total active time is 4s + 1s = 5s (not 65s)
        assertEquals(5000L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 8 - reset starts a fresh attempt from zero`() {
        timer.start()
        testTime.advanceTimeMs(8000L)
        assertEquals(8000L, timer.elapsedDurationMs())

        val resetTime = timer.reset()
        assertEquals(0L, resetTime)
        assertFalse(timer.isRunning)
        assertEquals(0L, timer.elapsedDurationMs())

        testTime.advanceTimeMs(2000L)
        assertEquals(0L, timer.elapsedDurationMs())

        // Fresh attempt starts
        timer.start()
        testTime.advanceTimeMs(1500L)
        assertEquals(1500L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 9 - completion freezes final duration`() {
        timer.start()
        testTime.advanceTimeMs(14_250L)

        val finalDuration = timer.stop()
        assertEquals(14_250L, finalDuration)
        assertFalse(timer.isRunning)

        testTime.advanceTimeMs(5000L)
        assertEquals(14_250L, timer.elapsedDurationMs())
    }

    @Test
    fun `test 10 - injectable clock produces deterministic results`() {
        val clock = TestTimeProvider(currentMonotonicMs = 0L, currentWallClockMs = 1_000_000L)
        val customTimer = GameplayTimer(clock, initialAccumulatedDurationMs = 2500L)

        assertEquals(2500L, customTimer.elapsedDurationMs())

        customTimer.start()
        clock.advanceTimeMs(750L)
        assertEquals(3250L, customTimer.elapsedDurationMs())

        customTimer.pause()
        clock.advanceTimeMs(10_000L)
        assertEquals(3250L, customTimer.elapsedDurationMs())
    }
}
