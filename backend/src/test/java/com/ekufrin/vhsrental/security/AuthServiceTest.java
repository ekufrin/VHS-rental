package com.ekufrin.vhsrental.security;

import com.ekufrin.vhsrental.security.dto.AuthResponseDto;
import com.ekufrin.vhsrental.security.dto.UserLoginRequest;
import com.ekufrin.vhsrental.user.User;
import com.ekufrin.vhsrental.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_ValidCredentials_ReturnsAuthResponse() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .password("encoded")
                .name("Test User")
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtUtil.generateAccessToken(user.getId())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("refresh-token");
        when(refreshTokenRepository.save(any())).thenReturn(RefreshToken.builder().user(user).jti("jti").expiryDate(Instant.now()).revoked(false).build());

        AuthResponseDto response = authService.authenticate(new UserLoginRequest("user@test.com", "password"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void authenticate_InvalidEmail_ThrowsAuthenticationServiceException() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(new UserLoginRequest("missing@test.com", "pw")))
                .isInstanceOf(AuthenticationServiceException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewTokens() {
        User user = User.builder().id(UUID.randomUUID()).email("user@test.com").build();
        RefreshToken refreshToken = RefreshToken.builder().jti("old-jti").user(user).revoked(false).expiryDate(Instant.now().plusSeconds(3600)).build();

        when(jwtUtil.getJtiFromToken("refresh-token")).thenReturn("old-jti");
        when(refreshTokenRepository.findUserByJti("old-jti")).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByJti("old-jti")).thenReturn(refreshToken);
        when(jwtUtil.generateAccessToken(user.getId())).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("new-refresh");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponseDto response = authService.refreshToken("refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void generateAccessFromRefresh_InvalidToken_ThrowsException() {
        when(jwtUtil.validateJwtToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.generateAccessFromRefresh("bad-token"))
                .isInstanceOf(AuthenticationServiceException.class)
                .hasMessageContaining("Invalid or expired refresh token");
    }

    @Test
    void logout_InvalidToken_ThrowsException() {
        when(jwtUtil.getJtiFromToken("bad-token")).thenReturn("missing-jti");
        when(refreshTokenRepository.findUserByJti("missing-jti")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout("bad-token"))
                .isInstanceOf(AuthenticationServiceException.class)
                .hasMessageContaining("Invalid refresh token");
    }
}
