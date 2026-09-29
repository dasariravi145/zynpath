package com.zynpath.game.core.auth.model

/**
 * Type-safe outcome of an authentication or account-linking operation.
 *
 * Implements Prompt 18 Sections 22, 27 & 37:
 * - Differentiates Success, Errors, Cancellations, Conflicts, and Unconfigured Providers.
 */
sealed interface AuthResult {
    data class Success(val session: AuthSession) : AuthResult
    data class Error(val errorCode: String, val message: String) : AuthResult
    data object Cancelled : AuthResult
    data class ProviderNotConfigured(
        val provider: AuthProvider,
        val message: String
    ) : AuthResult
    data class Conflict(
        val provider: AuthProvider,
        val message: String
    ) : AuthResult
}
