package com.zynpath.game.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.zynpath.game.core.error.ErrorClassifier
import com.zynpath.game.core.error.ZynpathDiagnostics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setTutorialCompleted(completed: Boolean)
    suspend fun setTutorialStage(stage: Int)
    suspend fun setTutorialSkipped(skipped: Boolean)
    suspend fun markFeatureTipSeen(tipId: String)
    suspend fun resetTutorialProgress()
    suspend fun setSfxEnabled(enabled: Boolean)
    suspend fun setMusicEnabled(enabled: Boolean)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setThemePreference(theme: String)
    suspend fun setReducedMotion(reduced: Boolean)
    suspend fun setSelectedLanguage(language: String)
    suspend fun setLastSelectedWorld(worldId: Int)
    suspend fun setLastSelectedLevel(levelId: Int)
    suspend fun setFreeHintsRemaining(hints: Int)
    suspend fun setRewardedHintCredits(credits: Int)
    suspend fun addRewardedHintCredits(delta: Int)
    suspend fun setPremium(isPremium: Boolean)
    suspend fun setTapInputMode(enabled: Boolean)
    suspend fun setEquippedTheme(themeId: String)
    suspend fun setEquippedPathEffect(pathEffectId: String)
    suspend fun setEquippedAvatarFrame(avatarFrameId: String)
    suspend fun setEquippedCosmetics(themeId: String?, pathEffectId: String?, avatarFrameId: String?)
    suspend fun setFriendAlertsEnabled(enabled: Boolean)
    suspend fun setMultiplayerAlertsEnabled(enabled: Boolean)
    suspend fun setDailyReminderEnabled(enabled: Boolean)
    suspend fun setDailyReminderTime(hour: Int, minute: Int)
    suspend fun setPushToken(token: String)
    suspend fun setProfileVisibility(visibility: String)
    suspend fun setAllowZynpathIdSearch(allow: Boolean)
    suspend fun setAllowFriendRequests(allow: Boolean)
    suspend fun setHighContrast(enabled: Boolean)
    suspend fun setTouchSensitivity(sensitivity: Float)
    suspend fun clearAllPreferences()
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val TUTORIAL_COMPLETED = booleanPreferencesKey("tutorial_completed")
        val GUEST_UUID = stringPreferencesKey("guest_uuid")
        val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val LAST_SELECTED_WORLD = intPreferencesKey("last_selected_world")
        val LAST_SELECTED_LEVEL = intPreferencesKey("last_selected_level")
        val FREE_HINTS_REMAINING = intPreferencesKey("free_hints_remaining")
        val REWARDED_HINT_CREDITS = intPreferencesKey("rewarded_hint_credits")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val TAP_INPUT_MODE = booleanPreferencesKey("tap_input_mode")
        val EQUIPPED_THEME_ID = stringPreferencesKey("equipped_theme_id")
        val EQUIPPED_PATH_EFFECT_ID = stringPreferencesKey("equipped_path_effect_id")
        val EQUIPPED_AVATAR_FRAME_ID = stringPreferencesKey("equipped_avatar_frame_id")
        val FRIEND_ALERTS_ENABLED = booleanPreferencesKey("friend_alerts_enabled")
        val MULTIPLAYER_ALERTS_ENABLED = booleanPreferencesKey("multiplayer_alerts_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val PUSH_TOKEN = stringPreferencesKey("push_token")
        val PROFILE_VISIBILITY = stringPreferencesKey("profile_visibility")
        val ALLOW_ZYNPATH_ID_SEARCH = booleanPreferencesKey("allow_zynpath_id_search")
        val ALLOW_FRIEND_REQUESTS = booleanPreferencesKey("allow_friend_requests")
        val IS_HIGH_CONTRAST = booleanPreferencesKey("is_high_contrast")
        val TOUCH_SENSITIVITY = floatPreferencesKey("touch_sensitivity")
        val TUTORIAL_STAGE = intPreferencesKey("tutorial_stage")
        val TUTORIAL_SKIPPED = booleanPreferencesKey("tutorial_skipped")
        val SEEN_FEATURE_TIPS = stringSetPreferencesKey("seen_feature_tips")
    }

    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val guestId = preferences[PreferencesKeys.GUEST_UUID] ?: UUID.randomUUID().toString()
            UserPreferences(
                isOnboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
                isTutorialCompleted = preferences[PreferencesKeys.TUTORIAL_COMPLETED] ?: false,
                guestUuid = guestId,
                isSfxEnabled = preferences[PreferencesKeys.SFX_ENABLED] ?: true,
                isMusicEnabled = preferences[PreferencesKeys.MUSIC_ENABLED] ?: true,
                isHapticsEnabled = preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true,
                themePreference = preferences[PreferencesKeys.THEME_PREFERENCE] ?: "FOREST_NAVY",
                isReducedMotion = preferences[PreferencesKeys.REDUCED_MOTION] ?: false,
                selectedLanguage = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "en",
                lastSelectedWorld = preferences[PreferencesKeys.LAST_SELECTED_WORLD] ?: 1,
                lastSelectedLevel = preferences[PreferencesKeys.LAST_SELECTED_LEVEL] ?: 1,
                freeHintsRemaining = preferences[PreferencesKeys.FREE_HINTS_REMAINING] ?: 3,
                rewardedHintCredits = preferences[PreferencesKeys.REWARDED_HINT_CREDITS] ?: 0,
                isPremium = preferences[PreferencesKeys.IS_PREMIUM] ?: false,
                isTapInputMode = preferences[PreferencesKeys.TAP_INPUT_MODE] ?: false,
                equippedThemeId = preferences[PreferencesKeys.EQUIPPED_THEME_ID] ?: "theme_classic_midnight",
                equippedPathEffectId = preferences[PreferencesKeys.EQUIPPED_PATH_EFFECT_ID] ?: "path_solid_glow",
                equippedAvatarFrameId = preferences[PreferencesKeys.EQUIPPED_AVATAR_FRAME_ID] ?: "frame_default_slate",
                isFriendAlertsEnabled = preferences[PreferencesKeys.FRIEND_ALERTS_ENABLED] ?: true,
                isMultiplayerAlertsEnabled = preferences[PreferencesKeys.MULTIPLAYER_ALERTS_ENABLED] ?: true,
                isDailyReminderEnabled = preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: false,
                dailyReminderHour = preferences[PreferencesKeys.DAILY_REMINDER_HOUR] ?: 9,
                dailyReminderMinute = preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] ?: 0,
                pushToken = preferences[PreferencesKeys.PUSH_TOKEN] ?: "",
                profileVisibility = preferences[PreferencesKeys.PROFILE_VISIBILITY] ?: "PUBLIC",
                allowZynpathIdSearch = preferences[PreferencesKeys.ALLOW_ZYNPATH_ID_SEARCH] ?: true,
                allowFriendRequests = preferences[PreferencesKeys.ALLOW_FRIEND_REQUESTS] ?: true,
                isHighContrast = preferences[PreferencesKeys.IS_HIGH_CONTRAST] ?: false,
                touchSensitivity = preferences[PreferencesKeys.TOUCH_SENSITIVITY] ?: 1.0f,
                tutorialStage = preferences[PreferencesKeys.TUTORIAL_STAGE] ?: 1,
                isTutorialSkipped = preferences[PreferencesKeys.TUTORIAL_SKIPPED] ?: false,
                seenFeatureTips = preferences[PreferencesKeys.SEEN_FEATURE_TIPS] ?: emptySet()
            )
        }.distinctUntilChanged()

    private suspend fun safeEdit(transform: suspend (MutablePreferences) -> Unit) {
        try {
            dataStore.edit(transform)
        } catch (e: Exception) {
            ZynpathDiagnostics.recordError(
                "DATASTORE_WRITE_FAILURE",
                ErrorClassifier.classify(e)
            )
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun setTutorialCompleted(completed: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_COMPLETED] = completed
        }
    }

    override suspend fun setTutorialStage(stage: Int) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_STAGE] = stage
        }
    }

    override suspend fun setTutorialSkipped(skipped: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_SKIPPED] = skipped
        }
    }

    override suspend fun markFeatureTipSeen(tipId: String) {
        safeEdit { preferences ->
            val current = preferences[PreferencesKeys.SEEN_FEATURE_TIPS] ?: emptySet()
            preferences[PreferencesKeys.SEEN_FEATURE_TIPS] = current + tipId
        }
    }

    override suspend fun resetTutorialProgress() {
        safeEdit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_STAGE] = 1
            preferences[PreferencesKeys.TUTORIAL_COMPLETED] = false
            preferences[PreferencesKeys.TUTORIAL_SKIPPED] = false
        }
    }

    override suspend fun setSfxEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.SFX_ENABLED] = enabled
        }
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.MUSIC_ENABLED] = enabled
        }
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    override suspend fun setThemePreference(theme: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme
        }
    }

    override suspend fun setReducedMotion(reduced: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.REDUCED_MOTION] = reduced
        }
    }

    override suspend fun setSelectedLanguage(language: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language
        }
    }

    override suspend fun setLastSelectedWorld(worldId: Int) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_WORLD] = worldId
        }
    }

    override suspend fun setLastSelectedLevel(levelId: Int) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_LEVEL] = levelId
        }
    }

    override suspend fun setFreeHintsRemaining(hints: Int) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.FREE_HINTS_REMAINING] = hints
        }
    }

    override suspend fun setRewardedHintCredits(credits: Int) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.REWARDED_HINT_CREDITS] = credits.coerceAtLeast(0)
        }
    }

    override suspend fun addRewardedHintCredits(delta: Int) {
        safeEdit { preferences ->
            val current = preferences[PreferencesKeys.REWARDED_HINT_CREDITS] ?: 0
            preferences[PreferencesKeys.REWARDED_HINT_CREDITS] = (current + delta).coerceAtLeast(0)
        }
    }

    override suspend fun setPremium(isPremium: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] = isPremium
        }
    }

    override suspend fun setTapInputMode(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.TAP_INPUT_MODE] = enabled
        }
    }

    override suspend fun setEquippedTheme(themeId: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.EQUIPPED_THEME_ID] = themeId
        }
    }

    override suspend fun setEquippedPathEffect(pathEffectId: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.EQUIPPED_PATH_EFFECT_ID] = pathEffectId
        }
    }

    override suspend fun setEquippedAvatarFrame(avatarFrameId: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.EQUIPPED_AVATAR_FRAME_ID] = avatarFrameId
        }
    }

    override suspend fun setEquippedCosmetics(themeId: String?, pathEffectId: String?, avatarFrameId: String?) {
        safeEdit { preferences ->
            if (themeId != null) preferences[PreferencesKeys.EQUIPPED_THEME_ID] = themeId
            if (pathEffectId != null) preferences[PreferencesKeys.EQUIPPED_PATH_EFFECT_ID] = pathEffectId
            if (avatarFrameId != null) preferences[PreferencesKeys.EQUIPPED_AVATAR_FRAME_ID] = avatarFrameId
        }
    }

    override suspend fun setFriendAlertsEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.FRIEND_ALERTS_ENABLED] = enabled
        }
    }

    override suspend fun setMultiplayerAlertsEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.MULTIPLAYER_ALERTS_ENABLED] = enabled
        }
    }

    override suspend fun setDailyReminderEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = enabled
        }
    }

    override suspend fun setDailyReminderTime(hour: Int, minute: Int) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = hour
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = minute
        }
    }

    override suspend fun setPushToken(token: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.PUSH_TOKEN] = token
        }
    }

    override suspend fun setProfileVisibility(visibility: String) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.PROFILE_VISIBILITY] = visibility
        }
    }

    override suspend fun setAllowZynpathIdSearch(allow: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.ALLOW_ZYNPATH_ID_SEARCH] = allow
        }
    }

    override suspend fun setAllowFriendRequests(allow: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.ALLOW_FRIEND_REQUESTS] = allow
        }
    }

    override suspend fun setHighContrast(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.IS_HIGH_CONTRAST] = enabled
        }
    }

    override suspend fun setTouchSensitivity(sensitivity: Float) {
        safeEdit { preferences ->
            preferences[PreferencesKeys.TOUCH_SENSITIVITY] = sensitivity.coerceIn(0.5f, 2.0f)
        }
    }

    override suspend fun clearAllPreferences() {
        safeEdit { preferences ->
            preferences.clear()
            // Reset with fresh guest UUID and safe default preferences
            preferences[PreferencesKeys.GUEST_UUID] = UUID.randomUUID().toString()
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = true
            preferences[PreferencesKeys.TUTORIAL_COMPLETED] = true
            preferences[PreferencesKeys.SFX_ENABLED] = true
            preferences[PreferencesKeys.MUSIC_ENABLED] = true
            preferences[PreferencesKeys.HAPTICS_ENABLED] = true
            preferences[PreferencesKeys.THEME_PREFERENCE] = "FOREST_NAVY"
            preferences[PreferencesKeys.PROFILE_VISIBILITY] = "PUBLIC"
            preferences[PreferencesKeys.ALLOW_ZYNPATH_ID_SEARCH] = true
            preferences[PreferencesKeys.ALLOW_FRIEND_REQUESTS] = true
            preferences[PreferencesKeys.TUTORIAL_STAGE] = 1
            preferences[PreferencesKeys.TUTORIAL_SKIPPED] = false
            preferences[PreferencesKeys.SEEN_FEATURE_TIPS] = emptySet()
        }
    }
}
