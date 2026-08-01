package com.bank.kafka.consumer;

import com.bank.antifraud.models.FraudAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FraudEventConsumer {

    @KafkaListener(topics = "banking.fraud.alerts", groupId = "banking-core")
    public void consume(FraudAlert alert) {
        log.warn("Получен фрод-алерт: tx={}, score={}", alert.getTransactionId(), alert.getScore());
    }
}