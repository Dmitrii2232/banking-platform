package com.bank.query;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.query.projection.TransactionProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final TransactionProjection transactionProjection;

    public List<Event> getProductHistory(ProductId productId, int limit, int offset) {
        return transactionProjection.getTransactions(productId, limit, offset);
    }

    public List<Event> getClientHistory(ClientId clientId, LocalDateTime from, LocalDateTime to) {
        return transactionProjection.getClientTransactions(clientId, from, to);
    }

    public List<Event> getTransactionsByDateRange(LocalDateTime from, LocalDateTime to, int limit) {
        return transactionProjection.getTransactionsByDateRange(from, to, limit);
    }
}