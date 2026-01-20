package com.ekufrin.vhsrental.vhs;

import com.ekufrin.vhsrental.status.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VhsCreateRequest(
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Release date is required")
        String releaseDate,
        @NotBlank(message = "Genre ID is required")
        String genreId,
        @NotNull(message = "Rental price is required")
        @Positive(message = "Rental price must be positive")
        Double rentalPrice,
        @NotNull(message = "Stock level is required")
        @Positive(message = "Stock level must be positive")
        Integer stockLevel,
        @NotNull(message = "Status is required")
        Status status
) {
}
