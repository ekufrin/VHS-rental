package com.ekufrin.vhsrental.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequest(
        @NotNull(message = "Rating cannot be blank")
        @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
        @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
        Double rating,
        @Size(max = 100, message = "Comment cannot exceed 100 characters")
        String comment
) {
}
