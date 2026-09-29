package com.zynpath.game.core.error

/**
 * Actionable recovery recommendations presented to the user.
 *
 * Implements Prompt 38 Section 72:
 * - Provides explicit user recovery mechanisms without trapping on error screens.
 */
sealed class RecoveryAction(val label: String) {
    data class Retry(val customLabel: String = "Try Again") : RecoveryAction(customLabel)
    data class SignInAgain(val customLabel: String = "Sign In Again") : RecoveryAction(customLabel)
    data class ResumeGame(val customLabel: String = "Resume") : RecoveryAction(customLabel)
    data class StartFresh(val customLabel: String = "Start Fresh") : RecoveryAction(customLabel)
    data class GoHome(val customLabel: String = "Return Home") : RecoveryAction(customLabel)
    data class FreeStorageSpace(val customLabel: String = "Manage Storage") : RecoveryAction(customLabel)
    data class Dismiss(val customLabel: String = "Dismiss") : RecoveryAction(customLabel)
    object None : RecoveryAction("")
}
