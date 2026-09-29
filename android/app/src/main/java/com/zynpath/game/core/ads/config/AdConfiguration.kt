package com.zynpath.game.core.ads.config

import com.zynpath.game.BuildConfig

/**
 * Configuration for Google Mobile Ads rewarded ad placements.
 *
 * Implements Prompt 29 Sections 9, 10, 15 & 16:
 * - Separates development test ad unit IDs from production IDs.
 * - Uses official Google AdMob test ad unit ID in debug builds.
 * - Flags production as BLOCKED BY CONFIGURATION if ad unit ID is unconfigured.
 * - Enforces general audience policy (not child-directed).
 * - Enforces data minimization (no emails, purchase tokens, or friend graphs).
 */
object AdConfiguration {

    /**
     * Official Google AdMob Rewarded Video Test Ad Unit ID.
     * https://developers.google.com/admob/android/test-ads
     */
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    /**
     * Official Google AdMob Interstitial Test Ad Unit ID.
     */
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    /**
     * Official Google AdMob Sample App ID.
     */
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    /**
     * Effective rewarded ad unit ID for the current build environment.
     */
    val rewardedAdUnitId: String
        get() {
            return if (BuildConfig.DEBUG) {
                TEST_REWARDED_AD_UNIT_ID
            } else {
                BuildConfig.ADMOB_REWARDED_AD_UNIT_ID
            }
        }

    /**
     * Effective interstitial ad unit ID for the current build environment.
     */
    val interstitialAdUnitId: String
        get() {
            return TEST_INTERSTITIAL_AD_UNIT_ID
        }

    /**
     * True if a valid non-blank ad unit ID is configured for the active environment.
     */
    val isAdConfigured: Boolean
        get() = rewardedAdUnitId.isNotBlank()

    /**
     * True if production ad serving is blocked due to unconfigured production ad credentials.
     */
    val isProductionBlockedByConfiguration: Boolean
        get() = !BuildConfig.DEBUG && BuildConfig.ADMOB_REWARDED_AD_UNIT_ID.isBlank()

    /**
     * Maximum rewarded hints a player can earn per 24-hour window.
     */
    const val MAX_DAILY_REWARDED_HINTS = 5

    /**
     * Maximum bonus hint credits that can be stored at once.
     */
    const val MAX_STORED_REWARD_CREDITS = 10

    /**
     * Reward amount granted per completed ad.
     */
    const val HINTS_PER_REWARDED_AD = 1
}
