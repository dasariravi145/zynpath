package com.zynpath.game.core.account.model

data class DataExportResult(
    val schemaVersion: Int,
    val exportedAt: Long,
    val jsonContent: String
)
