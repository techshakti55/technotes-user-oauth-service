package com.technotes.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.net.URI;
import java.util.UUID;

@Configuration
public class OAuthClientConfig {

    @Bean
    CommandLineRunner registerTechNotesWebClient(
        RegisteredClientRepository registeredClientRepository,
        @Value("${OAUTH_WEB_REDIRECT_URI:http://localhost:5173/auth/callback}")
        String redirectUri) {

        return args -> {

            validateRedirectUri(redirectUri);

            RegisteredClient existing = registeredClientRepository
                .findByClientId("technotes-web");
            if (existing != null) {
                if (!existing.getRedirectUris().equals(java.util.Set.of(redirectUri))) {
                    // Preserve client identity, scopes, grants and settings. Remove
                    // stale callbacks so production does not retain localhost.
                    registeredClientRepository.save(
                        RegisteredClient.from(existing)
                            .redirectUris(uris -> {
                                uris.clear();
                                uris.add(redirectUri);
                            })
                            .build()
                    );
                }
                return;
            }

            RegisteredClient registeredClient =
                RegisteredClient.withId(UUID.randomUUID().toString())

                    .clientId("technotes-web")

                    // Public client: no client secret
                    .clientAuthenticationMethod(
                        ClientAuthenticationMethod.NONE
                    )

                    // Authorization Code only
                    .authorizationGrantType(
                        AuthorizationGrantType.AUTHORIZATION_CODE
                    )

                    // Exact React callback
                    .redirectUri(redirectUri)

                    .scope("openid")
                    .scope("profile")
                    .scope("notes.read")
                    .scope("notes.write")
                    .scope("notes.review")
                    .scope("taxonomy.write")
                    .scope("profile.read")

                    // Public client must use PKCE
                    .clientSettings(
                        ClientSettings.builder()
                            .requireProofKey(true)
                            .build()
                    )

                    .build();

            registeredClientRepository.save(registeredClient);
        };
    }
    private static void validateRedirectUri(String value) {
        URI uri = URI.create(value);
        boolean localHttp = "http".equals(uri.getScheme())
            && ("localhost".equals(uri.getHost()) || "127.0.0.1".equals(uri.getHost())
                || "[::1]".equals(uri.getHost()));
        if ((!"https".equals(uri.getScheme()) && !localHttp)
            || uri.getHost() == null || uri.getUserInfo() != null
            || uri.getRawQuery() != null || uri.getRawFragment() != null
            || !"/auth/callback".equals(uri.getPath())) {
            throw new IllegalArgumentException(
                "OAUTH_WEB_REDIRECT_URI must be an exact HTTPS /auth/callback URL (HTTP allowed only on loopback)."
            );
        }
    }
}
