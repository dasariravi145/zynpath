package com.zynpath.game.fake

import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePreferencesRepository(
    initialPreferences: UserPreferences = UserPreferences()
) : PreferencesRepository {

    private val _preferences = MutableStateFlow(initialPreferences)
    override val userPreferencesFlow: Flow<UserPreferences> = _preferences.asStateFlow()

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        _preferences.value = _preferences.value.copy(isOnboardingCompleted = completed)
    }

    override suspend fun setTutorialCompleted(completed: Boolean) {
        _preferences.value = _preferences.value.copy(isTutorialCompleted = completed)
    }

    override suspend fun setSfxEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isSfxEnabled = enabled)
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isMusicEnabled = enabled)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isHapticsEnabled = enabled)
    }

    override suspend fun setThemePreference(theme: String) {
        _preferences.value = _preferences.value.copy(themePreference = theme)
    }

    override suspend fun setReducedMotion(reduced: Boolean) {
        _preferences.value = _preferences.value.copy(isReducedMotion = reduced)
    }

    override suspend fun setSelectedLanguage(language: String) {
        _preferences.value = _preferences.value.copy(selectedLanguage = language)
    }

    override suspend fun setLastSelectedWorld(worldId: Int) {
        _preferences.value = _preferences.value.copy(lastSelectedWorld = worldId)
    }

    override suspend fun setLastSelectedLevel(levelId: Int) {
        _preferences.value = _preferences.value.copy(lastSelectedLevel = levelId)
    }

    override suspend fun setFreeHintsRemaining(hints: Int) {
        _preferences.value = _preferences.value.copy(freeHintsRemaining = hints)
    }

    override suspend fun setRewardedHintCredits(credits: Int) {
        _preferences.value = _preferences.value.copy(rewardedHintCredits = credits)
    }

    override suspend fun addRewardedHintCredits(delta: Int) {
        val current = _preferences.value.rewardedHintCredits
        _preferences.value = _preferences.value.copy(rewardedHintCredits = (current + delta).coerceAtLeast(0))
    }

    override suspend fun setPremium(isPremium: Boolean) {
        _preferences.value = _preferences.value.copy(isPremium = isPremium)
    }

    override suspend fun setTapInputMode(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isTapInputMode = enabled)
    }

    override suspend fun setEquippedTheme(themeId: String) {
        _preferences.value = _preferences.value.copy(equippedThemeId = themeId)
    }

    override suspend fun setEquippedPathEffect(pathEffectId: String) {
        _preferences.value = _preferences.value.copy(equippedPathEffectId = pathEffectId)
    }

    override suspend fun setEquippedAvatarFrame(avatarFrameId: String) {
        _preferences.value = _preferences.value.copy(equippedAvatarFrameId = avatarFrameId)
    }

    override suspend fun setEquippedCosmetics(themeId: String?, pathEffectId: String?, avatarFrameId: String?) {
        _preferences.value = _preferences.value.copy(
            equippedThemeId = themeId ?: _preferences.value.equippedThemeId,
            equippedPathEffectId = pathEffectId ?: _preferences.value.equippedPathEffectId,
            equippedAvatarFrameId = avatarFrameId ?: _preferences.value.equippedAvatarFrameId
        )
    }

    override suspend fun setFriendAlertsEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isFriendAlertsEnabled = enabled)
    }

    override suspend fun setMultiplayerAlertsEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isMultiplayerAlertsEnabled = enabled)
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isDailyReminderEnabled = enabled)
    }

    override suspend fun setDailyReminderTime(hour: Int, minute: Int) {
        _preferences.value = _preferences.value.copy(
            dailyReminderHour = hour,
            dailyReminderMinute = minute
        )
    }

    override suspend fun setPushToken(token: String) {
        _preferences.value = _preferences.value.copy(pushToken = token)
    }

    override suspend fun setProfileVisibility(visibility: String) {
        _preferences.value = _preferences.value.copy(profileVisibility = visibility)
    }

    override suspend fun setAllowZynpathIdSearch(allow: Boolean) {
        _preferences.value = _preferences.value.copy(allowZynpathIdSearch = allow)
    }

    override suspend fun setAllowFriendRequests(allow: Boolean) {
        _preferences.value = _preferences.value.copy(allowFriendRequests = allow)
    }

    override suspend fun setHighContrast(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(isHighContrast = enabled)
    }

    override suspend fun setTouchSensitivity(sensitivity: Float) {
        _preferences.value = _preferences.value.copy(touchSensitivity = sensitivity)
    }

    override suspend fun setTutorialStage(stage: Int) {
        _preferences.value = _preferences.value.copy(tutorialStage = stage)
    }

    override suspend fun setTutorialSkipped(skipped: Boolean) {
        _preferences.value = _preferences.value.copy(isTutorialSkipped = skipped)
    }

    override suspend fun markFeatureTipSeen(tipId: String) {}

    override suspend fun resetTutorialProgress() {
        _preferences.value = _preferences.value.copy(tutorialStage = 0, isTutorialCompleted = false)
    }

    override suspend fun clearAllPreferences() {
        _preferences.value = UserPreferences()
    }
}
