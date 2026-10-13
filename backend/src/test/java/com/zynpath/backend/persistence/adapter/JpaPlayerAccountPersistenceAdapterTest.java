package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.ExternalIdentity;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.persistence.entity.ExternalIdentityEntity;
import com.zynpath.backend.persistence.entity.PlayerAccountEntity;
import com.zynpath.backend.persistence.repository.ExternalIdentityRepository;
import com.zynpath.backend.persistence.repository.PlayerAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaPlayerAccountPersistenceAdapterTest {

    private PlayerAccountRepository accountRepository;
    private ExternalIdentityRepository identityRepository;
    private JpaPlayerAccountPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        accountRepository = mock(PlayerAccountRepository.class);
        identityRepository = mock(ExternalIdentityRepository.class);
        adapter = new JpaPlayerAccountPersistenceAdapter(accountRepository, identityRepository);
    }

    @Test
    void testFindById_foundActive() {
        PlayerAccountEntity entity = new PlayerAccountEntity(
                "p_test", "ZYN-1234-5678", "Alice", "REGISTERED", 1000L, 2000L, false
        );
        when(accountRepository.findById("p_test")).thenReturn(Optional.of(entity));

        Optional<PlayerAccount> result = adapter.findById("p_test");

        assertTrue(result.isPresent());
        assertEquals("p_test", result.get().playerId());
        assertEquals("ZYN-1234-5678", result.get().publicZynpathId());
    }

    @Test
    void testFindById_deleted_returnsEmpty() {
        PlayerAccountEntity entity = new PlayerAccountEntity(
                "p_test", "ZYN-1234-5678", "Alice", "REGISTERED", 1000L, 2000L, true
        );
        when(accountRepository.findById("p_test")).thenReturn(Optional.of(entity));

        Optional<PlayerAccount> result = adapter.findById("p_test");

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveAccount() {
        PlayerAccount account = new PlayerAccount(
                "p_save", "ZYN-ABCD-EFGH", "Bob", "GUEST", 5000L, 5000L
        );

        adapter.saveAccount(account);

        verify(accountRepository).save(any(PlayerAccountEntity.class));
    }

    @Test
    void testGetLinkedProviders_filtersOutGuest() {
        ExternalIdentityEntity google = new ExternalIdentityEntity("GOOGLE", "g_123", "p_1", 100L);
        ExternalIdentityEntity guest = new ExternalIdentityEntity("GUEST", "guest_uuid", "p_1", 50L);
        when(identityRepository.findByPlayerId("p_1")).thenReturn(List.of(google, guest));

        List<AuthProvider> providers = adapter.getLinkedProviders("p_1");

        assertEquals(1, providers.size());
        assertEquals(AuthProvider.GOOGLE, providers.get(0));
    }

    @Test
    void testFindGuestPlayerId() {
        ExternalIdentityEntity guest = new ExternalIdentityEntity("GUEST", "uuid_abc", "p_guest_target", 100L);
        when(identityRepository.findByProviderAndProviderSubjectId("GUEST", "uuid_abc")).thenReturn(Optional.of(guest));

        Optional<String> result = adapter.findGuestPlayerId("uuid_abc");

        assertTrue(result.isPresent());
        assertEquals("p_guest_target", result.get());
    }
}
