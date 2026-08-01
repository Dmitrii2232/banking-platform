package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.product.ProductTerms;
import java.time.LocalDateTime;
import java.util.UUID;

public record OpenProductCommand(
    ClientId clientId,
    String productType,
    ProductTerms terms,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public OpenProductCommand(ClientId clientId, String productType, ProductTerms terms) {
        this(clientId, productType, terms, UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    @Override
    public String getCommandId() { return commandId; }
    @Override
    public ClientId getClientId() { return clientId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getProductType() { return productType; }
    public ProductTerms getTerms() { return terms; }
}