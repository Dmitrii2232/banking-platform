package com.bank.query.projection;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.eventstore.EventSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class TransactionProjection {

    private final JdbcTemplate jdbc;
    private final EventSerializer serializer = new EventSerializer();

    public List<Event> getTransactions(ProductId productId, int limit, int offset) {
        String sql = "SELECT event_data FROM events WHERE product_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<String> jsons = jdbc.queryForList(sql, String.class, productId.getUuid(), limit, offset);
        return jsons.stream().map(serializer::deserialize).filter(Objects::nonNull).toList();
    }

    public List<Event> getClientTransactions(ClientId clientId, LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT e.event_data FROM events e
            JOIN products p ON e.product_id = p.id
            WHERE p.client_id = ? AND e.created_at BETWEEN ? AND ?
            ORDER BY e.created_at DESC
            """;
        List<String> jsons = jdbc.queryForList(sql, String.class,
            clientId.getUuid(), Timestamp.valueOf(from), Timestamp.valueOf(to));
        return jsons.stream().map(serializer::deserialize).filter(Objects::nonNull).toList();
    }

    public List<Event> getTransactionsByDateRange(LocalDateTime from, LocalDateTime to, int limit) {
        String sql = "SELECT event_data FROM events WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC LIMIT ?";
        List<String> jsons = jdbc.queryForList(sql, String.class,
            Timestamp.valueOf(from), Timestamp.valueOf(to), limit);
        return jsons.stream().map(serializer::deserialize).filter(Objects::nonNull).toList();
    }
}