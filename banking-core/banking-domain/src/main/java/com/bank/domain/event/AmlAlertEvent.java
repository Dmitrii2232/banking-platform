// 36. AmlAlertEvent.java
package com.bank.domain.event;
import java.time.LocalDateTime;
import java.util.List;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public record AmlAlertEvent(
    EventId eventId, ProductId productId, ClientId clientId, Money amount,
    List<String> violations, double suspicionScore, LocalDateTime timestamp
) implements Event {
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<String> getViolations() { return violations; }
    public double getSuspicionScore() { return suspicionScore; }
}