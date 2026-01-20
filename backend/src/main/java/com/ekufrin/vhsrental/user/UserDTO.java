package com.ekufrin.vhsrental.user;

import com.ekufrin.vhsrental.genre.Genre;

import java.util.List;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String email,
        List<Genre> favoriteGenres
) {
}
