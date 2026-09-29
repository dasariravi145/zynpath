package com.zynpath.backend.multiplayer.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Finite state machine representing the lifecycle of an authoritative multiplayer match.
 *
 * Implements Prompt 20 Sections 10 & 11:
 * Validated legal transitions enforce integrity and prevent client-side bypasses.
 */
public enum MatchState {
    CREATED,
    WAITING_FOR_PLAYERS,
    READY,
    COUNTDOWN,
    ACTIVE,
    COMPLETING,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    /**
     * Determines whether transitioning from this state to the next state is permitted.
     */
    public boolean canTransitionTo(MatchState next) {
        if (this == next) return true;

        return switch (this) {
            case CREATED -> next == WAITING_FOR_PLAYERS || next == READY || next == CANCELLED || next == EXPIRED;
            case WAITING_FOR_PLAYERS -> next == READY || next == CANCELLED || next == EXPIRED;
            case READY -> next == COUNTDOWN || next == WAITING_FOR_PLAYERS || next == CANCELLED || next == EXPIRED;
            case COUNTDOWN -> next == ACTIVE || next == CANCELLED;
            case ACTIVE -> next == COMPLETING || next == COMPLETED || next == CANCELLED;
            case COMPLETING -> next == COMPLETED || next == CANCELLED;
            case COMPLETED, CANCELLED, EXPIRED -> false; // Terminal states
        };
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == EXPIRED;
    }

    public boolean isPlayable() {
        return this == ACTIVE || this == COMPLETING;
    }
}
