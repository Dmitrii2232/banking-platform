// 34. LoanPaymentEvent.java
package com.bank.domain.event;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public record LoanPaymentEvent(
    EventId eventId, ProductId productId, ClientId clientId,
    Money amount, Money principalAmount, Money interestAmount,
    Money remainingBalance, LocalDate nextPaymentDate, LocalDateTime timestamp
) implements Event {
    public EventId getEventId() { return eventId; }
    public ProductId getProductId() { return productId; }
    public ClientId getClientId() { return clientId; }
    public Money getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Money getPrincipalAmount() { return principalAmount; }
    public Money getInterestAmount() { return interestAmount; }
    public Money getRemainingBalance() { return remainingBalance; }
    public LocalDate getNextPaymentDate() { return nextPaymentDate; }
}