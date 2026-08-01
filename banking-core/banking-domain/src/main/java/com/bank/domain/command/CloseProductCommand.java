package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import java.time.LocalDateTime;
import java.util.UUID;

public record CloseProductCommand(
    ProductId productId,
    ClientId clientId,
    String reason,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public CloseProductCommand(ProductId productId, ClientId clientId, String reason) {
        this(productId, clientId, reason, UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    @Override
    public String getCommandId() { return commandId; }
    @Override
    public ClientId getClientId() { return clientId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public ProductId getProductId() { return productId; }
    public String getReason() { return reason; }
}