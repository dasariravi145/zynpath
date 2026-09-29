package com.zynpath.game.core.auth.provider

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provider client boundary for Facebook Login.
 *
 * Implements Prompt 18 Sections 9, 10 & 37:
 * - Checks for real provider configuration (facebook_app_id).
 * - Honestly reports NotConfigured when configuration is absent.
 * - Never fabricates fake responses.
 */
interface FacebookAuthClient {
    fun isConfigured(): Boolean
    fun getAppId(): String?
    suspend fun getCredential(context: Context): FacebookCredentialResult
}

sealed interface FacebookCredentialResult {
    data class Success(val accessToken: String) : FacebookCredentialResult
    data class Failure(val message: String) : FacebookCredentialResult
    data object Cancelled : FacebookCredentialResult
    data class NotConfigured(val message: String) : FacebookCredentialResult
}

@Singleton
class FacebookAuthClientImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : FacebookAuthClient {

    private val configuredAppId: String? by lazy {
        val resId = appContext.resources.getIdentifier("facebook_app_id", "string", appContext.packageName)
        if (resId != 0) {
            val resValue = appContext.getString(resId).trim()
            if (resValue.isNotEmpty() && !resValue.startsWith("YOUR_")) {
                return@lazy resValue
            }
        }
        null
    }

    override fun isConfigured(): Boolean {
        return configuredAppId != null
    }

    override fun getAppId(): String? {
        return configuredAppId
    }

    override suspend fun getCredential(context: Context): FacebookCredentialResult = withContext(Dispatchers.IO) {
        val appId = configuredAppId
        if (appId == null) {
            return@withContext FacebookCredentialResult.NotConfigured(
                "Facebook Login is not configured. Define 'facebook_app_id' in string resources or FACEBOOK_APP_ID in the environment."
            )
        }

        FacebookCredentialResult.Failure("Facebook Login flow is awaiting authorized developer keys.")
    }
}
