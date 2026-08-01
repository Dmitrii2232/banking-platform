package com.bank.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.transaction-service.host:localhost}") private String txHost;
    @Value("${grpc.client.transaction-service.port:9091}") private int txPort;
    @Value("${grpc.client.fraud-service.host:localhost}") private String fraudHost;
    @Value("${grpc.client.fraud-service.port:9092}") private int fraudPort;
    @Value("${grpc.client.accounting-service.host:localhost}") private String accountingHost;
    @Value("${grpc.client.accounting-service.port:9093}") private int accountingPort;
    @Value("${grpc.client.product-service.host:localhost}") private String productHost;
    @Value("${grpc.client.product-service.port:9094}") private int productPort;

    @Bean
    public ManagedChannel transactionServiceChannel() { return buildChannel(txHost, txPort); }
    @Bean
    public ManagedChannel fraudServiceChannel() { return buildChannel(fraudHost, fraudPort); }
    @Bean
    public ManagedChannel accountingServiceChannel() { return buildChannel(accountingHost, accountingPort); }
    @Bean
    public ManagedChannel productServiceChannel() { return buildChannel(productHost, productPort); }

    private ManagedChannel buildChannel(String host, int port) {
        return NettyChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .keepAliveTime(30, TimeUnit.SECONDS)
            .keepAliveTimeout(10, TimeUnit.SECONDS)
            .maxInboundMessageSize(10 * 1024 * 1024)
            .build();
    }
}