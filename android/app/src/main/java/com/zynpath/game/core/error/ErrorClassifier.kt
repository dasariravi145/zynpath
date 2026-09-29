package com.zynpath.game.core.error

import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteFullException
import com.zynpath.game.core.network.NetworkResult
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps raw technical exceptions and API errors to unified [ZynpathError] instances.
 *
 * Implements Prompt 38 Sections 6, 7, 8, 9, 25, 54, 57:
 * - Traps technical exceptions in the infrastructure layer.
 * - Produces human-friendly messages without leaking stack traces.
 * - Correctly marks transient failures as retryable and storage full as user action required.
 */
object ErrorClassifier {

    fun classify(throwable: Throwable?): ZynpathError {
        if (throwable == null) {
            return ZynpathError(
                category = ErrorCategory.UNKNOWN,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.ERROR,
                userMessage = "An unexpected error occurred. Please try again.",
                suggestedAction = RecoveryAction.Retry()
            )
        }

        return when (throwable) {
            is SQLiteFullException -> {
                ZynpathError(
                    category = ErrorCategory.PERSISTENCE,
                    recoveryType = RecoveryType.USER_ACTION_REQUIRED,
                    severity = ErrorSeverity.CRITICAL,
                    userMessage = "Storage full. Progress could not be saved to disk. Please free up space.",
                    technicalMessage = "SQLiteFullException: ${throwable.message}",
                    suggestedAction = RecoveryAction.FreeStorageSpace()
                )
            }
            is SQLiteDatabaseLockedException -> {
                ZynpathError(
                    category = ErrorCategory.PERSISTENCE,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.WARNING,
                    userMessage = "Database is busy. Retrying operation.",
                    technicalMessage = "SQLiteDatabaseLockedException: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
            is SQLiteDiskIOException -> {
                ZynpathError(
                    category = ErrorCategory.PERSISTENCE,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.ERROR,
                    userMessage = "Disk read error. Retrying operation.",
                    technicalMessage = "SQLiteDiskIOException: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
            is UnknownHostException -> {
                ZynpathError(
                    category = ErrorCategory.NETWORK,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.WARNING,
                    userMessage = "No internet connection detected. Offline gameplay remains available.",
                    technicalMessage = "UnknownHostException: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
            is SocketTimeoutException -> {
                ZynpathError(
                    category = ErrorCategory.NETWORK,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.WARNING,
                    userMessage = "Connection timed out. Your progress is saved on this device.",
                    technicalMessage = "SocketTimeoutException: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
            is ConnectException, is SocketException -> {
                ZynpathError(
                    category = ErrorCategory.NETWORK,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.WARNING,
                    userMessage = "Connection lost. Trying to reconnect.",
                    technicalMessage = "${throwable::class.java.simpleName}: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
            is IOException -> {
                ZynpathError(
                    category = ErrorCategory.NETWORK,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.WARNING,
                    userMessage = "Network operation interrupted. Please try again.",
                    technicalMessage = "IOException: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
            else -> {
                ZynpathError(
                    category = ErrorCategory.UNKNOWN,
                    recoveryType = RecoveryType.RETRYABLE_FAILURE,
                    severity = ErrorSeverity.ERROR,
                    userMessage = "An unexpected error occurred. Please try again.",
                    technicalMessage = "${throwable::class.java.name}: ${throwable.message}",
                    suggestedAction = RecoveryAction.Retry()
                )
            }
        }
    }

    fun classifyHttpError(statusCode: Int, rawMessage: String?): ZynpathError {
        return when (statusCode) {
            401 -> ZynpathError(
                category = ErrorCategory.AUTHENTICATION,
                recoveryType = RecoveryType.USER_ACTION_REQUIRED,
                severity = ErrorSeverity.WARNING,
                userMessage = "Please sign in again.",
                technicalMessage = "HTTP 401: $rawMessage",
                suggestedAction = RecoveryAction.SignInAgain()
            )
            403 -> ZynpathError(
                category = ErrorCategory.AUTHORIZATION,
                recoveryType = RecoveryType.PERMANENT_REJECTION,
                severity = ErrorSeverity.ERROR,
                userMessage = "You do not have permission to perform this action.",
                technicalMessage = "HTTP 403: $rawMessage",
                suggestedAction = RecoveryAction.Dismiss()
            )
            404 -> ZynpathError(
                category = ErrorCategory.RESOURCE,
                recoveryType = RecoveryType.PERMANENT_REJECTION,
                severity = ErrorSeverity.WARNING,
                userMessage = "Requested content was not found.",
                technicalMessage = "HTTP 404: $rawMessage",
                suggestedAction = RecoveryAction.GoHome()
            )
            409 -> ZynpathError(
                category = ErrorCategory.SYNC_CONFLICT,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.INFO,
                userMessage = "Sync conflict detected. Local and cloud progress are reconciling.",
                technicalMessage = "HTTP 409: $rawMessage",
                suggestedAction = RecoveryAction.Retry()
            )
            429 -> ZynpathError(
                category = ErrorCategory.NETWORK,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.WARNING,
                userMessage = "Too many requests. Please wait a moment before trying again.",
                technicalMessage = "HTTP 429: $rawMessage",
                suggestedAction = RecoveryAction.Retry()
            )
            500, 502, 503, 504 -> ZynpathError(
                category = ErrorCategory.NETWORK,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.WARNING,
                userMessage = "Online services are temporarily unavailable. Your progress is saved on this device.",
                technicalMessage = "HTTP $statusCode: $rawMessage",
                suggestedAction = RecoveryAction.Retry()
            )
            else -> ZynpathError(
                category = ErrorCategory.NETWORK,
                recoveryType = RecoveryType.RETRYABLE_FAILURE,
                severity = ErrorSeverity.ERROR,
                userMessage = "Network request failed. Please try again.",
                technicalMessage = "HTTP $statusCode: $rawMessage",
                suggestedAction = RecoveryAction.Retry()
            )
        }
    }

    fun fromNetworkResult(result: NetworkResult<*>): ZynpathError? {
        return when (result) {
            is NetworkResult.Success -> null
            is NetworkResult.Error -> classifyHttpError(result.code, result.message)
            is NetworkResult.Exception -> classify(result.throwable)
        }
    }
}
