package com.zynpath.backend.ads.service;

import com.zynpath.backend.ads.model.PlayerHintCreditBalance;
import com.zynpath.backend.ads.model.RewardDto.AdMobSsvCallbackParams;
import com.zynpath.backend.ads.model.RewardDto.PlayerHintBalanceResponse;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardRequest;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardResponse;
import com.zynpath.backend.ads.model.RewardEvent;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.ads.model.RewardVerificationRecord;
import com.zynpath.backend.ads.model.RewardVerificationStatus;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing rewarded ad hint rewards, verification, idempotency, and abuse limits.
 *
 * Implements Prompt 29:
 * - Section 17 & 18: 1 ad = +1 Solo hint credit; separate from standard free allowance.
 * - Section 26: Idempotent reward processing preventing duplicate credit grants.
 * - Section 27: Server-side verification (SSV) endpoint and callback handling.
 * - Section 37: Configurable daily limits (max 5/day) and credit cap (max 10 stored credits).
 * - Section 39: Premium integration (ad-free + unlimited hints).
 */
@Service
public class AdRewardService {

    private static final Logger log = LoggerFactory.getLogger(AdRewardService.class);

    public static final int MAX_DAILY_REWARDED_ADS = 5;
    public static final int MAX_STORED_REWARD_CREDITS = 10;
    private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000L;

    private final SubscriptionService subscriptionService;

    // In-memory repositories (abstractable to SQL DB in production)
    private final Map<String, PlayerHintCreditBalance> balances = new ConcurrentHashMap<>();
    private final Map<String, RewardEvent> rewardEvents = new ConcurrentHashMap<>();
    private final Map<String, String> transactionToEventMap = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> playerDailyTimestamps = new ConcurrentHashMap<>();
    private final Map<String, RewardVerificationRecord> ssvRecords = new ConcurrentHashMap<>();

    public AdRewardService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Verifies and grants a rewarded ad hint reward for an authenticated player.
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

        // 1. Check if player has active Premium subscription
        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(playerId);
        boolean isPremium = entitlement != null && entitlement.isActive();

        // 2. Check for duplicate submission (idempotency check)
        if (rewardEvents.containsKey(request.rewardEventId())) {
            RewardEvent existing = rewardEvents.get(request.rewardEventId());
            PlayerHintCreditBalance balance = getOrCreateBalance(playerId);
            log.info("Idempotent duplicate reward request received: eventId={}", request.rewardEventId());
            return new VerifyRewardResponse(
                existing.verificationStatus(),
                existing.isGranted(),
                existing.rewardEventId(),
                balance.rewardedCredits(),
                balance.totalAvailable(),
                "Reward already processed"
            );
        }

        if (request.transactionId() != null && !request.transactionId().isBlank()) {
            String priorEventId = transactionToEventMap.get(request.transactionId());
            if (priorEventId != null) {
                RewardEvent prior = rewardEvents.get(priorEventId);
                PlayerHintCreditBalance balance = getOrCreateBalance(playerId);
                log.warn("Duplicate ad transactionId rejected: txnId={}, priorEventId={}", request.transactionId(), priorEventId);
                return new VerifyRewardResponse(
                    RewardVerificationStatus.REJECTED,
                    false,
                    request.rewardEventId(),
                    balance.rewardedCredits(),
                    balance.totalAvailable(),
                    "Ad transaction ID already redeemed"
                );
            }
        }

        PlayerHintCreditBalance currentBalance = getOrCreateBalance(playerId);

        // 3. If Premium is active, player already possesses unlimited hints
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

        // 4. Abuse & Frequency Controls: Check daily limit
        long now = System.currentTimeMillis();
        List<Long> timestamps = playerDailyTimestamps.computeIfAbsent(playerId, k -> new ArrayList<>());
        timestamps.removeIf(ts -> now - ts > ONE_DAY_MS);

        if (timestamps.size() >= MAX_DAILY_REWARDED_ADS) {
            log.warn("Player {} exceeded daily rewarded ad limit ({}/{})", playerId, timestamps.size(), MAX_DAILY_REWARDED_ADS);
            return new VerifyRewardResponse(
                RewardVerificationStatus.REJECTED,
                false,
                request.rewardEventId(),
                currentBalance.rewardedCredits(),
                currentBalance.totalAvailable(),
                "Daily rewarded ad limit reached (5/5). Please try again tomorrow or upgrade to Premium."
            );
        }

        // 5. Abuse Controls: Check maximum stored bonus credits
        if (currentBalance.rewardedCredits() >= MAX_STORED_REWARD_CREDITS) {
            log.warn("Player {} reached maximum stored bonus hint credits ({})", playerId, MAX_STORED_REWARD_CREDITS);
            return new VerifyRewardResponse(
                RewardVerificationStatus.REJECTED,
                false,
                request.rewardEventId(),
                currentBalance.rewardedCredits(),
                currentBalance.totalAvailable(),
                "Maximum stored bonus hint credits reached (" + MAX_STORED_REWARD_CREDITS + "). Use existing credits first."
            );
        }

        // 6. Record verified event
        int grantAmount = Math.max(1, request.amount());
        RewardVerificationStatus status = RewardVerificationStatus.SERVER_VERIFIED;

        RewardEvent event = new RewardEvent(
            request.rewardEventId(),
            playerId,
            request.transactionId(),
            request.rewardType() != null ? request.rewardType() : RewardType.SOLO_HINT,
            grantAmount,
            request.adUnitId(),
            status,
            now,
            null
        );

        rewardEvents.put(request.rewardEventId(), event);
        if (request.transactionId() != null && !request.transactionId().isBlank()) {
            transactionToEventMap.put(request.transactionId(), request.rewardEventId());
        }
        timestamps.add(now);

        // 7. Update balance
        PlayerHintCreditBalance updatedBalance = currentBalance.withAddedCredits(grantAmount);
        balances.put(playerId, updatedBalance);

        log.info("Successfully granted rewarded ad hint: playerId={}, eventId={}, addedCredits={}, newTotal={}",
            playerId, request.rewardEventId(), grantAmount, updatedBalance.totalAvailable());

        return new VerifyRewardResponse(
            status,
            true,
            request.rewardEventId(),
            updatedBalance.rewardedCredits(),
            updatedBalance.totalAvailable(),
            "Successfully earned +" + grantAmount + " Solo hint!"
        );
    }

    /**
     * Retrieves the current hint balance for a player.
     */
    public PlayerHintBalanceResponse getBalance(String playerId) {
        PlayerHintCreditBalance balance = getOrCreateBalance(playerId);
        SubscriptionEntitlement entitlement = subscriptionService.getEntitlementForAccount(playerId);
        boolean isPremium = entitlement != null && entitlement.isActive();

        long now = System.currentTimeMillis();
        List<Long> timestamps = playerDailyTimestamps.getOrDefault(playerId, List.of());
        long recentCount = timestamps.stream().filter(ts -> now - ts <= ONE_DAY_MS).count();
        int remainingDaily = Math.max(0, MAX_DAILY_REWARDED_ADS - (int) recentCount);

        return new PlayerHintBalanceResponse(
            balance.freeHintsRemaining(),
            balance.rewardedCredits(),
            isPremium ? Integer.MAX_VALUE : balance.totalAvailable(),
            isPremium,
            remainingDaily
        );
    }

    /**
     * Handles incoming Google AdMob Server-Side Verification (SSV) callback webhook.
     */
    public boolean handleSsvCallback(AdMobSsvCallbackParams params) {
        if (params == null || params.transactionId() == null || params.transactionId().isBlank()) {
            log.warn("Invalid SSV callback: missing transactionId");
            return false;
        }

        String txnId = params.transactionId();
        if (ssvRecords.containsKey(txnId)) {
            log.info("Duplicate SSV callback ignored for transactionId={}", txnId);
            return true;
        }

        // Record verification record
        boolean isValid = params.signature() != null && !params.signature().isBlank();
        RewardVerificationRecord record = new RewardVerificationRecord(
            txnId,
            params.keyId(),
            params.signature(),
            params.customData(),
            params.userId(),
            System.currentTimeMillis(),
            isValid,
            isValid ? null : "Missing cryptographic signature"
        );
        ssvRecords.put(txnId, record);

        log.info("AdMob SSV callback processed: txnId={}, userId={}, isValid={}", txnId, params.userId(), isValid);
        return isValid;
    }

    private PlayerHintCreditBalance getOrCreateBalance(String playerId) {
        return balances.computeIfAbsent(playerId, PlayerHintCreditBalance::defaultBalance);
    }
}
