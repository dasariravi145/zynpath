package com.zynpath.game.core.puzzle.session

/**
 * Clock abstraction for measuring active gameplay duration and capturing timestamps.
 *
 * Implements Prompt 13 Section 17:
 * - Separates monotonic in-process duration from wall-clock timestamps.
 * - Injectable for deterministic unit testing.
 */
interface TimeProvider {
    /**
     * Monotonic timestamp in milliseconds for measuring elapsed duration within a running process.
     * Guaranteed never to jump backwards due to NTP adjustments or user clock changes.
     */
    fun monotonicTimeMs(): Long

    /**
     * Wall-clock timestamp in milliseconds for persistent records and calendar dates.
     */
    fun wallClockTimeMs(): Long
}

/**
 * Production implementation using system clocks.
 */
class SystemTimeProvider : TimeProvider {
    override fun monotonicTimeMs(): Long = System.nanoTime() / 1_000_000L
    override fun wallClockTimeMs(): Long = System.currentTimeMillis()
}

/**
 * Deterministic test clock allowing manual time advancement.
 */
class TestTimeProvider(
    var currentMonotonicMs: Long = 0L,
    var currentWallClockMs: Long = 1_700_000_000_000L
) : TimeProvider {
    override fun monotonicTimeMs(): Long = currentMonotonicMs
    override fun wallClockTimeMs(): Long = currentWallClockMs

    fun advanceTimeMs(deltaMs: Long) {
        require(deltaMs >= 0) { "Time cannot move backwards ($deltaMs ms)" }
        currentMonotonicMs += deltaMs
        currentWallClockMs += deltaMs
    }
}
