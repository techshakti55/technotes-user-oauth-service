package com.technotes.auth.security;

import com.technotes.auth.entity.UserAccount;
import com.technotes.auth.enums.UserStatus;
import com.technotes.auth.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public AccountUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

        UserAccount account = userAccountRepository
            .findByEmailIgnoreCase(email)
            .orElseThrow(() ->
                new UsernameNotFoundException("User account not found"));

        return User.builder()
            .username(account.getId().toString())
            .password(account.getPasswordHash())
            .authorities(
                account.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role.name()))
                    .toList()
            )
            .disabled(account.getStatus() != UserStatus.ACTIVE)
            .build();
    }
}
