package com.ekufrin.vhsrental.security.dto;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}

