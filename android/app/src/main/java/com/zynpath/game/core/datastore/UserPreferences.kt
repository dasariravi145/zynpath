package com.zynpath.game.core.datastore

data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val guestUuid: String = "",
    val isSfxEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val themePreference: String = "FOREST_NAVY",
    val isReducedMotion: Boolean = false,
    val freeHintsRemaining: Int = 3,
    val isPremium: Boolean = false
)
