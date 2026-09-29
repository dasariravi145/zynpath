package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zynpath.game.core.notification.model.NotificationEventType
import com.zynpath.game.core.notification.model.ZynpathNotification

/**
 * Local Room entity for in-app notification persistence.
 *
 * Implements Prompt 31 Section 8, 9, 46, 55:
 * - Offline-first in-app notification persistence.
 * - Indexed by recipient and read status for fast badge and list queries.
 */
@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["recipientPlayerId"]),
        Index(value = ["recipientPlayerId", "isRead"]),
        Index(value = ["createdAt"])
    ]
)
data class NotificationEntity(
    @PrimaryKey
    val notificationId: String,
    val recipientPlayerId: String,
    val eventType: String,
    val title: String,
    val message: String,
    val relatedResourceId: String?,
    val actionDestination: String?,
    val createdAt: Long,
    val expiresAt: Long?,
    val isRead: Boolean,
    val isDismissed: Boolean = false
) {
    fun toDomain(): ZynpathNotification {
        val type = try {
            NotificationEventType.valueOf(eventType)
        } catch (e: Exception) {
            NotificationEventType.FRIEND_REQUEST
        }

        return ZynpathNotification(
            id = notificationId,
            recipientPlayerId = recipientPlayerId,
            eventType = type,
            title = title,
            message = message,
            relatedResourceId = relatedResourceId,
            actionDestination = actionDestination,
            createdAt = createdAt,
            expiresAt = expiresAt,
            isRead = isRead,
            isDismissed = isDismissed
        )
    }

    companion object {
        fun fromDomain(notification: ZynpathNotification): NotificationEntity {
            return NotificationEntity(
                notificationId = notification.id,
                recipientPlayerId = notification.recipientPlayerId,
                eventType = notification.eventType.name,
                title = notification.title,
                message = notification.message,
                relatedResourceId = notification.relatedResourceId,
                actionDestination = notification.actionDestination,
                createdAt = notification.createdAt,
                expiresAt = notification.expiresAt,
                isRead = notification.isRead,
                isDismissed = notification.isDismissed
            )
        }
    }
}
