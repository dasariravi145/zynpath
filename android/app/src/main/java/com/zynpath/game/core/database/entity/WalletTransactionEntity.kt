package com.zynpath.game.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Immutable ledger entry recording every virtual coin credit and debit in Zynpath.
 * Enforces transaction deduplication via a unique index on [idempotencyKey].
 */
@Entity(
    tableName = "wallet_transactions",
    indices = [
        Index(value = ["idempotencyKey"], unique = true),
        Index(value = ["playerId", "createdAt"]),
        Index(value = ["playerId", "type"])
    ]
)
data class WalletTransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val playerId: String,
    val type: String,
    val amount: Int,
    val balanceAfter: Int,
    val idempotencyKey: String,
    val metadataJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis()
)
