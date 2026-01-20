package com.ekufrin.vhsrental.user;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record UserFavoriteGenresRequest(
        @NotEmpty(message = "Genres list cannot be empty")
        List<UUID> favoriteGenres
) {
}
