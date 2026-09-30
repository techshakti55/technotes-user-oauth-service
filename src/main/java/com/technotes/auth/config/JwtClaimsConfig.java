package com.technotes.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;

@Configuration
public class JwtClaimsConfig {

    private static final String API_AUDIENCE = "technotes-api";
    private static final String ROLE_PREFIX = "ROLE_";

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {

        return context -> {

            if (!OAuth2TokenType.ACCESS_TOKEN.equals(
                context.getTokenType())) {
                return;
            }

            List<String> roles = context.getPrincipal()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                    authority.startsWith(ROLE_PREFIX))
                .map(authority ->
                    authority.substring(ROLE_PREFIX.length()))
                .distinct()
                .toList();

            context.getClaims()
                .subject(context.getPrincipal().getName())
                .audience(List.of(API_AUDIENCE))
                .claim("roles", roles);
        };
    }
}
