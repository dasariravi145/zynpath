package com.zynpath.game.core.player

/**
 * Validation result for display name updates.
 *
 * Implements Prompt 17 Section 11:
 * - Length limits
 * - Whitespace normalization
 * - Empty-name rejection
 * - Safe character handling
 */
sealed class DisplayNameValidationResult {
    data class Valid(val normalizedName: String) : DisplayNameValidationResult()
    data object Empty : DisplayNameValidationResult()
    data class TooShort(val minLength: Int = 2) : DisplayNameValidationResult()
    data class TooLong(val maxLength: Int = 20) : DisplayNameValidationResult()
    data object InvalidCharacters : DisplayNameValidationResult()
}

object DisplayNameValidator {
    const val MIN_LENGTH = 2
    const val MAX_LENGTH = 20

    private val ALLOWED_CHARACTERS_REGEX = Regex("^[a-zA-Z0-9 _-]+$")

    fun validate(rawName: String): DisplayNameValidationResult {
        val trimmed = rawName.trim().replace(Regex("\\s+"), " ")

        if (trimmed.isEmpty()) {
            return DisplayNameValidationResult.Empty
        }

        if (trimmed.length < MIN_LENGTH) {
            return DisplayNameValidationResult.TooShort(MIN_LENGTH)
        }

        if (trimmed.length > MAX_LENGTH) {
            return DisplayNameValidationResult.TooLong(MAX_LENGTH)
        }

        if (!ALLOWED_CHARACTERS_REGEX.matches(trimmed)) {
            return DisplayNameValidationResult.InvalidCharacters
        }

        return DisplayNameValidationResult.Valid(trimmed)
    }
}
