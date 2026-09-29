package com.zynpath.game.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zynpath.game.core.database.entity.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT balanceAfter FROM wallet_transactions WHERE playerId = :playerId ORDER BY createdAt DESC, rowid DESC LIMIT 1")
    fun observeBalance(playerId: String): Flow<Int?>

    @Query("SELECT balanceAfter FROM wallet_transactions WHERE playerId = :playerId ORDER BY createdAt DESC, rowid DESC LIMIT 1")
    suspend fun getCurrentBalance(playerId: String): Int?

    @Query("SELECT COUNT(*) > 0 FROM wallet_transactions WHERE idempotencyKey = :idempotencyKey")
    suspend fun hasTransaction(idempotencyKey: String): Boolean

    @Query("SELECT * FROM wallet_transactions WHERE idempotencyKey = :idempotencyKey LIMIT 1")
    suspend fun getTransactionByIdempotencyKey(idempotencyKey: String): WalletTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)

    @Query("SELECT * FROM wallet_transactions WHERE playerId = :playerId ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentTransactions(playerId: String, limit: Int = 20): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions WHERE playerId = :playerId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentTransactionsList(playerId: String, limit: Int = 20): List<WalletTransactionEntity>

    @Query("SELECT COUNT(*) FROM wallet_transactions WHERE playerId = :playerId AND type = :type AND createdAt >= :sinceTimestamp")
    suspend fun countTransactionsSince(playerId: String, type: String, sinceTimestamp: Long): Int

    @Query("UPDATE wallet_transactions SET playerId = :newPlayerId WHERE playerId = :oldPlayerId")
    suspend fun reassignPlayerTransactions(oldPlayerId: String, newPlayerId: String)

    @Query("DELETE FROM wallet_transactions WHERE playerId = :playerId")
    suspend fun clearPlayerTransactions(playerId: String)
}
