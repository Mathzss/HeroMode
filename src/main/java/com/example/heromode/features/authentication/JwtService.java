package com.example.heromode.features.authentication;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey(){
        // Apply SHA-256 to the configured secret so we always end up with a
        // 256-bit key regardless of how short the JWT_SECRET env var is.
        // HS256 requires >= 256 bits, and a short secret like the current
        // production one (240 bits) was causing WeakKeyException on every
        // login/register attempt.
        try {
            byte[] hashed = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is part of every JRE; this branch is unreachable.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String generateToken(String email, Long userId){
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();

    }

    public Long extractUserId(String token){
        return getClaims(token).get("userId", Long.class);
    }

    public boolean isValid(String token){
        try{
            getClaims(token);
            return true;
        }catch(JwtException e){
            return false;
        }
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
