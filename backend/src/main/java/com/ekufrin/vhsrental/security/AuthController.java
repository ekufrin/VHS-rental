package com.ekufrin.vhsrental.security;

import com.ekufrin.vhsrental.config.ApiResponse;
import com.ekufrin.vhsrental.config.ApiResponseFactory;
import com.ekufrin.vhsrental.security.dto.AccessTokenResponse;
import com.ekufrin.vhsrental.security.dto.AuthResponseDto;
import com.ekufrin.vhsrental.security.dto.UserLoginRequest;
import com.ekufrin.vhsrental.security.dto.UserRegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private static @NonNull AccessTokenResponse setRefreshCookie(HttpServletResponse response, AuthResponseDto auth) {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", auth.refreshToken())
                .httpOnly(true)
                .secure(false) // set to false for local non-HTTPS dev if needed
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(JWTUtil.REFRESH_TOKEN_EXPIRATION_MS / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return new AccessTokenResponse(auth.accessToken(), auth.tokenType());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpServletResponse response) {
        AuthResponseDto auth = authService.authenticate(userLoginRequest);
        AccessTokenResponse body = setRefreshCookie(response, auth);
        return ApiResponseFactory.success("Login successful", body, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest, HttpServletResponse response) {
        AuthResponseDto auth = authService.register(userRegisterRequest);
        AccessTokenResponse body = setRefreshCookie(response, auth);
        return ApiResponseFactory.success("Registration successful", body, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refreshToken(@CookieValue(name = "refresh_token") String request,
                                                                         HttpServletResponse response) {
        AuthResponseDto auth = authService.refreshToken(request);
        AccessTokenResponse body = setRefreshCookie(response, auth);

        return ApiResponseFactory.success("Token refreshed successfully", body, HttpStatus.OK);
    }

    @PostMapping("/access-token")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> generateAccessToken(@CookieValue(name = "refresh_token") String request) {
        AccessTokenResponse accessToken = authService.generateAccessFromRefresh(request);
        return ApiResponseFactory.success("Access token generated successfully", accessToken, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "refresh_token") String request,
                                                    HttpServletResponse response) {

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // set to false for local non-HTTPS dev if needed
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        authService.logout(request);
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ApiResponseFactory.success("Logout successful", null, HttpStatus.OK);
    }
}

