package com.zynpath.game.core.analytics.model

/**
 * Concrete milestone achieved in the player's personal Zynpath journey.
 *
 * Implements Prompt 30 Section 36:
 * - Deterministically calculated from recorded gameplay data.
 * - Never unlocked artificially or fabricated.
 */
data class PersonalMilestone(
    val id: String,
    val title: String,
    val description: String,
    val isAchieved: Boolean,
    val achievedDate: String?,
    val progress: Float, // 0f..1f
    val progressLabel: String
) {
    val isUnlocked: Boolean
        get() = isAchieved
}

/**
 * Authoritative, unified personal analytics report across all puzzle modalities.
 *
 * Implements Prompt 30 Sections 6, 7, 8, 9 & 40:
 * - Separates free baseline metrics from Premium deep analytics.
 * - Gated authoritatively by [PremiumFeatureKey.ADVANCED_PERSONAL_STATS].
 */
data class PersonalAnalyticsReport(
    val isPremiumUser: Boolean,
    val soloProgression: SoloProgressionAnalytics,
    val premiumPacks: PremiumPackAnalytics,
    val completionTrends: List<CompletionTrendItem>,
    val timeImprovements: List<TimeImprovementInsight>,
    val dailyChallenge: DailyChallengeAnalytics,
    val competitive: CompetitiveAnalyticsSummary,
    val milestones: List<PersonalMilestone>,
    val lastUpdatedMs: Long
) {
    val isAdvancedAnalyticsUnlocked: Boolean
        get() = isPremiumUser

    val premiumPackAnalytics: PremiumPackAnalytics
        get() = premiumPacks

    val dailyAnalytics: DailyChallengeAnalytics
        get() = dailyChallenge

    val competitiveSummary: CompetitiveAnalyticsSummary
        get() = competitive
}
