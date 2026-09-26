package com.zynpath.game.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setSfxEnabled(enabled: Boolean)
    suspend fun setMusicEnabled(enabled: Boolean)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setThemePreference(theme: String)
    suspend fun setReducedMotion(reduced: Boolean)
    suspend fun setPremium(isPremium: Boolean)
}

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val GUEST_UUID = stringPreferencesKey("guest_uuid")
        val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val FREE_HINTS_REMAINING = intPreferencesKey("free_hints_remaining")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
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
                guestUuid = guestId,
                isSfxEnabled = preferences[PreferencesKeys.SFX_ENABLED] ?: true,
                isMusicEnabled = preferences[PreferencesKeys.MUSIC_ENABLED] ?: true,
                isHapticsEnabled = preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true,
                themePreference = preferences[PreferencesKeys.THEME_PREFERENCE] ?: "FOREST_NAVY",
                isReducedMotion = preferences[PreferencesKeys.REDUCED_MOTION] ?: false,
                freeHintsRemaining = preferences[PreferencesKeys.FREE_HINTS_REMAINING] ?: 3,
                isPremium = preferences[PreferencesKeys.IS_PREMIUM] ?: false
            )
        }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    override suspend fun setSfxEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SFX_ENABLED] = enabled
        }
    }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MUSIC_ENABLED] = enabled
        }
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    override suspend fun setThemePreference(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme
        }
    }

    override suspend fun setReducedMotion(reduced: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REDUCED_MOTION] = reduced
        }
    }

    override suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_PREMIUM] = isPremium
        }
    }
}
