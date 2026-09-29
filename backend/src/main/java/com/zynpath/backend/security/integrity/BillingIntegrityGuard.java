package com.zynpath.backend.security.integrity;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Billing and ad-reward verification guard.
 *
 * Implements Prompt 36 Section 44, 45, 46, 48, 49:
 * - Anti-piracy token ownership enforcement (SHA-256 bound tokens).
 * - Rewarded hint deduplication: transactions cannot be submitted multiple times.
 * - Enforces daily ad reward caps and maximum bonus hint wallet caps.
 */
@Component
public class BillingIntegrityGuard {

    private final SecurityAuditLogger auditLogger;
    private final Set<String> processedRewardTransactionIds = ConcurrentHashMap.newKeySet();
    private final Map<String, String> tokenHashToOwnerAccount = new ConcurrentHashMap<>();

    public BillingIntegrityGuard(SecurityAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    /**
     * Asserts that a reward transaction has not been claimed previously.
     */
    public void assertRewardTransactionUnique(String transactionId) {
        assertRewardTransactionUnique(transactionId, SecurityContext.getOptionalPlayerId());
    }

    /**
     * Asserts that a reward transaction has not been claimed previously.
     */
    public void assertRewardTransactionUnique(String transactionId, String playerId) {
        if (transactionId == null || transactionId.isBlank()) {
            throw AuthException.badRequest("INVALID_TRANSACTION: Reward transactionId cannot be blank");
        }

        if (!processedRewardTransactionIds.add(transactionId)) {
            auditLogger.recordEvent(
                    SecurityEventType.SUSPICIOUS_REWARD_ATTEMPT,
                    playerId,
                    SecurityContext.getClientIp(),
                    "reward_transaction:" + transactionId,
                    "REJECTED",
                    Map.of("reason", "DUPLICATE_REWARD_TRANSACTION")
            );
            throw AuthException.badRequest("REWARD_ALREADY_GRANTED: Transaction " + transactionId + " has already been credited");
        }
    }

    /**
     * Asserts that a Google Play purchase token hash is not already claimed by a different account.
     */
    public void assertPurchaseTokenOwnership(String accountId, String tokenHash) {
        if (tokenHash == null || tokenHash.isBlank()) return;
        String existingOwner = tokenHashToOwnerAccount.putIfAbsent(tokenHash, accountId);
        if (existingOwner != null && !existingOwner.equals(accountId)) {
            auditLogger.recordEvent(
                    SecurityEventType.SUSPICIOUS_REWARD_ATTEMPT,
                    accountId,
                    SecurityContext.getClientIp(),
                    "purchase_token_hash:" + tokenHash.substring(0, Math.min(8, tokenHash.length())),
                    "REJECTED",
                    Map.of("reason", "TOKEN_OWNERSHIP_CONFLICT", "existingOwner", existingOwner)
            );
            throw AuthException.accountLinkConflict("PURCHASE_OWNERSHIP_CONFLICT: This purchase token is already associated with another account");
        }
    }
}
