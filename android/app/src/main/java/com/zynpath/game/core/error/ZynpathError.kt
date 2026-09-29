package com.zynpath.game.core.error

import java.util.UUID

/**
 * Unified, structured domain error representation across Zynpath.
 *
 * Implements Prompt 38 Sections 6, 7, 8, 9, 68:
 * - Technical details isolated from user-facing presentation.
 * - Concise, clear player-facing messaging.
 * - Safe correlation ID for diagnostic tracing without sensitive disclosures.
 */
data class ZynpathError(
    val category: ErrorCategory,
    val recoveryType: RecoveryType,
    val severity: ErrorSeverity = ErrorSeverity.ERROR,
    val userMessage: String,
    val technicalMessage: String? = null,
    val correlationId: String = generateCorrelationId(),
    val suggestedAction: RecoveryAction = RecoveryAction.Retry(),
    val isSilent: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isRetryable: Boolean
        get() = recoveryType == RecoveryType.RETRYABLE_FAILURE

    companion object {
        fun generateCorrelationId(): String {
            return "ERR-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
        }

        fun networkUnavailable(action: RecoveryAction = RecoveryAction.Retry()): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.NETWORK,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.WARNING,
                userMessage = "Connection lost. Trying to reconnect.",
                suggestedAction = action
            )
        }

        fun backendOutage(action: RecoveryAction = RecoveryAction.Retry()): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.NETWORK,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.WARNING,
                userMessage = "Online services are temporarily unavailable. Your progress is saved on this device.",
                suggestedAction = action
            )
        }

        fun sessionExpired(): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.AUTHENTICATION,
                recoveryType = RecoveryType.USER_ACTION_REQUIRED,
                severity = ErrorSeverity.WARNING,
                userMessage = "Your session has expired. Please sign in again.",
                suggestedAction = RecoveryAction.SignInAgain()
            )
        }

        fun storageFull(): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.PERSISTENCE,
                recoveryType = RecoveryType.USER_ACTION_REQUIRED,
                severity = ErrorSeverity.CRITICAL,
                userMessage = "Storage full. Progress could not be saved to disk. Please free up space.",
                suggestedAction = RecoveryAction.FreeStorageSpace()
            )
        }

        fun corruptSession(action: RecoveryAction = RecoveryAction.StartFresh()): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.GAMEPLAY_STATE,
                recoveryType = RecoveryType.UNRECOVERABLE_LOCAL_STATE,
                severity = ErrorSeverity.WARNING,
                userMessage = "Saved puzzle state could not be restored. Completed progress remains safe.",
                suggestedAction = action
            )
        }

        fun billingUnavailable(): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.BILLING,
                recoveryType = RecoveryType.CONFIGURATION_BLOCKER,
                severity = ErrorSeverity.WARNING,
                userMessage = "Google Play Store is currently unavailable. Please try again later.",
                suggestedAction = RecoveryAction.Dismiss()
            )
        }

        fun dailyVerificationPending(): ZynpathError {
            return ZynpathError(
                category = ErrorCategory.SYNC_CONFLICT,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.INFO,
                userMessage = "This result has not been verified yet. It will sync automatically when online.",
                suggestedAction = RecoveryAction.None
            )
        }
    }
}
