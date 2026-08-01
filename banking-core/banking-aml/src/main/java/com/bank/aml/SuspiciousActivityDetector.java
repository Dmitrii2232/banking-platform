package com.bank.aml;

import com.bank.antifraud.models.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class SuspiciousActivityDetector {

    public double analyze(Transaction transaction) {
        double score = 0.0;
        if (isRoundAmount(transaction.getAmount())) score += 0.2;
        if (isJustBelowThreshold(transaction.getAmount())) score += 0.3;
        if (isNightTime(transaction.getTimestamp())) score += 0.15;
        if (hasSuspiciousDescription(transaction)) score += 0.15;
        if (isUnusualGeography(transaction)) score += 0.2;
        return Math.min(score, 1.0);
    }

    private boolean isRoundAmount(BigDecimal amount) {
        BigDecimal remainder = amount.remainder(BigDecimal.valueOf(1000));
        return remainder.compareTo(BigDecimal.ZERO) == 0 && amount.compareTo(new BigDecimal("50000")) > 0;
    }

    private boolean isJustBelowThreshold(BigDecimal amount) {
        BigDecimal threshold = new BigDecimal("600000");
        BigDecimal lowerBound = threshold.multiply(new BigDecimal("0.95"));
        return amount.compareTo(lowerBound) >= 0 && amount.compareTo(threshold) < 0;
    }

    private boolean isNightTime(java.time.LocalDateTime timestamp) {
        int hour = timestamp.getHour();
        return hour >= 23 || hour <= 4;
    }

    private boolean hasSuspiciousDescription(Transaction transaction) {
        String desc = transaction.getDescription() != null ? transaction.getDescription().toLowerCase() : "";
        return desc.contains("наличные") || desc.contains("обналичивание");
    }

    private boolean isUnusualGeography(Transaction transaction) {
        return false;
    }
}