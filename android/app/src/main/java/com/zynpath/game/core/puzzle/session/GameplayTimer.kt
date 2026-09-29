package com.zynpath.game.core.puzzle.session

/**
 * Immutable snapshot of active gameplay timer state.
 */
data class GameplayTimerState(
    val accumulatedDurationMs: Long = 0L,
    val isRunning: Boolean = false,
    val activeIntervalStartMonotonicMs: Long? = null
) {
    /**
     * Calculates the true elapsed duration given the current [currentMonotonicMs].
     */
    fun currentElapsedDurationMs(currentMonotonicMs: Long): Long {
        return if (isRunning && activeIntervalStartMonotonicMs != null) {
            val interval = (currentMonotonicMs - activeIntervalStartMonotonicMs).coerceAtLeast(0L)
            accumulatedDurationMs + interval
        } else {
            accumulatedDurationMs
        }
    }
}

/**
 * High-precision, monotonic active-play timer.
 *
 * Implements Prompt 13 Sections 17–20:
 * - Uses monotonic clock for elapsed intervals within a running process.
 * - Separates accumulated active duration from current active interval.
 * - Freezes duration on pause; resumes without adding paused time.
 * - Repeated pause and resume operations are strictly idempotent.
 * - Resets cleanly for fresh attempts.
 */
class GameplayTimer(
    private val timeProvider: TimeProvider,
    initialAccumulatedDurationMs: Long = 0L
) {
    private var _state = GameplayTimerState(accumulatedDurationMs = initialAccumulatedDurationMs.coerceAtLeast(0L))
    val state: GameplayTimerState get() = _state

    val isRunning: Boolean get() = _state.isRunning

    /**
     * Starts active timing. No-op if already running.
     */
    fun start(): Long {
        if (_state.isRunning) return elapsedDurationMs()
        val now = timeProvider.monotonicTimeMs()
        _state = _state.copy(
            isRunning = true,
            activeIntervalStartMonotonicMs = now
        )
        return _state.accumulatedDurationMs
    }

    /**
     * Pauses the timer, accumulating the active interval duration.
     * Repeated calls are idempotent.
     */
    fun pause(): Long {
        if (!_state.isRunning) return _state.accumulatedDurationMs
        val now = timeProvider.monotonicTimeMs()
        val interval = if (_state.activeIntervalStartMonotonicMs != null) {
            (now - _state.activeIntervalStartMonotonicMs!!).coerceAtLeast(0L)
        } else {
            0L
        }
        val newAccumulated = _state.accumulatedDurationMs + interval
        _state = _state.copy(
            accumulatedDurationMs = newAccumulated,
            isRunning = false,
            activeIntervalStartMonotonicMs = null
        )
        return newAccumulated
    }

    /**
     * Resumes the timer starting a fresh monotonic interval.
     * Repeated calls are idempotent.
     */
    fun resume(): Long {
        if (_state.isRunning) return elapsedDurationMs()
        val now = timeProvider.monotonicTimeMs()
        _state = _state.copy(
            isRunning = true,
            activeIntervalStartMonotonicMs = now
        )
        return elapsedDurationMs()
    }

    /**
     * Resets accumulated duration to zero and halts timing.
     */
    fun reset(): Long {
        _state = GameplayTimerState(
            accumulatedDurationMs = 0L,
            isRunning = false,
            activeIntervalStartMonotonicMs = null
        )
        return 0L
    }

    /**
     * Stops timing and returns the final frozen elapsed duration.
     */
    fun stop(): Long {
        return pause()
    }

    /**
     * Current total active-play duration in milliseconds.
     */
    fun elapsedDurationMs(): Long {
        return _state.currentElapsedDurationMs(timeProvider.monotonicTimeMs())
    }
}
