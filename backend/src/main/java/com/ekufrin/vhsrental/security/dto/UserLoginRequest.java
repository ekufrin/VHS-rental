package com.ekufrin.vhsrental.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserLoginRequest(@NotBlank(message = "Email is required")
                               @Email(message = "Email should be in valid format",
                                       regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,63}",
                                       flags = Pattern.Flag.CASE_INSENSITIVE)
                               String email,
                               @NotBlank(message = "Password is required")
                               String password) {
}
