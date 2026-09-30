package com.technotes.auth.dto;

import com.technotes.auth.enums.Role;
import com.technotes.auth.enums.UserStatus;

import java.util.Set;
import java.util.UUID;

public record UserMeResponse(
    UUID id,
    String displayName,
    String email,
    Set<Role> roles,
    UserStatus status
) {
}
