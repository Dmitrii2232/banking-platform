package com.bank.grpc.server.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import java.util.Base64;
import java.util.Set;

@Slf4j
@Component
public class AuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION_HEADER =
        Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    private static final Set<String> PUBLIC_METHODS = Set.of(
        "grpc.health.v1.Health/Check",
        "grpc.reflection.v1alpha.ServerReflection/ServerReflectionInfo"
    );

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String method = call.getMethodDescriptor().getFullMethodName();
        if (PUBLIC_METHODS.contains(method)) return next.startCall(call, headers);

        String authHeader = headers.get(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String clientId = validateJwtToken(token);
            Context ctx = Context.current()
                .withValue(Constants.CLIENT_ID_CONTEXT_KEY, clientId)
                .withValue(Constants.AUTH_METHOD_CONTEXT_KEY, "JWT");
            return Contexts.interceptCall(ctx, call, headers, next);
        }

        call.close(Status.UNAUTHENTICATED.withDescription("Требуется аутентификация"), new Metadata());
        return new ServerCall.Listener<>() {};
    }

    private String validateJwtToken(String token) {
        if (token == null || token.isBlank()) throw new SecurityException("Пустой токен");
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new SecurityException("Неверный формат JWT");
            @SuppressWarnings("unused")
			String payload = new String(Base64.getDecoder().decode(parts[1]));
            return "client-" + token.hashCode();
        } catch (Exception e) {
            throw new SecurityException("Ошибка парсинга JWT");
        }
    }
}