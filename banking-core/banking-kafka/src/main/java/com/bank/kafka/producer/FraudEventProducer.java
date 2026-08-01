package com.bank.kafka.producer;

import com.bank.antifraud.models.FraudAlert;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "banking.fraud.alerts";

    public void sendFraudAlert(FraudAlert alert) {
        kafkaTemplate.send(TOPIC, alert.getClientId(), alert);
    }
}