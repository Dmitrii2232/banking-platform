package com.bank.grpc.server.interceptors;

import org.springframework.stereotype.Component;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class MetricsInterceptor implements ServerInterceptor {
    private final AtomicLong requestCount = new AtomicLong(0);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        requestCount.incrementAndGet();
        return next.startCall(call, headers);
    }

    public long getRequestCount() { return requestCount.get(); }
}