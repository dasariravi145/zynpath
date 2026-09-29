package com.zynpath.backend.social.model;

/**
 * Mutual friend relationship between two authenticated players.
 *
 * Implements Prompt 19 Section 11 & 14:
 * - Symmetric relationship model
 * - Uniqueness and containment helpers
 */
public record FriendRelationship(
    String playerId1,
    String playerId2,
    long establishedAt
) {
    public boolean contains(String playerId) {
        return playerId1.equals(playerId) || playerId2.equals(playerId);
    }

    public String getOtherPlayerId(String playerId) {
        if (playerId1.equals(playerId)) return playerId2;
        if (playerId2.equals(playerId)) return playerId1;
        throw new IllegalArgumentException("Player " + playerId + " is not part of this friendship");
    }

    public static String buildCanonicalKey(String idA, String idB) {
        return idA.compareTo(idB) < 0 ? idA + ":" + idB : idB + ":" + idA;
    }
}
