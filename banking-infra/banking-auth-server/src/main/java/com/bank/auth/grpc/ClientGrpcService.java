package com.bank.auth.grpc;

import com.bank.auth.model.User;
import com.bank.auth.repository.UserRepository;
import com.bank.grpc.auth.ClientServiceGrpc;
import com.bank.grpc.auth.FindByPhoneRequest;
import com.bank.grpc.auth.FindByPhoneResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ClientGrpcService extends ClientServiceGrpc.ClientServiceImplBase {

    private final UserRepository userRepository;

    @Value("${banking.internal.api-key:bank-internal-secret-key-2024}")
    private String expectedApiKey;

    @Override
    public void findByPhone(FindByPhoneRequest request, StreamObserver<FindByPhoneResponse> responseObserver) {
        String phone = request.getPhone().replaceAll("[^0-9]", "");
        log.debug("Internal gRPC call: findByPhone for phone={}", phone);
        
        User user = userRepository.findByUsername(phone)
            .or(() -> userRepository.findByPhone(phone))
            .orElse(null);
        
        if (user != null) {
            responseObserver.onNext(FindByPhoneResponse.newBuilder()
                .setFound(true)
                .setClientId(user.getClientId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .build());
        } else {
            responseObserver.onNext(FindByPhoneResponse.newBuilder()
                .setFound(false)
                .build());
        }
        responseObserver.onCompleted();
    }
}