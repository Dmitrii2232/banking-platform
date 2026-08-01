package com.bank.grpc.server.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class TracingInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> TRACE_ID_KEY =
        Metadata.Key.of("X-Trace-Id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String traceId = headers.get(TRACE_ID_KEY);
        if (traceId == null) traceId = UUID.randomUUID().toString();
        String spanId = UUID.randomUUID().toString();
        Context ctx = Context.current()
            .withValue(Constants.TRACE_ID_CONTEXT_KEY, traceId)
            .withValue(Constants.SPAN_ID_CONTEXT_KEY, spanId);
        return Contexts.interceptCall(ctx, call, headers, next);
    }
}