package com.bank.scheduler;

import com.bank.accounting.TrialBalanceVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrialBalanceScheduler {

    private final TrialBalanceVerifier verifier;

    @Scheduled(cron = "0 0 21 * * *")
    public void verifyDailyTrialBalance() {
        log.info("Проверка пробного баланса");
        verifier.dailyVerification();
    }
}