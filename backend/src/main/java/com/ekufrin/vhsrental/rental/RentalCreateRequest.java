package com.ekufrin.vhsrental.rental;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RentalCreateRequest(
        @NotBlank(message = "VHS ID cannot be blank")
        String vhsId,
        @NotBlank(message = "dueDate cannot be blank")
        @Pattern(
                regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
                message = "dueDate must be in ISO 8601 format (e.g., 2023-10-05T14:48:00Z)"
        )
        String dueDate
) {
}
