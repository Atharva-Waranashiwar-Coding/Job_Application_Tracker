package com.example.jobtracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Component
public class JwtTokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final int MIN_SECRET_BYTES = 32;
    private static final String DEFAULT_SECRET_PREFIX = "change-me-please";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SecretKey key;
    private final long expirationMillis;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret:}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMillis,
            @Value("${jwt.issuer:job-tracker}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(resolveSecret(secret));
        this.expirationMillis = Math.max(60000L, expirationMillis);
        this.issuer = (issuer == null || issuer.isBlank()) ? "job-tracker" : issuer.trim();
    }

    public String createToken(String username, Set<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return parseClaims(token).getBody();
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }

    private byte[] resolveSecret(String configuredSecret) {
        if (configuredSecret == null || configuredSecret.isBlank()
                || configuredSecret.startsWith(DEFAULT_SECRET_PREFIX)) {
            LOGGER.warn("JWT secret is not securely configured. Generating an ephemeral secret for this runtime.");
            byte[] generated = new byte[64];
            SECURE_RANDOM.nextBytes(generated);
            return generated;
        }

        byte[] secretBytes = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length >= MIN_SECRET_BYTES) {
            return secretBytes;
        }

        LOGGER.warn("JWT secret is shorter than {} bytes. Deriving a strong key using SHA-256.", MIN_SECRET_BYTES);
        return sha256(secretBytes);
    }

    private byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available on this runtime.", e);
        }
    }
}
