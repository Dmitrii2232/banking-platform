package com.bank.commands;

import com.bank.domain.command.DepositCashCommand;
import com.bank.domain.command.MakePaymentCommand;
import com.bank.domain.command.WithdrawCashCommand;
import com.bank.domain.event.Event;
import com.bank.domain.product.BankProduct;
import com.bank.eventstore.EventStore;
import com.bank.eventstore.PostgresEventStore.OptimisticLockException;
import com.bank.accounting.AccountingService;
import com.bank.kafka.producer.EventProducer;
import com.bank.kafka.topics.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class PaymentCommandHandler {

    private final EventStore eventStore;
    private final AccountingService accountingService;
    private final EventProducer eventProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:payment:";
    private static final int MAX_RETRIES = 3;

    public PaymentCommandHandler(EventStore eventStore, AccountingService accountingService,
                                  EventProducer eventProducer, RedisTemplate<String, Object> redisTemplate) {
        this.eventStore = eventStore;
        this.accountingService = accountingService;
        this.eventProducer = eventProducer;
        this.redisTemplate = redisTemplate;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<Event> handle(MakePaymentCommand cmd) {
        String idempotencyKey = cmd.getIdempotencyKey();
        @SuppressWarnings("unused")
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            String existing = checkIdempotency(idempotencyKey);
            if (existing != null) throw new IdempotentOperationException("Уже выполнено: " + existing);
            acquireIdempotencyLock(idempotencyKey);
        }

        List<Event> allEvents = new ArrayList<>();
        int retries = 0;

        while (retries < MAX_RETRIES) {
            try {
                BankProduct source = eventStore.loadProduct(cmd.getSourceProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Источник не найден"));
                BankProduct dest = eventStore.loadProduct(cmd.getDestinationProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Назначение не найдено"));

                WithdrawCashCommand withdrawCmd = new WithdrawCashCommand(
                    cmd.getSourceProductId(), cmd.getClientId(), cmd.getAmount(), cmd.getDestinationProductId().toString());
                List<Event> withdrawEvents = source.apply(withdrawCmd);
                eventStore.append(withdrawEvents);
                allEvents.addAll(withdrawEvents);

                DepositCashCommand depositCmd = new DepositCashCommand(
                    cmd.getDestinationProductId(), dest.getClientId(), cmd.getAmount(), cmd.getSourceProductId().toString());
                List<Event> depositEvents = dest.apply(depositCmd);
                eventStore.append(depositEvents);
                allEvents.addAll(depositEvents);

                var entries = accountingService.book(allEvents);
                accountingService.post(entries);

                for (Event event : allEvents) {
                    eventProducer.sendEvent(KafkaTopics.TRANSACTION_EVENTS_TOPIC, cmd.getClientId().toString(), event);
                }

                if (idempotencyKey != null) saveIdempotencyResult(idempotencyKey, allEvents.get(0).getEventId().toString());
                return allEvents;

            } catch (OptimisticLockException e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    if (idempotencyKey != null) releaseIdempotencyLock(idempotencyKey);
                    throw new RuntimeException("Не удалось выполнить платёж", e);
                }
                allEvents.clear();
                try { Thread.sleep((long) Math.pow(2, retries) * 50); }
                catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
            }
        }
        throw new RuntimeException("Не удалось выполнить платёж");
    }

    private String checkIdempotency(String key) {
        Object result = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + key);
        return result != null ? result.toString() : null;
    }

    private void acquireIdempotencyLock(String key) {
        redisTemplate.opsForValue().setIfAbsent(IDEMPOTENCY_PREFIX + key + ":lock", "locked", Duration.ofMinutes(5));
    }

    private void releaseIdempotencyLock(String key) {
        redisTemplate.delete(IDEMPOTENCY_PREFIX + key + ":lock");
    }

    private void saveIdempotencyResult(String key, String result) {
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + key, result, Duration.ofHours(24));
        redisTemplate.delete(IDEMPOTENCY_PREFIX + key + ":lock");
    }

    public static class IdempotentOperationException extends RuntimeException {
        public IdempotentOperationException(String message) { super(message); }
    }
}