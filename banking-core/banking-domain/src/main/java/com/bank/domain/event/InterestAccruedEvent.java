// 32. InterestAccruedEvent.java
package com.bank.domain.event;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public record InterestAccruedEvent(
    EventId eventId, ProductId productId, ClientId clientId,
    Money amount, LocalDate accrualDate, boolean capitalization, LocalDateTime timestamp
) implements Event {
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public LocalDate getAccrualDate() { return accrualDate; }
    public boolean isCapitalization() { return capitalization; }
}