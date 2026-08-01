package com.bank.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

public class OutboxMessage {
    private final UUID id;
    private final String topic;
    private final String messageKey;
    private final String payload;
    private final LocalDateTime createdAt;
    private boolean sent;
    private LocalDateTime sentAt;

    public OutboxMessage(String topic, String messageKey, String payload) {
        this.id = UUID.randomUUID();
        this.topic = topic;
        this.messageKey = messageKey;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
        this.sent = false;
    }

    public UUID getId() { return id; }
    public String getTopic() { return topic; }
    public String getMessageKey() { return messageKey; }
    public String getPayload() { return payload; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isSent() { return sent; }
    public void markSent() { this.sent = true; this.sentAt = LocalDateTime.now(); }
    public LocalDateTime getSentAt() { return sentAt; }
}