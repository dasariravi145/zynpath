package com.zynpath.game.core.account.model

data class AccountDeletionResult(
    val status: String,
    val playerId: String,
    val deletedAt: Long,
    val googlePlaySubscriptionNotice: String
)
