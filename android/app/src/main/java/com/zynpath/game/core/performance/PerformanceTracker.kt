package com.zynpath.game.core.performance

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Privacy-conscious in-memory performance telemetry tracker.
 *
 * Implements Prompt 37 Section 68:
 * - Records startup duration, board render latency, sync duration, backend latency,
 *   memory pressure, and WebSocket reconnection events.
 * - Strictly excludes raw player gestures, touch coordinates, private credentials, or PII.
 */
@Singleton
class PerformanceTracker @Inject constructor() {

    companion object {
        private const val TAG = "PerformanceTracker"
        private const val MAX_SAMPLES = 50
    }

    private val appStartupDurationMs = AtomicLong(0L)
    private val boardRenderLatencies = ConcurrentHashMap<Int, AtomicLong>()
    private val syncSuccessCount = AtomicInteger(0)
    private val syncFailureCount = AtomicInteger(0)
    private val lastSyncDurationMs = AtomicLong(0L)
    private val memoryPressureEvents = AtomicInteger(0)
    private val reconnectionEventCount = AtomicInteger(0)

    fun recordStartupDuration(durationMs: Long) {
        appStartupDurationMs.set(durationMs)
        Log.d(TAG, "Application startup completed in ${durationMs}ms")
    }

    fun recordBoardRenderLatency(gridSize: Int, durationMs: Long) {
        boardRenderLatencies.computeIfAbsent(gridSize) { AtomicLong(0L) }.set(durationMs)
        Log.v(TAG, "Board render latency for ${gridSize}x${gridSize}: ${durationMs}ms")
    }

    fun recordSyncDuration(durationMs: Long, success: Boolean) {
        lastSyncDurationMs.set(durationMs)
        if (success) {
            syncSuccessCount.incrementAndGet()
        } else {
            syncFailureCount.incrementAndGet()
        }
        Log.d(TAG, "Background sync completed in ${durationMs}ms (success=$success)")
    }

    fun recordMemoryPressure(level: String) {
        val count = memoryPressureEvents.incrementAndGet()
        Log.w(TAG, "Memory pressure signal #$count: $level")
    }

    fun recordReconnectionEvent(context: String) {
        val count = reconnectionEventCount.incrementAndGet()
        Log.d(TAG, "WebSocket reconnection #$count triggered ($context)")
    }

    fun getPerformanceSummary(): PerformanceSummary {
        return PerformanceSummary(
            startupDurationMs = appStartupDurationMs.get(),
            lastBoardRenderLatenciesMs = boardRenderLatencies.mapKeys { "${it.key}x${it.key}" }
                .mapValues { it.value.get() },
            syncSuccessCount = syncSuccessCount.get(),
            syncFailureCount = syncFailureCount.get(),
            lastSyncDurationMs = lastSyncDurationMs.get(),
            memoryPressureEvents = memoryPressureEvents.get(),
            reconnectionCount = reconnectionEventCount.get()
        )
    }
}

/**
 * Immutable snapshot of recent performance metrics.
 */
data class PerformanceSummary(
    val startupDurationMs: Long,
    val lastBoardRenderLatenciesMs: Map<String, Long>,
    val syncSuccessCount: Int,
    val syncFailureCount: Int,
    val lastSyncDurationMs: Long,
    val memoryPressureEvents: Int,
    val reconnectionCount: Int
)
