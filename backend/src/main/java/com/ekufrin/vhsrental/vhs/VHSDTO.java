package com.ekufrin.vhsrental.vhs;

import com.ekufrin.vhsrental.genre.GenreDTO;
import com.ekufrin.vhsrental.status.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VHSDTO {
    private UUID id;
    private String title;
    private Instant releaseDate;
    private GenreDTO genre;
    private Double rentalPrice;
    private Integer stockLevel;
    private String imageUrl;
    private Status status;
}
