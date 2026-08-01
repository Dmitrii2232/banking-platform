package com.bank.domain.event;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.product.ProductTerms;
import java.time.LocalDateTime;

public record ProductOpenedEvent(
    EventId eventId,
    ProductId productId,
    ClientId clientId,
    Money amount,
    String productType,
    ProductTerms terms,
    LocalDateTime timestamp
) implements Event {
    public ProductOpenedEvent {
        if (productType == null || productType.isBlank())
            throw new IllegalArgumentException("Тип продукта обязателен");
    }
    
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getProductType() { return productType; }
    public ProductTerms getTerms() { return terms; }
}