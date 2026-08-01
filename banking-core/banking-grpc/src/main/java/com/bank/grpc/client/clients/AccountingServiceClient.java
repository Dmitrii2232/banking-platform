package com.bank.grpc.client.clients;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import com.bank.grpc.accounting.AccountBalanceResponse;
import com.bank.grpc.accounting.AccountingServiceGrpc;
import com.bank.grpc.accounting.GetAccountBalanceRequest;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AccountingServiceClient {

    @GrpcClient("accounting-service")
    private AccountingServiceGrpc.AccountingServiceBlockingStub blockingStub;

    @SuppressWarnings("unused")
	private final ManagedChannel channel;
    public AccountingServiceClient(ManagedChannel accountingServiceChannel) { this.channel = accountingServiceChannel; }

    public AccountBalanceResponse getBalance(String accountCode, long timeoutSec) {
        return blockingStub.withDeadlineAfter(timeoutSec, TimeUnit.SECONDS)
            .getAccountBalance(GetAccountBalanceRequest.newBuilder().setAccountCode(accountCode).build());
    }
}