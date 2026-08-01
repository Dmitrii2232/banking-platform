package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import java.time.LocalDateTime;
import java.util.UUID;

public record BlockProductCommand(
    ProductId productId,
    ClientId clientId,
    String reason,
    String operatorId,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public BlockProductCommand(ProductId productId, ClientId clientId, String reason, String operatorId) {
        this(productId, clientId, reason, operatorId, UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    @Override
    public String getCommandId() { return commandId; }
    @Override
    public ClientId getClientId() { return clientId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public ProductId getProductId() { return productId; }
    public String getReason() { return reason; }
    public String getOperatorId() { return operatorId; }
}