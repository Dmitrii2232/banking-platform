package com.bank.grpc.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.Channel;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;


@Slf4j
@Component
public class InternalGrpcClient {

    private static final Metadata.Key<String> API_KEY_HEADER =
        Metadata.Key.of("X-Internal-Api-Key", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> SERVICE_NAME_HEADER =
        Metadata.Key.of("X-Service-Name", Metadata.ASCII_STRING_MARSHALLER);

    @Value("${banking.internal.api-key:bank-internal-secret-key-2024}")
    private String apiKey;

    @Value("${spring.application.name:unknown}")
    private String serviceName;

    /**
     * Создаёт перехватчик, который добавляет API-ключ ко всем исходящим вызовам
     */
    public ClientInterceptor getClientInterceptor() {
        return new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    MethodDescriptor<ReqT, RespT> method,
                    CallOptions callOptions,
                    Channel next) {

                return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                        next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        headers.put(API_KEY_HEADER, apiKey);
                        headers.put(SERVICE_NAME_HEADER, serviceName);
                        super.start(responseListener, headers);
                    }
                };
            }
        };
    }

    public Metadata getAuthHeaders() {
        Metadata headers = new Metadata();
        headers.put(API_KEY_HEADER, apiKey);
        headers.put(SERVICE_NAME_HEADER, serviceName);
        return headers;
    }
}