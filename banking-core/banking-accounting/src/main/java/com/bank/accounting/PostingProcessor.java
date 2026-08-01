package com.bank.accounting;

import com.bank.domain.accounting.AccountingEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class PostingProcessor {

    private final JdbcTemplate jdbc;

    public PostingProcessor(@Qualifier("accountingJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void post(List<AccountingEntry> entries) {
        if (entries.isEmpty()) return;

        String sql = """
            INSERT INTO accounting_entries (id, event_id, debit_account, credit_account,
                amount, currency, description, posted_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        List<Object[]> batchArgs = entries.stream()
            .map(entry -> new Object[]{
                UUID.randomUUID(),
                UUID.fromString(entry.getEventId()),
                entry.getDebitAccount().getCode(),
                entry.getCreditAccount().getCode(),
                entry.getAmount().getAmount(),
                entry.getAmount().getCurrency(),
                entry.getDescription(),
                LocalDate.now()
            }).toList();

        jdbc.batchUpdate(sql, batchArgs);
        log.debug("Проведено {} бухгалтерских проводок в БД accounting", entries.size());
    }
}