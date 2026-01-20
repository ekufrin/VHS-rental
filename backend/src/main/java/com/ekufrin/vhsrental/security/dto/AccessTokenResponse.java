package com.ekufrin.vhsrental.security.dto;

public record AccessTokenResponse(
        String accessToken,
        String tokenType
) {
}