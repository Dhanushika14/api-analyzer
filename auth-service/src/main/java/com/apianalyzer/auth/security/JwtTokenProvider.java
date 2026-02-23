package com.apianalyzer.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    // Tip: In prod, set a Base64-encoded secret in application config and decode below.
    @Value("${jwt.secret:your-secret-key-minimum-32-characters-long}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours
    private long jwtExpirationInMs;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days
    private long refreshTokenExpirationInMs;

    private SecretKey getSigningKey() {
        // If jwtSecret is Base64-encoded (recommended), decode like this:
        // return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        // If it's a plain string (dev), at least use a fixed charset:
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String email) {
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(exp)
                // 0.12.x preferred signature constant:
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(String email) {
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + refreshTokenExpirationInMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String extractEmail(String token) {
        // 0.12.x parsing API:
        return Jwts.parser()
                .verifyWith(getSigningKey())  // set the key used for verifying signature
                .build()
                .parseSignedClaims(token)     // parses & verifies; throws if invalid/expired
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return false; // no exception => not expired
        } catch (ExpiredJwtException e) {
            return true;  // specifically catch expiry
        }
    }
}