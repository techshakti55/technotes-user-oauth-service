package com.technotes.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;

@Configuration
public class OAuthClientConfig {

    @Bean
    CommandLineRunner registerTechNotesWebClient(
        RegisteredClientRepository registeredClientRepository) {

        return args -> {

            if (registeredClientRepository
                .findByClientId("technotes-web") != null) {
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
                    .redirectUri(
                        "http://localhost:5173/auth/callback"
                    )

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
}
