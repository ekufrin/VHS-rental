package com.ekufrin.vhsrental.vhs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VHSRepository extends JpaRepository<VHS, UUID> {

    @Query("SELECT DISTINCT v FROM VHS v INNER JOIN FETCH v.genre WHERE v.id = :id")
    Optional<VHS> findById(UUID id);

    @Query("SELECT v FROM VHS v INNER JOIN FETCH v.genre")
    Page<VHS> findAll(Pageable pageable);
}
