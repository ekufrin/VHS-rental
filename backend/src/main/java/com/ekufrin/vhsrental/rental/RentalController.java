package com.ekufrin.vhsrental.rental;

import com.ekufrin.vhsrental.config.ApiResponse;
import com.ekufrin.vhsrental.config.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<ApiResponse<RentalDTO>> createRental(@RequestBody @Valid RentalCreateRequest request, @AuthenticationPrincipal UserDetails user) {
        RentalDTO rentalDTO = rentalService.createRental(request, user.getUsername());
        return ApiResponseFactory.success("Rental created successfully", rentalDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/{rentalId}/finish")
    public ResponseEntity<ApiResponse<RentalDTO>> finishRental(@PathVariable UUID rentalId, @AuthenticationPrincipal UserDetails user) {
        RentalDTO rentalDTO = rentalService.finishRental(rentalId, user.getUsername());
        return ApiResponseFactory.success("Rental finished successfully", rentalDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RentalDTO>>> getAllRentals(Pageable pageable) {
        Page<RentalDTO> rentals = rentalService.getAllRentals(pageable);
        return ApiResponseFactory.success("Rentals retrieved successfully", rentals, HttpStatus.OK);
    }

    @GetMapping("/{rentalId}")
    public ResponseEntity<ApiResponse<RentalDTO>> getRentalById(@PathVariable UUID rentalId) {
        RentalDTO rentalDTO = rentalService.getRentalById(rentalId);
        return ApiResponseFactory.success("Rental retrieved successfully", rentalDTO, HttpStatus.OK);
    }
}
