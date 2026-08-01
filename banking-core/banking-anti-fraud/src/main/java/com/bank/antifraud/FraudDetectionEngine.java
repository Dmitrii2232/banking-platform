package com.bank.antifraud;

import com.bank.antifraud.ml.FraudModel;
import com.bank.antifraud.models.FraudCheckResult;
import com.bank.antifraud.models.RiskLevel;
import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.alerting.FraudAlertService;
import com.bank.antifraud.rules.FraudRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionEngine {

    private final List<FraudRule> rules;
    private final FraudModel mlModel;
    private final FraudAlertService alertService;
    @SuppressWarnings("unused")
    private final RedisTemplate<String, Object> redisTemplate;

    public FraudCheckResult analyzeTransaction(Transaction transaction) {
        List<String> reasons = new ArrayList<>();
        double maxScore = 0.0;
        boolean shouldBlock = false;

        for (FraudRule rule : rules) {
            if (!rule.isEnabled()) continue;
            try {
                FraudCheckResult result = rule.evaluate(transaction);
                if (!result.shouldBlock() && result.riskLevel() != RiskLevel.NONE) {
                    reasons.addAll(result.reasons());
                    maxScore = Math.max(maxScore, result.combinedScore());
                } else if (result.shouldBlock()) {
                    reasons.addAll(result.reasons());
                    maxScore = Math.max(maxScore, result.combinedScore());
                    shouldBlock = true;
                }
            } catch (Exception e) {
                log.error("Ошибка правила {}: {}", rule.getRuleName(), e.getMessage());
            }
        }

        double mlScore = mlModel.predict(transaction);
        double combinedScore = Math.max(maxScore, mlScore);

        RiskLevel riskLevel = determineRiskLevel(combinedScore, shouldBlock);

        FraudCheckResult result = new FraudCheckResult(
            transaction.getTransactionId(), riskLevel, combinedScore, reasons,
            shouldBlock, LocalDateTime.now()
        );

        if (shouldBlock || riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL) {
            alertService.createAlert(transaction, result);
        }

        return result;
    }

    private RiskLevel determineRiskLevel(double score, boolean shouldBlock) {
        if (shouldBlock || score >= 0.8) return RiskLevel.CRITICAL;
        if (score >= 0.6) return RiskLevel.HIGH;
        if (score >= 0.3) return RiskLevel.MEDIUM;
        if (score >= 0.1) return RiskLevel.LOW;
        return RiskLevel.NONE;
    }
}