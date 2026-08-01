package com.bank.domain.exception;

public class FraudBlockException extends BankingException {
    
    private final String transactionId;
    private final String reason;
    private final double fraudScore;
    
    public FraudBlockException(String transactionId, String reason, double fraudScore) {
        super("FRAUD_BLOCK",
            String.format("Транзакция %s заблокирована: %s (score=%.2f)", transactionId, reason, fraudScore));
        this.transactionId = transactionId;
        this.reason = reason;
        this.fraudScore = fraudScore;
    }
    
    public String getTransactionId() { return transactionId; }
    public String getReason() { return reason; }
    public double getFraudScore() { return fraudScore; }
}