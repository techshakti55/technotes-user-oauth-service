package com.technotes.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.mockito.ArgumentCaptor;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuthClientConfigTest {
    private final RegisteredClientRepository repository = mock(RegisteredClientRepository.class);
    private final OAuthClientConfig config = new OAuthClientConfig();
    private final String production = "https://technotes.co.in/auth/callback";

    private RegisteredClient existing(String uri) {
        return RegisteredClient.withId("persistent-id").clientId("technotes-web")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(uri).scope("openid").scope("notes.read")
            .clientSettings(ClientSettings.builder().requireProofKey(true).build()).build();
    }

    @Test void createsPublicPkceClientWithConfiguredCallback() throws Exception {
        config.registerTechNotesWebClient(repository, production).run();
        ArgumentCaptor<RegisteredClient> captured = ArgumentCaptor.forClass(RegisteredClient.class);
        verify(repository).save(captured.capture());
        RegisteredClient client = captured.getValue();
        assertEquals(Set.of(production), client.getRedirectUris());
        assertEquals(Set.of(ClientAuthenticationMethod.NONE), client.getClientAuthenticationMethods());
        assertTrue(client.getClientSettings().isRequireProofKey());
        assertNull(client.getClientSecret());
    }

    @Test void replacesPersistedCallbackWithoutChangingIdentityOrSettings() throws Exception {
        RegisteredClient old = existing("http://localhost:5173/auth/callback");
        when(repository.findByClientId("technotes-web")).thenReturn(old);
        config.registerTechNotesWebClient(repository, production).run();
        ArgumentCaptor<RegisteredClient> captured = ArgumentCaptor.forClass(RegisteredClient.class);
        verify(repository).save(captured.capture());
        RegisteredClient updated = captured.getValue();
        assertEquals(old.getId(), updated.getId());
        assertEquals(Set.of(production), updated.getRedirectUris());
        assertEquals(old.getScopes(), updated.getScopes());
        assertEquals(old.getClientSettings().getSettings(), updated.getClientSettings().getSettings());
        assertEquals(old.getTokenSettings().getSettings(), updated.getTokenSettings().getSettings());
    }

    @Test void unchangedCallbackDoesNotWrite() throws Exception {
        when(repository.findByClientId("technotes-web")).thenReturn(existing(production));
        config.registerTechNotesWebClient(repository, production).run();
        verify(repository, never()).save(any());
    }

    @Test void rejectsUnsafeCallbacksBeforeTouchingDatabase() {
        for (String uri : new String[]{"http://technotes.co.in/auth/callback", "https://user@technotes.co.in/auth/callback", "https://technotes.co.in/auth/callback?x=1", "https://technotes.co.in/auth/callback#x", "https://technotes.co.in/other"}) {
            assertThrows(IllegalArgumentException.class,
                () -> config.registerTechNotesWebClient(repository, uri).run());
        }
        verifyNoInteractions(repository);
    }

    @Test void retainsLocalDevelopmentCallback() throws Exception {
        config.registerTechNotesWebClient(repository, "http://localhost:5173/auth/callback").run();
        verify(repository).save(any());
    }
}
