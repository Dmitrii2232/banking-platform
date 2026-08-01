package com.bank.grpc.client.clients;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import com.bank.grpc.common.MoneyProto;
import com.bank.grpc.common.RequestContextProto;
import com.bank.grpc.transaction.DepositRequest;
import com.bank.grpc.transaction.DepositResponse;
import com.bank.grpc.transaction.TransactionServiceGrpc;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TransactionServiceClient {

    @GrpcClient("transaction-service")
    private TransactionServiceGrpc.TransactionServiceBlockingStub blockingStub;

    @SuppressWarnings("unused")
	private final ManagedChannel channel;
    public TransactionServiceClient(ManagedChannel transactionServiceChannel) { this.channel = transactionServiceChannel; }

    public DepositResponse deposit(String productId, String clientId, BigDecimal amount, String currency, long timeoutSec) {
        return blockingStub.withDeadlineAfter(timeoutSec, TimeUnit.SECONDS)
            .deposit(DepositRequest.newBuilder()
                .setProductId(productId)
                .setAmount(MoneyProto.newBuilder().setAmount(amount.toPlainString()).setCurrency(currency).build())
                .setContext(RequestContextProto.newBuilder().setUserId(clientId).build())
                .build());
    }
}