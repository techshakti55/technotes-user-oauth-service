package com.technotes.auth.controller;

import com.technotes.auth.exception.ApiExceptionHandler;
import com.technotes.auth.exception.UserDeactivatedException;
import com.technotes.auth.exception.UserNotFoundException;
import com.technotes.auth.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ApiExceptionHandler.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Test
    void shouldReturn404WhenAuthenticatedUserDoesNotExist() throws Exception {

        String missingUserId = UUID.randomUUID().toString();

        when(currentUserService.getCurrentUser(missingUserId))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(
                        get("/api/v1/users/me")
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject(missingUserId)))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn403WhenUserIsDeactivated() throws Exception {

        String deactivatedUserId = UUID.randomUUID().toString();

        when(currentUserService.getCurrentUser(deactivatedUserId))
                .thenThrow(new UserDeactivatedException());

        mockMvc.perform(
                        get("/api/v1/users/me")
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject(deactivatedUserId)))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("USER_DEACTIVATED"))
                .andExpect(jsonPath("$.status").value(403));
    }
}