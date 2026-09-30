package com.technotes.auth.service;

import com.technotes.auth.dto.UserMeResponse;
import com.technotes.auth.entity.UserAccount;
import com.technotes.auth.enums.UserStatus;
import com.technotes.auth.exception.UserDeactivatedException;
import com.technotes.auth.exception.UserNotFoundException;
import com.technotes.auth.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class CurrentUserService {

    private final UserAccountRepository userAccountRepository;

    public CurrentUserService(
        UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public UserMeResponse getCurrentUser(String subject) {

        UUID userId;

        try {
            userId = UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new UserNotFoundException();
        }

        UserAccount userAccount = userAccountRepository
            .findById(userId)
            .orElseThrow(UserNotFoundException::new);

        if (userAccount.getStatus() != UserStatus.ACTIVE) {
            throw new UserDeactivatedException();
        }

        return new UserMeResponse(
            userAccount.getId(),
            userAccount.getDisplayName(),
            userAccount.getEmail(),
            Set.copyOf(userAccount.getRoles()),
            userAccount.getStatus()
        );
    }
}
