package com.zynpath.game.core.error

/**
 * Distinguishes recoverable vs terminal failure modes.
 *
 * Implements Prompt 38 Section 8:
 * - RETRYABLE_FAILURE: Safe to retry automatically or via user gesture.
 * - USER_ACTION_REQUIRED: User must intervene (e.g. sign in, free storage, accept permissions).
 * - PERMANENT_REJECTION: Request rejected permanently (e.g. rule violation, expired room).
 * - CONFIGURATION_BLOCKER: Missing environment config or platform service unavailable.
 * - UNRECOVERABLE_LOCAL_STATE: Corrupted or incompatible local cache requiring safe fallback.
 */
enum class RecoveryType {
    RETRYABLE_FAILURE,
    USER_ACTION_REQUIRED,
    PERMANENT_REJECTION,
    CONFIGURATION_BLOCKER,
    UNRECOVERABLE_LOCAL_STATE
}
