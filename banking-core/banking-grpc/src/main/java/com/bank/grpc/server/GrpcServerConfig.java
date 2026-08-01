package com.bank.grpc.server;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bank.grpc.server.interceptors.AuthInterceptor;
import com.bank.grpc.server.interceptors.LoggingInterceptor;
import com.bank.grpc.server.interceptors.MetricsInterceptor;
import com.bank.grpc.server.interceptors.RateLimitingInterceptor;
import com.bank.grpc.server.interceptors.TracingInterceptor;
import com.bank.grpc.server.services.AccountingServiceImpl;
import com.bank.grpc.server.services.FraudServiceImpl;
import com.bank.grpc.server.services.ProductServiceImpl;
import com.bank.grpc.server.services.TransactionServiceImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}") private int port;
    @Value("${grpc.server.max-message-size-mb:1}") private int maxMessageSizeMb;
    @Value("${grpc.server.thread-pool-size:50}") private int threadPoolSize;

    @Bean
    public ServerBuilder<?> serverBuilder(
            AuthInterceptor authInterceptor,
            LoggingInterceptor loggingInterceptor,
            MetricsInterceptor metricsInterceptor,
            TracingInterceptor tracingInterceptor,
            RateLimitingInterceptor rateLimitingInterceptor,
            TransactionServiceImpl transactionService,
            FraudServiceImpl fraudService,
            AccountingServiceImpl accountingService,
            ProductServiceImpl productService) {
        return NettyServerBuilder.forPort(port)
            .executor(Executors.newFixedThreadPool(threadPoolSize))
            .maxInboundMessageSize(maxMessageSizeMb * 1024 * 1024)
            .keepAliveTime(30, TimeUnit.SECONDS)
            .keepAliveTimeout(10, TimeUnit.SECONDS)
            .permitKeepAliveWithoutCalls(false)
            .maxConnectionAge(30, TimeUnit.MINUTES)
            .intercept(tracingInterceptor)
            .intercept(metricsInterceptor)
            .intercept(rateLimitingInterceptor)
            .intercept(authInterceptor)
            .intercept(loggingInterceptor)
            .addService(transactionService)
            .addService(fraudService)
            .addService(accountingService)
            .addService(productService)
            .addService(ProtoReflectionService.newInstance());
    }
}