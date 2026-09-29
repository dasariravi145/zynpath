package com.zynpath.game.core.notification.repository

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.database.dao.NotificationDao
import com.zynpath.game.core.database.entity.NotificationEntity
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.notification.model.NotificationPreferences
import com.zynpath.game.core.notification.model.ZynpathNotification
import com.zynpath.game.core.notification.network.NotificationApiService
import com.zynpath.game.core.notification.push.PushTokenManager
import com.zynpath.game.core.notification.reminder.DailyReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first implementation of NotificationRepository.
 *
 * Implements Prompt 31 Section 7, 9, 10, 46, 47, 48, 50:
 * - Local Room persistence ensures in-app notifications are readable offline.
 * - Synchronizes with backend when authenticated.
 * - Isolates account state on sign-out.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationDao: NotificationDao,
    private val notificationApiService: NotificationApiService,
    private val preferencesRepository: PreferencesRepository,
    private val tokenStorage: SecureTokenStorage,
    private val dailyReminderScheduler: DailyReminderScheduler,
    private val pushTokenManager: PushTokenManager,
    private val playerProfileRepository: com.zynpath.game.core.player.PlayerProfileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotificationRepository {

    private val tag = "NotificationRepository"

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override val notifications: Flow<List<ZynpathNotification>> =
        playerProfileRepository.observeProfile()
            .flatMapLatest { profile ->
                notificationDao.observeNotifications(profile.playerId)
                    .map { list -> list.map { it.toDomain() } }
            }
            .flowOn(ioDispatcher)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override val unreadCount: Flow<Int> =
        playerProfileRepository.observeProfile()
            .flatMapLatest { profile ->
                notificationDao.observeUnreadCount(profile.playerId)
            }
            .flowOn(ioDispatcher)

    override val preferences: Flow<NotificationPreferences> =
        preferencesRepository.userPreferencesFlow.map { userPrefs ->
            val systemAllowed = NotificationManagerCompat.from(context).areNotificationsEnabled()
            NotificationPreferences(
                friendAlertsEnabled = userPrefs.isFriendAlertsEnabled,
                multiplayerAlertsEnabled = userPrefs.isMultiplayerAlertsEnabled,
                dailyReminderEnabled = userPrefs.isDailyReminderEnabled,
                dailyReminderHour = userPrefs.dailyReminderHour,
                dailyReminderMinute = userPrefs.dailyReminderMinute,
                isSystemPermissionGranted = systemAllowed
            )
        }.flowOn(ioDispatcher)

    override suspend fun refreshNotifications() = withContext(ioDispatcher) {
        val authToken = tokenStorage.getSessionToken()
        if (authToken.isNullOrBlank()) {
            Log.d(tag, "Guest or unauthenticated session, relying on local offline notifications")
            return@withContext
        }

        val profile = playerProfileRepository.getProfile()
        when (val result = notificationApiService.getNotifications(authToken, limit = 50)) {
            is NetworkResult.Success -> {
                val entities = result.data.map {
                    NotificationEntity.fromDomain(it.copy(recipientPlayerId = profile.playerId))
                }
                notificationDao.insertNotifications(entities)
                notificationDao.deleteExpired()
                Log.i(tag, "Synchronized ${entities.size} notifications from backend for ${profile.playerId}")
            }
            is NetworkResult.Error -> {
                Log.w(tag, "Failed to refresh remote notifications: ${result.message}")
            }
            is NetworkResult.Exception -> {
                Log.w(tag, "Network exception fetching notifications: ${result.throwable.message}")
            }
        }
    }

    override suspend fun markAsRead(notificationId: String) = withContext(ioDispatcher) {
        notificationDao.markAsRead(notificationId)
        val authToken = tokenStorage.getSessionToken()
        if (!authToken.isNullOrBlank()) {
            notificationApiService.markAsRead(authToken, notificationId)
        }
    }

    override suspend fun markAllAsRead() = withContext(ioDispatcher) {
        val profile = playerProfileRepository.getProfile()
        notificationDao.markAllAsRead(profile.playerId)
        val authToken = tokenStorage.getSessionToken()
        if (!authToken.isNullOrBlank()) {
            notificationApiService.markAllAsRead(authToken)
        }
    }

    override suspend fun dismissNotification(notificationId: String) = withContext(ioDispatcher) {
        notificationDao.dismissNotification(notificationId)
    }

    override suspend fun updatePreferences(preferences: NotificationPreferences) = withContext(ioDispatcher) {
        preferencesRepository.setFriendAlertsEnabled(preferences.friendAlertsEnabled)
        preferencesRepository.setMultiplayerAlertsEnabled(preferences.multiplayerAlertsEnabled)
        preferencesRepository.setDailyReminderEnabled(preferences.dailyReminderEnabled)
        preferencesRepository.setDailyReminderTime(preferences.dailyReminderHour, preferences.dailyReminderMinute)

        // Reschedule or cancel daily reminder alarm
        if (preferences.dailyReminderEnabled) {
            dailyReminderScheduler.scheduleDailyReminder(
                preferences.dailyReminderHour,
                preferences.dailyReminderMinute
            )
        } else {
            dailyReminderScheduler.cancelDailyReminder()
        }

        // Synchronize with backend if online
        val authToken = tokenStorage.getSessionToken()
        if (!authToken.isNullOrBlank()) {
            notificationApiService.updatePreferences(authToken, preferences)
        }
    }

    override suspend fun onSignOut(): Unit = withContext(ioDispatcher) {
        pushTokenManager.unregisterOnSignOut()
        val profile = playerProfileRepository.getProfile()
        notificationDao.clearAccountNotifications(profile.playerId)
        val meta = tokenStorage.getSessionMetadata()
        if (meta != null && meta.playerId != profile.playerId) {
            notificationDao.clearAccountNotifications(meta.playerId)
        }
        Log.i(tag, "Cleared account notifications on sign out for: ${profile.playerId}")
        Unit
    }
}
