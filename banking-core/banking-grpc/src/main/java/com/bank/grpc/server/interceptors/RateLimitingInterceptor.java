package com.bank.grpc.server.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitingInterceptor implements ServerInterceptor {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String clientId = headers.get(
            Metadata.Key.of("X-Client-Id", Metadata.ASCII_STRING_MARSHALLER));
        if (clientId == null) clientId = "anonymous";

        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(100, 100));
        if (!bucket.tryConsume()) {
            call.close(Status.RESOURCE_EXHAUSTED.withDescription("Rate limit exceeded"), new Metadata());
            return new ServerCall.Listener<>() {};
        }
        return next.startCall(call, headers);
    }

    static class TokenBucket {
        private final double maxTokens;
        private double tokens;
        private long lastRefill;

        TokenBucket(double maxTokens, double refillRate) {
            this.maxTokens = maxTokens; this.tokens = maxTokens; this.lastRefill = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1) { tokens--; return true; }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefill) / 1_000_000_000.0;
            tokens = Math.min(maxTokens, tokens + elapsed * maxTokens);
            lastRefill = now;
        }
    }
}