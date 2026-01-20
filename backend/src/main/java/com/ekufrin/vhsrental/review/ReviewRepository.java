package com.ekufrin.vhsrental.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsReviewByRental_Id(UUID rentalId);

    @Query("""
            SELECT DISTINCT r FROM Review r
            INNER JOIN FETCH r.rental rental
            INNER JOIN FETCH rental.vhs vhs
            INNER JOIN FETCH vhs.genre
            INNER JOIN FETCH rental.user
            WHERE vhs.id = :vhsId
            """)
    Page<Review> findAllByRental_Vhs_Id(@Param("vhsId") UUID vhsId, Pageable pageable);

    @Query("""
            SELECT r FROM Review r
            INNER JOIN FETCH r.rental rental
            INNER JOIN FETCH rental.vhs vhs
            INNER JOIN FETCH vhs.genre
            INNER JOIN FETCH rental.user
            WHERE r.id = :id
            """)
    Optional<Review> findByIdAndIncludeRental(@Param("id") UUID id);
}


