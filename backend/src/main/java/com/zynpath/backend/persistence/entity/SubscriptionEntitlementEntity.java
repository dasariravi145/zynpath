package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative JPA entity for verified Google Play subscription entitlements.
 * Maps to table: subscription_entitlements
 */
@Entity
@Table(
        name = "subscription_entitlements",
        indexes = {
                @Index(name = "idx_sub_token_hash", columnList = "purchase_token_hash")
        }
)
public class SubscriptionEntitlementEntity {

    @Id
    @Column(name = "account_id", length = 64, nullable = false)
    private String accountId;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "product_id", length = 64)
    private String productId;

    @Column(name = "base_plan_id", length = 64)
    private String basePlanId;

    @Column(name = "current_period_end_ms", nullable = false)
    private long currentPeriodEndMs;

    @Column(name = "last_verified_at_ms", nullable = false)
    private long lastVerifiedAtMs;

    @Column(name = "is_auto_renewing", nullable = false)
    private boolean isAutoRenewing;

    @Column(name = "purchase_token_hash", length = 128)
    private String purchaseTokenHash;

    public SubscriptionEntitlementEntity() {
    }

    public SubscriptionEntitlementEntity(
            String accountId,
            String status,
            String productId,
            String basePlanId,
            long currentPeriodEndMs,
            long lastVerifiedAtMs,
            boolean isAutoRenewing,
            String purchaseTokenHash
    ) {
        this.accountId = accountId;
        this.status = status;
        this.productId = productId;
        this.basePlanId = basePlanId;
        this.currentPeriodEndMs = currentPeriodEndMs;
        this.lastVerifiedAtMs = lastVerifiedAtMs;
        this.isAutoRenewing = isAutoRenewing;
        this.purchaseTokenHash = purchaseTokenHash;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBasePlanId() {
        return basePlanId;
    }

    public void setBasePlanId(String basePlanId) {
        this.basePlanId = basePlanId;
    }

    public long getCurrentPeriodEndMs() {
        return currentPeriodEndMs;
    }

    public void setCurrentPeriodEndMs(long currentPeriodEndMs) {
        this.currentPeriodEndMs = currentPeriodEndMs;
    }

    public long getLastVerifiedAtMs() {
        return lastVerifiedAtMs;
    }

    public void setLastVerifiedAtMs(long lastVerifiedAtMs) {
        this.lastVerifiedAtMs = lastVerifiedAtMs;
    }

    public boolean isAutoRenewing() {
        return isAutoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        isAutoRenewing = autoRenewing;
    }

    public String getPurchaseTokenHash() {
        return purchaseTokenHash;
    }

    public void setPurchaseTokenHash(String purchaseTokenHash) {
        this.purchaseTokenHash = purchaseTokenHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionEntitlementEntity that = (SubscriptionEntitlementEntity) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}
