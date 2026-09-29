package com.zynpath.game.core.premium

import android.content.Context
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.premium.model.EntitlementStatus
import com.zynpath.game.core.premium.model.PremiumEntitlement
import com.zynpath.game.core.premium.network.OkHttpSubscriptionApiService
import com.zynpath.game.core.premium.network.SubscriptionApiService
import com.zynpath.game.core.premium.network.SubscriptionRestoreRequest
import com.zynpath.game.core.premium.network.SubscriptionVerificationRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionEntitlementRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionApiService: SubscriptionApiService,
    private val secureTokenStorage: SecureTokenStorage,
    private val preferencesRepository: PreferencesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SubscriptionEntitlementRepository {

    private val cacheFile = File(context.filesDir, "zyn_entitlement_cache.json")
    private val _entitlement = MutableStateFlow(loadCachedEntitlement())
    override val entitlement: StateFlow<PremiumEntitlement> = _entitlement.asStateFlow()

    companion object {
        // Maximum offline grace window if currentPeriodEndMs is not explicitly set (7 days)
        private const val MAX_OFFLINE_CACHE_WINDOW_MS = 7L * 24 * 60 * 60 * 1000
    }

    override suspend fun refreshEntitlement(): Result<PremiumEntitlement> = withContext(ioDispatcher) {
        val sessionToken = secureTokenStorage.getSessionToken()
        if (sessionToken.isNullOrBlank()) {
            val fallback = loadCachedEntitlement()
            _entitlement.value = fallback
            preferencesRepository.setPremium(fallback.isPremiumActive)
            return@withContext Result.success(fallback)
        }

        when (val result = subscriptionApiService.getEntitlement(sessionToken)) {
            is NetworkResult.Success -> {
                val fresh = result.data.copy(isCachedOffline = false)
                saveCachedEntitlement(fresh)
                _entitlement.value = fresh
                preferencesRepository.setPremium(fresh.isPremiumActive)
                Result.success(fresh)
            }
            is NetworkResult.Error -> {
                // If unauthorized or server error, retain bounded offline cache if account matches
                val cached = loadCachedEntitlement()
                _entitlement.value = cached
                preferencesRepository.setPremium(cached.isPremiumActive)
                Result.failure(Exception("Failed to refresh entitlement: HTTP ${result.code} - ${result.message}"))
            }
            is NetworkResult.Exception -> {
                // Offline fallback
                val cached = loadCachedEntitlement()
                _entitlement.value = cached
                preferencesRepository.setPremium(cached.isPremiumActive)
                Result.success(cached)
            }
        }
    }

    override suspend fun verifyPurchase(
        purchaseToken: String,
        productId: String,
        basePlanId: String
    ): Result<PremiumEntitlement> = withContext(ioDispatcher) {
        val sessionToken = secureTokenStorage.getSessionToken()
            ?: return@withContext Result.failure(IllegalStateException("Authenticated account required to verify subscription"))

        val metadata = secureTokenStorage.getSessionMetadata()
        val request = SubscriptionVerificationRequest(
            purchaseToken = purchaseToken,
            productId = productId,
            basePlanId = basePlanId,
            obfuscatedAccountId = metadata?.playerId
        )

        when (val result = subscriptionApiService.verifyPurchase(sessionToken, request)) {
            is NetworkResult.Success -> {
                val fresh = result.data.copy(isCachedOffline = false)
                saveCachedEntitlement(fresh)
                _entitlement.value = fresh
                preferencesRepository.setPremium(fresh.isPremiumActive)
                Result.success(fresh)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Exception -> {
                Result.failure(result.throwable)
            }
        }
    }

    override suspend fun restorePurchases(purchaseTokens: List<String>): Result<PremiumEntitlement> = withContext(ioDispatcher) {
        if (purchaseTokens.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("No existing purchase tokens found on device"))
        }

        val sessionToken = secureTokenStorage.getSessionToken()
            ?: return@withContext Result.failure(IllegalStateException("Authenticated account required to restore purchases"))

        val request = SubscriptionRestoreRequest(purchaseTokens = purchaseTokens)
        when (val result = subscriptionApiService.restoreSubscriptions(sessionToken, request)) {
            is NetworkResult.Success -> {
                val restored = result.data.activeEntitlement ?: PremiumEntitlement.free(
                    accountId = secureTokenStorage.getSessionMetadata()?.playerId
                )
                saveCachedEntitlement(restored)
                _entitlement.value = restored
                preferencesRepository.setPremium(restored.isPremiumActive)
                Result.success(restored)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Exception -> {
                Result.failure(result.throwable)
            }
        }
    }

    override suspend fun onAccountSwitched(newAccountId: String?) = withContext(ioDispatcher) {
        // Enforce account isolation: do not leak previous account's entitlement
        clearCachedEntitlement()
        val fresh = PremiumEntitlement.free(accountId = newAccountId)
        _entitlement.value = fresh
        preferencesRepository.setPremium(false)
        if (newAccountId != null) {
            refreshEntitlement()
        }
    }

    override suspend fun clearEntitlement() = withContext(ioDispatcher) {
        clearCachedEntitlement()
        _entitlement.value = PremiumEntitlement.free()
        preferencesRepository.setPremium(false)
    }

    private fun loadCachedEntitlement(): PremiumEntitlement {
        if (!cacheFile.exists()) return PremiumEntitlement.free()
        return try {
            val json = JSONObject(cacheFile.readText())
            val statusStr = json.optString("status", "FREE")
            val status = try {
                EntitlementStatus.valueOf(statusStr)
            } catch (e: Exception) {
                EntitlementStatus.UNKNOWN
            }

            val currentPeriodEnd = json.optLong("currentPeriodEndMs", 0L)
            val lastVerifiedAt = json.optLong("lastVerifiedAtMs", 0L)
            val now = System.currentTimeMillis()

            // Bounded offline cache check (Prompt 26 Section 36)
            val isPeriodValid = currentPeriodEnd > 0L && now <= currentPeriodEnd
            val isWindowValid = currentPeriodEnd <= 0L && (now - lastVerifiedAt <= MAX_OFFLINE_CACHE_WINDOW_MS)
            val isStillValidOffline = status.isEntitled && (isPeriodValid || isWindowValid)

            if (isStillValidOffline) {
                PremiumEntitlement(
                    accountId = json.optString("accountId").ifEmpty { null },
                    status = status,
                    productId = json.optString("productId").ifEmpty { null },
                    basePlanId = json.optString("basePlanId").ifEmpty { null },
                    currentPeriodEndMs = currentPeriodEnd,
                    lastVerifiedAtMs = lastVerifiedAt,
                    isAutoRenewing = json.optBoolean("isAutoRenewing", false),
                    isCachedOffline = true
                )
            } else {
                PremiumEntitlement.free(accountId = json.optString("accountId").ifEmpty { null })
            }
        } catch (e: Exception) {
            PremiumEntitlement.free()
        }
    }

    private fun saveCachedEntitlement(entitlement: PremiumEntitlement) {
        try {
            val json = JSONObject().apply {
                entitlement.accountId?.let { put("accountId", it) }
                put("status", entitlement.status.name)
                entitlement.productId?.let { put("productId", it) }
                entitlement.basePlanId?.let { put("basePlanId", it) }
                put("currentPeriodEndMs", entitlement.currentPeriodEndMs)
                put("lastVerifiedAtMs", entitlement.lastVerifiedAtMs)
                put("isAutoRenewing", entitlement.isAutoRenewing)
            }.toString()
            cacheFile.writeText(json)
        } catch (ignored: Exception) {
            // Non-fatal cache failure
        }
    }

    private fun clearCachedEntitlement() {
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
    }
}
