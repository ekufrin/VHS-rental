package com.ekufrin.vhsrental.rental;

import com.ekufrin.vhsrental.exception.ForbiddenOperationException;
import com.ekufrin.vhsrental.exception.InvalidOperationException;
import com.ekufrin.vhsrental.exception.ResourceNotFoundException;
import com.ekufrin.vhsrental.user.User;
import com.ekufrin.vhsrental.user.UserRepository;
import com.ekufrin.vhsrental.vhs.VHS;
import com.ekufrin.vhsrental.vhs.VHSRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VHSRepository vhsRepository;
    private final RentalMapper rentalMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RentalDTO createRental(RentalCreateRequest request, String userEmail) {
        log.info("Create rental requested for vhsId={} by userEmail={}", request.vhsId(), userEmail);
        if(Instant.parse(request.dueDate()).isBefore(Instant.now())) {
            log.warn("Invalid due date for rental creation vhsId={} dueDate={}", request.vhsId(), request.dueDate());
            throw new InvalidOperationException(
                    "Due date must be in the future"
            );
        }
        VHS vhs = vhsRepository.findById(UUID.fromString(request.vhsId()))
                .orElseThrow(() -> new ResourceNotFoundException("VHS", "id", request.vhsId()));

        if (!isVHSAvailable(vhs)) {
            log.warn("VHS unavailable for rental vhsId={} stockLevel={}", request.vhsId(), vhs.getStockLevel());
            throw new InvalidOperationException(
                    "VHS with id: " + request.vhsId() + " is not currently available for rental"
            );
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Rental rental = Rental.builder()
                .vhs(vhs)
                .user(user)
                .rentalDate(Instant.now())
                .dueDate(Instant.parse(request.dueDate()))
                .returnDate(null)
                .price(null)
                .build();
        rentalRepository.save(rental);
        log.info("Rental created rentalId={} vhsId={} userId={} dueDate={}", rental.getId(), vhs.getId(), user.getId(), rental.getDueDate());
        return rentalMapper.toDTO(rental);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RentalDTO finishRental(UUID id, String userEmail) {
        log.info("Finish rental requested rentalId={} by userEmail={}", id, userEmail);
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));

        if (rental.getReturnDate() != null) {
            log.warn("Attempt to finish already finished rentalId={}", id);
            throw new InvalidOperationException(
                    "Rental has already been finished"
            );
        }

        if (!rental.getUser().getEmail().equals(userEmail)) {
            log.warn("Unauthorized finish attempt rentalId={} requestedBy={}", id, userEmail);
            throw new ForbiddenOperationException(
                    "You are not authorized to finish this rental"
            );
        }

        rental.setReturnDate(Instant.now());
        rental.calculatePrice();
        rentalRepository.save(rental);
        log.info("Rental finished rentalId={} userId={} price={}", rental.getId(), rental.getUser().getId(), rental.getPrice());
        return rentalMapper.toDTO(rental);
    }

    @Transactional(readOnly = true)
    public Page<RentalDTO> getAllRentals(Pageable pageable) {
        Page<Rental> rentals = rentalRepository.findAll(pageable);
        return rentalMapper.toDTO(rentals);
    }

    @Transactional(readOnly = true)
    public RentalDTO getRentalById(UUID id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));
        return rentalMapper.toDTO(rental);
    }

    private boolean isVHSAvailable(VHS vhs) {
        return rentalRepository.countByVhs_IdAndReturnDateIsNull(vhs.getId()) < vhs.getStockLevel();
    }

}
