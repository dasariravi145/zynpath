package com.zynpath.game.core.audio.model

/**
 * Cohesive audio event taxonomy across Zynpath gameplay and interface.
 *
 * Implements Prompt 34 Sections 6, 7, 8, 12-23:
 * - Decouples logical gameplay events from sound asset playback.
 * - Guarantees consistent audio language across Solo, Tutorial, Daily Challenge, and Multiplayer.
 */
enum class ZynpathAudioEvent {
    /** Triggered once when a path begins at checkpoint #1. */
    PATH_START,

    /** Subtle click when an orthogonal, unblocked cell is accepted into the path. */
    VALID_MOVE,

    /** Distinct melodic chime when an intermediate or subsequent checkpoint is reached. */
    CHECKPOINT_REACHED,

    /** Gentle, non-punitive thud when a move is rejected (wall, diagonal, revisit, etc.). */
    INVALID_MOVE,

    /** Soft reverse tick when a move is undone or path backtracks. */
    UNDO,

    /** Distinct brush/sweep sound when the entire path is reset. */
    RESET,

    /** Delicate shimmer when a valid Solo hint reveals the next step. */
    HINT_USED,

    /** Triumphant celebratory chime chord when all cells are covered and final checkpoint reached. */
    PUZZLE_COMPLETED,

    /** Neutral resolution sound when a puzzle fails or time expires. */
    PUZZLE_FAILED,

    /** Crisp, lightweight UI button tap. */
    BUTTON_TAP,

    /** Resonant alert sound when a multiplayer match is matched and ready. */
    MATCH_READY,

    /** Energetic sound when a multiplayer match finishes. */
    MATCH_COMPLETED,

    /** Friendly alert chime when a friend or duel invitation is received. */
    INVITATION_RECEIVED,

    /** Soft ambient tone when a main screen or game hub opens. */
    SCREEN_OPEN,

    /** Rewarding crystalline harmonic chime when a badge or milestone reward is revealed. */
    REWARD_REVEALED,

    /** Welcoming notification sound when an opponent or friend joins a room. */
    PLAYER_JOINED,

    /** Subtle exit tone when a player leaves the room. */
    PLAYER_LEFT,

    /** Decisive match starting chime when countdown concludes. */
    MATCH_STARTED,

    /** Triumphant celebratory victory chord fanfare. */
    VICTORY,

    /** Soft, gentle resolution tone on defeat or loss. */
    DEFEAT,

    /** Non-punitive error or disconnection indication tone. */
    ERROR
}
