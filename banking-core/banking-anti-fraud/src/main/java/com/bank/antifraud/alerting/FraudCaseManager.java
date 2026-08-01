package com.bank.antifraud.alerting;

import com.bank.antifraud.models.FraudAlert;
import com.bank.antifraud.models.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class FraudCaseManager {

    private final Map<String, FraudCase> cases = new ConcurrentHashMap<>();

    public void createCase(FraudAlert alert) {
        FraudCase fraudCase = new FraudCase(
            alert.getAlertId(), alert.getClientId(), alert.getTransactionId(),
            alert.getRiskLevel(), alert.getScore(), alert.getReasons(),
            CaseStatus.OPEN, LocalDateTime.now(), null);
        cases.put(alert.getAlertId(), fraudCase);
        log.info("Создан кейс расследования: id={}", alert.getAlertId());
    }

    public void updateCase(String caseId, CaseStatus status, String resolution) {
        FraudCase existing = cases.get(caseId);
        if (existing != null) {
            FraudCase updated = new FraudCase(existing.caseId(), existing.clientId(),
                existing.transactionId(), existing.riskLevel(), existing.fraudScore(),
                existing.reasons(), status, existing.createdAt(), resolution);
            cases.put(caseId, updated);
        }
    }

    public record FraudCase(String caseId, String clientId, String transactionId,
                             RiskLevel riskLevel, double fraudScore, List<String> reasons,
                             CaseStatus status, LocalDateTime createdAt, String resolution) {}

    public enum CaseStatus { OPEN, INVESTIGATING, CONFIRMED_FRAUD, FALSE_POSITIVE, CLOSED }
}