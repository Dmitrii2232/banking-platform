package com.bank.grpc.client.clients;

import com.bank.grpc.fraud.FraudCheckRequest;
import com.bank.grpc.fraud.FraudCheckResponse;
import com.bank.grpc.fraud.FraudServiceGrpc;
import com.bank.grpc.transaction.TransactionProto;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FraudServiceClient {

    @GrpcClient("fraud-service")
    private FraudServiceGrpc.FraudServiceBlockingStub blockingStub;

    @SuppressWarnings("unused")
	private final ManagedChannel channel;
    public FraudServiceClient(ManagedChannel fraudServiceChannel) { this.channel = fraudServiceChannel; }

    public FraudCheckResponse checkTransaction(TransactionProto tx, long timeoutSec) {
        return blockingStub.withDeadlineAfter(timeoutSec, TimeUnit.SECONDS)
            .checkTransaction(FraudCheckRequest.newBuilder().setTransaction(tx).build());
    }
}