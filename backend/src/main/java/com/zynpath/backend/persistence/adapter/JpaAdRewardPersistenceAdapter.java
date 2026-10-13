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
import com.zynpath.backend.persistence.entity.AdMobSsvRecordEntity;
import com.zynpath.backend.persistence.entity.PendingRewardClaimEntity;
import com.zynpath.backend.persistence.entity.PlayerHintBalanceEntity;
import com.zynpath.backend.persistence.entity.RewardEventEntity;
import com.zynpath.backend.persistence.port.AdRewardPersistencePort;
import com.zynpath.backend.persistence.repository.AdMobSsvRecordRepository;
import com.zynpath.backend.persistence.repository.PendingRewardClaimRepository;
import com.zynpath.backend.persistence.repository.PlayerHintBalanceRepository;
import com.zynpath.backend.persistence.repository.RewardEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaAdRewardPersistenceAdapter implements AdRewardPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaAdRewardPersistenceAdapter.class);

    private final PlayerHintBalanceRepository balanceRepository;
    private final PendingRewardClaimRepository claimRepository;
    private final AdMobSsvRecordRepository ssvRepository;
    private final RewardEventRepository eventRepository;

    public JpaAdRewardPersistenceAdapter(
        PlayerHintBalanceRepository balanceRepository,
        PendingRewardClaimRepository claimRepository,
        AdMobSsvRecordRepository ssvRepository,
        RewardEventRepository eventRepository
    ) {
        this.balanceRepository = balanceRepository;
        this.claimRepository = claimRepository;
        this.ssvRepository = ssvRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerHintCreditBalance> getBalance(String playerId) {
        return balanceRepository.findById(playerId).map(this::toBalanceModel);
    }

    @Override
    @Transactional
    public PlayerHintCreditBalance saveBalance(PlayerHintCreditBalance balance) {
        PlayerHintBalanceEntity entity = new PlayerHintBalanceEntity(
            balance.playerId(),
            balance.freeHintsRemaining(),
            balance.rewardedCredits(),
            balance.totalAvailable(),
            balance.lastUpdatedMs()
        );
        PlayerHintBalanceEntity saved = balanceRepository.save(entity);
        return toBalanceModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PendingRewardClaim> findPendingClaim(String claimId) {
        return claimRepository.findById(claimId).map(this::toClaimModel);
    }

    @Override
    @Transactional
    public PendingRewardClaim savePendingClaim(PendingRewardClaim claim) {
        PendingRewardClaimEntity entity = new PendingRewardClaimEntity(
            claim.claimId(),
            claim.playerId(),
            claim.adUnitId(),
            claim.levelId(),
            claim.rewardType(),
            claim.status().name(),
            claim.transactionId(),
            claim.createdAtMs(),
            claim.verifiedAtMs(),
            claim.consumedAtMs(),
            claim.failureReason()
        );
        PendingRewardClaimEntity saved = claimRepository.save(entity);
        return toClaimModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PendingRewardClaim> findClaimByTransactionId(String transactionId) {
        return claimRepository.findByTransactionId(transactionId).map(this::toClaimModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RewardVerificationRecord> findSsvRecord(String transactionId) {
        return ssvRepository.findById(transactionId).map(this::toSsvModel);
    }

    @Override
    @Transactional
    public RewardVerificationRecord saveSsvRecord(RewardVerificationRecord record) {
        AdMobSsvRecordEntity entity = new AdMobSsvRecordEntity(
            record.transactionId(),
            record.keyId(),
            record.signature(),
            record.customData(),
            record.userId(),
            record.claimId(),
            record.adUnitId(),
            record.rewardAmount(),
            record.rawQueryString(),
            record.verifiedAtMs(),
            record.isValid(),
            record.failureReason()
        );
        AdMobSsvRecordEntity saved = ssvRepository.save(entity);
        return toSsvModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RewardEvent> findRewardEvent(String rewardEventId) {
        return eventRepository.findById(rewardEventId).map(this::toEventModel);
    }

    @Override
    @Transactional
    public RewardEvent saveRewardEvent(RewardEvent event) {
        RewardEventEntity entity = new RewardEventEntity(
            event.rewardEventId(),
            event.playerId(),
            event.transactionId(),
            event.rewardType().name(),
            event.amount(),
            event.adUnitId(),
            event.verificationStatus().name(),
            event.grantedAtMs(),
            event.consumedAtMs()
        );
        RewardEventEntity saved = eventRepository.save(entity);
        return toEventModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public int countDailyRewards(String playerId, long sinceTimestampMs) {
        return (int) eventRepository.countByPlayerIdAndGrantedAtMsGreaterThanEqual(playerId, sinceTimestampMs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RewardGrantResult grantRewardAtomic(RewardGrantCommand command) {
        String txnId = command.transactionId();
        String playerId = command.playerId();
        String claimId = command.claimId();
        long now = command.timestampMs();

        // 1. Fast pre-check: if transaction was already processed, exit early before acquiring lock
        Optional<AdMobSsvRecordEntity> earlySsv = ssvRepository.findById(txnId);
        if (earlySsv.isPresent() && earlySsv.get().isValid()) {
            return RewardGrantResult.failure(RewardGrantStatus.DUPLICATE_TRANSACTION, "Duplicate transactionId already processed");
        }

        // 2. Multi-instance concurrency: acquire row-level lock on player hint balance FIRST
        PlayerHintBalanceEntity balanceEntity = lockOrCreateBalance(playerId, now);

        // 3. Recheck replay under lock: ensure concurrent identical callbacks cannot grant multiple credits
        Optional<AdMobSsvRecordEntity> lockedSsv = ssvRepository.findById(txnId);
        if (lockedSsv.isPresent() && lockedSsv.get().isValid()) {
            return RewardGrantResult.failure(RewardGrantStatus.DUPLICATE_TRANSACTION, "Duplicate transactionId already processed");
        }

        // 4. Validate existing claim content, ownership, ad unit, and lifecycle under lock
        Optional<PendingRewardClaimEntity> claimOpt = claimRepository.findById(claimId);
        if (claimOpt.isPresent()) {
            PendingRewardClaimEntity existingClaim = claimOpt.get();

            // Ownership validation: verify existing claim belongs to the callback's player.
            // Do NOT poison or mutate the legitimate player's claim if mismatched input was submitted.
            if (!existingClaim.getPlayerId().equals(playerId)) {
                return RewardGrantResult.failure(RewardGrantStatus.OWNERSHIP_MISMATCH,
                        "Ownership mismatch: claim belongs to " + existingClaim.getPlayerId() + " but callback for " + playerId);
            }

            // Ad unit validation: verify claim ad unit matches callback ad unit ID
            if (existingClaim.getAdUnitId() != null && !existingClaim.getAdUnitId().isBlank()
                    && command.adUnitId() != null && !command.adUnitId().isBlank()
                    && !existingClaim.getAdUnitId().equals(command.adUnitId())) {
                return RewardGrantResult.failure(RewardGrantStatus.AD_UNIT_MISMATCH,
                        "Ad unit mismatch: claim ad unit " + existingClaim.getAdUnitId() + " does not match callback ad unit " + command.adUnitId());
            }

            // Reward type validation: only SOLO_HINT is supported
            if (existingClaim.getRewardType() != null && !"SOLO_HINT".equalsIgnoreCase(existingClaim.getRewardType())) {
                return RewardGrantResult.failure(RewardGrantStatus.REWARD_TYPE_MISMATCH,
                        "Reward type mismatch: expected SOLO_HINT but claim has " + existingClaim.getRewardType());
            }

            // Lifecycle status validation
            if (RewardVerificationStatus.SERVER_VERIFIED.name().equals(existingClaim.getStatus())) {
                return RewardGrantResult.failure(RewardGrantStatus.DUPLICATE_CLAIM_ALREADY_VERIFIED,
                        "Duplicate claimId already verified");
            }

            if (RewardVerificationStatus.REJECTED.name().equals(existingClaim.getStatus())) {
                return RewardGrantResult.failure(RewardGrantStatus.CLAIM_PREVIOUSLY_REJECTED,
                        "Claim previously rejected: " + existingClaim.getFailureReason());
            }

            if (!RewardVerificationStatus.PENDING_VERIFICATION.name().equals(existingClaim.getStatus())) {
                return RewardGrantResult.failure(RewardGrantStatus.CLAIM_PREVIOUSLY_REJECTED,
                        "Claim has invalid status: " + existingClaim.getStatus());
            }
        }

        // 5. Abuse & frequency checks: daily limit (only enforced if finite limit is configured)
        if (command.maxDailyRewards() > 0 && command.maxDailyRewards() < Integer.MAX_VALUE) {
            long oneDayAgo = now - (24 * 60 * 60 * 1000L);
            int dailyCount = (int) eventRepository.countByPlayerIdAndGrantedAtMsGreaterThanEqual(playerId, oneDayAgo);
            if (dailyCount >= command.maxDailyRewards()) {
                if (claimOpt.isPresent()) {
                    PendingRewardClaimEntity c = claimOpt.get();
                    c.setStatus(RewardVerificationStatus.REJECTED.name());
                    c.setFailureReason("Daily limit exceeded (" + command.maxDailyRewards() + "/" + command.maxDailyRewards() + ")");
                    claimRepository.save(c);
                }
                return RewardGrantResult.failure(RewardGrantStatus.DAILY_LIMIT_EXCEEDED, "Daily limit exceeded");
            }
        }

        // 6. Abuse check: stored credits cap (only enforced if finite cap is configured)
        if (command.maxStoredCredits() > 0 && command.maxStoredCredits() < Integer.MAX_VALUE) {
            if (balanceEntity.getRewardedCredits() >= command.maxStoredCredits()) {
                if (claimOpt.isPresent()) {
                    PendingRewardClaimEntity c = claimOpt.get();
                    c.setStatus(RewardVerificationStatus.REJECTED.name());
                    c.setFailureReason("Maximum stored credits reached (" + command.maxStoredCredits() + ")");
                    claimRepository.save(c);
                }
                return RewardGrantResult.failure(RewardGrantStatus.CREDIT_CAP_REACHED, "Maximum stored credits reached");
            }
        }

        // 7. Transition / create pending claim to SERVER_VERIFIED
        if (claimOpt.isPresent()) {
            PendingRewardClaimEntity claim = claimOpt.get();
            claim.setStatus(RewardVerificationStatus.SERVER_VERIFIED.name());
            claim.setTransactionId(txnId);
            claim.setVerifiedAtMs(now);
            claimRepository.save(claim);
        } else {
            // Callback arrived before client
            PendingRewardClaimEntity newClaim = new PendingRewardClaimEntity(
                claimId,
                playerId,
                command.adUnitId() != null ? command.adUnitId() : "",
                command.levelId(),
                "SOLO_HINT",
                RewardVerificationStatus.SERVER_VERIFIED.name(),
                txnId,
                now,
                now,
                null,
                null
            );
            claimRepository.save(newClaim);
        }

        // 8. Update balance with overflow-safe arithmetic
        int currentRewarded = balanceEntity.getRewardedCredits();
        int newRewarded = (currentRewarded > Integer.MAX_VALUE - command.hintsToGrant())
                ? Integer.MAX_VALUE
                : currentRewarded + command.hintsToGrant();
        balanceEntity.setRewardedCredits(newRewarded);

        int currentTotal = balanceEntity.getTotalAvailable();
        int newTotal = (currentTotal > Integer.MAX_VALUE - command.hintsToGrant())
                ? Integer.MAX_VALUE
                : currentTotal + command.hintsToGrant();
        balanceEntity.setTotalAvailable(newTotal);
        balanceEntity.setLastUpdatedMs(now);
        PlayerHintBalanceEntity savedBalance = balanceRepository.save(balanceEntity);

        // 9. Persist reward event
        RewardEventEntity event = new RewardEventEntity(
            claimId,
            playerId,
            txnId,
            RewardType.SOLO_HINT.name(),
            command.hintsToGrant(),
            command.adUnitId(),
            RewardVerificationStatus.SERVER_VERIFIED.name(),
            now,
            null
        );
        eventRepository.save(event);

        // 10. Persist verified SSV transaction record
        AdMobSsvRecordEntity ssvRecord = new AdMobSsvRecordEntity(
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
        ssvRepository.save(ssvRecord);

        // 11. Flush all changes to force database unique constraints evaluation inside transaction
        claimRepository.flush();
        balanceRepository.flush();
        eventRepository.flush();
        ssvRepository.flush();

        return RewardGrantResult.success(toBalanceModel(savedBalance));
    }

    private PlayerHintBalanceEntity lockOrCreateBalance(String playerId, long nowMs) {
        balanceRepository.insertDefaultBalanceIfNotExists(playerId, nowMs);
        return balanceRepository.findByPlayerIdWithLock(playerId)
            .orElseThrow(() -> new IllegalStateException("Failed to acquire locked hint balance for player: " + playerId));
    }

    private PlayerHintCreditBalance toBalanceModel(PlayerHintBalanceEntity entity) {
        return new PlayerHintCreditBalance(
            entity.getPlayerId(),
            entity.getFreeHintsRemaining(),
            entity.getRewardedCredits(),
            entity.getTotalAvailable(),
            entity.getLastUpdatedMs()
        );
    }

    private PendingRewardClaim toClaimModel(PendingRewardClaimEntity entity) {
        return new PendingRewardClaim(
            entity.getClaimId(),
            entity.getPlayerId(),
            entity.getAdUnitId(),
            entity.getLevelId(),
            entity.getRewardType(),
            RewardVerificationStatus.valueOf(entity.getStatus()),
            entity.getTransactionId(),
            entity.getCreatedAtMs(),
            entity.getVerifiedAtMs(),
            entity.getConsumedAtMs(),
            entity.getFailureReason()
        );
    }

    private RewardVerificationRecord toSsvModel(AdMobSsvRecordEntity entity) {
        return new RewardVerificationRecord(
            entity.getTransactionId(),
            entity.getKeyId(),
            entity.getSignature(),
            entity.getCustomData(),
            entity.getUserId(),
            entity.getClaimId(),
            entity.getAdUnitId(),
            entity.getRewardAmount() != null ? entity.getRewardAmount() : 1,
            entity.getRawQueryString(),
            entity.getVerifiedAtMs(),
            entity.isValid(),
            entity.getFailureReason()
        );
    }

    private RewardEvent toEventModel(RewardEventEntity entity) {
        return new RewardEvent(
            entity.getRewardEventId(),
            entity.getPlayerId(),
            entity.getTransactionId(),
            RewardType.valueOf(entity.getRewardType()),
            entity.getAmount(),
            entity.getAdUnitId(),
            RewardVerificationStatus.valueOf(entity.getVerificationStatus()),
            entity.getGrantedAtMs(),
            entity.getConsumedAtMs()
        );
    }
}
