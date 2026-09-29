package com.zynpath.backend.security.model;

/**
 * Access tiers defining authorization boundaries across Zynpath backend REST APIs.
 *
 * Implements Prompt 36 Section 7 & 8:
 * - Explicit classification for every endpoint.
 * - Enforces Deny-By-Default on unclassified resources.
 */
public enum EndpointAccessTier {
    /** Open endpoints that do not require an active session token (e.g. login, public catalog, health). */
    PUBLIC,

    /** Requires an active, non-expired PlayerSession bearer token. */
    AUTHENTICATED,

    /** Resource is private to the authenticated player; principal must match target resource owner. */
    OWNER_ONLY,

    /** Action requires an established mutual accepted friendship between participants. */
    FRIEND_RELATIONSHIP_REQUIRED,

    /** Action or subscription requires active participant membership in the specified match. */
    MATCH_PARTICIPANT_ONLY,

    /** Action or subscription requires active room membership in the specified Mini League room. */
    ROOM_PARTICIPANT_ONLY,

    /** Reserved administrative operations requiring elevated administrative privileges. */
    ADMIN_ONLY
}
