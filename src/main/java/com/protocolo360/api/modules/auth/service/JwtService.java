package com.protocolo360.api.modules.auth.service;

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

@Service
public class JwtService {

    @Value("${TOKEN_SECRET}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenMetadata generateToken(String email) {

        long now = System.currentTimeMillis();
        long expirationTime = 86400000; // 24 hours
        long expiry = now + expirationTime;

        String token = Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.ofEpochMilli(now)))
                .expiration(Date.from(Instant.ofEpochMilli(expiry)))
                .signWith(getSigningKey())
                .compact();
                
        return new TokenMetadata(token, now, expiry);
    }

    public String validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public TokenMetadata refreshToken(String token) {
        String email = validateToken(token);
        if (email == null) return null;

        return generateToken(email);
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
}
