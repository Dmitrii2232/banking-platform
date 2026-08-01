package com.bank.resource.service;

import com.bank.grpc.common.MoneyProto;
import com.bank.grpc.common.RequestContextProto;
import com.bank.grpc.transaction.DepositRequest;
import com.bank.grpc.transaction.DepositResponse;
import com.bank.grpc.transaction.TransactionServiceGrpc;
import com.bank.grpc.transaction.TransferRequest;
import com.bank.grpc.transaction.TransferResponse;
import com.bank.grpc.transaction.WithdrawRequest;
import com.bank.grpc.transaction.WithdrawResponse;
import com.bank.resource.interceptor.JwtClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TransactionGrpcService {

    @Value("${grpc.client.transaction-service.host:banking-core}")
    private String host;

    @Value("${grpc.client.transaction-service.port:9090}")
    private int port;

    private ManagedChannel channel;
    private TransactionServiceGrpc.TransactionServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = NettyChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(new JwtClientInterceptor())
            .build();
        stub = TransactionServiceGrpc.newBlockingStub(channel);
        log.info("Transaction gRPC client connected to {}:{}", host, port);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public Map<String, Object> deposit(String productId, String clientId, BigDecimal amount, String currency) {
        DepositResponse response = stub
            .withDeadlineAfter(15, TimeUnit.SECONDS)
            .deposit(DepositRequest.newBuilder()
                .setProductId(productId)
                .setAmount(MoneyProto.newBuilder()
                    .setAmount(amount.toPlainString())
                    .setCurrency(currency)
                    .build())
                .setContext(RequestContextProto.newBuilder()
                    .setUserId(clientId)
                    .build())
                .build());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("transactionId", response.getTransactionId());
        result.put("status", response.getStatus().name());
        return result;
    }

    public Map<String, Object> withdraw(String productId, String clientId, BigDecimal amount, String currency) {
        WithdrawResponse response = stub
            .withDeadlineAfter(15, TimeUnit.SECONDS)
            .withdraw(WithdrawRequest.newBuilder()
                .setProductId(productId)
                .setAmount(MoneyProto.newBuilder()
                    .setAmount(amount.toPlainString())
                    .setCurrency(currency)
                    .build())
                .setContext(RequestContextProto.newBuilder()
                    .setUserId(clientId)
                    .build())
                .build());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("transactionId", response.getTransactionId());
        result.put("status", response.getStatus().name());
        return result;
    }

    public Map<String, Object> transfer(String sourceProductId, String destinationProductId,
                                         String clientId, BigDecimal amount, String currency) {
        TransferResponse response = stub
            .withDeadlineAfter(15, TimeUnit.SECONDS)
            .transfer(TransferRequest.newBuilder()
                .setSourceProductId(sourceProductId)
                .setDestinationProductId(destinationProductId)
                .setAmount(MoneyProto.newBuilder()
                    .setAmount(amount.toPlainString())
                    .setCurrency(currency)
                    .build())
                .setContext(RequestContextProto.newBuilder()
                    .setUserId(clientId)
                    .build())
                .build());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("transactionId", response.getTransactionId());
        result.put("status", response.getStatus().name());
        return result;
    }
}