package com.technotes.auth.controller;

import com.technotes.auth.dto.UserMeResponse;
import com.technotes.auth.service.CurrentUserService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CurrentUserService currentUserService;

    public UserController(
        CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public UserMeResponse getCurrentUser(
        JwtAuthenticationToken authentication) {

        return currentUserService.getCurrentUser(
            authentication.getName()
        );
    }
}
