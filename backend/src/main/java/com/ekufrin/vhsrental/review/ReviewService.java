package com.ekufrin.vhsrental.review;

import com.ekufrin.vhsrental.exception.ForbiddenOperationException;
import com.ekufrin.vhsrental.exception.ResourceNotFoundException;
import com.ekufrin.vhsrental.rental.Rental;
import com.ekufrin.vhsrental.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final RentalRepository rentalRepository;

    @Transactional
    public ReviewDTO createReview(ReviewCreateRequest request, String userEmail) {
        if (reviewRepository.existsReviewByRental_Id(request.rentalId())) {
            throw new ForbiddenOperationException("A review for this rental already exists.");
        }

        Rental rental = rentalRepository.findById(request.rentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", request.rentalId()));

        if (!rental.getUser().getEmail().equals(userEmail)) {
            throw new ForbiddenOperationException("You are not allowed to review this rental.");
        }

        Review review = Review.builder()
                .rental(rental)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review created reviewId={} rentalId={} rating={}", savedReview.getId(), request.rentalId(), request.rating());
        return reviewMapper.toDTO(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAllReviewsForVHS(UUID vhsId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByRental_Vhs_Id(vhsId, pageable);
        return reviewMapper.toDTO(reviews);
    }

    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(UUID id) {
        Review review = reviewRepository.findByIdAndIncludeRental(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return reviewMapper.toDTO(review);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateReview(UUID id, ReviewUpdateRequest request, String userEmail) {
        Review review = reviewRepository.findByIdAndIncludeRental(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getRental().getUser().getEmail().equals(userEmail)) {
            throw new ForbiddenOperationException("You are not allowed to update this review.");
        }

        review.setRating(request.rating());
        review.setComment(request.comment() != null ? request.comment() : review.getComment());

        reviewRepository.save(review);
        log.info("Review updated reviewId={} updatedBy={} newRating={}", id, userEmail, request.rating());
    }

    @Transactional
    public void deleteReview(UUID id, String userEmail) {
        Review review = reviewRepository.findByIdAndIncludeRental(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getRental().getUser().getEmail().equals(userEmail)) {
            throw new ForbiddenOperationException("You are not allowed to delete this review.");
        }

        reviewRepository.delete(review);
        log.info("Review deleted reviewId={} deletedBy={}", id, userEmail);
    }
}
