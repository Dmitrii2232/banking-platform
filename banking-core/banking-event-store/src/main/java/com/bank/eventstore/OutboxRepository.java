package com.bank.eventstore;

import com.bank.domain.common.OutboxMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class OutboxRepository {

    private final JdbcTemplate jdbc;

    public OutboxRepository(JdbcTemplate eventStoreJdbcTemplate) {
        this.jdbc = eventStoreJdbcTemplate;
    }

    public void save(OutboxMessage message) {
        jdbc.update(
            "INSERT INTO outbox_messages (id, topic, message_key, payload) VALUES (?, ?, ?, ?::jsonb)",
            message.getId(), message.getTopic(), message.getMessageKey(), message.getPayload()
        );
    }

    public List<OutboxMessage> findUnsent(int limit) {
        String sql = "SELECT id, topic, message_key, payload FROM outbox_messages WHERE sent = FALSE ORDER BY created_at LIMIT ?";
        return jdbc.query(sql, (rs, rowNum) -> {
            OutboxMessage msg = new OutboxMessage(
                rs.getString("topic"),
                rs.getString("message_key"),
                rs.getString("payload")
            );
            return msg;
        }, limit);
    }

    public void markAsSent(UUID id) {
        jdbc.update("UPDATE outbox_messages SET sent = TRUE, sent_at = NOW() WHERE id = ?", id);
    }
}