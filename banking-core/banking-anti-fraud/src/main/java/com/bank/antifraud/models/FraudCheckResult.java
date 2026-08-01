package com.bank.antifraud.models;

import java.time.LocalDateTime;
import java.util.List;

public record FraudCheckResult(
    String transactionId,
    RiskLevel riskLevel,
    double combinedScore,
    List<String> reasons,
    boolean shouldBlock,
    LocalDateTime checkTimestamp
) {
    public static FraudCheckResult pass() {
        return new FraudCheckResult(null, RiskLevel.NONE, 0.0, List.of(), false, LocalDateTime.now());
    }

    public static FraudCheckResult fail(String rule, String reason) {
        return new FraudCheckResult(null, RiskLevel.HIGH, 0.8, List.of(rule + ": " + reason), true, LocalDateTime.now());
    }

    public static FraudCheckResult warn(String rule, String reason) {
        return new FraudCheckResult(null, RiskLevel.MEDIUM, 0.4, List.of(rule + ": " + reason), false, LocalDateTime.now());
    }
}