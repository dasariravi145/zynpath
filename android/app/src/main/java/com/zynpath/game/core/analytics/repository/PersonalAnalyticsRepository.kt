package com.zynpath.game.core.analytics.repository

import com.zynpath.game.core.analytics.model.PersonalAnalyticsReport
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for retrieving and observing authoritative personal analytics.
 *
 * Implements Prompt 30:
 * - Offline-first aggregation of local Solo progress and Daily Challenge records.
 * - Server-authoritative competitive analytics with graceful offline caching.
 * - Authoritative feature gating via [FeatureAccessPolicy].
 */
interface PersonalAnalyticsRepository {
    /**
     * Observes real-time personal analytics report combining all local and remote sources.
     */
    fun observeAnalyticsReport(): Flow<PersonalAnalyticsReport>

    /**
     * Synchronously computes the current snapshot of personal analytics.
     */
    suspend fun getAnalyticsReport(): PersonalAnalyticsReport

    /**
     * Triggers an online sync to refresh server-authoritative competitive statistics.
     * Returns true if refresh succeeded, false if offline or unauthenticated.
     */
    suspend fun refreshCompetitiveAnalytics(): Boolean
}
