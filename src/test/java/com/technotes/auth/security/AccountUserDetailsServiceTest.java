package com.technotes.auth.security;

import com.technotes.auth.entity.UserAccount;
import com.technotes.auth.enums.Role;
import com.technotes.auth.enums.UserStatus;
import com.technotes.auth.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountUserDetailsServiceTest {

    private UserAccountRepository userAccountRepository;
    private PasswordEncoder passwordEncoder;
    private DaoAuthenticationProvider authenticationProvider;

    private static final String EMAIL = "techshakti55@gmail.com";
    private static final String PASSWORD = "test-password";
    private static final UUID USER_ID =
        UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {

        userAccountRepository = mock(UserAccountRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();

        AccountUserDetailsService userDetailsService =
            new AccountUserDetailsService(userAccountRepository);

        authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void shouldAuthenticateActiveUserWithValidPassword() {

        when(userAccountRepository.findByEmailIgnoreCase(EMAIL))
            .thenReturn(Optional.of(createAccount(UserStatus.ACTIVE)));

        Authentication authentication = authenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                EMAIL,
                PASSWORD
            )
        );

        assertTrue(authentication.isAuthenticated());
        assertEquals(USER_ID.toString(), authentication.getName());
    }

    @Test
    void shouldRejectInvalidPassword() {

        when(userAccountRepository.findByEmailIgnoreCase(EMAIL))
            .thenReturn(Optional.of(createAccount(UserStatus.ACTIVE)));

        assertThrows(
            AuthenticationException.class,
            () -> authenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                    EMAIL,
                    "wrong-password"
                )
            )
        );
    }

    @Test
    void shouldRejectInactiveUser() {

        when(userAccountRepository.findByEmailIgnoreCase(EMAIL))
            .thenReturn(Optional.of(createAccount(UserStatus.INACTIVE)));

        assertThrows(
            DisabledException.class,
            () -> authenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                    EMAIL,
                    PASSWORD
                )
            )
        );
    }

    private UserAccount createAccount(UserStatus status) {

        UserAccount account = new UserAccount();

        account.setId(USER_ID);
        account.setEmail(EMAIL);
        account.setDisplayName("Test Owner");
        account.setPasswordHash(passwordEncoder.encode(PASSWORD));
        account.setStatus(status);
        account.setRoles(Set.of(Role.ADMIN));

        return account;
    }
}
