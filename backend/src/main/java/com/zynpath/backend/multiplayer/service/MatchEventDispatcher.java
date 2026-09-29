package com.zynpath.backend.multiplayer.service;

import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;

/**
 * Contract for dispatching real-time multiplayer events to active sessions and connected players.
 *
 * Implements Prompt 20 Section 27 & 29.
 */
public interface MatchEventDispatcher {
    void dispatchToMatch(String matchId, MultiplayerEventEnvelope envelope);
    void dispatchToPlayer(String playerId, MultiplayerEventEnvelope envelope);
    void dispatchToFriendsArenaRoom(String roomId, MultiplayerEventEnvelope envelope);
}
