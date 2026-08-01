package com.bank.kafka.producer;

import com.bank.domain.common.OutboxMessage;
import com.bank.eventstore.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxSender {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 100) // каждые 100 мс
    public void sendOutboxMessages() {
        List<OutboxMessage> messages = outboxRepository.findUnsent(50);
        
        for (OutboxMessage msg : messages) {
            try {
                kafkaTemplate.send(msg.getTopic(), msg.getMessageKey(), msg.getPayload()).get();
                outboxRepository.markAsSent(msg.getId());
                log.debug("Outbox message sent: topic={}, key={}", msg.getTopic(), msg.getMessageKey());
            } catch (Exception e) {
                log.error("Failed to send outbox message: id={}, topic={}, error={}", 
                    msg.getId(), msg.getTopic(), e.getMessage());
                // Сообщение останется неотправленным и повторится на следующей итерации
            }
        }
    }
}