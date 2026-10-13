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

    @Test
    void testBindGuestUuid_newMapping_insertsSuccessfully() {
        when(identityRepository.findByProviderAndProviderSubjectId("GUEST", "new_guest_uuid"))
                .thenReturn(Optional.empty());
        when(identityRepository.insertGuestIdentityIfNotExists(org.mockito.ArgumentMatchers.eq("new_guest_uuid"), org.mockito.ArgumentMatchers.eq("p_new"), org.mockito.ArgumentMatchers.anyLong()))
                .thenReturn(1);

        adapter.bindGuestUuid("new_guest_uuid", "p_new");

        verify(identityRepository).insertGuestIdentityIfNotExists(org.mockito.ArgumentMatchers.eq("new_guest_uuid"), org.mockito.ArgumentMatchers.eq("p_new"), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void testBindGuestUuid_sameOwner_isIdempotent() {
        ExternalIdentityEntity existing = new ExternalIdentityEntity("GUEST", "existing_uuid", "p_same", 100L);
        when(identityRepository.findByProviderAndProviderSubjectId("GUEST", "existing_uuid"))
                .thenReturn(Optional.of(existing));

        adapter.bindGuestUuid("existing_uuid", "p_same");

        verify(identityRepository, org.mockito.Mockito.never()).insertGuestIdentityIfNotExists(any(), any(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void testBindGuestUuid_differentOwner_throwsConflict() {
        ExternalIdentityEntity existing = new ExternalIdentityEntity("GUEST", "victim_uuid", "p_victim", 100L);
        when(identityRepository.findByProviderAndProviderSubjectId("GUEST", "victim_uuid"))
                .thenReturn(Optional.of(existing));

        com.zynpath.backend.auth.model.AuthException ex = org.junit.jupiter.api.Assertions.assertThrows(
                com.zynpath.backend.auth.model.AuthException.class,
                () -> adapter.bindGuestUuid("victim_uuid", "p_attacker")
        );

        assertEquals("ACCOUNT_LINK_CONFLICT", ex.getErrorCode());
        assertEquals(org.springframework.http.HttpStatus.CONFLICT, ex.getHttpStatus());
        verify(identityRepository, org.mockito.Mockito.never()).insertGuestIdentityIfNotExists(any(), any(), org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void testBindGuestUuid_concurrentConflict_throwsConflict() {
        when(identityRepository.findByProviderAndProviderSubjectId("GUEST", "race_uuid"))
                .thenReturn(Optional.empty()) // Initial check
                .thenReturn(Optional.of(new ExternalIdentityEntity("GUEST", "race_uuid", "p_race_winner", 100L))); // Post-conflict check

        when(identityRepository.insertGuestIdentityIfNotExists(org.mockito.ArgumentMatchers.eq("race_uuid"), org.mockito.ArgumentMatchers.eq("p_race_loser"), org.mockito.ArgumentMatchers.anyLong()))
                .thenReturn(0); // Insert failed due to race

        com.zynpath.backend.auth.model.AuthException ex = org.junit.jupiter.api.Assertions.assertThrows(
                com.zynpath.backend.auth.model.AuthException.class,
                () -> adapter.bindGuestUuid("race_uuid", "p_race_loser")
        );

        assertEquals("ACCOUNT_LINK_CONFLICT", ex.getErrorCode());
    }

    @Test
    void testBindGuestUuid_concurrentSameOwner_succeedsIdempotently() {
        when(identityRepository.findByProviderAndProviderSubjectId("GUEST", "race_uuid"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new ExternalIdentityEntity("GUEST", "race_uuid", "p_same", 100L)));

        when(identityRepository.insertGuestIdentityIfNotExists(org.mockito.ArgumentMatchers.eq("race_uuid"), org.mockito.ArgumentMatchers.eq("p_same"), org.mockito.ArgumentMatchers.anyLong()))
                .thenReturn(0);

        adapter.bindGuestUuid("race_uuid", "p_same");
    }
}
