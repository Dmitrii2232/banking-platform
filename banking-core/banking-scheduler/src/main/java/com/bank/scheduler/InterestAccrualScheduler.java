package com.bank.scheduler;

import com.bank.commands.InterestAccrualHandler;
import com.bank.domain.common.ProductId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestAccrualScheduler {

    private final InterestAccrualHandler interestHandler;
    private final JdbcTemplate jdbc;

    @Scheduled(cron = "0 0 2 * * *")
    public void accrueDailyInterest() {
        log.info("Запуск ежедневного начисления процентов");
        LocalDate today = LocalDate.now();
        String sql = "SELECT id FROM products WHERE status = 'ACTIVE' AND product_type IN ('TermDeposit','CurrentAccount','CreditCard')";
        List<String> productIds = jdbc.queryForList(sql, String.class);
        List<ProductId> ids = productIds.stream().map(ProductId::new).toList();
        interestHandler.handleBatch(ids, today);
        log.info("Начисление процентов завершено. Обработано: {}", ids.size());
    }
}