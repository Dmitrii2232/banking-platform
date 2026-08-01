package com.bank.accounting;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
public class TrialBalanceVerifier {

    private final JdbcTemplate jdbc;
    private final com.bank.monitoring.AlertingService alertingService;

    public TrialBalanceVerifier(@Qualifier("accountingJdbcTemplate") JdbcTemplate jdbc,
                                 com.bank.monitoring.AlertingService alertingService) {
        this.jdbc = jdbc;
        this.alertingService = alertingService;
    }

    public boolean verify(LocalDate date) {
        String sql = """
            SELECT
                SUM(CASE WHEN coa.side = 'ACTIVE' THEN ab.closing_balance ELSE 0 END) as total_debit,
                SUM(CASE WHEN coa.side = 'PASSIVE' THEN ab.closing_balance ELSE 0 END) as total_credit
            FROM account_balances ab
            JOIN chart_of_accounts coa ON ab.account_code = coa.account_code
            WHERE ab.balance_date = ?
            """;

        Map<String, Object> result = jdbc.queryForMap(sql, java.sql.Date.valueOf(date));
        BigDecimal totalDebit = result.get("total_debit") != null ? (BigDecimal) result.get("total_debit") : BigDecimal.ZERO;
        BigDecimal totalCredit = result.get("total_credit") != null ? (BigDecimal) result.get("total_credit") : BigDecimal.ZERO;
        boolean isBalanced = totalDebit.compareTo(totalCredit) == 0;

        if (!isBalanced) {
            BigDecimal difference = totalDebit.subtract(totalCredit).abs();
            log.error("НАРУШЕНИЕ БАЛАНСА! Дебет={}, Кредит={}, Разница={}", totalDebit, totalCredit, difference);
            alertingService.sendCriticalAlert(String.format("Нарушение пробного баланса на %s: разница=%s", date, difference));
        }
        return isBalanced;
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void dailyVerification() {
        LocalDate today = LocalDate.now();
        boolean balanced = verify(today);
        if (balanced) log.info("Пробный баланс на {} сошёлся", today);
    }
}