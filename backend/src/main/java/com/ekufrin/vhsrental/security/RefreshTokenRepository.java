package com.ekufrin.vhsrental.security;

import com.ekufrin.vhsrental.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query("""
                         SELECT rt.user FROM RefreshToken rt
                                      WHERE rt.jti = :jti
                                                  AND rt.revoked = false
                                                               AND rt.expiryDate > CURRENT_TIMESTAMP
            """)
    Optional<User> findUserByJti(String jti);

    RefreshToken findByJti(String jti);
}
