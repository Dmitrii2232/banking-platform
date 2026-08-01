package com.bank.antifraud.rules;

import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class AmountDeviationRule implements FraudRule {

    @Value("${fraud.rules.amount.min-samples-for-deviation:10}")
    private int minSamples;

    private final Map<String, List<BigDecimal>> clientAmounts = new ConcurrentHashMap<>();

    @Override public String getRuleName() { return "AMOUNT_DEVIATION"; }
    @Override public boolean isEnabled() { return true; }
    @Override public int getPriority() { return 3; }

    @Override
    public FraudCheckResult evaluate(Transaction transaction) {
        String clientId = transaction.getClientId();
        BigDecimal amount = transaction.getAmount();

        List<BigDecimal> amounts = clientAmounts.computeIfAbsent(clientId, k -> new CopyOnWriteArrayList<>());
        if (amounts.size() < minSamples) {
            amounts.add(amount);
            return FraudCheckResult.pass();
        }

        BigDecimal mean = calculateMean(amounts);
        BigDecimal stdDev = calculateStdDev(amounts, mean);

        if (stdDev.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal zScore = amount.subtract(mean).abs().divide(stdDev, 4, RoundingMode.HALF_UP);
            if (zScore.compareTo(BigDecimal.valueOf(3.0)) > 0) {
                amounts.add(amount);
                return FraudCheckResult.fail(getRuleName(), String.format("Экстремальное отклонение: Z=%.2f", zScore));
            }
        }

        amounts.add(amount);
        if (amounts.size() > 100) amounts.remove(0);
        return FraudCheckResult.pass();
    }

    private BigDecimal calculateMean(List<BigDecimal> values) {
        BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(values.size()), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateStdDev(List<BigDecimal> values, BigDecimal mean) {
        BigDecimal sumSquaredDiff = values.stream()
            .map(v -> v.subtract(mean).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal variance = sumSquaredDiff.divide(BigDecimal.valueOf(values.size()), 4, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }
}