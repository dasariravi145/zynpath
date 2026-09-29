package com.zynpath.game.fake

import com.zynpath.game.core.database.dao.GameSessionDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.PlayerStatsDao
import com.zynpath.game.core.database.dao.SessionRevisionTuple
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.entity.PlayerStatsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeLevelProgressDao : LevelProgressDao {
    private val records = mutableMapOf<Int, LevelProgressEntity>()
    private val flow = MutableStateFlow<List<LevelProgressEntity>>(emptyList())

    private fun emit() {
        flow.value = records.values.toList()
    }

    override fun getLevelsForWorld(worldId: Int): Flow<List<LevelProgressEntity>> {
        return flow.map { list -> list.filter { it.worldId == worldId } }
    }

    override suspend fun getLevelProgress(levelId: Int): LevelProgressEntity? {
        return records[levelId]
    }

    override fun observeLevelProgress(levelId: Int): Flow<LevelProgressEntity?> {
        return flow.map { records[levelId] }
    }

    override fun observeAllProgress(): Flow<List<LevelProgressEntity>> = flow

    override fun getCompletedLevelCount(): Flow<Int> {
        return flow.map { list -> list.count { it.isCompleted } }
    }

    override fun getTotalStarsEarned(): Flow<Int?> {
        return flow.map { list -> list.sumOf { it.stars } }
    }

    override suspend fun getAllProgressList(): List<LevelProgressEntity> {
        return records.values.toList()
    }

    override suspend fun upsertLevelProgress(progress: LevelProgressEntity) {
        records[progress.levelId] = progress
        emit()
    }

    override suspend fun atomicUpsertProgress(progress: LevelProgressEntity) {
        upsertLevelProgress(progress)
    }

    override suspend fun deleteProgressForLevel(levelId: Int) {
        records.remove(levelId)
        emit()
    }

    override suspend fun clearAllProgress() {
        records.clear()
        emit()
    }
}

class FakePlayerStatsDao : PlayerStatsDao {
    private var stats = PlayerStatsEntity()
    private val flow = MutableStateFlow<PlayerStatsEntity?>(stats)

    override fun getPlayerStats(): Flow<PlayerStatsEntity?> = flow

    override suspend fun upsertPlayerStats(newStats: PlayerStatsEntity) {
        stats = newStats
        flow.value = stats
    }

    override suspend fun clearStats() {
        stats = PlayerStatsEntity()
        flow.value = stats
    }
}

class FakeGameSessionDao : GameSessionDao {
    private val sessions = mutableMapOf<String, GameSessionEntity>()

    override suspend fun insertSession(session: GameSessionEntity) {
        sessions[session.sessionId] = session
    }

    override suspend fun updateSession(session: GameSessionEntity) {
        sessions[session.sessionId] = session
    }

    override suspend fun getSessionById(sessionId: String): GameSessionEntity? {
        return sessions[sessionId]
    }

    override suspend fun getResumableSessionForLevel(levelId: Int): GameSessionEntity? {
        return sessions.values
            .filter { it.levelId == levelId && (it.status == "ACTIVE" || it.status == "PAUSED" || it.status == "NOT_STARTED") }
            .sortedWith(compareByDescending<GameSessionEntity> { it.revision }.thenByDescending { it.lastUpdatedAt })
            .firstOrNull()
    }

    override suspend fun getActiveSession(levelId: Int): GameSessionEntity? {
        return sessions.values.firstOrNull { it.levelId == levelId && it.status == "ACTIVE" }
    }

    override suspend fun getLatestResumableSession(): GameSessionEntity? {
        return sessions.values
            .filter { it.status == "ACTIVE" || it.status == "PAUSED" }
            .maxByOrNull { it.lastUpdatedAt }
    }

    override suspend fun getSessionRevisionAndStatus(sessionId: String): SessionRevisionTuple? {
        val s = sessions[sessionId] ?: return null
        return SessionRevisionTuple(s.revision, s.status)
    }

    override suspend fun pauseAllActiveSessions(timestamp: Long) {
        for ((key, value) in sessions.toMap()) {
            if (value.status == "ACTIVE") {
                sessions[key] = value.copy(status = "PAUSED", lastUpdatedAt = timestamp)
            }
        }
    }

    override suspend fun abandonActiveSession(levelId: Int, timestamp: Long) {
        val active = sessions.values.firstOrNull { it.levelId == levelId && (it.status == "ACTIVE" || it.status == "PAUSED") }
        if (active != null) {
            sessions[active.sessionId] = active.copy(
                status = "ABANDONED",
                lastUpdatedAt = timestamp
            )
        }
    }

    override suspend fun deleteSessionById(sessionId: String) {
        sessions.remove(sessionId)
    }

    override suspend fun deleteSessionsForLevel(levelId: Int) {
        val keysToRemove = sessions.filter { it.value.levelId == levelId }.keys
        keysToRemove.forEach { sessions.remove(it) }
    }

    override suspend fun clearAllSessions() {
        sessions.clear()
    }
}

class FakeNotificationDao : com.zynpath.game.core.database.dao.NotificationDao {
    private val records = mutableMapOf<String, com.zynpath.game.core.database.entity.NotificationEntity>()
    private val flow = MutableStateFlow<List<com.zynpath.game.core.database.entity.NotificationEntity>>(emptyList())

    private fun emit() {
        flow.value = records.values.toList()
    }

    override fun observeNotifications(playerId: String): Flow<List<com.zynpath.game.core.database.entity.NotificationEntity>> {
        return flow.map { list ->
            list.filter { it.recipientPlayerId == playerId && !it.isDismissed }
                .sortedByDescending { it.createdAt }
        }
    }

    override fun observeUnreadCount(playerId: String, now: Long): Flow<Int> {
        return flow.map { list ->
            list.count {
                it.recipientPlayerId == playerId && !it.isRead && !it.isDismissed &&
                    (it.expiresAt == null || it.expiresAt > now)
            }
        }
    }

    override suspend fun getNotificationsList(playerId: String): List<com.zynpath.game.core.database.entity.NotificationEntity> {
        return records.values.filter { it.recipientPlayerId == playerId && !it.isDismissed }
            .sortedByDescending { it.createdAt }
    }

    override suspend fun insertNotifications(notifications: List<com.zynpath.game.core.database.entity.NotificationEntity>) {
        notifications.forEach { records[it.notificationId] = it }
        emit()
    }

    override suspend fun insertNotification(notification: com.zynpath.game.core.database.entity.NotificationEntity) {
        records[notification.notificationId] = notification
        emit()
    }

    override suspend fun markAsRead(notificationId: String) {
        records[notificationId]?.let {
            records[notificationId] = it.copy(isRead = true)
            emit()
        }
    }

    override suspend fun markAllAsRead(playerId: String) {
        records.values.filter { it.recipientPlayerId == playerId && !it.isRead }.forEach {
            records[it.notificationId] = it.copy(isRead = true)
        }
        emit()
    }

    override suspend fun dismissNotification(notificationId: String) {
        records[notificationId]?.let {
            records[notificationId] = it.copy(isDismissed = true)
            emit()
        }
    }

    override suspend fun deleteExpired(now: Long) {
        records.values.filter { it.expiresAt != null && it.expiresAt < now }.forEach {
            records.remove(it.notificationId)
        }
        emit()
    }

    override suspend fun clearAccountNotifications(playerId: String) {
        records.values.filter { it.recipientPlayerId == playerId }.forEach {
            records.remove(it.notificationId)
        }
        emit()
    }
}

class FakeSyncOperationDao : com.zynpath.game.core.database.dao.SyncOperationDao {
    private val operations = mutableMapOf<String, com.zynpath.game.core.sync.model.SyncOperationEntity>()
    private val flow = MutableStateFlow<List<com.zynpath.game.core.sync.model.SyncOperationEntity>>(emptyList())

    private fun emit() {
        flow.value = operations.values.toList()
    }

    override suspend fun insertOperation(operation: com.zynpath.game.core.sync.model.SyncOperationEntity) {
        operations[operation.operationId] = operation
        emit()
    }

    override suspend fun insertOperations(operations: List<com.zynpath.game.core.sync.model.SyncOperationEntity>) {
        operations.forEach { this.operations[it.operationId] = it }
        emit()
    }

    override suspend fun updateOperation(operation: com.zynpath.game.core.sync.model.SyncOperationEntity) {
        operations[operation.operationId] = operation
        emit()
    }

    override suspend fun getOperationById(operationId: String): com.zynpath.game.core.sync.model.SyncOperationEntity? {
        return operations[operationId]
    }

    override suspend fun getPendingOperationForResource(ownerIdentity: String, resourceIdentity: String): com.zynpath.game.core.sync.model.SyncOperationEntity? {
        return operations.values.firstOrNull {
            it.ownerIdentity == ownerIdentity &&
            it.resourceIdentity == resourceIdentity &&
            (it.status == "PENDING" || it.status == "RETRYABLE_FAILURE")
        }
    }

    override suspend fun getEligibleOperations(ownerIdentity: String, limit: Int): List<com.zynpath.game.core.sync.model.SyncOperationEntity> {
        return operations.values
            .filter { it.ownerIdentity == ownerIdentity && (it.status == "PENDING" || it.status == "RETRYABLE_FAILURE") }
            .sortedBy { it.createdAt }
            .take(limit)
    }

    override fun observePendingCount(ownerIdentity: String): Flow<Int> {
        return flow.map { list ->
            list.count { it.ownerIdentity == ownerIdentity && (it.status == "PENDING" || it.status == "RETRYABLE_FAILURE") }
        }
    }

    override suspend fun markInProgress(operationIds: List<String>, timestamp: Long) {
        operationIds.forEach { id ->
            operations[id]?.let {
                operations[id] = it.copy(
                    status = "IN_PROGRESS",
                    lastAttemptAt = timestamp,
                    attemptCount = it.attemptCount + 1
                )
            }
        }
        emit()
    }

    override suspend fun markSucceeded(operationIds: List<String>) {
        operationIds.forEach { id ->
            operations[id]?.let {
                operations[id] = it.copy(status = "SUCCEEDED", lastError = null)
            }
        }
        emit()
    }

    override suspend fun markFailed(operationId: String, error: String, retryable: Boolean, timestamp: Long) {
        operations[operationId]?.let {
            operations[operationId] = it.copy(
                status = if (retryable) "RETRYABLE_FAILURE" else "PERMANENT_FAILURE",
                lastError = error,
                lastAttemptAt = timestamp
            )
        }
        emit()
    }

    override suspend fun deleteOldSucceeded(olderThanTimestamp: Long): Int {
        val toRemove = operations.values.filter { it.status == "SUCCEEDED" && it.createdAt < olderThanTimestamp }.map { it.operationId }
        toRemove.forEach { operations.remove(it) }
        emit()
        return toRemove.size
    }

    override suspend fun deleteOperationsForOwner(ownerIdentity: String) {
        val toRemove = operations.values.filter { it.ownerIdentity == ownerIdentity }.map { it.operationId }
        toRemove.forEach { operations.remove(it) }
        emit()
    }

    override suspend fun getTotalOperationsCountForOwner(ownerIdentity: String): Int {
        return operations.values.count { it.ownerIdentity == ownerIdentity }
    }

    override suspend fun rebindOperationsToNewOwner(oldOwner: String, newOwner: String): Int {
        val toRebind = operations.values.filter { it.ownerIdentity == oldOwner && it.status != "SUCCEEDED" }
        toRebind.forEach {
            operations[it.operationId] = it.copy(ownerIdentity = newOwner)
        }
        emit()
        return toRebind.size
    }
}

class FakeWalletDao : com.zynpath.game.core.database.dao.WalletDao {
    val transactions = mutableListOf<com.zynpath.game.core.database.entity.WalletTransactionEntity>()
    private val balanceFlows = mutableMapOf<String, MutableStateFlow<Int?>>()

    private fun getOrCreateFlow(playerId: String): MutableStateFlow<Int?> {
        return balanceFlows.getOrPut(playerId) { MutableStateFlow(transactions.filter { it.playerId == playerId }.lastOrNull()?.balanceAfter) }
    }

    override fun observeBalance(playerId: String): Flow<Int?> = getOrCreateFlow(playerId)

    override suspend fun getCurrentBalance(playerId: String): Int? {
        val playerTxs = transactions.filter { it.playerId == playerId }
        return if (playerTxs.isEmpty()) null else playerTxs.last().balanceAfter
    }

    override suspend fun insertTransaction(transaction: com.zynpath.game.core.database.entity.WalletTransactionEntity) {
        transactions.add(transaction)
        getOrCreateFlow(transaction.playerId).value = transaction.balanceAfter
    }

    override suspend fun getTransactionByIdempotencyKey(idempotencyKey: String): com.zynpath.game.core.database.entity.WalletTransactionEntity? {
        return transactions.firstOrNull { it.idempotencyKey == idempotencyKey }
    }

    override suspend fun hasTransaction(idempotencyKey: String): Boolean {
        return transactions.any { it.idempotencyKey == idempotencyKey }
    }

    override fun observeRecentTransactions(playerId: String, limit: Int): Flow<List<com.zynpath.game.core.database.entity.WalletTransactionEntity>> {
        return kotlinx.coroutines.flow.flow {
            emit(transactions.filter { it.playerId == playerId }.sortedByDescending { it.createdAt }.take(limit))
        }
    }

    override suspend fun getRecentTransactionsList(playerId: String, limit: Int): List<com.zynpath.game.core.database.entity.WalletTransactionEntity> {
        return transactions.filter { it.playerId == playerId }.sortedByDescending { it.createdAt }.take(limit)
    }

    override suspend fun countTransactionsSince(playerId: String, type: String, sinceTimestamp: Long): Int {
        return transactions.count { it.playerId == playerId && it.type == type && it.createdAt >= sinceTimestamp }
    }

    override suspend fun reassignPlayerTransactions(oldPlayerId: String, newPlayerId: String) {
        val updated = transactions.map {
            if (it.playerId == oldPlayerId) {
                it.copy(playerId = newPlayerId)
            } else it
        }
        transactions.clear()
        transactions.addAll(updated)
        getOrCreateFlow(newPlayerId).value = getCurrentBalance(newPlayerId)
    }

    override suspend fun clearPlayerTransactions(playerId: String) {
        transactions.removeAll { it.playerId == playerId }
        getOrCreateFlow(playerId).value = null
    }
}

