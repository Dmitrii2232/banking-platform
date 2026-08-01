package com.bank.grpc.server.services;

import com.bank.antifraud.FraudDetectionEngine;
import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;
import com.bank.commands.CommandBus;
import com.bank.domain.command.DepositCashCommand;
import com.bank.domain.command.MakePaymentCommand;
import com.bank.domain.command.WithdrawCashCommand;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.grpc.transaction.DepositRequest;
import com.bank.grpc.transaction.DepositResponse;
import com.bank.grpc.transaction.TransactionStatusProto;
import com.bank.grpc.transaction.TransferRequest;
import com.bank.grpc.transaction.TransferResponse;
import com.bank.grpc.transaction.WithdrawRequest;
import com.bank.grpc.transaction.WithdrawResponse;
import com.bank.grpc.transaction.TransactionServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class TransactionServiceImpl extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final CommandBus commandBus;
    private final FraudDetectionEngine fraudEngine;

    @Override
    public void deposit(DepositRequest request, StreamObserver<DepositResponse> responseObserver) {
        String txId = UUID.randomUUID().toString();
        try {
            // Проверка на мошенничество
            Transaction tx = buildFraudTransaction(request.getProductId(), 
                request.getContext().getUserId(), 
                new BigDecimal(request.getAmount().getAmount()),
                "DEPOSIT");
            FraudCheckResult fraudResult = fraudEngine.analyzeTransaction(tx);
            
            if (fraudResult.shouldBlock()) {
                responseObserver.onNext(DepositResponse.newBuilder()
                    .setTransactionId(txId)
                    .setStatus(TransactionStatusProto.BLOCKED)
                    .build());
                responseObserver.onCompleted();
                return;
            }

            Money amount = new Money(
                new BigDecimal(request.getAmount().getAmount()),
                request.getAmount().getCurrency());
            DepositCashCommand cmd = new DepositCashCommand(
                new ProductId(request.getProductId()),
                new ClientId(request.getContext().getUserId()),
                amount,
                request.getSourceId());
            commandBus.dispatch(cmd);

            responseObserver.onNext(DepositResponse.newBuilder()
                .setTransactionId(txId)
                .setStatus(TransactionStatusProto.COMPLETED)
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Deposit error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<WithdrawResponse> responseObserver) {
        String txId = UUID.randomUUID().toString();
        try {
            Transaction tx = buildFraudTransaction(request.getProductId(),
                request.getContext().getUserId(),
                new BigDecimal(request.getAmount().getAmount()),
                "WITHDRAWAL");
            FraudCheckResult fraudResult = fraudEngine.analyzeTransaction(tx);
            
            if (fraudResult.shouldBlock()) {
                responseObserver.onNext(WithdrawResponse.newBuilder()
                    .setTransactionId(txId)
                    .setStatus(TransactionStatusProto.BLOCKED)
                    .build());
                responseObserver.onCompleted();
                return;
            }

            Money amount = new Money(
                new BigDecimal(request.getAmount().getAmount()),
                request.getAmount().getCurrency());
            WithdrawCashCommand cmd = new WithdrawCashCommand(
                new ProductId(request.getProductId()),
                new ClientId(request.getContext().getUserId()),
                amount,
                request.getDestinationId());
            commandBus.dispatch(cmd);

            responseObserver.onNext(WithdrawResponse.newBuilder()
                .setTransactionId(txId)
                .setStatus(TransactionStatusProto.COMPLETED)
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Withdraw error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
        String txId = UUID.randomUUID().toString();
        try {
            Transaction tx = buildFraudTransaction(request.getSourceProductId(),
                request.getContext().getUserId(),
                new BigDecimal(request.getAmount().getAmount()),
                "TRANSFER");
            FraudCheckResult fraudResult = fraudEngine.analyzeTransaction(tx);
            
            if (fraudResult.shouldBlock()) {
                responseObserver.onNext(TransferResponse.newBuilder()
                    .setTransactionId(txId)
                    .setStatus(TransactionStatusProto.BLOCKED)
                    .build());
                responseObserver.onCompleted();
                return;
            }

            Money amount = new Money(
                new BigDecimal(request.getAmount().getAmount()),
                request.getAmount().getCurrency());
            MakePaymentCommand cmd = new MakePaymentCommand(
                new ProductId(request.getSourceProductId()),
                new ProductId(request.getDestinationProductId()),
                new ClientId(request.getContext().getUserId()),
                amount);
            commandBus.dispatch(cmd);

            responseObserver.onNext(TransferResponse.newBuilder()
                .setTransactionId(txId)
                .setStatus(TransactionStatusProto.COMPLETED)
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Transfer error: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private Transaction buildFraudTransaction(String productId, String clientId, 
                                               BigDecimal amount, String type) {
        return Transaction.builder()
            .transactionId(UUID.randomUUID().toString())
            .clientId(clientId)
            .amount(amount)
            .type(type)
            .sourceProductId(productId)
            .timestamp(LocalDateTime.now())
            .build();
    }
}