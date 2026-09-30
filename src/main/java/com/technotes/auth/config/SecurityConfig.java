package com.technotes.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(
        HttpSecurity http) throws Exception {

        http
            .securityMatcher("/api/**")

            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .csrf(csrf -> csrf.disable())

            .cors(Customizer.withDefaults())

            .oauth2ResourceServer(resourceServer ->
                resourceServer.jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain browserSecurityFilterChain(
        HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            )

            .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
