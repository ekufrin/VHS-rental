package com.ekufrin.vhsrental.genre;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    Page<Genre> findAll(@NonNull Pageable pageable);

    List<Genre> findAllByIdIn(List<UUID> ids);

    boolean existsByName(String name);
}
