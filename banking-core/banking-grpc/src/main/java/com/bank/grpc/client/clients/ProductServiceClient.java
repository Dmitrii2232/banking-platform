package com.bank.grpc.client.clients;

import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import com.bank.grpc.product.CloseProductRequest;
import com.bank.grpc.product.CloseProductResponse;
import com.bank.grpc.product.GetProductRequest;
import com.bank.grpc.product.OpenProductRequest;
import com.bank.grpc.product.OpenProductResponse;
import com.bank.grpc.product.ProductProto;
import com.bank.grpc.product.ProductServiceGrpc;
import com.bank.grpc.product.ProductTermsProto;
import com.bank.grpc.product.ProductTypeProto;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProductServiceClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub blockingStub;

    @SuppressWarnings("unused")
	private final ManagedChannel channel;
    public ProductServiceClient(ManagedChannel productServiceChannel) { this.channel = productServiceChannel; }

    public OpenProductResponse openProduct(String clientId, ProductTypeProto type, ProductTermsProto terms, long timeoutSec) {
        return blockingStub.withDeadlineAfter(timeoutSec, TimeUnit.SECONDS)
            .openProduct(OpenProductRequest.newBuilder().setClientId(clientId).setProductType(type).setTerms(terms).build());
    }

    public ProductProto getProduct(String productId) {
        return blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).getProduct(GetProductRequest.newBuilder().setProductId(productId).build());
    }

    public CloseProductResponse closeProduct(String productId, String reason) {
        return blockingStub.withDeadlineAfter(15, TimeUnit.SECONDS)
            .closeProduct(CloseProductRequest.newBuilder().setProductId(productId).setReason(reason).build());
    }
}