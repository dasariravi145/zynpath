package com.zynpath.backend.duel.model;

/**
 * Lifecycle states of an online 1v1 duel match.
 */
public enum DuelSessionState {
    MATCHMAKING,
    COUNTDOWN,
    IN_PROGRESS,
    VALIDATING_CLAIM,
    COMPLETED,
    ABANDONED
}
