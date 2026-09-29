package com.zynpath.backend.player.service;

import com.zynpath.backend.player.model.PlayerProfileSummary;
import java.util.Optional;

/**
 * Service contract for fetching and updating player profile information.
 */
public interface PlayerBoundaryService {
    Optional<PlayerProfileSummary> getProfile(String playerId);
}
