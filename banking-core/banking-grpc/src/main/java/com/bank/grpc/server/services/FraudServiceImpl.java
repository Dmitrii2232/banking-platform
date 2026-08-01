package com.bank.grpc.server.services;

import com.bank.antifraud.FraudDetectionEngine;
import com.bank.antifraud.models.Transaction;
import com.bank.grpc.common.EmptyResponse;
import com.bank.grpc.common.RiskLevelProto;
import com.bank.grpc.fraud.BlacklistRequest;
import com.bank.grpc.fraud.FraudCheckRequest;
import com.bank.grpc.fraud.FraudCheckResponse;
import com.bank.grpc.fraud.FraudServiceGrpc;
import com.bank.antifraud.models.FraudCheckResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import java.math.BigDecimal;

@GrpcService
@RequiredArgsConstructor
public class FraudServiceImpl extends FraudServiceGrpc.FraudServiceImplBase {

    private final FraudDetectionEngine fraudEngine;
    private final com.bank.antifraud.rules.BlacklistRule blacklistRule;

    @Override
    public void checkTransaction(FraudCheckRequest request, StreamObserver<FraudCheckResponse> responseObserver) {
        try {
            Transaction tx = Transaction.builder()
                .transactionId(request.getTransaction().getTransactionId().getId())
                .clientId(request.getTransaction().getClientId().getId())
                .amount(new BigDecimal(request.getTransaction().getAmount().getAmount()))
                .type(request.getTransaction().getType().name())
                .deviceFingerprint(request.getTransaction().getDeviceFingerprint())
                .ipAddress(request.getTransaction().getIpAddress())
                .build();
            FraudCheckResult result = fraudEngine.analyzeTransaction(tx);
            responseObserver.onNext(FraudCheckResponse.newBuilder()
                .setTransactionId(result.transactionId())
                .setRiskLevel(RiskLevelProto.valueOf(result.riskLevel().name()))
                .setFraudScore(result.combinedScore())
                .addAllReasons(result.reasons())
                .setShouldBlock(result.shouldBlock())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addToBlacklist(BlacklistRequest request, StreamObserver<EmptyResponse> responseObserver) {
        blacklistRule.addToBlacklist(request.getEntityType(), request.getEntityId(), request.getReason());
        responseObserver.onNext(EmptyResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}