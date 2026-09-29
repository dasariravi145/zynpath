package com.zynpath.game.core.auth.provider

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provider client boundary for Google Sign-In using Android Credential Manager.
 *
 * Implements:
 * - Real Google Sign-In with Android Credential Manager and Google ID Credential.
 * - Server client ID driven by 'default_web_client_id'.
 * - Explicit account selection (filterByAuthorizedAccounts = false, autoSelectEnabled = false).
 * - Safe resolution of Activity context for Credential Manager UI prompt.
 * - Comprehensive error mapping (cancellation, unavailable, provider configuration, parsing).
 * - Honest unconfigured state when Web OAuth Client ID is missing without fabricating tokens.
 */
interface GoogleAuthClient {
    fun isConfigured(): Boolean
    fun getClientId(): String?
    suspend fun getCredential(context: Context): GoogleCredentialResult
}

sealed interface GoogleCredentialResult {
    data class Success(val idToken: String) : GoogleCredentialResult
    data class Failure(val message: String) : GoogleCredentialResult
    data object Cancelled : GoogleCredentialResult
    data class NotConfigured(val message: String) : GoogleCredentialResult
}

@Singleton
class GoogleAuthClientImpl @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : GoogleAuthClient {

    private var explicitClientId: String? = null

    // Internal constructor for testing without Android resource lookups
    internal constructor(appContext: Context, explicitClientId: String?) : this(appContext) {
        this.explicitClientId = explicitClientId
    }

    private val configuredClientId: String? by lazy {
        explicitClientId ?: resolveClientIdFromResources()
    }

    private fun resolveClientIdFromResources(): String? {
        return try {
            val resId = appContext.resources?.getIdentifier("default_web_client_id", "string", appContext.packageName) ?: 0
            if (resId != 0) {
                val resValue = appContext.getString(resId).trim()
                if (resValue.isNotEmpty() && !resValue.startsWith("YOUR_")) {
                    return resValue
                }
            }
            null
        } catch (_: Throwable) {
            null
        }
    }

    override fun isConfigured(): Boolean {
        return configuredClientId != null
    }

    override fun getClientId(): String? {
        return configuredClientId
    }

    override suspend fun getCredential(context: Context): GoogleCredentialResult = withContext(Dispatchers.Main) {
        val clientId = configuredClientId
        if (clientId == null) {
            return@withContext GoogleCredentialResult.NotConfigured(
                "Google Sign-In is not configured. Define 'default_web_client_id' in string resources or set googleWebClientId in local.properties / GOOGLE_WEB_CLIENT_ID in the environment."
            )
        }

        val activity = context.findActivity() ?: return@withContext GoogleCredentialResult.Failure(
            "Google Sign-In requires an active Activity context."
        )

        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(activity)
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    if (idToken.isNotBlank()) {
                        GoogleCredentialResult.Success(idToken)
                    } else {
                        GoogleCredentialResult.Failure("Google Sign-In returned an empty ID token.")
                    }
                } catch (e: GoogleIdTokenParsingException) {
                    GoogleCredentialResult.Failure("Failed to parse Google ID token credential: ${e.message}")
                }
            } else {
                GoogleCredentialResult.Failure("Received unsupported credential type: ${credential.type}")
            }
        } catch (e: GetCredentialCancellationException) {
            GoogleCredentialResult.Cancelled
        } catch (e: NoCredentialException) {
            GoogleCredentialResult.Failure("No Google accounts found or credentials unavailable on this device.")
        } catch (e: GetCredentialProviderConfigurationException) {
            GoogleCredentialResult.Failure("Google Sign-In configuration error: ${e.message ?: "Invalid provider configuration"}")
        } catch (e: GetCredentialInterruptedException) {
            GoogleCredentialResult.Failure("Google Sign-In was interrupted: ${e.message}")
        } catch (e: GetCredentialCustomException) {
            GoogleCredentialResult.Failure("Google Sign-In custom error: ${e.type} - ${e.message}")
        } catch (e: GetCredentialException) {
            GoogleCredentialResult.Failure("Google Sign-In failed: ${e.message}")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            GoogleCredentialResult.Failure("Google Sign-In unexpected error: ${e.message ?: "Unknown error"}")
        }
    }

    private fun Context.findActivity(): Activity? {
        var ctx: Context? = this
        while (ctx != null) {
            if (ctx is Activity) return ctx
            if (ctx is ContextWrapper) {
                ctx = try { ctx.baseContext } catch (_: Throwable) { null }
            } else {
                break
            }
        }
        return null
    }
}
