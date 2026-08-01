package com.bank.eventstore;

import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.event.ProductOpenedEvent;
import com.bank.domain.product.BankProduct;
import com.bank.domain.product.CreditCard;
import com.bank.domain.product.CurrentAccount;
import com.bank.domain.product.LoanProduct;
import com.bank.domain.product.ProductTerms;
import com.bank.domain.product.TermDeposit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class PostgresEventStore implements EventStore {

    private final JdbcTemplate jdbc;
    private final EventSerializer serializer;
    private final SnapshotRepository snapshotRepository;

    public PostgresEventStore(JdbcTemplate eventStoreJdbcTemplate,
                               EventSerializer serializer,
                               SnapshotRepository snapshotRepository) {
        this.jdbc = eventStoreJdbcTemplate;
        this.serializer = serializer;
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void append(List<Event> events) {
        if (events.isEmpty()) return;
        ProductId productId = events.get(0).getProductId();
        long expectedVersion = getCurrentVersion(productId);
        for (Event event : events) {
            expectedVersion++;
            appendSingleEvent(event, expectedVersion);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void append(Event event) {
        ProductId productId = event.getProductId();
        long expectedVersion = getCurrentVersion(productId) + 1;
        appendSingleEvent(event, expectedVersion);
    }

    private void appendSingleEvent(Event event, long expectedVersion) {
        String checkSql = "SELECT COUNT(*) FROM events WHERE product_id = ? AND version = ?";
        Integer existingCount = jdbc.queryForObject(checkSql, Integer.class,
            event.getProductId().getUuid(), expectedVersion);

        if (existingCount != null && existingCount > 0) {
            throw new OptimisticLockException(
                String.format("Конфликт версий: продукт %s, версия %d уже существует",
                    event.getProductId(), expectedVersion));
        }

        String sql = """
            INSERT INTO events (id, product_id, event_type, event_data, version, created_at)
            VALUES (?, ?, ?, ?::jsonb, ?, ?)
            """;

        try {
            jdbc.update(sql,
                event.getEventId().getUuid(),
                event.getProductId().getUuid(),
                event.getClass().getSimpleName(),
                serializer.serialize(event),
                expectedVersion,
                Timestamp.valueOf(event.getTimestamp()));
        } catch (DuplicateKeyException e) {
            throw new OptimisticLockException(
                String.format("Событие уже существует: продукт %s, версия %d",
                    event.getProductId(), expectedVersion), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(ProductId productId) {
        String sql = "SELECT event_data FROM events WHERE product_id = ? ORDER BY version ASC";
        List<String> jsons = jdbc.queryForList(sql, String.class, productId.getUuid());
        return deserializeEvents(jsons);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsAfterVersion(ProductId productId, long version) {
        String sql = "SELECT event_data FROM events WHERE product_id = ? AND version > ? ORDER BY version ASC";
        List<String> jsons = jdbc.queryForList(sql, String.class, productId.getUuid(), version);
        return deserializeEvents(jsons);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BankProduct> loadProduct(ProductId productId) {
        Optional<BankProduct> fromSnapshot = snapshotRepository.loadFromSnapshot(productId);
        List<Event> events;
        if (fromSnapshot.isPresent()) {
            BankProduct product = fromSnapshot.get();
            long snapshotVersion = snapshotRepository.getSnapshotVersion(productId);
            events = getEventsAfterVersion(productId, snapshotVersion);
            for (Event event : events) product.replay(event);
            return Optional.of(product);
        }
        events = getEvents(productId);
        if (events.isEmpty()) return Optional.empty();
        BankProduct product = createProductFromEvents(events);
        return Optional.ofNullable(product);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCurrentVersion(ProductId productId) {
        String sql = "SELECT COALESCE(MAX(version), 0) FROM events WHERE product_id = ?";
        Long version = jdbc.queryForObject(sql, Long.class, productId.getUuid());
        return version != null ? version : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsEvent(String eventId) {
        String sql = "SELECT COUNT(*) FROM events WHERE id = ?::uuid";
        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, UUID.fromString(eventId));
            return count != null && count > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private List<Event> deserializeEvents(List<String> jsons) {
        return jsons.stream()
            .map(json -> {
                try { return serializer.deserialize(json); }
                catch (Exception e) { log.error("Ошибка десериализации, пропускаем", e); return null; }
            })
            .filter(Objects::nonNull)
            .toList();
    }

    private BankProduct createProductFromEvents(List<Event> events) {
        if (events.isEmpty()) return null;
        Event firstEvent = events.get(0);
        ProductId productId = firstEvent.getProductId();
        BankProduct product = null;
        if (firstEvent instanceof ProductOpenedEvent opened) {
            product = createProductByType(opened, productId);
        }
        if (product != null) {
            for (Event event : events) {
                try { product.replay(event); }
                catch (Exception e) { log.error("Ошибка реплея: {}", e.getMessage()); }
            }
        }
        return product;
    }

    private BankProduct createProductByType(ProductOpenedEvent opened, ProductId productId) {
        ProductTerms terms = opened.getTerms() != null ? opened.getTerms() : ProductTerms.builder().build();
        return switch (opened.getProductType()) {
            case "TermDeposit" -> new TermDeposit(productId, opened.getClientId(), terms);
            case "CurrentAccount" -> new CurrentAccount(productId, opened.getClientId(), terms);
            case "CreditCard" -> new CreditCard(productId, opened.getClientId(), terms);
            case "LoanProduct" -> new LoanProduct(productId, opened.getClientId(), terms);
            default -> throw new IllegalArgumentException("Неизвестный тип: " + opened.getProductType());
        };
    }

    public static class OptimisticLockException extends RuntimeException {
        public OptimisticLockException(String message) { super(message); }
        public OptimisticLockException(String message, Throwable cause) { super(message, cause); }
    }
}