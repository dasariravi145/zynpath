package com.zynpath.backend.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for external identities: (provider, providerSubjectId).
 */
public class ExternalIdentityId implements Serializable {

    private String provider;
    private String providerSubjectId;

    public ExternalIdentityId() {
    }

    public ExternalIdentityId(String provider, String providerSubjectId) {
        this.provider = provider;
        this.providerSubjectId = providerSubjectId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalIdentityId that = (ExternalIdentityId) o;
        return Objects.equals(provider, that.provider) && Objects.equals(providerSubjectId, that.providerSubjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, providerSubjectId);
    }
}
