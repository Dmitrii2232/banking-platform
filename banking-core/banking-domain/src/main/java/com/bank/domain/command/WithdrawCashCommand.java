package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import java.time.LocalDateTime;
import java.util.UUID;

public record WithdrawCashCommand(
    ProductId productId,
    ClientId clientId,
    Money amount,
    String destinationId,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public WithdrawCashCommand(ProductId productId, ClientId clientId, Money amount, String destinationId) {
        this(productId, clientId, amount, destinationId, UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    @Override
    public String getCommandId() { return commandId; }
    @Override
    public ClientId getClientId() { return clientId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public ProductId getProductId() { return productId; }
    public Money getAmount() { return amount; }
    public String getDestinationId() { return destinationId; }
}