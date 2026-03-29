package com.rdmishra.backend_tms.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rdmishra.backend_tms.dto.UserRole;
import com.rdmishra.backend_tms.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Service
@Data
public class JWTService {

    private final SecretKey key;
    private final long accessTtlseconds;
    private final long refreshTtlseconds;
    private final String issure;

    public JWTService(
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlseconds,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlseconds,
            @Value("${security.jwt.issuer}") String issure,
            @Value("${security.jwt.secert}") String secret) {

        if (secret == null || secret.length() < 64) {
            throw new IllegalArgumentException("Invalid Secert");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlseconds = accessTtlseconds;
        this.refreshTtlseconds = refreshTtlseconds;
        this.issure = issure;
    }

    // generateAccess token
    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        UserRole role = user.getRole() == null ? UserRole.USER : user.getRole();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issure)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlseconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", List.of(user.getRole().name()),
                        "typ", "access"))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }

    // generateRefers Token
    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issure)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlseconds)))
                .claims(Map.of(
                        "typ", "refresh"))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

    }

    // parse token

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    // check

    public boolean isAccessToken(String token) {
        Claims c = parse(token).getPayload();
        return "access".equals(c.get("typ"));
    }

    public boolean isRefreshToken(String token) {
        Claims c = parse(token).getPayload();
        return "refresh".equals(c.get("typ"));
    }
    // getuserid

    public UUID getUserID(String token) {
        Claims c = parse(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    // gettokenid

    public String getJti(String token) {
        return parse(token).getPayload().getId();
    }

    public String getEmail(String token) {
        Claims c = parse(token).getPayload();
        return (String) c.get("email");
    }

    public List<String> getRoles(String token) {
        Claims c = parse(token).getPayload();
        Object roles = c.get("roles");

        if (roles instanceof List<?>) {
            return ((List<?>) roles)
                    .stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }
}
