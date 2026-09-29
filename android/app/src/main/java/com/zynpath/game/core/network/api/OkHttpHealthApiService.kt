package com.zynpath.game.core.network.api

import com.zynpath.game.BuildConfig
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.network.model.BackendHealthDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpHealthApiService(
    private val okHttpClient: OkHttpClient,
    private var customBaseUrl: String? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HealthApiService {

    @Inject
    constructor(okHttpClient: OkHttpClient) : this(okHttpClient, null, Dispatchers.IO)

    override fun setCustomBaseUrl(url: String?) {
        this.customBaseUrl = url
    }

    override fun getBaseUrl(): String {
        return customBaseUrl?.removeSuffix("/") ?: BuildConfig.BACKEND_BASE_URL.removeSuffix("/")
    }

    override suspend fun checkHealth(): NetworkResult<BackendHealthDto> = checkHealth(null)

    override suspend fun checkHealth(targetUrl: String?): NetworkResult<BackendHealthDto> = withContext(ioDispatcher) {
        val rawBase = (targetUrl ?: getBaseUrl()).trim().removeSuffix("/")
        val resolvedUrl = when {
            rawBase.endsWith("/api/v1/health") -> rawBase
            rawBase.endsWith("/api/v1") -> "$rawBase/health"
            rawBase.endsWith("/health") -> rawBase
            else -> "$rawBase/api/v1/health"
        }

        val request = Request.Builder()
            .url(resolvedUrl)
            .get()
            .addHeader("Accept", "application/json")
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext NetworkResult.Error(
                        code = response.code,
                        message = response.message.ifEmpty { "HTTP ${response.code}" }
                    )
                }

                val bodyString = response.body?.string()
                    ?: return@withContext NetworkResult.Error(code = response.code, message = "Empty response body")

                val dto = parseHealthJson(bodyString)
                NetworkResult.Success(dto)
            }
        } catch (e: IOException) {
            NetworkResult.Exception(e)
        } catch (e: Exception) {
            NetworkResult.Exception(e)
        }
    }

    companion object {
        fun parseHealthJson(jsonString: String): BackendHealthDto {
            fun extractString(key: String, default: String): String {
                val match = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"").find(jsonString)
                return match?.groupValues?.get(1) ?: default
            }
            fun extractLong(key: String, default: Long): Long {
                val match = Regex("\"$key\"\\s*:\\s*(\\d+)").find(jsonString)
                return match?.groupValues?.get(1)?.toLongOrNull() ?: default
            }

            return try {
                val json = JSONObject(jsonString)
                val status = json.optString("status").ifEmpty { extractString("status", "UNKNOWN") }
                val service = json.optString("service").ifEmpty { extractString("service", "Zynpath Backend") }
                val version = json.optString("version").ifEmpty { extractString("version", "1.0.0") }
                val timestamp = if (json.has("timestamp")) json.optLong("timestamp") else extractLong("timestamp", System.currentTimeMillis())
                val env = json.optString("environment").ifEmpty { extractString("environment", "unknown") }

                BackendHealthDto(
                    status = status,
                    service = service,
                    version = version,
                    timestamp = timestamp,
                    environment = env
                )
            } catch (t: Throwable) {
                // JVM stub fallback
                BackendHealthDto(
                    status = extractString("status", "UNKNOWN"),
                    service = extractString("service", "Zynpath Backend"),
                    version = extractString("version", "1.0.0"),
                    timestamp = extractLong("timestamp", System.currentTimeMillis()),
                    environment = extractString("environment", "unknown")
                )
            }
        }
    }
}
