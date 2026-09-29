package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zynpath.game.core.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for local notification records.
 *
 * Implements Prompt 31 Section 47, 48, 49:
 * - Read/unread filtering, unread badge count, mark as read, expiration cleanup.
 */
@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE recipientPlayerId = :playerId AND isDismissed = 0 ORDER BY createdAt DESC")
    fun observeNotifications(playerId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientPlayerId = :playerId AND isRead = 0 AND isDismissed = 0 AND (expiresAt IS NULL OR expiresAt > :now)")
    fun observeUnreadCount(playerId: String, now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("SELECT * FROM notifications WHERE recipientPlayerId = :playerId AND isDismissed = 0 ORDER BY createdAt DESC")
    suspend fun getNotificationsList(playerId: String): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientPlayerId = :playerId AND isRead = 0")
    suspend fun markAllAsRead(playerId: String)

    @Query("UPDATE notifications SET isDismissed = 1 WHERE notificationId = :notificationId")
    suspend fun dismissNotification(notificationId: String)

    @Query("DELETE FROM notifications WHERE expiresAt IS NOT NULL AND expiresAt < :now")
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())

    @Query("DELETE FROM notifications WHERE recipientPlayerId = :playerId")
    suspend fun clearAccountNotifications(playerId: String)
}
