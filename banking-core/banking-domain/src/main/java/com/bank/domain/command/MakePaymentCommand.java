package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import java.time.LocalDateTime;
import java.util.UUID;

public record MakePaymentCommand(
    ProductId sourceProductId,
    ProductId destinationProductId,
    ClientId clientId,
    Money amount,
    String idempotencyKey,
    String description,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public MakePaymentCommand(ProductId sourceProductId, ProductId destinationProductId,
                               ClientId clientId, Money amount) {
        this(sourceProductId, destinationProductId, clientId, amount, null, null,
             UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    public MakePaymentCommand withIdempotencyKey(String key) {
        return new MakePaymentCommand(sourceProductId, destinationProductId, clientId,
            amount, key, description, commandId, timestamp);
    }
    
    @Override
    public String getCommandId() { return commandId; }
    @Override
    public ClientId getClientId() { return clientId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public ProductId getSourceProductId() { return sourceProductId; }
    public ProductId getDestinationProductId() { return destinationProductId; }
    public Money getAmount() { return amount; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getDescription() { return description; }
}