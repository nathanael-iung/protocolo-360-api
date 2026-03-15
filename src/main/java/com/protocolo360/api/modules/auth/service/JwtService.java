package com.protocolo360.api.modules.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.protocolo360.api.modules.auth.dto.TokenMetadata;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JwtService {

    @Value("${TOKEN_SECRET}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenMetadata generateToken(String email, Set<String> roles) {
        long now = System.currentTimeMillis();
        long expirationTime = 86400000; // 24 hours
        long expiry = now + expirationTime;

        String token = Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(Date.from(Instant.ofEpochMilli(now)))
                .expiration(Date.from(Instant.ofEpochMilli(expiry)))
                .signWith(getSigningKey())
                .compact();

        return new TokenMetadata(token, now, expiry);
    }

    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public TokenMetadata refreshToken(String token) {
        Claims claims = getClaims(token); // Use the helper that parses the payload

        if (claims == null)
            return null;

        String email = claims.getSubject();

        // Extract the roles list from the claims
        @SuppressWarnings("unchecked")
        List<String> rolesList = claims.get("roles", List.class);
        Set<String> roles = new HashSet<>(rolesList);

        // Generate a new token including those same roles
        return generateToken(email, roles);
    }

    public String recoverToken(HttpServletRequest request) {

        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "protocolo360_auth".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
