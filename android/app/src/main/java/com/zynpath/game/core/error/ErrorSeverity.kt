package com.zynpath.game.core.error

/**
 * Severity grading for application events and errors.
 *
 * Implements Prompt 38 Section 6 & 8.
 */
enum class ErrorSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}
