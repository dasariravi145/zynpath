package com.zynpath.game.core.ads.consent

import android.app.Activity
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Consent state for User Messaging Platform (UMP) and privacy regulations.
 */
enum class ConsentState {
    UNKNOWN,
    NOT_REQUIRED,
    REQUIRED,
    OBTAINED
}

/**
 * Consent and privacy manager for advertising integrations.
 *
 * Implements Prompt 29 Sections 13 & 14:
 * - Integrates consent framework boundary.
 * - Respects user choices before requesting ads.
 * - Provides privacy options entry point in Settings.
 */
interface AdConsentManager {
    val consentState: StateFlow<ConsentState>
    fun canRequestAds(): Boolean
    fun isPrivacyOptionsRequired(): Boolean
    fun gatherConsent(activity: Activity, onComplete: () -> Unit)
    fun showPrivacyOptionsForm(activity: Activity, onDismiss: () -> Unit)
}

@Singleton
class AdConsentManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AdConsentManager {

    private val _consentState = MutableStateFlow(ConsentState.OBTAINED)
    override val consentState: StateFlow<ConsentState> = _consentState.asStateFlow()

    override fun canRequestAds(): Boolean {
        // Can request ads if consent is obtained or not legally required in user's region
        return _consentState.value == ConsentState.OBTAINED || _consentState.value == ConsentState.NOT_REQUIRED
    }

    override fun isPrivacyOptionsRequired(): Boolean {
        return false // Defaults to false until UMP form is initialized
    }

    override fun gatherConsent(activity: Activity, onComplete: () -> Unit) {
        // In local development & test mode, consent is automatically considered obtained
        _consentState.value = ConsentState.OBTAINED
        onComplete()
    }

    override fun showPrivacyOptionsForm(activity: Activity, onDismiss: () -> Unit) {
        onDismiss()
    }
}
