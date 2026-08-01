package com.bank.domain.event;

import java.time.LocalDateTime;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;

public record MasterAccountChangedEvent(
    EventId eventId,
    ProductId productId,
    ClientId clientId,
    Money amount,
    String newMasterProductId,
    String oldMasterProductId,
    LocalDateTime timestamp
) implements Event {
    @Override
    public EventId getEventId() { return eventId; }
    @Override
    public ProductId getProductId() { return productId; }
    @Override
    public ClientId getClientId() { return clientId; }
    @Override
    public Money getAmount() { return Money.ZERO_RUB; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNewMasterProductId() { return newMasterProductId; }
    public String getOldMasterProductId() { return oldMasterProductId; }
}