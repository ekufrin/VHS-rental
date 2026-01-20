package com.ekufrin.vhsrental.rental;

import com.ekufrin.vhsrental.exception.NotReturned;
import com.ekufrin.vhsrental.user.User;
import com.ekufrin.vhsrental.vhs.VHS;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rentals")
public class Rental {
    public static final double LATE_FEE_MULTIPLIER = 0.1;
    @Id
    @UuidGenerator
    private UUID id;
    @Version
    private Long version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vhs_id", nullable = false)
    private VHS vhs;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    @Column(nullable = false)
    private Instant rentalDate;
    @Column(nullable = false)
    private Instant dueDate;
    private Instant returnDate;
    private Double price;

    public synchronized void calculatePrice() {
        if (returnDate == null) {
            throw new NotReturned(NotReturned.DEFAULT_MESSAGE);
        }

        Double rentalPrice = (vhs != null) ? vhs.getRentalPrice() : null;
        if (rentalPrice == null) {
            throw new IllegalStateException("Rental price is not available for this rental.");
        }

        double lateFee = 0.0;
        if (returnDate.isAfter(dueDate)) {
            lateFee = calculateLateFee(rentalPrice);
        }

        long rentalDays = Math.max(1, (long) Math.ceil(Duration.between(rentalDate, dueDate).toHours() / 24.0));
        this.price = Math.round((rentalPrice * rentalDays + lateFee) * 100.0) / 100.0;

    }

    private Double calculateLateFee(Double basePrice) {
        long daysLate = Duration.between(dueDate, returnDate).toDays();
        if (daysLate <= 0) {
            return 0.0;
        }
        return daysLate * (basePrice * LATE_FEE_MULTIPLIER);
    }
}
