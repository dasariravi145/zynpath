package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Authoritative JPA entity for external OAuth provider identity mappings (e.g. GOOGLE, FACEBOOK).
 * Maps to table: external_identities
 */
@Entity
@Table(
        name = "external_identities",
        indexes = {
                @Index(name = "idx_ext_identity_player", columnList = "player_id")
        }
)
@IdClass(ExternalIdentityId.class)
public class ExternalIdentityEntity {

    @Id
    @Column(name = "provider", length = 32, nullable = false)
    private String provider;

    @Id
    @Column(name = "provider_subject_id", length = 128, nullable = false)
    private String providerSubjectId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "linked_at", nullable = false)
    private long linkedAt;

    public ExternalIdentityEntity() {
    }

    public ExternalIdentityEntity(String provider, String providerSubjectId, String playerId, long linkedAt) {
        this.provider = provider;
        this.providerSubjectId = providerSubjectId;
        this.playerId = playerId;
        this.linkedAt = linkedAt;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderSubjectId() {
        return providerSubjectId;
    }

    public void setProviderSubjectId(String providerSubjectId) {
        this.providerSubjectId = providerSubjectId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(long linkedAt) {
        this.linkedAt = linkedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalIdentityEntity that = (ExternalIdentityEntity) o;
        return Objects.equals(provider, that.provider) && Objects.equals(providerSubjectId, that.providerSubjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, providerSubjectId);
    }
}
