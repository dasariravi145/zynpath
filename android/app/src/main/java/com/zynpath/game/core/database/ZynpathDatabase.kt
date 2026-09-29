package com.zynpath.game.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zynpath.game.core.database.dao.AchievementDao
import com.zynpath.game.core.database.dao.DailyChallengeDao
import com.zynpath.game.core.database.dao.GameSessionDao
import com.zynpath.game.core.database.dao.LevelProgressDao
import com.zynpath.game.core.database.dao.NotificationDao
import com.zynpath.game.core.database.dao.PlayerProfileDao
import com.zynpath.game.core.database.dao.PlayerStatsDao
import com.zynpath.game.core.database.dao.PremiumPackProgressDao
import com.zynpath.game.core.database.entity.AchievementEntity
import com.zynpath.game.core.database.entity.DailyChallengeEntity
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.dao.SyncOperationDao
import com.zynpath.game.core.database.entity.NotificationEntity
import com.zynpath.game.core.database.entity.PlayerProfileEntity
import com.zynpath.game.core.database.entity.PlayerStatsEntity
import com.zynpath.game.core.database.entity.PremiumPackProgressEntity
import com.zynpath.game.core.database.entity.WalletTransactionEntity
import com.zynpath.game.core.database.dao.WalletDao
import com.zynpath.game.core.sync.model.SyncOperationEntity

@Database(
    entities = [
        LevelProgressEntity::class,
        PlayerStatsEntity::class,
        DailyChallengeEntity::class,
        GameSessionEntity::class,
        PlayerProfileEntity::class,
        AchievementEntity::class,
        PremiumPackProgressEntity::class,
        NotificationEntity::class,
        SyncOperationEntity::class,
        WalletTransactionEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class ZynpathDatabase : RoomDatabase() {
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun playerStatsDao(): PlayerStatsDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun premiumPackProgressDao(): PremiumPackProgressDao
    abstract fun notificationDao(): NotificationDao
    abstract fun syncOperationDao(): SyncOperationDao
    abstract fun walletDao(): WalletDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to level_progress
                db.execSQL("ALTER TABLE level_progress ADD COLUMN isUnlocked INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE level_progress ADD COLUMN bestHintCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE level_progress ADD COLUMN completionCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE level_progress ADD COLUMN firstCompletedAt INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE level_progress ADD COLUMN lastCompletedAt INTEGER DEFAULT NULL")

                // Create game_sessions table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS game_sessions (
                        sessionId TEXT NOT NULL PRIMARY KEY,
                        levelId INTEGER NOT NULL,
                        worldId INTEGER NOT NULL,
                        puzzleSeed INTEGER NOT NULL,
                        startedAt INTEGER NOT NULL,
                        lastUpdatedAt INTEGER NOT NULL,
                        elapsedActiveTimeMs INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        pathSnapshot TEXT DEFAULT NULL,
                        moveCount INTEGER NOT NULL,
                        hintCount INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add Prompt 13 identity, revision, and snapshot schema columns to game_sessions
                db.execSQL("ALTER TABLE game_sessions ADD COLUMN puzzleId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE game_sessions ADD COLUMN puzzleVersion INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE game_sessions ADD COLUMN catalogVersion TEXT NOT NULL DEFAULT '1.0.0'")
                db.execSQL("ALTER TABLE game_sessions ADD COLUMN revision INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE game_sessions ADD COLUMN snapshotSchemaVersion INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add Prompt 16 Daily Challenge identity, schedule, and attempt tracking columns
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN challengeId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN challengeVersion INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN puzzleId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN puzzleVersion INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN puzzleFingerprint TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN scheduleVersion TEXT NOT NULL DEFAULT '1.0.0'")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN attemptCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN bestTimeMs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN firstAttemptAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN lastAttemptAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN movesCount INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 17 Player Profile and Local Achievement tables
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS player_profile (
                        playerId TEXT NOT NULL PRIMARY KEY,
                        displayName TEXT NOT NULL,
                        avatarId TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        lastActiveAt INTEGER NOT NULL,
                        accountType TEXT NOT NULL,
                        publicZynpathId TEXT DEFAULT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS achievements (
                        achievementId TEXT NOT NULL PRIMARY KEY,
                        unlockedAt INTEGER DEFAULT NULL,
                        currentProgress INTEGER NOT NULL DEFAULT 0,
                        targetProgress INTEGER NOT NULL DEFAULT 1,
                        isUnlocked INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 25 Daily Challenge online verification & leaderboard eligibility columns
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN verificationStatus TEXT NOT NULL DEFAULT 'LOCAL_COMPLETION'")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN serverAttemptId TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE daily_challenge ADD COLUMN isLeaderboardEligible INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 27 Premium Solo puzzle-pack progress tracking
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS premium_pack_progress (
                        playerId TEXT NOT NULL,
                        packId TEXT NOT NULL,
                        levelIndex INTEGER NOT NULL,
                        puzzleId TEXT NOT NULL,
                        puzzleVersion INTEGER NOT NULL DEFAULT 1,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        bestSolveTimeMs INTEGER NOT NULL DEFAULT 0,
                        completedAt INTEGER DEFAULT NULL,
                        PRIMARY KEY (playerId, packId, levelIndex)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_premium_pack_progress_playerId_packId ON premium_pack_progress (playerId, packId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_premium_pack_progress_playerId ON premium_pack_progress (playerId)")
            }
        }

        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 31 In-app notifications table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS notifications (
                        notificationId TEXT NOT NULL PRIMARY KEY,
                        recipientPlayerId TEXT NOT NULL,
                        eventType TEXT NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        relatedResourceId TEXT DEFAULT NULL,
                        actionDestination TEXT DEFAULT NULL,
                        createdAt INTEGER NOT NULL,
                        expiresAt INTEGER DEFAULT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        isDismissed INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_recipientPlayerId ON notifications (recipientPlayerId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_recipientPlayerId_isRead ON notifications (recipientPlayerId, isRead)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_createdAt ON notifications (createdAt)")
            }
        }

        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 35 Durable offline sync operations queue
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sync_operations (
                        operationId TEXT NOT NULL PRIMARY KEY,
                        ownerIdentity TEXT NOT NULL,
                        operationType TEXT NOT NULL,
                        payloadVersion INTEGER NOT NULL DEFAULT 1,
                        resourceIdentity TEXT NOT NULL,
                        payloadJson TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        attemptCount INTEGER NOT NULL DEFAULT 0,
                        lastAttemptAt INTEGER DEFAULT NULL,
                        lastError TEXT DEFAULT NULL,
                        status TEXT NOT NULL DEFAULT 'PENDING'
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_operations_ownerIdentity_status ON sync_operations (ownerIdentity, status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_operations_createdAt ON sync_operations (createdAt)")
            }
        }

        val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 37 Performance optimization: Add indexes for frequent query paths
                db.execSQL("CREATE INDEX IF NOT EXISTS index_level_progress_worldId_isCompleted ON level_progress (worldId, isCompleted)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_level_progress_isCompleted ON level_progress (isCompleted)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_challenge_isCompleted_dateKey ON daily_challenge (isCompleted, dateKey)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_game_sessions_levelId_status ON game_sessions (levelId, status)")
            }
        }

        val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Prompt 38 Session ownership isolation: Add ownerIdentity to game_sessions
                db.execSQL("ALTER TABLE game_sessions ADD COLUMN ownerIdentity TEXT DEFAULT NULL")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_game_sessions_ownerIdentity ON game_sessions (ownerIdentity)")
            }
        }

        val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS wallet_transactions (
                        transactionId TEXT NOT NULL PRIMARY KEY,
                        playerId TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        balanceAfter INTEGER NOT NULL,
                        idempotencyKey TEXT NOT NULL,
                        metadataJson TEXT NOT NULL DEFAULT '{}',
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_wallet_transactions_idempotencyKey ON wallet_transactions (idempotencyKey)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_wallet_transactions_playerId_createdAt ON wallet_transactions (playerId, createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_wallet_transactions_playerId_type ON wallet_transactions (playerId, type)")
            }
        }
    }
}

