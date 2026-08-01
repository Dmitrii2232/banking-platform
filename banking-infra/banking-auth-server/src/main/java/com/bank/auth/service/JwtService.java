package com.bank.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    private final SecretKey signingKey;

    public JwtService(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String userId, String username, String clientId,
                                       Collection<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(username)
            .issuer(issuer)
            .issuedAt(now)
            .expiration(expiry)
            .claim("userId", userId)
            .claim("clientId", clientId)
            .claim("roles", roles)
            .claim("type", "access")
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    public String generateRefreshToken(String userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(username)
            .issuer(issuer)
            .issuedAt(now)
            .expiration(expiry)
            .claim("userId", userId)
            .claim("type", "refresh")
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .requireIssuer(issuer)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String getUsernameFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public String getClientIdFromToken(String token) {
        return validateToken(token).get("clientId", String.class);
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}