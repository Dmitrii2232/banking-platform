// banking-command-handler/src/main/java/com/bank/commands/ProductManagementHandler.java
package com.bank.commands;

import com.bank.domain.command.ChangeMasterAccountCommand;
import com.bank.domain.command.CloseProductCommand;
import com.bank.domain.command.OpenProductCommand;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.event.MasterAccountChangedEvent;
import com.bank.domain.product.BankProduct;
import com.bank.domain.product.CreditCard;
import com.bank.domain.product.CurrentAccount;
import com.bank.domain.product.LoanProduct;
import com.bank.domain.product.TermDeposit;
import com.bank.eventstore.EventStore;
import com.bank.kafka.producer.EventProducer;
import com.bank.kafka.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductManagementHandler {

    private final EventStore eventStore;
    private final EventProducer eventProducer;
    private final JdbcTemplate jdbc;

    @Transactional
    public List<Event> handle(OpenProductCommand cmd) {
        ProductId productId = ProductId.generate();
        BankProduct product = switch (cmd.getProductType()) {
            case "TERM_DEPOSIT" -> new TermDeposit(productId, cmd.getClientId(), cmd.getTerms());
            case "CURRENT_ACCOUNT" -> new CurrentAccount(productId, cmd.getClientId(), cmd.getTerms());
            case "CREDIT_CARD" -> new CreditCard(productId, cmd.getClientId(), cmd.getTerms());
            case "LOAN" -> new LoanProduct(productId, cmd.getClientId(), cmd.getTerms());
            default -> throw new IllegalArgumentException("Неизвестный тип: " + cmd.getProductType());
        };
        
        // Проверяем, есть ли уже продукты у клиента (через проекцию)
        boolean isFirstProduct = isFirstProductForClient(cmd.getClientId());
        
        // Получаем события активации продукта
        List<Event> events = new ArrayList<>(product.activate());
        
        // Если это первый продукт и это CurrentAccount - добавляем событие мастер-счета
        if (isFirstProduct && product instanceof CurrentAccount) {
            MasterAccountChangedEvent masterEvent = new MasterAccountChangedEvent(
                EventId.generate(),
                productId,
                cmd.getClientId(),
                Money.ZERO_RUB,
                productId.toString(),
                null,  // null означает, что это первый мастер-счет
                LocalDateTime.now()
            );
            events.add(masterEvent);
            
            // Применяем событие к агрегату
            product.replay(masterEvent);
            
            log.info("Установлен мастер-счет для первого продукта клиента: {}", productId);
        }
        
        // Сохраняем все события
        eventStore.append(events);

        // Отправляем в Kafka
        for (Event event : events) {
            eventProducer.sendEvent(KafkaTopics.PRODUCT_EVENTS_TOPIC,
                cmd.getClientId().toString(), event);
        }

        log.info("Продукт открыт: id={}, type={}, isMaster={}, isFirst={}", 
            productId, cmd.getProductType(), product.isMaster(), isFirstProduct);
        return events;
    }

    @Transactional
    public List<Event> handle(CloseProductCommand cmd) {
        BankProduct product = eventStore.loadProduct(cmd.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Продукт не найден"));
        
        if (product.isMaster()) {
            throw new IllegalStateException("Нельзя закрыть мастер-счет. Сначала смените мастер-счет.");
        }
        
        List<Event> events = product.close();
        eventStore.append(events);

        for (Event event : events) {
            eventProducer.sendEvent(KafkaTopics.PRODUCT_EVENTS_TOPIC,
                cmd.getClientId().toString(), event);
        }

        return events;
    }

    @Transactional
    public List<Event> handleChangeMaster(ChangeMasterAccountCommand cmd) {
        ProductId newMasterId = cmd.getNewMasterProductId();
        
        // Загружаем новый счет
        BankProduct newMaster = eventStore.loadProduct(newMasterId)
            .orElseThrow(() -> new IllegalArgumentException("Счет не найден: " + newMasterId));
        
        // Проверяем, что счет принадлежит клиенту
        if (!newMaster.getClientId().equals(cmd.getClientId())) {
            throw new IllegalArgumentException("Счет не принадлежит клиенту");
        }
        
        // Проверяем, что это текущий счет и он активен
        if (!newMaster.canBeMaster()) {
            throw new IllegalArgumentException("Мастер-счетом может быть только активный текущий счет");
        }
        
        // Ищем старый мастер-счет через проекцию
        String oldMasterId = findCurrentMasterAccount(cmd.getClientId());
        
        // Проверяем, что не пытаемся установить тот же счет
        if (oldMasterId != null && oldMasterId.equals(newMasterId.toString())) {
            throw new IllegalArgumentException("Этот счет уже является мастер-счетом");
        }
        
        // Создаем событие смены мастер-счета
        MasterAccountChangedEvent event = new MasterAccountChangedEvent(
            EventId.generate(),
            newMasterId,
            cmd.getClientId(),
            Money.ZERO_RUB,
            newMasterId.toString(),
            oldMasterId,
            LocalDateTime.now()
        );
        
        // Применяем событие к агрегатам
        newMaster.replay(event);
        if (oldMasterId != null) {
            eventStore.loadProduct(new ProductId(oldMasterId)).ifPresent(oldMaster -> {
                oldMaster.replay(event);
            });
        }
        
        // Сохраняем событие
        eventStore.append(event);
        
        // Отправляем в Kafka
        eventProducer.sendEvent(KafkaTopics.PRODUCT_EVENTS_TOPIC,
            cmd.getClientId().toString(), event);
        
        log.info("Master account changed: {} -> {}", oldMasterId, newMasterId);
        return List.of(event);
    }
    
    /**
     * Проверяет, есть ли у клиента другие продукты (через таблицу проекции)
     */
    private boolean isFirstProductForClient(ClientId clientId) {
        try {
            String sql = "SELECT COUNT(*) FROM products WHERE client_id = ?";
            Integer count = jdbc.queryForObject(sql, Integer.class, clientId.getUuid());
            return count == null || count == 0;
        } catch (Exception e) {
            log.warn("Ошибка проверки продуктов клиента: {}", e.getMessage());
            return true; // В случае ошибки считаем первым продуктом
        }
    }
    
    /**
     * Ищет текущий мастер-счет клиента через проекцию
     */
    private String findCurrentMasterAccount(ClientId clientId) {
        try {
            String sql = "SELECT id FROM products WHERE client_id = ? AND is_master = TRUE AND status = 'ACTIVE'";
            return jdbc.queryForObject(sql, String.class, clientId.getUuid());
        } catch (Exception e) {
            return null;
        }
    }
}