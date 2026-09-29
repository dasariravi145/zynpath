package com.zynpath.game.core.error

/**
 * Unified error classification taxonomy across Zynpath client and backend.
 *
 * Implements Prompt 38 Section 6:
 * - Shared error taxonomy covering all application subsystems.
 * - Distinguishes technical subsystem origin while maintaining domain boundaries.
 */
enum class ErrorCategory {
    NETWORK,
    AUTHENTICATION,
    AUTHORIZATION,
    VALIDATION,
    PERSISTENCE,
    SYNC_CONFLICT,
    GAMEPLAY_STATE,
    MULTIPLAYER,
    BILLING,
    ENTITLEMENT,
    RESOURCE,
    UNKNOWN
}
