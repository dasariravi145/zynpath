package com.zynpath.game.core.sync.conflict

import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus

/**
 * Domain rules and conflict resolution policies for offline-first synchronization.
 *
 * Implements Prompt 35 Sections 6, 20, 21, 22, 28, 29, 30, 32, 44 & 51:
 * - Data Authority Matrix:
 *   - Solo Progress & Personal Bests: Durable local records reconciled with authenticated backend records.
 *   - Daily Participation: Local completion authority preserved locally; verification and leaderboard eligibility strictly server-authoritative.
 *   - Competitive Multiplayer (Duels & Leagues): Strictly backend-authoritative. Offline client records cannot invent or finalize competitive results.
 *   - Premium Entitlements: Google Play Billing / Backend verification authoritative.
 *   - Preferences: Device-specific settings local; privacy & account settings backend-authoritative.
 */
object SyncConflictPolicy {

    /**
     * Reconciles local and remote Solo level progress non-destructively.
     *
     * Principles:
     * - A valid completion remains completed (completions are never rolled back).
     * - Best time strictly preserves fastest positive solve time (>0L). Zero or missing values never overwrite valid times.
     * - Fewest moves strictly preserves lowest positive move count (>0).
     * - Stars are maxed.
     * - Fewest hints are preserved.
     * - Unlock state is unioned.
     * - Earliest completion timestamp is preserved for historical accuracy.
     */
    fun mergeLevelProgress(
        local: LevelProgressEntity?,
        remote: LevelProgressEntity
    ): LevelProgressEntity {
        if (local == null) {
            return remote
        }

        val mergedCompleted = local.isCompleted || remote.isCompleted
        val mergedUnlocked = local.isUnlocked || remote.isUnlocked
        val mergedStars = maxOf(local.stars, remote.stars)

        val mergedBestTimeMs = when {
            local.bestTimeMs <= 0L -> remote.bestTimeMs
            remote.bestTimeMs <= 0L -> local.bestTimeMs
            else -> minOf(local.bestTimeMs, remote.bestTimeMs)
        }

        val mergedMovesCount = when {
            local.movesCount <= 0 -> remote.movesCount
            remote.movesCount <= 0 -> local.movesCount
            else -> minOf(local.movesCount, remote.movesCount)
        }

        val mergedBestHintCount = minOf(local.bestHintCount, remote.bestHintCount)
        val mergedCompletionCount = maxOf(local.completionCount, remote.completionCount)

        val mergedFirstCompletedAt = listOfNotNull(
            local.firstCompletedAt,
            local.completedAt,
            remote.firstCompletedAt,
            remote.completedAt
        ).minOrNull()

        val mergedLastCompletedAt = listOfNotNull(
            local.lastCompletedAt,
            local.completedAt,
            remote.lastCompletedAt,
            remote.completedAt
        ).maxOrNull()

        return local.copy(
            isCompleted = mergedCompleted,
            isUnlocked = mergedUnlocked,
            stars = mergedStars,
            bestTimeMs = mergedBestTimeMs,
            movesCount = mergedMovesCount,
            bestHintCount = mergedBestHintCount,
            completionCount = mergedCompletionCount,
            firstCompletedAt = mergedFirstCompletedAt,
            lastCompletedAt = mergedLastCompletedAt,
            completedAt = mergedLastCompletedAt ?: local.completedAt
        )
    }

    /**
     * Reconciles Daily Challenge verification status.
     *
     * Local participation is always kept intact so the player never loses their offline solve.
     * However, leaderboard eligibility and verified status require backend confirmation.
     */
    fun reconcileDailyVerification(
        currentLocalStatus: DailyVerificationStatus,
        serverReportedStatus: DailyVerificationStatus
    ): DailyVerificationStatus {
        return when (serverReportedStatus) {
            DailyVerificationStatus.LEADERBOARD_ELIGIBLE -> DailyVerificationStatus.LEADERBOARD_ELIGIBLE
            DailyVerificationStatus.SERVER_VALIDATED -> DailyVerificationStatus.SERVER_VALIDATED
            DailyVerificationStatus.PROVISIONAL -> DailyVerificationStatus.PROVISIONAL
            DailyVerificationStatus.NOT_ELIGIBLE -> {
                // Keep local completion visible on device, but mark not eligible for online boards
                DailyVerificationStatus.NOT_ELIGIBLE
            }
            DailyVerificationStatus.LOCAL_COMPLETION -> currentLocalStatus
        }
    }

    /**
     * Validates whether a personal best solve time should be updated.
     */
    fun isBetterSolveTime(existingTimeMs: Long, newTimeMs: Long): Boolean {
        if (newTimeMs <= 0L) return false
        if (existingTimeMs <= 0L) return true
        return newTimeMs < existingTimeMs
    }
}
