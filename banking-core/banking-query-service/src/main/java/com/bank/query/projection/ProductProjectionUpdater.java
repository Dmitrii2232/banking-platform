// banking-query-service/src/main/java/com/bank/query/projection/ProductProjectionUpdater.java
package com.bank.query.projection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bank.domain.event.CashDepositedEvent;
import com.bank.domain.event.CashWithdrawnEvent;
import com.bank.domain.event.Event;
import com.bank.domain.event.InterestAccruedEvent;
import com.bank.domain.event.MasterAccountChangedEvent;
import com.bank.domain.event.ProductClosedEvent;
import com.bank.domain.event.ProductOpenedEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductProjectionUpdater {

    private final JdbcTemplate jdbc;

    @KafkaListener(topics = "banking.product.events", groupId = "banking-core")
    public void onProductEvent(Event event) {
        try {
            if (event instanceof ProductOpenedEvent e) {
                handleProductOpened(e);
            } else if (event instanceof CashDepositedEvent e) {
                handleCashDeposited(e);
            } else if (event instanceof CashWithdrawnEvent e) {
                handleCashWithdrawn(e);
            } else if (event instanceof InterestAccruedEvent e) {
                handleInterestAccrued(e);
            } else if (event instanceof ProductClosedEvent e) {
                handleProductClosed(e);
            } else if (event instanceof MasterAccountChangedEvent e) {
                handleMasterAccountChanged(e);
            }
        } catch (Exception e) {
            log.error("Error processing product event: {}", e.getMessage(), e);
        }
    }

    private void handleProductOpened(ProductOpenedEvent event) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM products WHERE client_id = ?",
            Integer.class, event.getClientId().getUuid()
        );
        boolean isFirst = count == null || count == 0;
        
        // Мастер-счетом может быть только CurrentAccount
        boolean canBeMaster = "CurrentAccount".equals(event.getProductType());
        boolean isMaster = isFirst && canBeMaster;

        jdbc.update(
            "INSERT INTO products (id, client_id, product_type, status, balance_amount, balance_currency, version, is_master) " +
            "VALUES (?, ?, ?, 'ACTIVE', ?, ?, 1, ?) ON CONFLICT (id) DO NOTHING",
            event.getProductId().getUuid(), 
            event.getClientId().getUuid(),
            event.getProductType(), 
            BigDecimal.ZERO, 
            "RUB", 
            isMaster
        );
        log.info("Product created: {} (master={}, type={})", event.getProductId(), isMaster, event.getProductType());
    }

    private void handleCashDeposited(CashDepositedEvent event) {
        jdbc.update("UPDATE products SET balance_amount = balance_amount + ?, version = version + 1, updated_at = NOW() WHERE id = ?",
            event.getAmount().getAmount(), event.getProductId().getUuid());
    }

    private void handleCashWithdrawn(CashWithdrawnEvent event) {
        jdbc.update("UPDATE products SET balance_amount = balance_amount - ?, version = version + 1, updated_at = NOW() WHERE id = ?",
            event.getAmount().getAmount(), event.getProductId().getUuid());
    }

    private void handleInterestAccrued(InterestAccruedEvent event) {
        if (event.isCapitalization()) {
            jdbc.update("UPDATE products SET balance_amount = balance_amount + ?, version = version + 1, updated_at = NOW() WHERE id = ?",
                event.getAmount().getAmount(), event.getProductId().getUuid());
        }
    }

    private void handleProductClosed(ProductClosedEvent event) {
        jdbc.update("UPDATE products SET status = 'CLOSED', is_master = FALSE, version = version + 1, updated_at = NOW() WHERE id = ?",
            event.getProductId().getUuid());
    }

    private void handleMasterAccountChanged(MasterAccountChangedEvent event) {
        // Снимаем флаг is_master со старого мастер-счета
        if (event.getOldMasterProductId() != null) {
            jdbc.update("UPDATE products SET is_master = FALSE, version = version + 1 WHERE id = ?",
                UUID.fromString(event.getOldMasterProductId()));
        }
        
        // Устанавливаем флаг is_master для нового мастер-счета
        jdbc.update("UPDATE products SET is_master = TRUE, version = version + 1 WHERE id = ?",
            UUID.fromString(event.getNewMasterProductId()));
            
        log.info("Master account changed: {} -> {}", event.getOldMasterProductId(), event.getNewMasterProductId());
    }
}