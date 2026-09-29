package com.zynpath.backend.multiplayer.model;

/**
 * Competitive online multiplayer game modes for Zynpath.
 *
 * Implements Prompt 20 Section 8:
 * - QUICK_DUEL: Two automatically matched players.
 * - FRIEND_DUEL: Two players connected through direct friend invitation.
 * - MINI_LEAGUE: 2 to 5 total participants (host + up to 4 invitees).
 */
public enum GameMode {
    QUICK_DUEL(2, 2),
    FRIEND_DUEL(2, 2),
    MINI_LEAGUE(2, 5),
    FRIENDS_ARENA(2, 5);

    private final int minParticipants;
    private final int maxParticipants;

    GameMode(int minParticipants, int maxParticipants) {
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
    }

    public int getMinParticipants() {
        return minParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }
}
