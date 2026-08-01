package com.bank.eventstore;

import com.bank.domain.common.ProductId;
import com.bank.domain.product.BankProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class SnapshotRepository {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SnapshotRepository(JdbcTemplate eventStoreJdbcTemplate) {
        this.jdbc = eventStoreJdbcTemplate;
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    public void createSnapshot(ProductId productId, BankProduct product, long version) {
        try {
            String stateJson = objectMapper.writeValueAsString(product);
            String sql = """
                INSERT INTO snapshots (product_id, version, state, created_at)
                VALUES (?, ?, ?::jsonb, NOW())
                ON CONFLICT (product_id, version) DO NOTHING
                """;
            jdbc.update(sql, productId.getUuid(), version, stateJson);
            log.debug("Создан снапшот для продукта {} на версии {}", productId, version);
        } catch (Exception e) {
            log.error("Ошибка создания снапшота: {}", e.getMessage());
        }
    }

    public Optional<BankProduct> loadFromSnapshot(ProductId productId) {
        String sql = "SELECT state FROM snapshots WHERE product_id = ? ORDER BY version DESC LIMIT 1";
        try {
            String stateJson = jdbc.queryForObject(sql, String.class, productId.getUuid());
            if (stateJson != null) {
                BankProduct product = objectMapper.readValue(stateJson, BankProduct.class);
                return Optional.of(product);
            }
        } catch (Exception e) {
            log.warn("Не удалось загрузить снапшот: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public long getSnapshotVersion(ProductId productId) {
        String sql = "SELECT COALESCE(MAX(version), 0) FROM snapshots WHERE product_id = ?";
        Long version = jdbc.queryForObject(sql, Long.class, productId.getUuid());
        return version != null ? version : 0;
    }
}