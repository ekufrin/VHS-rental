package com.ekufrin.vhsrental.security;

import com.ekufrin.vhsrental.security.dto.AccessTokenResponse;
import com.ekufrin.vhsrental.security.dto.AuthResponseDto;
import com.ekufrin.vhsrental.security.dto.UserLoginRequest;
import com.ekufrin.vhsrental.security.dto.UserRegisterRequest;
import com.ekufrin.vhsrental.user.User;
import com.ekufrin.vhsrental.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    public static final String BEARER = "Bearer";
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    public AuthResponseDto authenticate(UserLoginRequest userLoginRequest) throws AuthenticationServiceException {
        User user = userRepository.findByEmail(userLoginRequest.email()).orElseThrow(
                () -> new AuthenticationServiceException("Invalid email or password")
        );
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getId(), userLoginRequest.password()));

        String jti = UUID.randomUUID().toString();
        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), jti);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .expiryDate(Instant.now().plusMillis(JWTUtil.REFRESH_TOKEN_EXPIRATION_MS))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                BEARER
        );
    }

    @Transactional
    public AuthResponseDto register(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmail(userRegisterRequest.email())) {
            throw new AuthenticationServiceException("Email is already in use");
        }

        User newUser = User.builder()
                .name(userRegisterRequest.name())
                .email(userRegisterRequest.email())
                .password(passwordEncoder.encode(userRegisterRequest.password()))
                .build();
        userRepository.save(newUser);

        return authenticate(
                new UserLoginRequest(
                        userRegisterRequest.email(),
                        userRegisterRequest.password()
                )
        );
    }

    public AccessTokenResponse generateAccessFromRefresh(String request) {
        if (request == null || request.isEmpty()) {
            throw new AuthenticationServiceException("Refresh token is missing");
        }

        if (!jwtUtil.validateJwtToken(request)) {
            throw new AuthenticationServiceException("Invalid or expired refresh token");
        }
        if (!jwtUtil.isRefreshToken(request)) {
            throw new AuthenticationServiceException("Token is not a refresh token");
        }

        String jti = jwtUtil.getJtiFromToken(request);

        User user = refreshTokenRepository.findUserByJti(jti).orElseThrow(
                () -> new AuthenticationServiceException("Invalid or revoked refresh token")
        );

        String accessToken = jwtUtil.generateAccessToken(user.getId());
        return new AccessTokenResponse(accessToken, BEARER);
    }

    @Transactional
    public AuthResponseDto refreshToken(String request) {
        User user = revokeRefreshTokenAndGetUser(request);

        String newJti = UUID.randomUUID().toString();
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .revoked(false)
                .expiryDate(Instant.now().plusMillis(JWTUtil.REFRESH_TOKEN_EXPIRATION_MS))
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        String newAccessToken = jwtUtil.generateAccessToken(user.getId());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), newJti);

        return new AuthResponseDto(
                newAccessToken,
                newRefreshToken,
                BEARER
        );
    }

    @Transactional
    public void logout(String request) {
        revokeRefreshTokenAndGetUser(request);
    }

    private User revokeRefreshTokenAndGetUser(String request) {
        if (request == null || request.isEmpty()) {
            throw new AuthenticationServiceException("Refresh token is missing");
        }
        String jti = jwtUtil.getJtiFromToken(request);
        User user = refreshTokenRepository.findUserByJti(jti).orElseThrow(
                () -> new AuthenticationServiceException("Invalid refresh token")
        );
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return user;
    }
}
