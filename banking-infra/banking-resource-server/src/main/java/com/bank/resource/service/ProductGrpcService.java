package com.bank.resource.service;

import com.bank.grpc.product.CloseProductRequest;
import com.bank.grpc.product.CloseProductResponse;
import com.bank.grpc.product.FindClientByPhoneRequest;
import com.bank.grpc.product.FindClientByPhoneResponse;
import com.bank.grpc.product.GetClientProductsRequest;
import com.bank.grpc.product.GetClientProductsResponse;
import com.bank.grpc.product.GetMasterAccountRequest;
import com.bank.grpc.product.GetProductRequest;
import com.bank.grpc.product.OpenProductRequest;
import com.bank.grpc.product.OpenProductResponse;
import com.bank.grpc.product.ProductProto;
import com.bank.grpc.product.ProductServiceGrpc;
import com.bank.grpc.product.ProductTermsProto;
import com.bank.grpc.product.ProductTypeProto;
import com.bank.grpc.product.SetMasterAccountRequest;
import com.bank.grpc.product.SetMasterAccountResponse;
import com.bank.resource.interceptor.JwtClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductGrpcService {

    @Value("${grpc.client.product-service.host:banking-core}")
    private String host;

    @Value("${grpc.client.product-service.port:9090}")
    private int port;

    private ManagedChannel channel;
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = NettyChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(new JwtClientInterceptor())
            .build();
        stub = ProductServiceGrpc.newBlockingStub(channel);
        log.info("Product gRPC client connected to {}:{}", host, port);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (channel != null) channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Map<String, Object> getClientProducts(String clientId) {
        GetClientProductsResponse response = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .getClientProducts(GetClientProductsRequest.newBuilder().setClientId(clientId).build());
        List<Map<String, Object>> products = response.getProductsList().stream()
            .map(this::convertProductToMap).collect(Collectors.toList());
        return Map.of("products", products, "totalCount", response.getTotalCount());
    }

    public Map<String, Object> getProduct(String productId) {
        ProductProto product = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .getProduct(GetProductRequest.newBuilder().setProductId(productId).build());
        return convertProductToMap(product);
    }

    public Map<String, Object> getMasterAccount(String clientId) {
        ProductProto product = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .getMasterAccount(GetMasterAccountRequest.newBuilder().setClientId(clientId).build());
        return convertProductToMap(product);
    }

    public Map<String, Object> findClientByPhone(String phone) {
        FindClientByPhoneResponse response = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .findClientByPhone(FindClientByPhoneRequest.newBuilder().setPhone(phone).build());
        return Map.of(
            "found", response.getFound(),
            "clientId", response.getClientId(),
            "masterProductId", response.getMasterProductId()
        );
    }

    public Map<String, Object> openProduct(String clientId, Map<String, Object> request) {
        String productType = (String) request.getOrDefault("productType", "CURRENT_ACCOUNT");
        ProductTypeProto typeProto;
        try { typeProto = ProductTypeProto.valueOf(productType); }
        catch (IllegalArgumentException e) { typeProto = ProductTypeProto.CURRENT_ACCOUNT; }

        ProductTermsProto.Builder termsBuilder = ProductTermsProto.newBuilder()
            .setProductName((String) request.getOrDefault("productName", ""))
            .setInterestRate((String) request.getOrDefault("interestRate", "0"))
            .setTermMonths((Integer) request.getOrDefault("termMonths", 12))
            .setCapitalization((Boolean) request.getOrDefault("capitalization", false))
            .setReplenishable((Boolean) request.getOrDefault("replenishable", false))
            .setPartialWithdrawal((Boolean) request.getOrDefault("partialWithdrawal", false));

        // Добавляем кредитный лимит если есть
        if (request.containsKey("creditLimit")) {
            termsBuilder.setCreditLimit(com.bank.grpc.common.MoneyProto.newBuilder()
                .setAmount(request.get("creditLimit").toString())
                .setCurrency("RUB")
                .build());
        }
   

        OpenProductResponse response = stub
            .withDeadlineAfter(10, TimeUnit.SECONDS)
            .openProduct(OpenProductRequest.newBuilder()
                .setClientId(clientId).setProductType(typeProto).setTerms(termsBuilder.build()).build());

        return Map.of("productId", response.getProductId(), "status", response.getStatus().name());
    }

    public Map<String, Object> closeProduct(String productId, String reason) {
    CloseProductResponse response = stub
        .withDeadlineAfter(10, TimeUnit.SECONDS)
        .closeProduct(CloseProductRequest.newBuilder()
            .setProductId(productId)
            .setReason(reason)
            .build());

    return Map.of(
        "productId", response.getProductId(),
        "status", response.getStatus().name()
    );
}

    public Map<String, Object> setMasterAccount(String clientId, String productId) {
        SetMasterAccountResponse response = stub
            .withDeadlineAfter(5, TimeUnit.SECONDS)
            .setMasterAccount(SetMasterAccountRequest.newBuilder()
                .setClientId(clientId)
                .setProductId(productId)
                .build());
        
        return Map.of(
            "success", response.getSuccess(),
            "message", response.getMessage()
        );
    }

    private Map<String, Object> convertProductToMap(ProductProto p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("productId", p.getProductId());
        map.put("clientId", p.getClientId());
        map.put("productType", p.getProductType().name());
        map.put("status", p.getStatus().name());
        map.put("balance", p.getBalance().getAmount());
        map.put("currency", p.getBalance().getCurrency());
        map.put("version", p.getVersion());
        map.put("isMaster", p.getIsMaster());  // ← из gRPC!
        return map;
    }
}