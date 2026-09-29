package com.zynpath.backend.reaction.model;

/**
 * Ephemeral reaction payload relayed between match participants.
 * Zero database persistence or audit archives are created for reactions.
 */
public record EphemeralReactionEvent(
    String matchId,
    String senderId,
    ReactionType reaction,
    long timestamp
) {}
