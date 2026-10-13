package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.ads.model.PendingRewardClaim;
import com.zynpath.backend.ads.model.PlayerHintCreditBalance;
import com.zynpath.backend.ads.model.RewardEvent;
import com.zynpath.backend.ads.model.RewardGrantCommand;
import com.zynpath.backend.ads.model.RewardGrantResult;
import com.zynpath.backend.ads.model.RewardGrantStatus;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.ads.model.RewardVerificationRecord;
import com.zynpath.backend.ads.model.RewardVerificationStatus;
import com.zynpath.backend.persistence.port.AdRewardPersistencePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryAdRewardPersistenceAdapter implements AdRewardPersistencePort {

    private final Map<String, PlayerHintCreditBalance> balances = new ConcurrentHashMap<>();
    private final Map<String, PendingRewardClaim> pendingClaims = new ConcurrentHashMap<>();
    private final Map<String, RewardVerificationRecord> ssvRecords = new ConcurrentHashMap<>();
    private final Map<String, RewardEvent> rewardEvents = new ConcurrentHashMap<>();
    private final Map<String, Object> playerLocks = new ConcurrentHashMap<>();

    @Override
    public Optional<PlayerHintCreditBalance> getBalance(String playerId) {
        return Optional.ofNullable(balances.get(playerId));
    }

    @Override
    public PlayerHintCreditBalance saveBalance(PlayerHintCreditBalance balance) {
        balances.put(balance.playerId(), balance);
        return balance;
    }

    @Override
    public Optional<PendingRewardClaim> findPendingClaim(String claimId) {
        return Optional.ofNullable(pendingClaims.get(claimId));
    }

    @Override
    public PendingRewardClaim savePendingClaim(PendingRewardClaim claim) {
        pendingClaims.put(claim.claimId(), claim);
        return claim;
    }

    @Override
    public Optional<PendingRewardClaim> findClaimByTransactionId(String transactionId) {
        return pendingClaims.values().stream()
            .filter(c -> transactionId.equals(c.transactionId()))
            .findFirst();
    }

    @Override
    public Optional<RewardVerificationRecord> findSsvRecord(String transactionId) {
        return Optional.ofNullable(ssvRecords.get(transactionId));
    }

    @Override
    public RewardVerificationRecord saveSsvRecord(RewardVerificationRecord record) {
        ssvRecords.put(record.transactionId(), record);
        return record;
    }

    @Override
    public Optional<RewardEvent> findRewardEvent(String rewardEventId) {
        return Optional.ofNullable(rewardEvents.get(rewardEventId));
    }

    @Override
    public RewardEvent saveRewardEvent(RewardEvent event) {
        rewardEvents.put(event.rewardEventId(), event);
        return event;
    }

    @Override
    public int countDailyRewards(String playerId, long sinceTimestampMs) {
        return (int) rewardEvents.values().stream()
            .filter(e -> playerId.equals(e.playerId()) && e.grantedAtMs() >= sinceTimestampMs)
            .count();
    }

    @Override
    public RewardGrantResult grantRewardAtomic(RewardGrantCommand command) {
        String playerId = command.playerId();
        String txnId = command.transactionId();
        String claimId = command.claimId();
        long now = command.timestampMs();

        // 1. Fast pre-check: check if transaction was already processed
        RewardVerificationRecord earlySsv = ssvRecords.get(txnId);
        if (earlySsv != null && earlySsv.isValid()) {
            return RewardGrantResult.failure(RewardGrantStatus.DUPLICATE_TRANSACTION, "Duplicate transactionId already processed");
        }

        Object lock = playerLocks.computeIfAbsent(playerId, k -> new Object());
        synchronized (lock) {
            // 2. Recheck replay under player lock
            RewardVerificationRecord lockedSsv = ssvRecords.get(txnId);
            if (lockedSsv != null && lockedSsv.isValid()) {
                return RewardGrantResult.failure(RewardGrantStatus.DUPLICATE_TRANSACTION, "Duplicate transactionId already processed");
            }

            // 3. Fetch or initialize player balance
            PlayerHintCreditBalance currentBalance = balances.computeIfAbsent(
                playerId,
                k -> PlayerHintCreditBalance.defaultBalance(playerId)
            );

            // 4. Validate existing claim content, ownership, ad unit, and lifecycle under lock
            PendingRewardClaim existingClaim = pendingClaims.get(claimId);
            if (existingClaim != null) {
                // Ownership validation: do NOT mutate or poison the legitimate player's claim
                if (!existingClaim.playerId().equals(playerId)) {
                    return RewardGrantResult.failure(RewardGrantStatus.OWNERSHIP_MISMATCH,
                            "Ownership mismatch: claim belongs to " + existingClaim.playerId() + " but callback for " + playerId);
                }

                // Ad unit validation
                if (existingClaim.adUnitId() != null && !existingClaim.adUnitId().isBlank()
                        && command.adUnitId() != null && !command.adUnitId().isBlank()
                        && !existingClaim.adUnitId().equals(command.adUnitId())) {
                    return RewardGrantResult.failure(RewardGrantStatus.AD_UNIT_MISMATCH,
                            "Ad unit mismatch: claim ad unit " + existingClaim.adUnitId() + " does not match callback ad unit " + command.adUnitId());
                }

                // Reward type validation
                if (existingClaim.rewardType() != null && !"SOLO_HINT".equalsIgnoreCase(existingClaim.rewardType())) {
                    return RewardGrantResult.failure(RewardGrantStatus.REWARD_TYPE_MISMATCH,
                            "Reward type mismatch: expected SOLO_HINT but claim has " + existingClaim.rewardType());
                }

                // Lifecycle status validation
                if (existingClaim.isVerified()) {
                    return RewardGrantResult.failure(RewardGrantStatus.DUPLICATE_CLAIM_ALREADY_VERIFIED, "Duplicate claimId already verified");
                }
                if (existingClaim.status() == RewardVerificationStatus.REJECTED) {
                    return RewardGrantResult.failure(RewardGrantStatus.CLAIM_PREVIOUSLY_REJECTED,
                            "Claim previously rejected: " + existingClaim.failureReason());
                }
                if (existingClaim.status() != RewardVerificationStatus.PENDING_VERIFICATION) {
                    return RewardGrantResult.failure(RewardGrantStatus.CLAIM_PREVIOUSLY_REJECTED,
                            "Claim has invalid status: " + existingClaim.status());
                }
            }

            // 4. Daily limits (only enforced if finite limit is configured)
            if (command.maxDailyRewards() > 0 && command.maxDailyRewards() < Integer.MAX_VALUE) {
                long oneDayAgo = now - (24 * 60 * 60 * 1000L);
                int dailyCount = countDailyRewards(playerId, oneDayAgo);
                if (dailyCount >= command.maxDailyRewards()) {
                    if (existingClaim != null) {
                        pendingClaims.put(claimId, existingClaim.withRejected("Daily limit exceeded (" + command.maxDailyRewards() + "/" + command.maxDailyRewards() + ")"));
                    }
                    return RewardGrantResult.failure(RewardGrantStatus.DAILY_LIMIT_EXCEEDED, "Daily limit exceeded");
                }
            }

            // 5. Stored credits cap (only enforced if finite cap is configured)
            if (command.maxStoredCredits() > 0 && command.maxStoredCredits() < Integer.MAX_VALUE) {
                if (currentBalance.rewardedCredits() >= command.maxStoredCredits()) {
                    if (existingClaim != null) {
                        pendingClaims.put(claimId, existingClaim.withRejected("Maximum stored credits reached (" + command.maxStoredCredits() + ")"));
                    }
                    return RewardGrantResult.failure(RewardGrantStatus.CREDIT_CAP_REACHED, "Maximum stored credits reached");
                }
            }

            // 6. Transition / create pending claim to SERVER_VERIFIED
            if (existingClaim != null) {
                pendingClaims.put(claimId, existingClaim.withVerified(txnId, now));
            } else {
                PendingRewardClaim newClaim = new PendingRewardClaim(
                    claimId,
                    playerId,
                    command.adUnitId() != null ? command.adUnitId() : "",
                    command.levelId(),
                    "SOLO_HINT",
                    RewardVerificationStatus.SERVER_VERIFIED,
                    txnId,
                    now,
                    now,
                    null,
                    null
                );
                pendingClaims.put(claimId, newClaim);
            }

            // 7. Update balance
            PlayerHintCreditBalance updatedBalance = currentBalance.withAddedCredits(command.hintsToGrant());
            balances.put(playerId, updatedBalance);

            // 8. Persist reward event
            RewardEvent event = new RewardEvent(
                claimId,
                playerId,
                txnId,
                RewardType.SOLO_HINT,
                command.hintsToGrant(),
                command.adUnitId(),
                RewardVerificationStatus.SERVER_VERIFIED,
                now,
                null
            );
            rewardEvents.put(claimId, event);

            // 9. Persist verified SSV record
            RewardVerificationRecord ssvRecord = new RewardVerificationRecord(
                txnId,
                command.keyId(),
                command.signature(),
                command.customData(),
                playerId,
                claimId,
                command.adUnitId(),
                command.hintsToGrant(),
                command.rawQueryString(),
                now,
                true,
                null
            );
            ssvRecords.put(txnId, ssvRecord);

            return RewardGrantResult.success(updatedBalance);
        }
    }
}
