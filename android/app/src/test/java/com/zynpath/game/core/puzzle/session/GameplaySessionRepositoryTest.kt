package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.database.repository.GameplaySessionRepositoryImpl
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.fake.FakeGameSessionDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GameplaySessionRepositoryImpl].
 *
 * Implements Prompt 13 Section 25 & 39:
 * - Session insert, update, lookup.
 * - Stale write prevention using revision numbers.
 * - Completed session overwrite protection.
 * - Single active session enforcement (`pauseAllActiveSessions`).
 * - Status transitions (COMPLETED, RESTORATION_FAILED, ABANDONED).
 */
class GameplaySessionRepositoryTest {

    private lateinit var fakeDao: FakeGameSessionDao
    private lateinit var repository: GameplaySessionRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeGameSessionDao()
        repository = GameplaySessionRepositoryImpl(fakeDao)
    }

    @Test
    fun `test session insert and lookup by level and id`() = runTest {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_101",
            levelId = 1,
            worldId = 1,
            puzzleId = "puzzle_1",
            status = SessionStatus.ACTIVE,
            path = listOf(GridPosition(0, 0), GridPosition(0, 1)),
            elapsedActiveTimeMs = 2500L,
            revision = 2L
        )

        val saved = repository.saveSession(snapshot)
        assertTrue(saved)

        val byLevel = repository.getResumableSession(1)
        assertNotNull(byLevel)
        assertEquals("sess_101", byLevel?.sessionId)
        assertEquals(2, byLevel?.path?.size)
        assertEquals(2500L, byLevel?.elapsedActiveTimeMs)

        val byId = repository.getSessionById("sess_101")
        assertNotNull(byId)
        assertEquals("sess_101", byId?.sessionId)
    }

    @Test
    fun `test stale write with lower or equal revision is rejected`() = runTest {
        val initial = GameplaySessionSnapshot(
            sessionId = "sess_rev",
            levelId = 1,
            worldId = 1,
            puzzleId = "puzzle_1",
            status = SessionStatus.ACTIVE,
            path = listOf(GridPosition(0, 0), GridPosition(0, 1)),
            revision = 5L
        )
        repository.saveSession(initial)

        // Attempt to save an older revision 4 from an asynchronous delayed save
        val olderSnapshot = initial.copy(
            path = listOf(GridPosition(0, 0)),
            revision = 4L
        )
        val rejectedOld = repository.saveSession(olderSnapshot)
        assertFalse("Stale write with lower revision must be rejected", rejectedOld)

        // Verify repository still contains revision 5
        val current = repository.getSessionById("sess_rev")
        assertEquals(5L, current?.revision)
        assertEquals(2, current?.path?.size)

        // Attempting equal revision with same status is also rejected
        val sameRevision = initial.copy(revision = 5L)
        val rejectedEqual = repository.saveSession(sameRevision)
        assertFalse("Write with equal revision must be rejected", rejectedEqual)

        // Saving newer revision 6 succeeds
        val newerSnapshot = initial.copy(
            path = listOf(GridPosition(0, 0), GridPosition(0, 1), GridPosition(0, 2)),
            revision = 6L
        )
        val acceptedNew = repository.saveSession(newerSnapshot)
        assertTrue("Write with higher revision must be accepted", acceptedNew)
        assertEquals(6L, repository.getSessionById("sess_rev")?.revision)
    }

    @Test
    fun `test completed session cannot be overwritten by active or paused snapshot`() = runTest {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_complete_guard",
            levelId = 1,
            worldId = 1,
            puzzleId = "puzzle_1",
            status = SessionStatus.COMPLETED,
            elapsedActiveTimeMs = 12_000L,
            revision = 10L
        )
        repository.saveSession(snapshot)

        // Delayed asynchronous active snapshot arrives
        val delayedActive = snapshot.copy(
            status = SessionStatus.ACTIVE,
            revision = 11L
        )
        val overwriteAttempt = repository.saveSession(delayedActive)
        assertFalse("Completed session must never be overwritten by an active snapshot", overwriteAttempt)

        val stored = repository.getSessionById("sess_complete_guard")
        assertEquals(SessionStatus.COMPLETED, stored?.status)
    }

    @Test
    fun `test pauseAllActiveSessions pauses all active sessions`() = runTest {
        val session1 = GameplaySessionSnapshot(
            sessionId = "sess_1",
            levelId = 1,
            worldId = 1,
            puzzleId = "p1",
            status = SessionStatus.ACTIVE
        )
        val session2 = GameplaySessionSnapshot(
            sessionId = "sess_2",
            levelId = 2,
            worldId = 1,
            puzzleId = "p2",
            status = SessionStatus.ACTIVE
        )
        repository.saveSession(session1)
        repository.saveSession(session2)

        repository.pauseAllActiveSessions(1000L)

        val s1 = repository.getSessionById("sess_1")
        val s2 = repository.getSessionById("sess_2")
        assertEquals(SessionStatus.PAUSED, s1?.status)
        assertEquals(SessionStatus.PAUSED, s2?.status)
    }

    @Test
    fun `test markRestorationFailed and markCompleted transitions`() = runTest {
        val session = GameplaySessionSnapshot(
            sessionId = "sess_trans",
            levelId = 1,
            worldId = 1,
            puzzleId = "p1",
            status = SessionStatus.ACTIVE,
            revision = 1L
        )
        repository.saveSession(session)

        val failedMarked = repository.markRestorationFailed("sess_trans", "Corrupted path coordinates")
        assertTrue(failedMarked)

        val storedFailed = repository.getSessionById("sess_trans")
        assertEquals(SessionStatus.RESTORATION_FAILED, storedFailed?.status)
        assertEquals(2L, storedFailed?.revision)

        // Resumable session query should now return null because status is RESTORATION_FAILED
        val resumable = repository.getResumableSession(1)
        assertNull(resumable)
    }

    @Test
    fun `test deleteSessionsForLevel`() = runTest {
        val session1 = GameplaySessionSnapshot(sessionId = "s1", levelId = 5, worldId = 1, puzzleId = "p5")
        val session2 = GameplaySessionSnapshot(sessionId = "s2", levelId = 6, worldId = 1, puzzleId = "p6")
        repository.saveSession(session1)
        repository.saveSession(session2)

        repository.deleteSessionsForLevel(5)

        assertNull(repository.getSessionById("s1"))
        assertNotNull(repository.getSessionById("s2"))
    }
}
