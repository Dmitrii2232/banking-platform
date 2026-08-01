package com.bank.kafka.consumer;

import com.bank.domain.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountingEventConsumer {

    @KafkaListener(topics = "banking.accounting.entries", groupId = "banking-core")
    public void consume(Event event) {
        log.debug("Получено бухгалтерское событие: {}", event.getEventId());
    }
}