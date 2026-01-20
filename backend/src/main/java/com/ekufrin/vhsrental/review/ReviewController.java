package com.ekufrin.vhsrental.review;

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
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@RequestBody @Valid ReviewCreateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ReviewDTO review = reviewService.createReview(request, userDetails.getUsername());
        return ApiResponseFactory.success("Review created successfully", review, HttpStatus.CREATED);
    }

    @GetMapping("/vhs/{vhsId}")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getAllReviewsForVHS(@PathVariable UUID vhsId, Pageable pageable) {
        Page<ReviewDTO> reviews = reviewService.getAllReviewsForVHS(vhsId, pageable);
        return ApiResponseFactory.success("Reviews retrieved successfully", reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO>> getReviewById(@PathVariable UUID id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return ApiResponseFactory.success("Review retrieved successfully", review, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateReview(@PathVariable UUID id, @RequestBody @Valid ReviewUpdateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.updateReview(id, request, userDetails.getUsername());
        return ApiResponseFactory.success("Review updated successfully", null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(id, userDetails.getUsername());
        return ApiResponseFactory.success("Review deleted successfully", null, HttpStatus.NO_CONTENT);
    }
}
