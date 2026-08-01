package com.bank.eventstore;

import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.product.BankProduct;
import java.util.List;
import java.util.Optional;

public interface EventStore {
    void append(List<Event> events);
    void append(Event event);
    List<Event> getEvents(ProductId productId);
    List<Event> getEventsAfterVersion(ProductId productId, long version);
    Optional<BankProduct> loadProduct(ProductId productId);
    long getCurrentVersion(ProductId productId);
    boolean existsEvent(String eventId);
}