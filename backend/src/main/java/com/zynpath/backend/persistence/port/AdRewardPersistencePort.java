package com.zynpath.backend.persistence.port;

import com.zynpath.backend.ads.model.PendingRewardClaim;
import com.zynpath.backend.ads.model.PlayerHintCreditBalance;
import com.zynpath.backend.ads.model.RewardEvent;
import com.zynpath.backend.ads.model.RewardGrantCommand;
import com.zynpath.backend.ads.model.RewardGrantResult;
import com.zynpath.backend.ads.model.RewardVerificationRecord;

import java.util.Optional;

/**
 * Port contract for persisting rewarded ad hint balances, pending claims, SSV records, and reward events.
 */
public interface AdRewardPersistencePort {

    Optional<PlayerHintCreditBalance> getBalance(String playerId);

    PlayerHintCreditBalance saveBalance(PlayerHintCreditBalance balance);

    Optional<PendingRewardClaim> findPendingClaim(String claimId);

    PendingRewardClaim savePendingClaim(PendingRewardClaim claim);

    Optional<PendingRewardClaim> findClaimByTransactionId(String transactionId);

    Optional<RewardVerificationRecord> findSsvRecord(String transactionId);

    RewardVerificationRecord saveSsvRecord(RewardVerificationRecord record);

    Optional<RewardEvent> findRewardEvent(String rewardEventId);

    RewardEvent saveRewardEvent(RewardEvent event);

    int countDailyRewards(String playerId, long sinceTimestampMs);

    RewardGrantResult grantRewardAtomic(RewardGrantCommand command);
}
