package com.ekufrin.vhsrental.rental;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {

    long countByVhs_IdAndReturnDateIsNull(UUID vhsId);

    @Query("""
            SELECT DISTINCT r FROM Rental r
            INNER JOIN FETCH r.vhs v
            INNER JOIN FETCH v.genre
            INNER JOIN FETCH r.user
            """)
    Page<Rental> findAll(Pageable pageable);

    @Query("""
             SELECT r FROM Rental r
             INNER JOIN FETCH r.vhs v
             INNER JOIN FETCH v.genre
             INNER JOIN FETCH r.user
             WHERE r.id = :id
            """)
    Optional<Rental> findById(UUID id);
}
