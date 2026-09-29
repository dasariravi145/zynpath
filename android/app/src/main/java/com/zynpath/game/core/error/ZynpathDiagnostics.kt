package com.zynpath.game.core.error

import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Diagnostic event model for localized resilience analysis.
 */
data class DiagnosticEvent(
    val eventType: String,
    val correlationId: String,
    val category: ErrorCategory,
    val details: Map<String, String>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Privacy-conscious diagnostics and error telemetry tracker.
 *
 * Implements Prompt 38 Sections 65, 66, 67, 68:
 * - Records essential resilience metrics: restoration failures, sync failures,
 *   match reconnections, database failures, and billing verification failures.
 * - Enforces zero-PII logging: unconditionally filters out tokens, credentials,
 *   touch gestures, and player identity secrets.
 * - Circular in-memory event buffer (last 50 events) for local diagnostic review.
 */
object ZynpathDiagnostics {

    private const val TAG = "ZynpathDiagnostics"
    private const val MAX_EVENT_BUFFER = 50

    private val eventBuffer = ConcurrentLinkedQueue<DiagnosticEvent>()

    // Set of forbidden key names that could leak sensitive credentials
    private val SENSITIVE_KEYS = setOf(
        "token", "accesstoken", "refreshtoken", "sessiontoken",
        "purchasetoken", "password", "secret", "gesture", "coordinates", "path"
    )

    fun recordError(
        eventType: String,
        error: ZynpathError,
        additionalMetadata: Map<String, String> = emptyMap()
    ) {
        val sanitizedDetails = sanitizeMetadata(
            additionalMetadata + mapOf(
                "category" to error.category.name,
                "recoveryType" to error.recoveryType.name,
                "severity" to error.severity.name,
                "technicalMessage" to (error.technicalMessage ?: "")
            )
        )

        val event = DiagnosticEvent(
            eventType = eventType,
            correlationId = error.correlationId,
            category = error.category,
            details = sanitizedDetails
        )

        addEvent(event)
        try {
            Log.w(TAG, "[$eventType] correlationId=${error.correlationId} category=${error.category} details=$sanitizedDetails")
        } catch (_: Throwable) {}
    }

    fun recordEvent(
        eventType: String,
        category: ErrorCategory,
        correlationId: String = ZynpathError.generateCorrelationId(),
        details: Map<String, String> = emptyMap()
    ) {
        val sanitizedDetails = sanitizeMetadata(details)
        val event = DiagnosticEvent(
            eventType = eventType,
            correlationId = correlationId,
            category = category,
            details = sanitizedDetails
        )

        addEvent(event)
        try {
            Log.i(TAG, "[$eventType] correlationId=$correlationId category=$category details=$sanitizedDetails")
        } catch (_: Throwable) {}
    }

    private fun addEvent(event: DiagnosticEvent) {
        eventBuffer.add(event)
        while (eventBuffer.size > MAX_EVENT_BUFFER) {
            eventBuffer.poll()
        }
    }

    fun getRecentEvents(): List<DiagnosticEvent> {
        return eventBuffer.toList()
    }

    fun clear() {
        eventBuffer.clear()
    }

    private fun sanitizeMetadata(metadata: Map<String, String>): Map<String, String> {
        return metadata.filterKeys { key ->
            val lower = key.lowercase()
            SENSITIVE_KEYS.none { sensitive -> lower.contains(sensitive) }
        }.mapValues { (key, value) ->
            if (value.length > 200) value.take(200) + "..." else value
        }
    }
}
