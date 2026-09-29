package com.zynpath.game.core.haptics.model

/**
 * Cohesive haptic event taxonomy with discrete tactile signatures.
 *
 * Implements Prompt 34 Sections 31, 32, 34-37:
 * - Decouples logical gameplay actions from device vibration hardware.
 * - Provides subtle, restrained tactile feedback that enhances spatial confidence.
 */
enum class ZynpathHapticEvent {
    /** Light tactile tick (15ms) when starting the path at checkpoint 1. */
    PATH_START,

    /** Ultra-short subtle pulse (8ms) when a valid orthogonal cell is added to the path. */
    VALID_MOVE,

    /** Firm, satisfying click (25ms) when a numbered checkpoint is reached. */
    CHECKPOINT_REACHED,

    /** Distinguishable double buzz (20ms on, 30ms off, 20ms on) for move rejection. */
    INVALID_MOVE,

    /** Light tick (12ms) on undo or backtracking. */
    UNDO,

    /** Distinct pulse (35ms) on full path reset. */
    RESET,

    /** Celebratory three-step rhythmic pattern on puzzle completion. */
    COMPLETION,

    /** Standard UI confirmation tap (10ms) for primary buttons. */
    BUTTON_CONFIRM,

    /** Subtle ambient tick (8ms) on screen entrance. */
    SCREEN_OPEN,

    /** Double pulse on reward reveal. */
    REWARD_REVEALED,

    /** Short welcoming pulse when a player joins. */
    PLAYER_JOINED,

    /** Firm vibration when a match begins. */
    MATCH_STARTED,

    /** Triumphant celebratory pattern on victory. */
    VICTORY,

    /** Gentle muted vibration on defeat. */
    DEFEAT,

    /** Double-buzz on error or disconnection. */
    ERROR
}
