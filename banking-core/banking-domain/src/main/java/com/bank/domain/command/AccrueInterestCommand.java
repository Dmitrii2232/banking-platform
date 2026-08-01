package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccrueInterestCommand(
    ProductId productId,
    LocalDate date,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public AccrueInterestCommand(ProductId productId, LocalDate date) {
        this(productId, date, UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    @Override
    public String getCommandId() { return commandId; }
    @Override
    public ClientId getClientId() {
        return new ClientId("00000000-0000-0000-0000-000000000000");
    }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public ProductId getProductId() { return productId; }
    public LocalDate getDate() { return date; }
}