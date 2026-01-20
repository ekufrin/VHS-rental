package com.ekufrin.vhsrental.genre;

import jakarta.validation.constraints.NotBlank;

public record GenreCreateRequest(
        @NotBlank(message = "Genre name must not be blank")
        String name
) {
}
