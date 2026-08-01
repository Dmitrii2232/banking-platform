// 30. CashDepositedEvent.java
package com.bank.domain.event;
import java.time.LocalDateTime;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public record CashDepositedEvent(
    EventId eventId, ProductId productId, ClientId clientId,
    Money amount, String sourceId, LocalDateTime timestamp
) implements Event {
    public CashDepositedEvent {
        if (amount == null || !amount.isPositive())
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
    }
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSourceId() { return sourceId; }
}