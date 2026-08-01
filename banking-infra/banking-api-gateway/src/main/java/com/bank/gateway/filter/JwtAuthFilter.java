package com.bank.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final SecretKey signingKey;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    private static final List<String> PUBLIC_PATHS = List.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/actuator/health",
        "/actuator/metrics",
        "/actuator/prometheus",
        "/fallback"
    );

    public JwtAuthFilter(
            @Value("${jwt.secret-key}") String secretKey,
            ReactiveRedisTemplate<String, String> redisTemplate) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String path = exchange.getRequest().getURI().getPath();
        String traceIdHeader = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        final String traceId = traceIdHeader != null ? traceIdHeader : UUID.randomUUID().toString().substring(0, 8);

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[{}] Missing Authorization header for: {}", traceId, path);
            return unauthorized(exchange, "Missing Authorization header");
        }

        final String token = authHeader.substring(7);

        return redisTemplate.hasKey(BLACKLIST_PREFIX + token)
            .flatMap(isBlacklisted -> {
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    log.warn("[{}] Blacklisted token used for: {}", traceId, path);
                    return unauthorized(exchange, "Token has been revoked");
                }

                try {
                    final Claims claims = Jwts.parser()
                        .verifyWith(signingKey)
                        .requireIssuer("banking-auth-server")
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                    final String username = claims.getSubject();
                    final String clientId = claims.get("clientId", String.class);
                    final String userId = claims.get("userId", String.class);

                    exchange.getRequest().mutate()
                        .header("X-Client-Id", clientId)
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
                        .header("X-Trace-Id", traceId);

                    log.debug("[{}] JWT OK: user={}, clientId={}, path={}", traceId, username, clientId, path);
                    return chain.filter(exchange);

                } catch (ExpiredJwtException e) {
                    log.warn("[{}] Expired token: {}", traceId, e.getMessage());
                    return unauthorized(exchange, "Token expired");
                } catch (SignatureException e) {
                    log.warn("[{}] Invalid signature: {}", traceId, e.getMessage());
                    return unauthorized(exchange, "Invalid token signature");
                } catch (MalformedJwtException e) {
                    log.warn("[{}] Malformed token: {}", traceId, e.getMessage());
                    return unauthorized(exchange, "Malformed token");
                } catch (Exception e) {
                    log.error("[{}] JWT error: {}", traceId, e.getMessage());
                    return unauthorized(exchange, "Token validation failed");
                }
            });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("WWW-Authenticate", "Bearer");
        exchange.getResponse().getHeaders().add("X-Error", message);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}