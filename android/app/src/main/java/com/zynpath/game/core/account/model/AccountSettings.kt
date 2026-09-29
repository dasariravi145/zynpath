package com.zynpath.game.core.account.model

data class AccountSettings(
    val playerId: String,
    val publicZynpathId: String,
    val displayName: String,
    val email: String?,
    val avatarUrl: String?,
    val linkedProviders: List<String>,
    val privacySettings: PlayerPrivacySettings,
    val isPremium: Boolean,
    val createdAt: Long
)
