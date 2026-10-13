package com.zynpath.backend.ads.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.ads.model.PendingRewardClaim;
import com.zynpath.backend.ads.model.PlayerHintCreditBalance;
import com.zynpath.backend.ads.model.RewardDto.AdMobSsvCallbackParams;
import com.zynpath.backend.ads.model.RewardDto.PlayerHintBalanceResponse;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardRequest;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardResponse;
import com.zynpath.backend.ads.model.RewardGrantCommand;
import com.zynpath.backend.ads.model.RewardGrantResult;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.ads.model.RewardVerificationRecord;
import com.zynpath.backend.ads.model.RewardVerificationStatus;
import com.zynpath.backend.persistence.port.AdRewardPersistencePort;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service managing rewarded ad hint rewards, AdMob Server-Side Verification (SSV),
 * two-phase claim matching, replay protection, and abuse limits.
 *
 * Implements Security Hardening:
 * - Atomic database transaction boundary for all reward state transitions.
 * - Multi-instance database-backed concurrency safety.
 * - Strict exact AdMob ad unit ID verification (no suffix matching).
 * - Exact reward amount & reward item verification.
 * - Prevention of transaction ID poisoning by unverified/failed callbacks.
 * - URL duplicate parameter rejection.
 */
@Service
public class AdRewardService {

    private static final Logger log = LoggerFactory.getLogger(AdRewardService.class);

    public static final int UNLIMITED_DAILY_REWARDED_ADS = Integer.MAX_VALUE;
    public static final int UNLIMITED_STORED_REWARD_CREDITS = Integer.MAX_VALUE;
    public static final int MAX_DAILY_REWARDED_ADS = UNLIMITED_DAILY_REWARDED_ADS;
    public static final int MAX_STORED_REWARD_CREDITS = UNLIMITED_STORED_REWARD_CREDITS;
    public static final int HINTS_PER_VERIFIED_AD = 1;
    private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000L;
    private static final long MAX_TIMESTAMP_AGE_MS = 24 * 60 * 60 * 1000L;
    private static final long MAX_TIMESTAMP_SKEW_FUTURE_MS = 5 * 60 * 1000L;

    private final SubscriptionService subscriptionService;
    private final AdMobSsvVerifier ssvVerifier;
    private final AdRewardPersistencePort persistencePort;
    private final ObjectMapper objectMapper;
    private final SecurityAuditLogger auditLogger;
    private final Set<String> allowedAdUnitIds;

    @Autowired
    public AdRewardService(
        SubscriptionService subscriptionService,
        AdMobSsvVerifier ssvVerifier,
        AdRewardPersistencePort persistencePort,
        ObjectMapper objectMapper,
        SecurityAuditLogger auditLogger,
        @Value("${zynpath.ads.rewarded.ad-unit-ids:ca-app-pub-8131384105009406/6659667211,6659667211,ca-app-pub-3940256099942544/5224354917,5224354917,admob_unit_rewarded_001}") String allowedAdUnitIdsConfig
    ) {
        this.subscriptionService = subscriptionService;
        this.ssvVerifier = ssvVerifier;
        this.persistencePort = persistencePort;
        this.objectMapper = objectMapper;
        this.auditLogger = auditLogger;
        this.allowedAdUnitIds = parseAllowedAdUnitIds(allowedAdUnitIdsConfig);
    }

    public AdRewardService(
        SubscriptionService subscriptionService,
        AdMobSsvVerifier ssvVerifier,
        AdRewardPersistencePort persistencePort,
        ObjectMapper objectMapper,
        String allowedAdUnitIdsConfig
    ) {
        this(subscriptionService, ssvVerifier, persistencePort, objectMapper, null, allowedAdUnitIdsConfig);
    }

    private static Set<String> parseAllowedAdUnitIds(String config) {
        if (config == null || config.isBlank()) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>();
        for (String s : config.split("\\s*,\\s*")) {
            if (!s.isBlank()) {
                set.add(s.trim());
            }
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Checks verification status or registers a pending reward claim for an authenticated player.
     * Never grants hints immediately unless a matching verified SSV transaction has already been committed.
     */
    public synchronized VerifyRewardResponse verifyReward(String playerId, VerifyRewardRequest request) {
        if (playerId == null || playerId.isBlank()) {
            return new VerifyRewardResponse(
                RewardVerificationStatus.REJECTED,
                false,
                request.rewardEventId(),
                0,
                0,
                "Authentication required to verify reward"
            );
        }

        // 1. Premium subscription check: active subscribers possess unlimited hints
        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(playerId);
        boolean isPremium = entitlement != null && entitlement.isActive();
        PlayerHintCreditBalance currentBalance = getOrCreateBalance(playerId);

        if (isPremium) {
            return new VerifyRewardResponse(
                RewardVerificationStatus.SERVER_VERIFIED,
                true,
                request.rewardEventId(),
                currentBalance.rewardedCredits(),
                Integer.MAX_VALUE,
                "Active Premium subscription provides unlimited Solo hints"
            );
        }

        String claimId = request.rewardEventId();

        // 2. Check if claim already exists in database
        Optional<PendingRewardClaim> claimOpt = persistencePort.findPendingClaim(claimId);
        if (claimOpt.isPresent()) {
            PendingRewardClaim existing = claimOpt.get();

            // Ownership validation: ensure claim belongs to the authenticated player
            if (!existing.playerId().equals(playerId)) {
                log.warn("Ownership mismatch: claimId={} belongs to player {} but request issued by {}",
                        claimId, existing.playerId(), playerId);
                return new VerifyRewardResponse(
                    RewardVerificationStatus.REJECTED,
                    false,
                    claimId,
                    currentBalance.rewardedCredits(),
                    currentBalance.totalAvailable(),
                    "Reward claim does not belong to the authenticated player"
                );
            }

            // Case A: SSV callback has already verified this claim
            if (existing.isVerified()) {
                log.info("Claim {} is already SERVER_VERIFIED for player {}", claimId, playerId);
                return new VerifyRewardResponse(
                    RewardVerificationStatus.SERVER_VERIFIED,
                    true,
                    claimId,
                    currentBalance.rewardedCredits(),
                    currentBalance.totalAvailable(),
                    "Reward verified and credited (+1 Solo hint)"
                );
            }

            // Case B: Still awaiting SSV callback
            if (existing.isPending()) {
                log.info("Claim {} is PENDING_VERIFICATION for player {}", claimId, playerId);
                return new VerifyRewardResponse(
                    RewardVerificationStatus.PENDING_VERIFICATION,
                    false,
                    claimId,
                    currentBalance.rewardedCredits(),
                    currentBalance.totalAvailable(),
                    "Reward verification is pending AdMob server callback"
                );
            }

            // Case C: Rejected
            log.info("Claim {} is REJECTED for player {}: {}", claimId, playerId, existing.failureReason());
            return new VerifyRewardResponse(
                RewardVerificationStatus.REJECTED,
                false,
                claimId,
                currentBalance.rewardedCredits(),
                currentBalance.totalAvailable(),
                existing.failureReason() != null ? existing.failureReason() : "Reward verification rejected"
            );
        }

        // 3. Claim does not exist yet (Client called before Google's SSV webhook arrived)
        long now = System.currentTimeMillis();

        // Register new PENDING claim
        PendingRewardClaim pendingClaim = new PendingRewardClaim(
            claimId,
            playerId,
            request.adUnitId() != null ? request.adUnitId() : "",
            null,
            "SOLO_HINT",
            RewardVerificationStatus.PENDING_VERIFICATION,
            null,
            now,
            null,
            null,
            null
        );
        persistencePort.savePendingClaim(pendingClaim);

        log.info("Registered pending reward claim: claimId={}, playerId={}", claimId, playerId);
        return new VerifyRewardResponse(
            RewardVerificationStatus.PENDING_VERIFICATION,
            false,
            claimId,
            currentBalance.rewardedCredits(),
            currentBalance.totalAvailable(),
            "Reward verification is pending AdMob server callback"
        );
    }

    /**
     * Handles incoming Google AdMob Server-Side Verification (SSV) callback webhook using fallback query string.
     */
    public boolean handleSsvCallback(AdMobSsvCallbackParams params) {
        return handleSsvCallback(params, buildFallbackQueryString(params));
    }

    /**
     * Handles incoming Google AdMob Server-Side Verification (SSV) callback webhook.
     * Validates cryptographic signature, exact ad unit, timestamp freshness, reward amount and item,
     * and performs atomic database-backed state transition and credit grant.
     */
    public boolean handleSsvCallback(AdMobSsvCallbackParams params, String rawQueryString) {
        String effectiveQueryString = (rawQueryString != null && !rawQueryString.isBlank())
                ? rawQueryString
                : buildFallbackQueryString(params);

        if (params == null || params.transactionId() == null || params.transactionId().isBlank()) {
            log.warn("Invalid SSV callback: missing transactionId");
            return false;
        }

        String txnId = params.transactionId().trim();

        // 1. Replay prevention: check if transaction was already processed
        Optional<RewardVerificationRecord> existingRecord = persistencePort.findSsvRecord(txnId);
        if (existingRecord.isPresent() && existingRecord.get().isValid()) {
            log.warn("Duplicate AdMob SSV transactionId rejected: {}", txnId);
            return false;
        }

        // 2. Reject duplicate parameters in query string
        if (AdMobSsvVerifier.hasDuplicateParameters(effectiveQueryString)) {
            log.warn("Rejected malformed SSV callback: duplicate query parameters for txnId={}", txnId);
            logSecurityAlert(params.userId(), "Duplicate query parameters in SSV callback", txnId);
            return false;
        }

        // 3. Validate required parameters
        if (params.userId() == null || params.userId().isBlank()
                || params.keyId() == null || params.keyId().isBlank()
                || params.signature() == null || params.signature().isBlank()
                || params.customData() == null || params.customData().isBlank()
                || params.adUnit() == null || params.adUnit().isBlank()
                || params.timestamp() == null || params.timestamp().isBlank()
                || params.rewardAmount() == null || params.rewardAmount().isBlank()
                || params.rewardItem() == null || params.rewardItem().isBlank()) {
            log.warn("Invalid SSV callback for txnId={}: missing required parameters", txnId);
            logSecurityAlert(params.userId(), "Missing required parameters in SSV callback", txnId);
            return false;
        }

        // 4. Validate exact Ad Unit ID (reject missing or mismatch, no suffix matching)
        String adUnit = params.adUnit().trim();
        if (!isAdUnitAllowed(adUnit)) {
            log.warn("Unexpected AdMob rewarded ad unit rejected for txnId={}: {}", txnId, adUnit);
            logSecurityAlert(params.userId(), "Unexpected ad unit: " + adUnit, txnId);
            return false;
        }

        // 5. Validate reward amount and reward item
        int rewardAmount;
        try {
            rewardAmount = Integer.parseInt(params.rewardAmount().trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid reward_amount format for txnId={}: {}", txnId, params.rewardAmount());
            logSecurityAlert(params.userId(), "Invalid reward_amount: " + params.rewardAmount(), txnId);
            return false;
        }
        if (rewardAmount != HINTS_PER_VERIFIED_AD) {
            log.warn("Unexpected reward_amount for txnId={}: expected {}, got {}", txnId, HINTS_PER_VERIFIED_AD, rewardAmount);
            logSecurityAlert(params.userId(), "Unexpected reward_amount: " + rewardAmount, txnId);
            return false;
        }

        if (!"SOLO_HINT".equalsIgnoreCase(params.rewardItem().trim())) {
            log.warn("Unexpected reward_item for txnId={}: expected SOLO_HINT, got {}", txnId, params.rewardItem());
            logSecurityAlert(params.userId(), "Unexpected reward_item: " + params.rewardItem(), txnId);
            return false;
        }

        // 6. Validate timestamp freshness
        long now = System.currentTimeMillis();
        long ssvTimestamp;
        try {
            ssvTimestamp = Long.parseLong(params.timestamp().trim());
        } catch (Exception e) {
            log.warn("Invalid timestamp in SSV callback for txnId={}: {}", txnId, params.timestamp());
            logSecurityAlert(params.userId(), "Invalid timestamp format", txnId);
            return false;
        }

        if (now - ssvTimestamp > MAX_TIMESTAMP_AGE_MS) {
            log.warn("Stale SSV callback timestamp for txnId={}: ageMs={}", txnId, now - ssvTimestamp);
            logSecurityAlert(params.userId(), "Stale SSV timestamp", txnId);
            return false;
        }
        if (ssvTimestamp - now > MAX_TIMESTAMP_SKEW_FUTURE_MS) {
            log.warn("Future SSV callback timestamp rejected for txnId={}: futureSkewMs={}", txnId, ssvTimestamp - now);
            logSecurityAlert(params.userId(), "Future SSV timestamp", txnId);
            return false;
        }

        // 7. Cryptographic signature verification using raw query string excluding signature & key_id
        String messageToVerify = AdMobSsvVerifier.extractMessageToVerify(effectiveQueryString);
        boolean isSignatureValid = ssvVerifier.verifySignature(messageToVerify, params.keyId().trim(), params.signature().trim());
        if (!isSignatureValid) {
            log.warn("AdMob SSV signature verification failed for txnId={}", txnId);
            logSecurityAlert(params.userId(), "Invalid cryptographic signature", txnId);
            return false;
        }

        // 8. Safe custom_data parsing
        ParsedCustomData customData = parseCustomData(params.customData());
        if (customData == null || customData.claimId() == null || customData.claimId().isBlank()) {
            log.warn("AdMob SSV custom_data missing claimId for txnId={}", txnId);
            logSecurityAlert(params.userId(), "Missing claimId in custom_data", txnId);
            return false;
        }

        if (!"SOLO_HINT".equalsIgnoreCase(customData.rewardType())) {
            log.warn("Unsupported rewardType in custom_data for txnId={}: {}", txnId, customData.rewardType());
            logSecurityAlert(params.userId(), "Unsupported rewardType: " + customData.rewardType(), txnId);
            return false;
        }

        // 9. Atomic, database-backed state transition and credit grant
        RewardGrantCommand command = new RewardGrantCommand(
            params.userId().trim(),
            customData.claimId().trim(),
            txnId,
            adUnit,
            customData.levelId(),
            params.keyId().trim(),
            params.signature().trim(),
            params.customData().trim(),
            effectiveQueryString,
            MAX_DAILY_REWARDED_ADS,
            MAX_STORED_REWARD_CREDITS,
            HINTS_PER_VERIFIED_AD,
            now
        );

        RewardGrantResult result;
        try {
            result = persistencePort.grantRewardAtomic(command);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.warn("Database unique constraint violation for txnId={}: {}", txnId, e.getMessage());
            logSecurityAlert(params.userId(), "Duplicate transaction or claim rejected by database unique constraint", txnId);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error executing atomic reward grant for txnId={}: {}", txnId, e.getMessage(), e);
            return false;
        }

        if (!result.isSuccess()) {
            log.warn("AdMob SSV reward grant failed for txnId={}: status={}, reason={}",
                    txnId, result.status(), result.failureReason());
            logSecurityAlert(params.userId(), "Reward grant failed: " + result.status(), txnId);
            return false;
        }

        log.info("AdMob SSV verified and committed: txnId={}, claimId={}, playerId={}, newCredits={}",
                txnId, customData.claimId(), params.userId(), result.updatedBalance().rewardedCredits());
        return true;
    }

    /**
     * Retrieves the current hint balance for a player.
     */
    public PlayerHintBalanceResponse getBalance(String playerId) {
        PlayerHintCreditBalance balance = getOrCreateBalance(playerId);
        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(playerId);
        boolean isPremium = entitlement != null && entitlement.isActive();

        return new PlayerHintBalanceResponse(
            balance.freeHintsRemaining(),
            balance.rewardedCredits(),
            isPremium ? Integer.MAX_VALUE : balance.totalAvailable(),
            isPremium,
            Integer.MAX_VALUE
        );
    }

    private PlayerHintCreditBalance getOrCreateBalance(String playerId) {
        return persistencePort.getBalance(playerId)
                .orElseGet(() -> PlayerHintCreditBalance.defaultBalance(playerId));
    }

    private boolean isAdUnitAllowed(String adUnit) {
        if (adUnit == null || adUnit.isBlank()) {
            return false;
        }
        if (allowedAdUnitIds.isEmpty()) {
            return false;
        }
        return allowedAdUnitIds.contains(adUnit.trim());
    }

    private void logSecurityAlert(String playerId, String reason, String txnId) {
        if (auditLogger != null) {
            auditLogger.logEvent(
                SecurityEventType.SUSPICIOUS_REWARD_ATTEMPT,
                playerId,
                Map.of("reason", reason != null ? reason : "Unknown", "txnId", txnId != null ? txnId : "N/A")
            );
        }
    }

    private ParsedCustomData parseCustomData(String customDataStr) {
        if (customDataStr == null || customDataStr.isBlank()) {
            return null;
        }
        try {
            String decoded = customDataStr;
            if (customDataStr.contains("%")) {
                decoded = URLDecoder.decode(customDataStr, StandardCharsets.UTF_8);
            }
            JsonNode node = objectMapper.readTree(decoded);
            String claimId = node.path("claimId").asText(null);
            Integer levelId = node.has("levelId") ? node.get("levelId").asInt() : null;
            String rewardType = node.path("rewardType").asText("SOLO_HINT");
            return new ParsedCustomData(claimId, levelId, rewardType);
        } catch (Exception e) {
            log.warn("Failed to parse custom_data JSON: '{}', error: {}", customDataStr, e.getMessage());
            return null;
        }
    }

    public String buildFallbackQueryString(AdMobSsvCallbackParams params) {
        if (params == null) return "";
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (params.adNetwork() != null) parts.add("ad_network=" + params.adNetwork());
        if (params.adUnit() != null) parts.add("ad_unit=" + params.adUnit());
        if (params.customData() != null) parts.add("custom_data=" + params.customData());
        if (params.rewardAmount() != null) parts.add("reward_amount=" + params.rewardAmount());
        if (params.rewardItem() != null) parts.add("reward_item=" + params.rewardItem());
        if (params.timestamp() != null) parts.add("timestamp=" + params.timestamp());
        if (params.transactionId() != null) parts.add("transaction_id=" + params.transactionId());
        if (params.userId() != null) parts.add("user_id=" + params.userId());
        if (params.signature() != null) parts.add("signature=" + params.signature());
        if (params.keyId() != null) parts.add("key_id=" + params.keyId());
        return String.join("&", parts);
    }

    private record ParsedCustomData(String claimId, Integer levelId, String rewardType) {}
}
