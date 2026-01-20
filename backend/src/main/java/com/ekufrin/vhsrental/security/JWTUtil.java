package com.ekufrin.vhsrental.security;

import com.ekufrin.vhsrental.config.JWTConfigurationProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JWTUtil {
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 days
    static final Logger logger = Logger.getLogger(JWTUtil.class.getName());
    private final JWTConfigurationProperties jwtConfigurationProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtConfigurationProperties.key().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UUID id) {
        return Jwts.builder()
                .subject(String.valueOf(id))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtConfigurationProperties.expiration()))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(UUID id, String jti) {
        return Jwts.builder()
                .subject(String.valueOf(id))
                .id(jti)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(secretKey)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getJtiFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getId();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            logger.info("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.info("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.info("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.info("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.info("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    public boolean isRefreshToken(String token) {
        try {
            var jwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            Object type = jwt.getPayload().get("type");
            return type != null && "refresh".equals(type.toString());
        } catch (Exception e) {
            logger.info("Failed to determine token type: " + e.getMessage());
            return false;
        }
    }

}
