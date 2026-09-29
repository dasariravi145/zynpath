package com.zynpath.backend.matchmaking.service;

/**
 * Contract for managing the in-memory 1v1 matchmaking pool.
 */
public interface MatchmakingQueueService {
    String enqueue(String playerId, int eloRating);
    boolean dequeue(String ticketId);
}
