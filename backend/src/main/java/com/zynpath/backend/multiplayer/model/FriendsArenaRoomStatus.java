package com.zynpath.backend.multiplayer.model;

/**
 * Authoritative lifecycle status of a Friends Arena room.
 *
 * Implements Prompt 18 Task 2:
 * - WAITING: Room open, host waiting for participants (1-5 players).
 * - STARTING: Host initiated match start, eligibility validated.
 * - IN_GAME: Match session actively in progress.
 * - CLOSED: Room closed or disbanded.
 */
public enum FriendsArenaRoomStatus {
    WAITING,
    STARTING,
    IN_GAME,
    CLOSED
}
