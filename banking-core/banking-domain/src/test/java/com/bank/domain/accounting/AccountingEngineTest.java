package com.bank.domain.accounting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.CashDepositedEvent;
import com.bank.domain.event.ProductClosedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

class AccountingEngineTest {

    private AccountingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new AccountingEngine();
    }

    @Test
    void shouldBookCashDeposit() {
        CashDepositedEvent event = new CashDepositedEvent(
            EventId.generate(),
            new ProductId("00000000-0000-0000-0000-000000000001"),
            new ClientId("00000000-0000-0000-0000-000000000002"),
            Money.rubles("100.00"),
            "source",
            LocalDateTime.now()
        );

        List<AccountingEntry> entries = engine.book(List.of(event));
        assertEquals(1, entries.size());
        assertEquals(AccountType.CASH_DESK, entries.get(0).getDebitAccount());
        assertEquals(AccountType.CLIENT_DEPOSIT, entries.get(0).getCreditAccount());
        assertEquals(Money.rubles("100.00"), entries.get(0).getAmount());
    }

    @Test
    void shouldReturnEmptyForUnknownEvent() {
        ProductClosedEvent event = new ProductClosedEvent(
            EventId.generate(),
            new ProductId("00000000-0000-0000-0000-000000000001"),
            new ClientId("00000000-0000-0000-0000-000000000002"),
            Money.ZERO_RUB,
            LocalDateTime.now()
        );
        List<AccountingEntry> entries = engine.book(List.of(event));
        assertTrue(entries.isEmpty());
    }
}
