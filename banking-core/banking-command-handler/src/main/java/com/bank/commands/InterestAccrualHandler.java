package com.bank.commands;

import com.bank.domain.command.AccrueInterestCommand;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.product.BankProduct;
import com.bank.eventstore.EventStore;
import com.bank.accounting.AccountingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestAccrualHandler {

    private final EventStore eventStore;
    private final AccountingService accountingService;

    public List<Event> handle(AccrueInterestCommand cmd) {
        BankProduct product = eventStore.loadProduct(cmd.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
        List<Event> events = product.apply(cmd);
        if (!events.isEmpty()) {
            eventStore.append(events);
            var entries = accountingService.book(events);
            accountingService.post(entries);
        }
        return events;
    }

    public List<Event> handleBatch(List<ProductId> productIds, LocalDate date) {
        List<Event> allEvents = new ArrayList<>();
        for (ProductId id : productIds) {
            try {
                allEvents.addAll(handle(new AccrueInterestCommand(id, date)));
            } catch (Exception e) {
                log.error("Ошибка начисления процентов для {}: {}", id, e.getMessage());
            }
        }
        return allEvents;
    }
}