package com.bank.antifraud.alerting;

import com.bank.antifraud.models.FraudAlert;
import com.bank.antifraud.models.FraudCheckResult;
import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.AlertStatus;
import com.bank.antifraud.models.RiskLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudCaseManager caseManager;

    public FraudAlert createAlert(Transaction transaction, FraudCheckResult result) {
        FraudAlert alert = FraudAlert.builder()
            .alertId(UUID.randomUUID().toString())
            .transactionId(transaction.getTransactionId())
            .clientId(transaction.getClientId())
            .riskLevel(result.riskLevel())
            .score(result.combinedScore())
            .reasons(result.reasons())
            .amount(transaction.getAmount())
            .timestamp(LocalDateTime.now())
            .status(AlertStatus.NEW)
            .build();

        if (result.riskLevel() == RiskLevel.HIGH || result.riskLevel() == RiskLevel.CRITICAL) {
            caseManager.createCase(alert);
        }
        log.info("Создан фрод-алерт: id={}, level={}, score={}", alert.getAlertId(), alert.getRiskLevel(), alert.getScore());
        return alert;
    }
}