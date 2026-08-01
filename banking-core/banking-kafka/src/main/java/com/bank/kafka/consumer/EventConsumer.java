package com.bank.kafka.consumer;

import com.bank.domain.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventConsumer {

    @KafkaListener(topics = "banking.transaction.events", groupId = "banking-core")
    public void consume(Event event) {
        log.debug("Получено событие: {}", event.getEventId());
    }
}