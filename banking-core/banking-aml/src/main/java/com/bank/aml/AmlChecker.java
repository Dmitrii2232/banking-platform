package com.bank.aml;

import com.bank.antifraud.models.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmlChecker {

    private final SanctionsListService sanctionsService;
    private final LimitMonitor limitMonitor;
    private final SuspiciousActivityDetector suspiciousDetector;

    @Value("${aml.daily-limit-rub:600000}")
    private BigDecimal dailyLimit;

    public AmlCheckResult check(Transaction transaction) {
        List<String> violations = new ArrayList<>();

        if (sanctionsService.isSanctioned(transaction.getClientId())) {
            violations.add("SANCTIONED_CLIENT");
        }
        if (!limitMonitor.checkDailyLimit(transaction.getClientId(), transaction.getAmount())) {
            violations.add("DAILY_LIMIT_EXCEEDED");
        }
        if (!limitMonitor.checkMonthlyLimit(transaction.getClientId(), transaction.getAmount())) {
            violations.add("MONTHLY_LIMIT_EXCEEDED");
        }

        double suspicionScore = suspiciousDetector.analyze(transaction);
        if (suspicionScore > 0.7) {
            violations.add("SUSPICIOUS_ACTIVITY");
        }
        if (checkStructuring(transaction)) {
            violations.add("STRUCTURING");
        }

        boolean passed = violations.isEmpty();
        return new AmlCheckResult(passed, violations, suspicionScore);
    }

    private boolean checkStructuring(Transaction transaction) {
        List<Transaction> recent = limitMonitor.getRecentTransactions(transaction.getClientId(), 10);
        int nearLimitCount = 0;
        BigDecimal nearLimitThreshold = dailyLimit.multiply(new BigDecimal("0.9"));
        for (Transaction t : recent) {
            if (t.getAmount().compareTo(nearLimitThreshold) >= 0
                && t.getAmount().compareTo(dailyLimit) < 0) {
                nearLimitCount++;
            }
        }
        return nearLimitCount >= 3;
    }

    public record AmlCheckResult(boolean passed, List<String> violations, double suspicionScore) {
        public String getReason() { return passed ? "OK" : String.join(", ", violations); }
    }
}