package com.technotes.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.technotes.auth.security.SigningKeyProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwkConfig {

    @Bean
    public JWKSource<SecurityContext> jwkSource(
        SigningKeyProvider signingKeyProvider) {

        RSAKey rsaKey = signingKeyProvider.loadSigningKey();
        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, securityContext) ->
            jwkSelector.select(jwkSet);
    }
}
