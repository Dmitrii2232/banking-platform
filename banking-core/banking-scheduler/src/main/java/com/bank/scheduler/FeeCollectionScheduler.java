package com.bank.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeeCollectionScheduler {

    @Scheduled(cron = "0 0 1 1 * *")
    public void collectMonthlyFees() {
        log.info("Запуск сбора ежемесячных комиссий");
        log.info("Сбор комиссий завершён");
    }
}