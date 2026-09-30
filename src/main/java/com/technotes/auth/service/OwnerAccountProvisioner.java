package com.technotes.auth.service;

import com.technotes.auth.entity.UserAccount;
import com.technotes.auth.enums.Role;
import com.technotes.auth.enums.UserStatus;
import com.technotes.auth.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OwnerAccountProvisioner implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${OWNER_EMAIL:}")
    private String ownerEmail;

    @Value("${OWNER_DISPLAY_NAME:}")
    private String ownerDisplayName;

    @Value("${OWNER_PASSWORD:}")
    private String ownerPassword;

    public OwnerAccountProvisioner(
        UserAccountRepository userAccountRepository,
        PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (ownerEmail.isBlank()
            || ownerDisplayName.isBlank()
            || ownerPassword.isBlank()) {
            return;
        }

        if (userAccountRepository.findByEmailIgnoreCase(ownerEmail).isPresent()) {
            return;
        }

        UserAccount owner = new UserAccount();
        owner.setEmail(ownerEmail.trim().toLowerCase());
        owner.setDisplayName(ownerDisplayName.trim());
        owner.setPasswordHash(passwordEncoder.encode(ownerPassword));
        owner.setStatus(UserStatus.ACTIVE);
        owner.setRoles(Set.of(Role.ADMIN));

        userAccountRepository.save(owner);
    }
}
