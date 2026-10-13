package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(
    name = "admob_ssv_records",
    indexes = {
        @Index(name = "idx_ssv_user", columnList = "user_id"),
        @Index(name = "idx_ssv_claim_id", columnList = "claim_id")
    }
)
public class AdMobSsvRecordEntity {

    @Id
    @Column(name = "transaction_id", length = 128, nullable = false)
    private String transactionId;

    @Column(name = "key_id", length = 64)
    private String keyId;

    @Column(name = "signature", columnDefinition = "TEXT")
    private String signature;

    @Column(name = "custom_data", columnDefinition = "TEXT")
    private String customData;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "claim_id", length = 128)
    private String claimId;

    @Column(name = "ad_unit_id", length = 128)
    private String adUnitId;

    @Column(name = "reward_amount")
    private Integer rewardAmount;

    @Column(name = "raw_query_string", columnDefinition = "TEXT")
    private String rawQueryString;

    @Column(name = "verified_at_ms", nullable = false)
    private long verifiedAtMs;

    @Column(name = "is_valid", nullable = false)
    private boolean isValid;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    public AdMobSsvRecordEntity() {}

    public AdMobSsvRecordEntity(
        String transactionId,
        String keyId,
        String signature,
        String customData,
        String userId,
        String claimId,
        String adUnitId,
        Integer rewardAmount,
        String rawQueryString,
        long verifiedAtMs,
        boolean isValid,
        String failureReason
    ) {
        this.transactionId = transactionId;
        this.keyId = keyId;
        this.signature = signature;
        this.customData = customData;
        this.userId = userId;
        this.claimId = claimId;
        this.adUnitId = adUnitId;
        this.rewardAmount = rewardAmount;
        this.rawQueryString = rawQueryString;
        this.verifiedAtMs = verifiedAtMs;
        this.isValid = isValid;
        this.failureReason = failureReason;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getCustomData() { return customData; }
    public void setCustomData(String customData) { this.customData = customData; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getClaimId() { return claimId; }
    public void setClaimId(String claimId) { this.claimId = claimId; }

    public String getAdUnitId() { return adUnitId; }
    public void setAdUnitId(String adUnitId) { this.adUnitId = adUnitId; }

    public Integer getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(Integer rewardAmount) { this.rewardAmount = rewardAmount; }

    public String getRawQueryString() { return rawQueryString; }
    public void setRawQueryString(String rawQueryString) { this.rawQueryString = rawQueryString; }

    public long getVerifiedAtMs() { return verifiedAtMs; }
    public void setVerifiedAtMs(long verifiedAtMs) { this.verifiedAtMs = verifiedAtMs; }

    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdMobSsvRecordEntity that = (AdMobSsvRecordEntity) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
