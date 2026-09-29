package com.zynpath.backend.league.model;

import java.util.List;

/**
 * Summary view of a 2-5 player private Mini League room.
 */
public record MiniLeagueSummary(
    String leagueId,
    String hostPlayerId,
    int playerCount,
    int maxPlayers,
    int currentRound,
    int totalRounds,
    List<String> playerIds
) {}
