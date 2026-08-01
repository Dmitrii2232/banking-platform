package com.bank.commands;

import com.bank.domain.command.WithdrawCashCommand;
import com.bank.domain.event.Event;
import com.bank.domain.product.BankProduct;
import com.bank.eventstore.EventStore;
import com.bank.eventstore.OutboxRepository;
import com.bank.accounting.AccountingService;
import com.bank.eventstore.EventSerializer;
import com.bank.kafka.topics.KafkaTopics;
import com.bank.domain.common.OutboxMessage;
import com.bank.eventstore.PostgresEventStore.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class WithdrawalCommandHandler {

    private final EventStore eventStore;
    private final AccountingService accountingService;
    private final OutboxRepository outboxRepository;
    private final EventSerializer eventSerializer;

    private static final int MAX_RETRIES = 3;

    public WithdrawalCommandHandler(EventStore eventStore,
                                     AccountingService accountingService,
                                     OutboxRepository outboxRepository,
                                     EventSerializer eventSerializer) {
        this.eventStore = eventStore;
        this.accountingService = accountingService;
        this.outboxRepository = outboxRepository;
        this.eventSerializer = eventSerializer;
    }

    public List<Event> handle(WithdrawCashCommand cmd) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return doHandle(cmd);
            } catch (OptimisticLockException e) {
                retries++;
                log.warn("Конфликт версий при списании, попытка {}/{}: {}", retries, MAX_RETRIES, e.getMessage());
                if (retries >= MAX_RETRIES) {
                    throw new RuntimeException("Не удалось выполнить списание после " + MAX_RETRIES + " попыток", e);
                }
                try { Thread.sleep((long) Math.pow(2, retries) * 50); }
                catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
            }
        }
        throw new RuntimeException("Не удалось выполнить списание");
    }

    @Transactional
    protected List<Event> doHandle(WithdrawCashCommand cmd) {
        log.info("Обработка списания: product={}", 
            cmd.getProductId().toString().substring(0, Math.min(8, cmd.getProductId().toString().length())));

        BankProduct product = eventStore.loadProduct(cmd.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));

        List<Event> events = product.apply(cmd);
        eventStore.append(events);
        var entries = accountingService.book(events);
        accountingService.post(entries);

        // Transactional Outbox
        for (Event event : events) {
            String payload = eventSerializer.serialize(event);
            outboxRepository.save(new OutboxMessage(
                KafkaTopics.TRANSACTION_EVENTS_TOPIC,
                cmd.getClientId().toString(),
                payload
            ));
            outboxRepository.save(new OutboxMessage(
                KafkaTopics.PRODUCT_EVENTS_TOPIC,
                cmd.getClientId().toString(),
                payload
            ));
        }
        return events;
    }
}