package com.bank.resource.service;

import com.bank.grpc.fraud.BlacklistRequest;
import com.bank.grpc.fraud.FraudCheckRequest;
import com.bank.grpc.fraud.FraudCheckResponse;
import com.bank.grpc.fraud.FraudServiceGrpc;
import com.bank.grpc.transaction.TransactionProto;
import com.bank.resource.interceptor.JwtClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FraudGrpcService {

    @Value("${grpc.client.fraud-service.host:banking-core}")
    private String host;

    @Value("${grpc.client.fraud-service.port:9090}")
    private int port;

    private ManagedChannel channel;
    private FraudServiceGrpc.FraudServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = NettyChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(new JwtClientInterceptor())
            .build();
        stub = FraudServiceGrpc.newBlockingStub(channel);
        log.info("Fraud gRPC client connected to {}:{}", host, port);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public FraudCheckResponse checkTransaction(TransactionProto transaction) {
        return stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .checkTransaction(FraudCheckRequest.newBuilder()
                .setTransaction(transaction)
                .build());
    }

    public void addToBlacklist(String entityId, String entityType, String reason) {
        stub.withDeadlineAfter(5, TimeUnit.SECONDS)
            .addToBlacklist(BlacklistRequest.newBuilder()
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setReason(reason)
                .build());
    }
}