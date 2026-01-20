package com.ekufrin.vhsrental.rental;

import com.ekufrin.vhsrental.user.UserDTO;
import com.ekufrin.vhsrental.vhs.VHSDTO;

import java.time.Instant;

public record RentalDTO(
        String id,
        VHSDTO vhs,
        UserDTO user,
        Instant rentalDate,
        Instant dueDate,
        Instant returnDate,
        Double price
) {
}
