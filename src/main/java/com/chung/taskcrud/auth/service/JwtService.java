package com.chung.taskcrud.auth.service;

import com.chung.taskcrud.auth.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final Key key;
    private final long accessExpirationMs;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secret,
            @Value("${application.security.jwt.expiration}") long accessExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
    }

    public String generateAccessToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName())
                .toList();

        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName())
                .distinct()
                .toList();

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long userId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public List<String> permissions(String token) {
        Object v = parseClaims(token).get("permissions");
        if (v instanceof java.util.Collection<?> c) return c.stream().map(Object::toString).toList();
        return List.of();
    }

    public List<String> roles(String token) {
        Object v = parseClaims(token).get("roles");
        if (v instanceof java.util.Collection<?> c) return c.stream().map(Object::toString).toList();
        return List.of();
    }
}