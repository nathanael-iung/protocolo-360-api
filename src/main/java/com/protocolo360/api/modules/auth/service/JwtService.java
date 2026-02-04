package com.protocolo360.api.modules.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

@Service
public class JwtService {

    @Value("${TOKEN_SECRET}")
    private String secret;

    public String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
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
            return null; // Token invalid or expired
        }
    }

    public String refreshToken(String token) {
        String email = validateToken(token);
        if (email == null)
            return null;

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
