package com.bank.kafka.producer;

import com.bank.domain.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountingEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "banking.accounting.entries";

    public void send(Event event) {
        kafkaTemplate.send(TOPIC, event.getEventId().toString(), event);
    }
}