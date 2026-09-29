package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Authoritative local Room persistence for the player's profile and guest identity.
 *
 * Implements Prompt 17 Section 9:
 * - Internal player ID (stable UUID generated once)
 * - Display name (editable)
 * - Avatar selection
 * - Account type (GUEST, LINKING, LINKED, LINK_FAILED)
 * - Public Zynpath ID (null if unissued)
 */
@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey
    val playerId: String,
    val displayName: String = "Pathfinder",
    val avatarId: String = "avatar_compass",
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val accountType: String = "GUEST",
    val publicZynpathId: String? = null
)
