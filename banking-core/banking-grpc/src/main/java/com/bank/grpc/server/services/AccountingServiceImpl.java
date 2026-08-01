package com.bank.grpc.server.services;

import com.bank.accounting.AccountingService;
import com.bank.domain.accounting.AccountType;
import com.bank.domain.common.Money;
import com.bank.grpc.accounting.AccountingServiceGrpc;
import com.bank.grpc.accounting.AccountBalanceResponse;
import com.bank.grpc.accounting.GetAccountBalanceRequest;
import com.bank.grpc.accounting.VerifyBalanceRequest;
import com.bank.grpc.accounting.VerifyBalanceResponse;
import com.bank.grpc.common.MoneyProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.LocalDate;

@GrpcService
@RequiredArgsConstructor
public class AccountingServiceImpl extends AccountingServiceGrpc.AccountingServiceImplBase {

    private final AccountingService accountingService;

    @Override
    public void getAccountBalance(GetAccountBalanceRequest request, StreamObserver<AccountBalanceResponse> responseObserver) {
        try {
            AccountType accountType = AccountType.fromCode(request.getAccountCode());
            Money balance = accountingService.getBalance(accountType, LocalDate.now());
            responseObserver.onNext(AccountBalanceResponse.newBuilder()
                .setAccountCode(accountType.getCode())
                .setAccountName(accountType.getName())
                .setSide(accountType.getSide().name())
                .setBalance(MoneyProto.newBuilder()
                    .setAmount(balance.getAmount().toPlainString())
                    .setCurrency(balance.getCurrency())
                    .build())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void verifyBalance(VerifyBalanceRequest request, StreamObserver<VerifyBalanceResponse> responseObserver) {
        boolean balanced = accountingService.verifyTrialBalance(LocalDate.now());
        responseObserver.onNext(VerifyBalanceResponse.newBuilder().setIsBalanced(balanced).build());
        responseObserver.onCompleted();
    }
}