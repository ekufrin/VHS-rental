package com.ekufrin.vhsrental.vhs;

import com.ekufrin.vhsrental.genre.Genre;
import com.ekufrin.vhsrental.status.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "vhs")
public class VHS {
    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private Instant releaseDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    @JsonBackReference
    private Genre genre;
    @Column(nullable = false)
    private Double rentalPrice;
    @Column(nullable = false)
    private Integer stockLevel;
    private UUID imageId;
    private String imageExtension;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


}
