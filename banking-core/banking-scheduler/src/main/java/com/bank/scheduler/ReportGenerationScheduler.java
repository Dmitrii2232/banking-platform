package com.bank.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportGenerationScheduler {

    @Scheduled(cron = "0 0 4 * * *")
    public void generateDailyReports() {
        log.info("Генерация ежедневных отчётов");
        log.info("Отчёты сгенерированы");
    }
}