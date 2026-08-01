// 37. TransactionFailedEvent.java
package com.bank.domain.event;
import java.time.LocalDateTime;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public record TransactionFailedEvent(
    EventId eventId, ProductId productId, ClientId clientId, Money amount,
    String transactionId, String errorMessage, LocalDateTime timestamp
) implements Event {
    public TransactionFailedEvent(EventId eventId, String transactionId,
                                   String errorMessage, LocalDateTime timestamp) {
        this(eventId, new ProductId("00000000-0000-0000-0000-000000000000"),
             new ClientId("00000000-0000-0000-0000-000000000000"), Money.ZERO_RUB,
             transactionId, errorMessage, timestamp);
    }
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTransactionId() { return transactionId; }
    public String getErrorMessage() { return errorMessage; }
}