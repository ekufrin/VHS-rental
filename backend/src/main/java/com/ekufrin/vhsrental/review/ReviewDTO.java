package com.ekufrin.vhsrental.review;

import java.util.UUID;

public record ReviewDTO(
        UUID id,
        Double rating,
        String comment,
        UserSummaryDTO user,
        VHSSummaryDTO vhs
) {
}
