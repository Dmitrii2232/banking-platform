package com.bank.resource.service;

import com.bank.grpc.accounting.AccountBalanceResponse;
import com.bank.grpc.accounting.AccountingServiceGrpc;
import com.bank.grpc.accounting.GetAccountBalanceRequest;
import com.bank.grpc.accounting.VerifyBalanceRequest;
import com.bank.grpc.accounting.VerifyBalanceResponse;
import com.bank.resource.interceptor.JwtClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccountingGrpcService {

    @Value("${grpc.client.accounting-service.host:banking-core}")
    private String host;

    @Value("${grpc.client.accounting-service.port:9090}")
    private int port;

    private ManagedChannel channel;
    private AccountingServiceGrpc.AccountingServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = NettyChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(new JwtClientInterceptor())
            .build();
        stub = AccountingServiceGrpc.newBlockingStub(channel);
        log.info("Accounting gRPC client connected to {}:{}", host, port);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public Map<String, Object> getBalance(String accountCode) {
        AccountBalanceResponse response = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .getAccountBalance(GetAccountBalanceRequest.newBuilder()
                .setAccountCode(accountCode)
                .build());

        return Map.of(
            "accountCode", response.getAccountCode(),
            "accountName", response.getAccountName(),
            "side", response.getSide(),
            "balance", response.getBalance().getAmount(),
            "currency", response.getBalance().getCurrency()
        );
    }

    public Map<String, Object> verifyBalance() {
        VerifyBalanceResponse response = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .verifyBalance(VerifyBalanceRequest.newBuilder().build());

        return Map.of("isBalanced", response.getIsBalanced());
    }
}