package com.technotes.auth.controller;

import com.technotes.auth.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "apiJwtDecoder")
    private JwtDecoder apiJwtDecoder;

    @Test
    void anonymousLoginPageContainsFormAndCsrfToken() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Welcome back.")))
                .andExpect(content().string(containsString("name=\"username\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void loginStylesAreAvailableWithoutSigningIn() throws Exception {
        mockMvc.perform(get("/login-assets/login.css"))
                .andExpect(status().isOk());
    }

    @Test
    void failedLoginShowsHelpfulMessage() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("We could not sign you in.")));
    }

    @Test
    void submittingWithoutCsrfIsRejected() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "someone")
                        .param("password", "invalid"))
                .andExpect(status().isForbidden());
    }

    @Test
    void otherBrowserRoutesStillRequireAuthentication() throws Exception {
        mockMvc.perform(get("/private"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
