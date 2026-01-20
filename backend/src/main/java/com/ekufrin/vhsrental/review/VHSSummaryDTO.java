package com.ekufrin.vhsrental.review;

import java.time.Instant;
import java.util.UUID;

public record VHSSummaryDTO(UUID id, String title, String genre, Instant releaseDate) {
}
