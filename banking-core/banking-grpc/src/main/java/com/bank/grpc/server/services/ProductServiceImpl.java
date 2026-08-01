package com.bank.grpc.server.services;

import com.bank.commands.CommandBus;
import com.bank.commands.ProductManagementHandler;
import com.bank.domain.command.ChangeMasterAccountCommand;
import com.bank.domain.command.CloseProductCommand;
import com.bank.domain.command.OpenProductCommand;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.product.BankProduct;
import com.bank.domain.product.ProductStatus;
import com.bank.domain.product.ProductTerms;
import com.bank.eventstore.EventStore;
import com.bank.grpc.auth.ClientServiceGrpc;
import com.bank.grpc.auth.FindByPhoneRequest;
import com.bank.grpc.auth.FindByPhoneResponse;
import com.bank.grpc.client.InternalGrpcClient;
import com.bank.grpc.product.BlockProductRequest;
import com.bank.grpc.product.BlockProductResponse;
import com.bank.grpc.product.CloseProductRequest;
import com.bank.grpc.product.CloseProductResponse;
import com.bank.grpc.product.FindClientByPhoneResponse;
import com.bank.grpc.product.GetClientProductsRequest;
import com.bank.grpc.product.GetClientProductsResponse;
import com.bank.grpc.product.GetMasterAccountRequest;
import com.bank.grpc.product.GetProductRequest;
import com.bank.grpc.product.OpenProductRequest;
import com.bank.grpc.product.OpenProductResponse;
import com.bank.grpc.product.ProductProto;
import com.bank.grpc.product.ProductStatusProto;
import com.bank.grpc.product.ProductTermsProto;
import com.bank.grpc.product.ProductTypeProto;
import com.bank.grpc.product.ProductServiceGrpc;
import com.bank.grpc.product.UnblockProductResponse;
import com.bank.grpc.product.FindClientByPhoneRequest;
import com.bank.grpc.product.SetMasterAccountRequest;
import com.bank.grpc.product.SetMasterAccountResponse;
import com.bank.grpc.product.UnblockProductRequest;
import com.bank.query.ProductQueryService;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@GrpcService
public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductManagementHandler productHandler;
    private final CommandBus commandBus;
    private final EventStore eventStore;
    private final ProductQueryService productQueryService;
    private final JdbcTemplate jdbc;
    private final InternalGrpcClient internalGrpcClient;
    private ManagedChannel authChannel;
    private ClientServiceGrpc.ClientServiceBlockingStub authStub;

    @Value("${grpc.client.auth-service.host:banking-auth-server}")
    private String authHost;

    @Value("${grpc.client.auth-service.port:9085}")
    private int authPort;

    public ProductServiceImpl(ProductManagementHandler productHandler,
                               CommandBus commandBus,
                               EventStore eventStore,
                               ProductQueryService productQueryService,
                               JdbcTemplate jdbc,
                               InternalGrpcClient internalGrpcClient) {
        this.productHandler = productHandler;
        this.commandBus = commandBus;
        this.eventStore = eventStore;
        this.productQueryService = productQueryService;
        this.jdbc = jdbc;
        this.internalGrpcClient = internalGrpcClient;
    }

    @PostConstruct
    public void init() {
        authChannel = NettyChannelBuilder.forAddress(authHost, authPort)
            .usePlaintext()
            .intercept(internalGrpcClient.getClientInterceptor())  // API Key
            .build();
        authStub = ClientServiceGrpc.newBlockingStub(authChannel);
        log.info("Secure gRPC client connected to {}:{}", authHost, authPort);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        if (authChannel != null) authChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public void openProduct(OpenProductRequest request, StreamObserver<OpenProductResponse> responseObserver) {
        try {
            ProductTerms terms = convertToProductTerms(request.getTerms());
            String productType = convertProductType(request.getProductType());
            OpenProductCommand cmd = new OpenProductCommand(new ClientId(request.getClientId()), productType, terms);
            List<Event> events = productHandler.handle(cmd);
            String productId = events.stream()
                .filter(e -> e instanceof com.bank.domain.event.ProductOpenedEvent)
                .findFirst().map(e -> e.getProductId().toString()).orElse(UUID.randomUUID().toString());

            responseObserver.onNext(OpenProductResponse.newBuilder()
                .setProductId(productId).setStatus(ProductStatusProto.ACTIVE).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error opening product: {}", e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void closeProduct(CloseProductRequest request, StreamObserver<CloseProductResponse> responseObserver) {
        try {
            BankProduct product = eventStore.loadProduct(new ProductId(request.getProductId()))
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
            CloseProductCommand cmd = new CloseProductCommand(
                new ProductId(request.getProductId()), product.getClientId(), request.getReason());
            productHandler.handle(cmd);
            responseObserver.onNext(CloseProductResponse.newBuilder()
                .setProductId(request.getProductId()).setStatus(ProductStatusProto.CLOSED).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductProto> responseObserver) {
        try {
            BankProduct product = eventStore.loadProduct(new ProductId(request.getProductId()))
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
            responseObserver.onNext(convertToProto(product));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getClientProducts(GetClientProductsRequest request,
                                   StreamObserver<GetClientProductsResponse> responseObserver) {
        try {
            List<BankProduct> products = productQueryService.getClientProducts(
                new ClientId(request.getClientId()));
            GetClientProductsResponse.Builder builder = GetClientProductsResponse.newBuilder()
                .setTotalCount(products.size());
            products.forEach(p -> builder.addProducts(convertToProto(p)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void blockProduct(BlockProductRequest request, StreamObserver<BlockProductResponse> responseObserver) {
        try {
            BankProduct product = eventStore.loadProduct(new ProductId(request.getProductId()))
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
            product.block(request.getReason());
            responseObserver.onNext(BlockProductResponse.newBuilder()
                .setProductId(request.getProductId()).setStatus(ProductStatusProto.BLOCKED).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void unblockProduct(UnblockProductRequest request, StreamObserver<UnblockProductResponse> responseObserver) {
        try {
            BankProduct product = eventStore.loadProduct(new ProductId(request.getProductId()))
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
            product.unblock();
            responseObserver.onNext(UnblockProductResponse.newBuilder()
                .setProductId(request.getProductId()).setStatus(ProductStatusProto.ACTIVE).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void findClientByPhone(FindClientByPhoneRequest request, 
                                   StreamObserver<FindClientByPhoneResponse> responseObserver) {
        try {
            String phone = request.getPhone().replaceAll("[^0-9]", "");
            
            // Безопасный вызов к auth-серверу через gRPC с API-ключом
            FindByPhoneResponse authResponse = authStub
                .withDeadlineAfter(5, TimeUnit.SECONDS)
                .findByPhone(FindByPhoneRequest.newBuilder().setPhone(phone).build());
            
            if (!authResponse.getFound()) {
                responseObserver.onNext(FindClientByPhoneResponse.newBuilder()
                    .setFound(false).build());
                responseObserver.onCompleted();
                return;
            }
            
            String clientId = authResponse.getClientId();
            String masterSql = "SELECT id FROM products WHERE client_id = ? AND is_master = TRUE AND status = 'ACTIVE'";
            String masterProductId = jdbc.queryForObject(masterSql, String.class, UUID.fromString(clientId));
            
            FindClientByPhoneResponse.Builder response = FindClientByPhoneResponse.newBuilder()
                .setFound(true)
                .setClientId(clientId);
            
            if (masterProductId != null) {
                response.setMasterProductId(masterProductId);
            }
            
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.warn("Client not found by phone {}: {}", request.getPhone(), e.getMessage());
            responseObserver.onNext(FindClientByPhoneResponse.newBuilder().setFound(false).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getMasterAccount(GetMasterAccountRequest request,
                                  StreamObserver<ProductProto> responseObserver) {
        try {
            String sql = "SELECT id FROM products WHERE client_id = ? AND is_master = TRUE AND status = 'ACTIVE'";
            String productId = jdbc.queryForObject(sql, String.class, UUID.fromString(request.getClientId()));
            
            if (productId == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("Мастер-счет не найден").asRuntimeException());
                return;
            }
            
            BankProduct product = eventStore.loadProduct(new ProductId(productId))
                .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
            
            responseObserver.onNext(convertToProto(product));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void setMasterAccount(SetMasterAccountRequest request,
                                  StreamObserver<SetMasterAccountResponse> responseObserver) {
        try {
            ClientId clientId = new ClientId(request.getClientId());
            ProductId newMasterId = new ProductId(request.getProductId());
            
            BankProduct product = eventStore.loadProduct(newMasterId)
                .orElseThrow(() -> new IllegalArgumentException("Счет не найден"));
            
            if (!product.getClientId().equals(clientId)) {
                responseObserver.onNext(SetMasterAccountResponse.newBuilder()
                    .setSuccess(false).setMessage("Счет не принадлежит клиенту").build());
                responseObserver.onCompleted();
                return;
            }
            
            if (!product.canBeMaster()) {
                responseObserver.onNext(SetMasterAccountResponse.newBuilder()
                    .setSuccess(false).setMessage("Мастер-счетом может быть только активный текущий счет").build());
                responseObserver.onCompleted();
                return;
            }
            
            ChangeMasterAccountCommand cmd = new ChangeMasterAccountCommand(clientId, newMasterId);
            commandBus.dispatch(cmd);
            
            responseObserver.onNext(SetMasterAccountResponse.newBuilder()
                .setSuccess(true).setMessage("Мастер-счет изменен").build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error setting master account: {}", e.getMessage(), e);
            responseObserver.onNext(SetMasterAccountResponse.newBuilder()
                .setSuccess(false).setMessage(e.getMessage()).build());
            responseObserver.onCompleted();
        }
    }

    private ProductTerms convertToProductTerms(ProductTermsProto proto) {
        ProductTerms.Builder builder = ProductTerms.builder();
        builder.interestRate(new BigDecimal(
            proto.getInterestRate() != null && !proto.getInterestRate().isEmpty() ? proto.getInterestRate() : "0"));
        builder.termMonths(proto.getTermMonths() > 0 ? proto.getTermMonths() : 12);
        builder.capitalization(proto.getCapitalization());
        builder.replenishable(proto.getReplenishable());
        builder.partialWithdrawal(proto.getPartialWithdrawal());
        return builder.build();
    }

    private String convertProductType(ProductTypeProto proto) {
        return switch (proto) {
            case TERM_DEPOSIT -> "TERM_DEPOSIT";
            case CURRENT_ACCOUNT -> "CURRENT_ACCOUNT";
            case CREDIT_CARD -> "CREDIT_CARD";
            case LOAN -> "LOAN";
            default -> throw new IllegalArgumentException("Неизвестный тип: " + proto);
        };
    }

    private ProductProto convertToProto(BankProduct product) {
        ProductTypeProto typeProto = switch (product.getClass().getSimpleName()) {
            case "TermDeposit" -> ProductTypeProto.TERM_DEPOSIT;
            case "CurrentAccount" -> ProductTypeProto.CURRENT_ACCOUNT;
            case "CreditCard" -> ProductTypeProto.CREDIT_CARD;
            case "LoanProduct" -> ProductTypeProto.LOAN;
            default -> ProductTypeProto.UNKNOWN;
        };

        boolean isMaster = product.isMaster();
        if (!isMaster) {
            try {
                Boolean master = jdbc.queryForObject(
                    "SELECT is_master FROM products WHERE id = ?",
                    Boolean.class, product.getId().getUuid());
                isMaster = master != null && master;
            } catch (Exception ignored) {}
        }

        return ProductProto.newBuilder()
            .setProductId(product.getId().toString())
            .setClientId(product.getClientId().toString())
            .setProductType(typeProto)
            .setStatus(convertStatus(product.getStatus()))
            .setBalance(com.bank.grpc.common.MoneyProto.newBuilder()
                .setAmount(product.getBalance().getAmount().toPlainString())
                .setCurrency(product.getBalance().getCurrency()).build())
            .setVersion(product.getVersion())
            .setIsMaster(isMaster)
            .build();
    }

    private ProductStatusProto convertStatus(ProductStatus status) {
        return switch (status) {
            case DRAFT -> ProductStatusProto.DRAFT;
            case ACTIVE -> ProductStatusProto.ACTIVE;
            case FROZEN -> ProductStatusProto.FROZEN;
            case BLOCKED -> ProductStatusProto.BLOCKED;
            case MATURED -> ProductStatusProto.MATURED;
            case CLOSED -> ProductStatusProto.CLOSED;
            default -> ProductStatusProto.DRAFT;
        };
    }
}