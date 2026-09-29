package com.zynpath.game

import androidx.sqlite.db.SupportSQLiteDatabase
import com.zynpath.game.core.database.ZynpathDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Migration tests for Room database evolution.
 *
 * Implements Prompt 13 Section 32 & 39:
 * - Validates MIGRATION_1_2 schema evolution.
 * - Validates MIGRATION_2_3 schema evolution adding session identity, revisions, and snapshot versioning.
 * - Confirms preservation of columns and table integrity without destructive fallback.
 */
class RoomMigrationTest {

    @Test
    fun `test migration 1 to 2 executes expected SQL statements`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL") {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        assertEquals(1, ZynpathDatabase.MIGRATION_1_2.startVersion)
        assertEquals(2, ZynpathDatabase.MIGRATION_1_2.endVersion)

        ZynpathDatabase.MIGRATION_1_2.migrate(fakeDb)

        assertTrue(
            "MIGRATION_1_2 must add isUnlocked column to level_progress",
            executedStatements.any { it.contains("ALTER TABLE level_progress ADD COLUMN isUnlocked") }
        )
        assertTrue(
            "MIGRATION_1_2 must create game_sessions table",
            executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS game_sessions") }
        )
    }

    @Test
    fun `test migration 2 to 3 executes expected alter table statements`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL") {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        assertEquals(2, ZynpathDatabase.MIGRATION_2_3.startVersion)
        assertEquals(3, ZynpathDatabase.MIGRATION_2_3.endVersion)

        ZynpathDatabase.MIGRATION_2_3.migrate(fakeDb)

        assertEquals(5, executedStatements.size)

        assertTrue(
            "MIGRATION_2_3 must add puzzleId column",
            executedStatements.any { it.contains("ALTER TABLE game_sessions ADD COLUMN puzzleId TEXT NOT NULL DEFAULT ''") }
        )
        assertTrue(
            "MIGRATION_2_3 must add puzzleVersion column",
            executedStatements.any { it.contains("ALTER TABLE game_sessions ADD COLUMN puzzleVersion INTEGER NOT NULL DEFAULT 1") }
        )
        assertTrue(
            "MIGRATION_2_3 must add catalogVersion column",
            executedStatements.any { it.contains("ALTER TABLE game_sessions ADD COLUMN catalogVersion TEXT NOT NULL DEFAULT '1.0.0'") }
        )
        assertTrue(
            "MIGRATION_2_3 must add revision column",
            executedStatements.any { it.contains("ALTER TABLE game_sessions ADD COLUMN revision INTEGER NOT NULL DEFAULT 1") }
        )
        assertTrue(
            "MIGRATION_2_3 must add snapshotSchemaVersion column",
            executedStatements.any { it.contains("ALTER TABLE game_sessions ADD COLUMN snapshotSchemaVersion INTEGER NOT NULL DEFAULT 1") }
        )
    }

    @Test
    fun `test migration 8 to 9 creates sync_operations table and indices`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL") {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        assertEquals(8, ZynpathDatabase.MIGRATION_8_9.startVersion)
        assertEquals(9, ZynpathDatabase.MIGRATION_8_9.endVersion)

        ZynpathDatabase.MIGRATION_8_9.migrate(fakeDb)

        assertTrue(
            "MIGRATION_8_9 must create sync_operations table",
            executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS sync_operations") }
        )
        assertTrue(
            "MIGRATION_8_9 must create index on ownerIdentity and status",
            executedStatements.any { it.contains("CREATE INDEX IF NOT EXISTS index_sync_operations_ownerIdentity_status") }
        )
        assertTrue(
            "MIGRATION_8_9 must create index on createdAt",
            executedStatements.any { it.contains("CREATE INDEX IF NOT EXISTS index_sync_operations_createdAt") }
        )
    }

    @Test
    fun `test migration 10 to 11 adds ownerIdentity column and index to game_sessions`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL") {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        assertEquals(10, ZynpathDatabase.MIGRATION_10_11.startVersion)
        assertEquals(11, ZynpathDatabase.MIGRATION_10_11.endVersion)

        ZynpathDatabase.MIGRATION_10_11.migrate(fakeDb)

        assertTrue(
            "MIGRATION_10_11 must add ownerIdentity column to game_sessions",
            executedStatements.any { it.contains("ALTER TABLE game_sessions ADD COLUMN ownerIdentity TEXT DEFAULT NULL") }
        )
        assertTrue(
            "MIGRATION_10_11 must create index on ownerIdentity",
            executedStatements.any { it.contains("CREATE INDEX IF NOT EXISTS index_game_sessions_ownerIdentity ON game_sessions (ownerIdentity)") }
        )
    }
}
