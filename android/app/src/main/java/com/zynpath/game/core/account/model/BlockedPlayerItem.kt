package com.zynpath.game.core.account.model

data class BlockedPlayerItem(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val avatarUrl: String?,
    val blockedAt: Long
)
