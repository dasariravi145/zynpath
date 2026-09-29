package com.zynpath.game.core.datastore

/**
 * Immutable user preferences model persisted via Jetpack DataStore.
 */
data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val isTutorialCompleted: Boolean = false,
    val guestUuid: String = "",
    val isSfxEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val themePreference: String = "FOREST_NAVY",
    val isReducedMotion: Boolean = false,
    val selectedLanguage: String = "en",
    val lastSelectedWorld: Int = 1,
    val lastSelectedLevel: Int = 1,
    val freeHintsRemaining: Int = 3,
    val rewardedHintCredits: Int = 0,
    val isPremium: Boolean = false,
    val isTapInputMode: Boolean = false,
    val equippedThemeId: String = "theme_classic_midnight",
    val equippedPathEffectId: String = "path_solid_glow",
    val equippedAvatarFrameId: String = "frame_default_slate",
    val isFriendAlertsEnabled: Boolean = true,
    val isMultiplayerAlertsEnabled: Boolean = true,
    val isDailyReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 9,
    val dailyReminderMinute: Int = 0,
    val pushToken: String = "",
    val profileVisibility: String = "PUBLIC",
    val allowZynpathIdSearch: Boolean = true,
    val allowFriendRequests: Boolean = true,
    val isHighContrast: Boolean = false,
    val touchSensitivity: Float = 1.0f,
    val tutorialStage: Int = 1,
    val isTutorialSkipped: Boolean = false,
    val seenFeatureTips: Set<String> = emptySet()
)
