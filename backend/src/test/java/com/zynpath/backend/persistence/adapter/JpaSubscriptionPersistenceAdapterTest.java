package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.entity.SubscriptionEntitlementEntity;
import com.zynpath.backend.persistence.repository.SubscriptionEntitlementRepository;
import com.zynpath.backend.subscription.model.EntitlementStatus;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.model.SubscriptionTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaSubscriptionPersistenceAdapterTest {

    private SubscriptionEntitlementRepository repository;
    private JpaSubscriptionPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SubscriptionEntitlementRepository.class);
        adapter = new JpaSubscriptionPersistenceAdapter(repository);
    }

    @Test
    void testGetEntitlement_activeMonthly() {
        SubscriptionEntitlementEntity entity = new SubscriptionEntitlementEntity(
                "p_sub_1", "ACTIVE", "zynpath_premium", "premium-monthly",
                System.currentTimeMillis() + 86400000L, System.currentTimeMillis(), true, "hash_123"
        );
        when(repository.findById("p_sub_1")).thenReturn(Optional.of(entity));

        SubscriptionEntitlement result = adapter.getEntitlement("p_sub_1");

        assertNotNull(result);
        assertEquals(SubscriptionTier.PREMIUM_MONTHLY, result.tier());
        assertEquals(EntitlementStatus.ACTIVE, result.status());
        assertTrue(result.isActive());
    }

    @Test
    void testGetAccountByTokenHash() {
        SubscriptionEntitlementEntity entity = new SubscriptionEntitlementEntity(
                "p_sub_2", "ACTIVE", "zynpath_premium", "premium-six-months",
                System.currentTimeMillis() + 86400000L, System.currentTimeMillis(), true, "token_hash_abc"
        );
        when(repository.findByPurchaseTokenHash("token_hash_abc")).thenReturn(Optional.of(entity));

        Optional<String> account = adapter.getAccountByTokenHash("token_hash_abc");

        assertTrue(account.isPresent());
        assertEquals("p_sub_2", account.get());
    }

    @Test
    void testSaveEntitlement() {
        SubscriptionEntitlement entitlement = new SubscriptionEntitlement(
                "p_sub_3", SubscriptionTier.PREMIUM_MONTHLY, EntitlementStatus.ACTIVE,
                "zynpath_premium", "premium-monthly", 200000L, 100000L, "hash_xyz", "GOOGLE_PLAY", true
        );

        adapter.saveEntitlement(entitlement);

        verify(repository).save(any(SubscriptionEntitlementEntity.class));
    }
}
