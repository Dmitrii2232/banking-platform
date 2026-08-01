package com.bank.grpc.server.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

@Slf4j
@Component
public class InternalAuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> API_KEY_HEADER =
        Metadata.Key.of("X-Internal-Api-Key", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> SERVICE_NAME_HEADER =
        Metadata.Key.of("X-Service-Name", Metadata.ASCII_STRING_MARSHALLER);

    @Value("${banking.internal.api-key:bank-internal-secret-key-2024}")
    private String expectedApiKey;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String method = call.getMethodDescriptor().getFullMethodName();
        String apiKey = headers.get(API_KEY_HEADER);
        String serviceName = headers.get(SERVICE_NAME_HEADER);

        // Публичные методы (health check) пропускаем без проверки
        if (method.contains("grpc.health") || method.contains("grpc.reflection")) {
            return next.startCall(call, headers);
        }

        if (apiKey == null || !expectedApiKey.equals(apiKey)) {
            log.warn("Unauthorized internal call: method={}, service={}, ip={}", 
                method, serviceName, call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
            call.close(Status.UNAUTHENTICATED
                .withDescription("Invalid internal API key. Service: " + serviceName), 
                new Metadata());
            return new ServerCall.Listener<>() {};
        }

        log.debug("Internal call authorized: method={}, service={}", method, serviceName);
        return next.startCall(call, headers);
    }
}