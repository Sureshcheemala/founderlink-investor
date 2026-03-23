package com.founderlink.auth_service.security;

import com.founderlink.auth_service.entity.Role;
import com.founderlink.auth_service.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String SECRET = "ejfbniuNeJFkJDneaiKJKNol298y2938tjleknjrjebfuyiwefg";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    // Generate JWT
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .setIssuer("auth-service")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    // Extract all claims (single parse)
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from claims
    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    // Extract roles from claims
    public List<String> extractRoles(Claims claims) {
        Object rolesObj = claims.get("roles");

        if (rolesObj == null) {
            return List.of();
        }

        if (rolesObj instanceof List<?> rolesList) {
            return rolesList.stream()
                    .map(Object::toString)
                    .toList();
        }

        if (rolesObj instanceof String role) {
            return List.of(role);
        }

        return List.of();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // single parse
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Signing key (used everywhere)
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}