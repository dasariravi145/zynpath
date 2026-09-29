package com.zynpath.game.core.auth.api

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.network.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpAuthApiService(
    private val okHttpClient: OkHttpClient,
    private val customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun exchangeToken(
        provider: AuthProvider,
        providerToken: String,
        guestUuid: String?
    ): NetworkResult<AuthResponseDto> = withContext(ioDispatcher) {
        val jsonPayload = JSONObject().apply {
            put("provider", provider.name)
            put("providerToken", providerToken)
            if (guestUuid != null) put("guestUuid", guestUuid)
        }.toString()

        val request = Request.Builder()
            .url("${getBaseUrl()}/auth/exchange")
            .post(jsonPayload.toRequestBody(jsonMediaType))
            .addHeader("Accept", "application/json")
            .build()

        executeAuthRequest(request)
    }

    override suspend fun linkAccount(
        provider: AuthProvider,
        providerToken: String,
        guestUuid: String,
        displayName: String?
    ): NetworkResult<AuthResponseDto> = withContext(ioDispatcher) {
        val jsonPayload = JSONObject().apply {
            put("provider", provider.name)
            put("providerToken", providerToken)
            put("guestUuid", guestUuid)
            if (displayName != null) put("displayName", displayName)
        }.toString()

        val request = Request.Builder()
            .url("${getBaseUrl()}/auth/link")
            .post(jsonPayload.toRequestBody(jsonMediaType))
            .addHeader("Accept", "application/json")
            .build()

        executeAuthRequest(request)
    }

    override suspend fun getCurrentPlayer(sessionToken: String): NetworkResult<AuthResponseDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/auth/me")
            .get()
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeAuthRequest(request)
    }

    override suspend fun refreshSession(sessionToken: String): NetworkResult<AuthResponseDto> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/auth/refresh")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        executeAuthRequest(request)
    }

    override suspend fun signOut(sessionToken: String): NetworkResult<Unit> = withContext(ioDispatcher) {
        val request = Request.Builder()
            .url("${getBaseUrl()}/auth/signout")
            .post("{}".toRequestBody(jsonMediaType))
            .addHeader("Authorization", "Bearer $sessionToken")
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(response.code, "Failed to sign out on server")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun executeAuthRequest(request: Request): NetworkResult<AuthResponseDto> {
        return try {
            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errorMessage = parseErrorMessage(bodyString, response.code)
                    return NetworkResult.Error(
                        code = response.code,
                        message = errorMessage
                    )
                }

                if (bodyString.isBlank()) {
                    return NetworkResult.Error(code = response.code, message = "Empty response body from auth server")
                }

                val json = JSONObject(bodyString)
                val dto = AuthResponseDto(
                    sessionToken = json.getString("sessionToken"),
                    playerId = json.getString("playerId"),
                    publicZynpathId = json.getString("publicZynpathId"),
                    displayName = json.getString("displayName"),
                    accountType = json.getString("accountType"),
                    expiresAt = json.getLong("expiresAt")
                )
                NetworkResult.Success(dto)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    private fun parseErrorMessage(body: String, statusCode: Int): String {
        return try {
            val json = JSONObject(body)
            when {
                json.has("errorCode") -> "[${json.getString("errorCode")}] ${json.optString("message", "")}".trim()
                json.has("message") -> json.getString("message")
                else -> "HTTP $statusCode"
            }
        } catch (e: Exception) {
            "HTTP $statusCode: $body"
        }
    }
}
