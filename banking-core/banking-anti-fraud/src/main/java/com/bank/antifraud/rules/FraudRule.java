package com.bank.antifraud.rules;

import com.bank.antifraud.models.Transaction;
import com.bank.antifraud.models.FraudCheckResult;

public interface FraudRule {
    String getRuleName();
    FraudCheckResult evaluate(Transaction transaction);
    boolean isEnabled();
    int getPriority();
}