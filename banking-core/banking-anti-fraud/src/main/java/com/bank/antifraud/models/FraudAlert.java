package com.bank.antifraud.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FraudAlert {
    private final String alertId;
    private final String transactionId;
    private final String clientId;
    private final RiskLevel riskLevel;
    private final double score;
    private final List<String> reasons;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final AlertStatus status;
    private final String assignedTo;

    private FraudAlert(Builder builder) {
        this.alertId = builder.alertId;
        this.transactionId = builder.transactionId;
        this.clientId = builder.clientId;
        this.riskLevel = builder.riskLevel;
        this.score = builder.score;
        this.reasons = builder.reasons;
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.assignedTo = builder.assignedTo;
    }

    public static Builder builder() { return new Builder(); }

    public String getAlertId() { return alertId; }
    public String getTransactionId() { return transactionId; }
    public String getClientId() { return clientId; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public double getScore() { return score; }
    public List<String> getReasons() { return reasons; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public AlertStatus getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }

    public static class Builder {
        private String alertId;
        private String transactionId;
        private String clientId;
        private RiskLevel riskLevel;
        private double score;
        private List<String> reasons;
        private BigDecimal amount;
        private LocalDateTime timestamp;
        private AlertStatus status;
        private String assignedTo;

        public Builder alertId(String v) { alertId = v; return this; }
        public Builder transactionId(String v) { transactionId = v; return this; }
        public Builder clientId(String v) { clientId = v; return this; }
        public Builder riskLevel(RiskLevel v) { riskLevel = v; return this; }
        public Builder score(double v) { score = v; return this; }
        public Builder reasons(List<String> v) { reasons = v; return this; }
        public Builder amount(BigDecimal v) { amount = v; return this; }
        public Builder timestamp(LocalDateTime v) { timestamp = v; return this; }
        public Builder status(AlertStatus v) { status = v; return this; }
        public Builder assignedTo(String v) { assignedTo = v; return this; }
        public FraudAlert build() { return new FraudAlert(this); }
    }
}