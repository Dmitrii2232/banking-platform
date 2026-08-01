// 35. FraudDetectedEvent.java
package com.bank.domain.event;
import java.time.LocalDateTime;
import java.util.List;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public record FraudDetectedEvent(
    EventId eventId, ProductId productId, ClientId clientId, Money amount,
    String transactionId, String clientIdStr, String riskLevel,
    double fraudScore, List<String> reasons, LocalDateTime timestamp
) implements Event {
    public FraudDetectedEvent(EventId eventId, String transactionId, String clientIdStr,
                               String riskLevel, double fraudScore, List<String> reasons,
                               LocalDateTime timestamp) {
        this(eventId, new ProductId("00000000-0000-0000-0000-000000000000"),
             new ClientId("00000000-0000-0000-0000-000000000000"), Money.ZERO_RUB,
             transactionId, clientIdStr, riskLevel, fraudScore, reasons, timestamp);
    }
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTransactionId() { return transactionId; }
    public String getClientIdStr() { return clientIdStr; }
    public String getRiskLevel() { return riskLevel; }
    public double getFraudScore() { return fraudScore; }
    public List<String> getReasons() { return reasons; }
}