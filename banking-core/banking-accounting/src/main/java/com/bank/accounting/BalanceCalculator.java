package com.bank.accounting;

import com.bank.domain.accounting.AccountType;
import com.bank.domain.accounting.AccountingEntry;
import com.bank.domain.common.Money;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class BalanceCalculator {

    private final JdbcTemplate jdbc;

    public BalanceCalculator(@Qualifier("accountingJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Money getBalance(AccountType accountType, LocalDate date) {
        String sql = "SELECT closing_balance FROM account_balances WHERE account_code = ? AND balance_date = ?";
        BigDecimal balance = jdbc.queryForObject(sql, BigDecimal.class,
            accountType.getCode(), java.sql.Date.valueOf(date));
        return balance != null ? new Money(balance, "RUB") : Money.ZERO_RUB;
    }

    public void updateBalances(List<AccountingEntry> entries) {
        for (AccountingEntry entry : entries) {
            updateAccountBalance(entry.getDebitAccount(), entry.getAmount(), true);
            updateAccountBalance(entry.getCreditAccount(), entry.getAmount(), false);
        }
    }

    private void updateAccountBalance(AccountType accountType, Money amount, boolean isDebit) {
        String sql = """
            INSERT INTO account_balances (account_code, balance_date, debit_turnover,
                credit_turnover, closing_balance, currency)
            VALUES (?, CURRENT_DATE, ?, ?, ?, ?)
            ON CONFLICT (account_code, balance_date, currency) DO UPDATE SET
                debit_turnover = account_balances.debit_turnover + EXCLUDED.debit_turnover,
                credit_turnover = account_balances.credit_turnover + EXCLUDED.credit_turnover,
                closing_balance = CASE
                    WHEN (SELECT side FROM chart_of_accounts WHERE account_code = ?) = 'ACTIVE'
                    THEN account_balances.opening_balance +
                         account_balances.debit_turnover + EXCLUDED.debit_turnover -
                         account_balances.credit_turnover - EXCLUDED.credit_turnover
                    ELSE account_balances.opening_balance +
                         account_balances.credit_turnover + EXCLUDED.credit_turnover -
                         account_balances.debit_turnover - EXCLUDED.debit_turnover
                END
            """;

        BigDecimal debitAmount = isDebit ? amount.getAmount() : BigDecimal.ZERO;
        BigDecimal creditAmount = isDebit ? BigDecimal.ZERO : amount.getAmount();

        jdbc.update(sql, accountType.getCode(), debitAmount, creditAmount,
            amount.getAmount(), amount.getCurrency(), accountType.getCode());
    }
}