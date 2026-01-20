package com.ekufrin.vhsrental.security;

import com.ekufrin.vhsrental.security.dto.AuthResponseDto;
import com.ekufrin.vhsrental.security.dto.UserLoginRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("login with valid credentials returns 200 OK with tokens")
    void login_ValidCredentials_Returns200() throws Exception {
        UserLoginRequest request = new UserLoginRequest("user@example.com", "password123");
        AuthResponseDto response = new AuthResponseDto("access-token", "refresh-token", "Bearer");
        given(authService.authenticate(any(UserLoginRequest.class))).willReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));

        then(authService).should().authenticate(any(UserLoginRequest.class));
    }

    @Test
    @DisplayName("login with invalid credentials returns 401 Unauthorized")
    void login_InvalidCredentials_Returns401() throws Exception {
        UserLoginRequest request = new UserLoginRequest("user@example.com", "wrong");
        given(authService.authenticate(any(UserLoginRequest.class)))
                .willThrow(new AuthenticationServiceException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication error"));
    }

    @Test
    @DisplayName("refresh token with valid cookie returns 200 OK with new tokens")
    void refreshToken_ValidCookie_Returns200() throws Exception {
        AuthResponseDto response = new AuthResponseDto("new-access", "new-refresh", "Bearer");
        given(authService.refreshToken("valid-refresh"))
                .willReturn(response);

        mockMvc.perform(post("/auth/refresh-token")
                        .cookie(new Cookie("refresh_token", "valid-refresh")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("new-access"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("access-token with invalid refresh cookie returns 401")
    void accessToken_InvalidCookie_Returns401() throws Exception {
        given(authService.generateAccessFromRefresh("bad"))
                .willThrow(new AuthenticationServiceException("Invalid refresh token"));

        mockMvc.perform(post("/auth/access-token")
                        .cookie(new Cookie("refresh_token", "bad")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication error"));
    }

    @Test
    @DisplayName("logout clears refresh cookie and returns 200")
    void logout_ClearsCookie_Returns200() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .cookie(new Cookie("refresh_token", "valid")))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refresh_token", 0));

        then(authService).should().logout("valid");
    }
}
