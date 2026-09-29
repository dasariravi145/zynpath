package com.zynpath.backend.reaction.service;

import com.zynpath.backend.reaction.model.EphemeralReactionEvent;

/**
 * Service contract for rate-limited, in-memory reaction dispatching.
 */
public interface ReactionRelayService {
    boolean relayReaction(EphemeralReactionEvent event);
}
